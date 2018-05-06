package com.six.group.listener.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Scripts {

    private Context context;

    public Scripts(Context context) {
        this.context = context;
    }

    public String clickScript() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n <!-- Insercion -->\n");
        builder.append("<script src=\"data:text/javascript;base64," + obtenerScriptBase64("jQuery") + "\"></script>\n");
        builder.append("<script src=\"data:text/javascript;base64," + obtenerScriptBase64("click") + "\"></script>\n");
        builder.append("</head>\n");
        return builder.toString();
    }

    private String obtenerScriptBase64(String name) {
        String cadena;
        FileReader f;
        StringBuilder builder = new StringBuilder();
        try {
            f = new FileReader(context.getAssets().open(name + ".js").toString());
            BufferedReader b = new BufferedReader(f);
            while ((cadena = b.readLine()) != null) {
                builder.append(cadena + "\n");
            }
            b.close();
        }  catch (IOException e) {
        }
        return MyBase64.encode(builder.toString());
    }
}