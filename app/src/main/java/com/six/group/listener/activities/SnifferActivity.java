package com.six.group.listener.activities;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import com.six.group.R.id;
import com.six.group.R.layout;
import com.six.group.listener.data.AdaptadorDB;
import com.six.group.listener.data.conf.ArchivoXml;
import com.six.group.listener.data.json.Tarea;
import com.six.group.listener.tasks.SnfBackgroundTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.ViewById;

import java.net.InetAddress;

/**
 * Created by user on 3/04/18.
 */
@EActivity(layout.sniffer)
public class SnifferActivity extends Activity {

    private boolean esperar = true;
    private Long tiempo;
    private Long transcurrido;

    @NonConfigurationInstance
    @Bean
    SnfBackgroundTask BgTask;

    @Extra
    Integer puerto;

    @Extra
    String direccion;

    @Extra
    Integer edad;

    @Extra
    String sexo;

    @Extra
    ArchivoXml datosXml;

    @ViewById(id.texto_label)
    TextView texto_label;

    @ViewById(id.tiempoText)
    TextView tiempoText;

    @AfterViews
    void init() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            InetAddress direc;
            if (!direccion.equals(""))
                direc = InetAddress.getByName(direccion.toString());
            else
                direc = InetAddress.getLocalHost();

            BgTask.doSomethingInBackground(puerto, direc);
            texto_label.setText("Conectado:" + direc.getHostAddress() + ":" + puerto);

        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    @Click(id.iniciar)
    @Background
    void procesarTarea() {
        try {
            for (Tarea tarea : datosXml.getDatos()) {
                realizarInsercionTarea(tarea);
                setTextMessage(tarea.getInstrucciones());
                BgTask.setUser(datosXml.getIdUsuario());
                BgTask.setTarea(tarea);
                tiempo = Long.parseLong(tarea.getTiempo()) * 1000;
                transcurrido = tiempo / 1000;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        CountDownTimer timer = new CountDownTimer(tiempo, 1000) {
                            public void onTick(long millisUntilFinished) {
                                transcurrido = millisUntilFinished / 1000;
                                setTiempoText(transcurrido.toString());
                            }
                            public void onFinish() {
                                esperar = false;
                            }
                        };
                        timer.start();
                    }
                });
                while (esperar) { /* nop; */ }
                esperar = true;
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    private void setTiempoText(final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tiempoText.setText(value);
            }
        });
    }

    public void setTextMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                texto_label.setText(texto_label.getText().toString() + "\n" + message);
            }
        });
    }

    private void realizarInsercionTarea(Tarea tarea) {
        AdaptadorDB db = new AdaptadorDB(this);
        db.abrir();
        db.insertarTarea(tarea.getKeyTarea(), tarea.getInstrucciones(), tarea.getUrlInicio(), tarea.getUrlFinal(), Integer.parseInt(tarea.getTiempo()));
        db.cerrar();
    }

    @Click(id.salir)
    void exitProxy() {
        this.finish();
    }
}
