package com.six.group.listener.utils;

import android.os.StrictMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.six.group.listener.data.json.Datos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServicesUtils {
    private static Integer sincro = 1800;
    private static String urlWebServiceRest;


    public WebServicesUtils(String puerto, String ip) {
        urlWebServiceRest = "http://" + ip + ":" + puerto + "/WebService/enviar/";
    }

    public String invocaWebServiceHttp(final Datos datos, String action) throws Exception {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        String result = "";
        String urlToInvocate = urlWebServiceRest + action;

        System.out.println("Sincronizacion : TRAZA - URL (" + urlToInvocate + ") segundo timeout (" + sincro + ")");

        final URL url = new URL(urlToInvocate);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(sincro * 1000);
        connection.setReadTimeout(sincro * 1000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonRequest = mapper.writeValueAsString(datos);
        final OutputStream os = connection.getOutputStream();
        os.write(jsonRequest.getBytes());
        os.flush();
        final InputStream content = connection.getInputStream();
        final BufferedReader in = new BufferedReader(new InputStreamReader(content));
        String line;
        while (null != (line = in.readLine())) {
            result += line;
        }
        return result;
    }
}
