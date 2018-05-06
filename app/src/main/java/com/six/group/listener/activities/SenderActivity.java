package com.six.group.listener.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import com.six.group.R.id;
import com.six.group.R.layout;
import com.six.group.listener.tasks.DbBackgroundTask;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

/**
 * Created by user on 3/04/18.
 */

@EActivity(layout.listener)
public class SenderActivity extends Activity {

    @NonConfigurationInstance
    @Bean
    DbBackgroundTask BgTask;

    @ViewById(id.ws_puerto)
    EditText puerto;

    @ViewById(id.ws_ip)
    EditText ip;

    @Click(id.ws_enviar)
    void enviar() {
        String port = puerto.getText().toString();
        if (port == null || "".equals(port))
            port = "8080";
        String direct = ip.getText().toString();
        if (direct == null || "".equals(direct))
            direct = "localhost";
        BgTask.doSomethingInBackground(port, direct);
    }

    @Click(id.ws_salir)
    void exitProxy() {
        super.finish();
    }


    public void retorno(String isOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultado del envío");
        if ("isOk".equals(isOk))
            builder.setMessage("El envio ha resultado exitoso");
        else if ("noOk".equals(isOk))
            builder.setMessage("El envio ha resultado fallido");
        else if ("".equals(isOk))
            builder.setMessage("No hay datos para enviar");
        else
            builder.setMessage(isOk);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
