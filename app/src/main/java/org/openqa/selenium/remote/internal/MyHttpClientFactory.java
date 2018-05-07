package org.openqa.selenium.remote.internal;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;

import java.io.IOException;
import java.net.ProxySelector;

import javax.net.ssl.SSLContext;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MyHttpClientFactory {
    private final CloseableHttpClient httpClient;
    private static final int TIMEOUT_THREE_HOURS = (int) SECONDS.toMillis(60 * 60 * 3);
    private static final int TIMEOUT_TWO_MINUTES = (int) SECONDS.toMillis(60 * 2);
    private final HttpClientConnectionManager gridClientConnectionManager =
            getClientConnectionManager();

    public MyHttpClientFactory() {
        this(TIMEOUT_TWO_MINUTES, TIMEOUT_THREE_HOURS);
    }

    public MyHttpClientFactory(int connectionTimeout, int socketTimeout) {
        httpClient = createHttpClient(null, connectionTimeout, socketTimeout);
    }

    private static HttpClientConnectionManager getClientConnectionManager() {
        PoolingHttpClientConnectionManager cm = null;
        try {
            SSLContext sslcontext = SSLContexts.createSystemDefault();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();

            cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            cm.setMaxTotal(2000);
            cm.setDefaultMaxPerRoute(2000);
            return cm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cm;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public CloseableHttpClient createHttpClient(Credentials credentials) {
        return createHttpClient(credentials, TIMEOUT_TWO_MINUTES, TIMEOUT_THREE_HOURS);
    }

    public CloseableHttpClient createHttpClient(Credentials credentials, int connectionTimeout, int socketTimeout) {
        if (connectionTimeout <= 0) {
            throw new IllegalArgumentException("connection timeout must be > 0");
        }
        if (socketTimeout <= 0) {
            throw new IllegalArgumentException("socket timeout must be > 0");
        }

        SocketConfig socketConfig = createSocketConfig(socketTimeout);
        RequestConfig requestConfig = createRequestConfig(connectionTimeout, socketTimeout);

        HttpClientBuilder builder = HttpClientBuilder.create()
                .setConnectionManager(getClientConnectionManager())
                .setDefaultSocketConfig(createSocketConfig(socketTimeout))
                .setDefaultRequestConfig(createRequestConfig(connectionTimeout, socketTimeout))
                .setRoutePlanner(createRoutePlanner());

        if (credentials != null) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, credentials);
            builder.setDefaultCredentialsProvider(provider);
        }

        return builder.build();
    }

    public HttpClient getGridHttpClient(int connectionTimeout, int socketTimeout) {
        gridClientConnectionManager.closeIdleConnections(100, MILLISECONDS);

        SocketConfig socketConfig = createSocketConfig(socketTimeout);
        RequestConfig requestConfig = createRequestConfig(connectionTimeout, socketTimeout);

        return HttpClientBuilder.create()
                .setConnectionManager(gridClientConnectionManager)
                .setRedirectStrategy(new HttpClientFactory.MyRedirectHandler())
                .setDefaultSocketConfig(socketConfig)
                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(createRoutePlanner())
                .build();
    }

    private SocketConfig createSocketConfig(int socketTimeout) {
        return SocketConfig.custom()
                .setSoReuseAddress(true)
                .setSoTimeout(socketTimeout)
                .build();
    }

    private RequestConfig createRequestConfig(int connectionTimeout, int socketTimeout) {
        return RequestConfig.custom()
                .setStaleConnectionCheckEnabled(true)
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }

    private HttpRoutePlanner createRoutePlanner() {
        return new SystemDefaultRoutePlanner(
                new DefaultSchemePortResolver(), ProxySelector.getDefault());
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gridClientConnectionManager.shutdown();
    }

    static class MyRedirectHandler implements RedirectStrategy {

        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                throws ProtocolException {
            return false;
        }

        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response,
                                          HttpContext context) throws ProtocolException {
            return null;
        }
    }
}
