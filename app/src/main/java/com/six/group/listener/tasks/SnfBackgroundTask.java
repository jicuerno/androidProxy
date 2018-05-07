package com.six.group.listener.tasks;

import android.provider.Settings.Secure;
import android.util.Log;

import com.six.group.listener.activities.SnifferActivity;
import com.six.group.listener.data.AdaptadorDB;
import com.six.group.listener.data.json.Linea;
import com.six.group.listener.data.json.Tarea;
import com.six.group.listener.utils.Scripts;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.mitm.MyRootCertificateGenerator;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.MyRemoteWebDriver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import static java.net.URLDecoder.decode;

/**
 * Created by user on 3/04/18.
 */
@EBean
public class SnfBackgroundTask {

    private BrowserMobProxy server;
    private WebDriver webDriver;
    private String script;
    private String user;
    private Tarea tarea;

    @RootContext
    SnifferActivity activity;

    @Background
    public void doSomethingInBackground(Integer puerto, InetAddress direccion) {
        setupSniffer(puerto, direccion);
    }

    @UiThread
    void setupSniffer(Integer puerto, InetAddress direccion) {

        MyRootCertificateGenerator rootCG = MyRootCertificateGenerator.builder().build();
        String path = activity.getFilesDir().getPath();
        File cer = new File(path, "/certificate.cer");
        File pem = new File(path, "/private-key.pem");
        File p12 = new File(path, "/keystore.p12");
        rootCG.saveRootCertificateAsPemFile(cer);
        rootCG.savePrivateKeyAsPemFile(pem, "password");
        rootCG.saveRootCertificateAndKey("PKCS12", p12, "privateKeyAlias", "password");

        ImpersonatingMitmManager mitmManager = ImpersonatingMitmManager.builder().rootCertificateSource(rootCG).build();

        server = new BrowserMobProxyServer();
        server.setMitmManager(mitmManager);

        try {
            script = new Scripts(activity).clickScript();

            server.addRequestFilter(new RequestFilter() {
                @Override
                public HttpResponse filterRequest(HttpRequest request, HttpMessageContents contents,
                                                  HttpMessageInfo messageInfo) {
                    String url = messageInfo.getOriginalUrl().toLowerCase();
                    if (url.contains("www.myservice.com")) {
                        String messageContents = contents.getTextContents();

                        messageContents = messageContents.replace("data=", "").replace("url=", "")
                                .replace("session=", "").replace("time=", "").replace("pcIp=", "");

                        String[] array = messageContents.split("&");
                        try {
                            String data = decode(array[0], "UTF-8");
                            String uri = decode(array[1], "UTF-8");
                            String time = decode(array[2], "UTF-8");
                            String session = decode(array[3], "UTF-8");
                            String pcIp = decode(array[4], "UTF-8");

                            if (tarea.getUrlFinal().contains(uri)) {
                                Linea linea = new Linea(user, tarea.getKeyTarea(), data, uri, session, time, pcIp);
                                realizarInsercion(linea);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            });

            server.addResponseFilter(new ResponseFilter() {

                @Override
                public void filterResponse(HttpResponse response, HttpMessageContents contents,
                                           HttpMessageInfo messageInfo) {
                    String messageContents = contents.getTextContents();

                    if (messageContents.contains("</HEAD>") || messageContents.contains("</head>")) {
                        // lo injectamos
                        String newContents = messageContents.replaceAll("</HEAD>", script);
                        if (!newContents.contains("//custom Insert"))
                            newContents = messageContents.replaceAll("</head>", script);
                        // lo metemos otra vez
                        contents.setTextContents(newContents);
                        System.out.println("#--> recuperado: " + newContents);
                    }
                }
            });

            server.start(puerto, direccion);
            Log.d("PROXY iniciado:", direccion.getHostAddress() + ":" + puerto);
            server.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
            setProfileRemote(puerto, direccion.getHostAddress());
        } catch (Exception e) {
            server.stop();
        }
    }

    private void setProfileFirefox(int port, String ip) {

        webDriver = null;

        try {

            FirefoxProfile ffProfile = new FirefoxProfile();
            ffProfile.setPreference("acceptInsecureCerts", true);
            ffProfile.setPreference("acceptSslCerts", true);
            ffProfile.setPreference("enableNativeEvents", true);
            ffProfile.setPreference("network.proxy.type", 1);
            ffProfile.setPreference("network.proxy.http", ip);
            ffProfile.setPreference("network.proxy.http_port", port);
            ffProfile.setPreference("network.proxy.https", ip);
            ffProfile.setPreference("network.proxy.https_port", port);
            ffProfile.setPreference("security.mixed_content.block_active_content", false);

/*
            File file = new File(activity.getFilesDir().getPath() + "/geckodriver");
            if (!file.exists()) {
                AssetManager mngr = activity.getAssets();
                InputStream inputStream = mngr.open("geckodriver");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                OutputStream outStream = new FileOutputStream(file);
                outStream.write(buffer);
                file.setExecutable(true);
            }
            System.setProperty("webdriver.gecko.driver", file.getPath());*/
            webDriver = new FirefoxDriver(ffProfile);

        } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
        }
    }

    private void setProfileRemote(int port, String ip) {

        webDriver = null;
        String deviceId = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
        try {

            String proxyInfo = ip + ":" + port;
            Proxy proxy = new Proxy();
            proxy.setProxyType(Proxy.ProxyType.MANUAL);
            proxy.setHttpProxy(proxyInfo);
            proxy.setSslProxy(proxyInfo);
            proxy.setNoProxy(null);

            DesiredCapabilities dc = DesiredCapabilities.android();
            dc.setCapability("proxy", proxy);
            dc.setCapability("deviceName", deviceId);
            dc.setCapability("platformName", "Android");
            dc.setCapability("platformVersion", "5.1");

           /* FirefoxProfile ffProfile = new FirefoxProfile();
            ffProfile.setPreference("acceptInsecureCerts", true);
            ffProfile.setPreference("acceptSslCerts", true);
            ffProfile.setPreference("enableNativeEvents", true);
            ffProfile.setPreference("network.proxy.type", 1);
            ffProfile.setPreference("security.mixed_content.block_active_content", false);
            dc.setCapability("firefox_profile", ffProfile);*/

            //  webDriver = new MyRemoteWebDriver(dc);

        } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
        }
    }


    public void saltoTarea(Tarea tarea) {
        webDriver.get(tarea.getUrlInicio());
    }

    public void close() {
        String path = activity.getFilesDir().getPath();
        File file = new File(path + "/private-key.pem");
        if (file.exists())
            file.delete();
        file = new File(path + "/keystore.p12");
        if (file.exists())
            file.delete();
        file = new File(path + "/certificate.cer");
        if (file.exists())
            file.delete();
    }


    private void realizarInsercion(Linea linea) {
        AdaptadorDB db = new AdaptadorDB(activity);
        db.insertarRequest(linea.getKeyUsuario(), linea.getKeyTarea(), linea.getElemento(),
                linea.getUrl(), linea.getEvento(), linea.getTiempo(), linea.getPcIp());
        db.cerrar();
    }

    public BrowserMobProxy getServer() {
        return server;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public void setServer(BrowserMobProxy server) {
        this.server = server;
    }


}
