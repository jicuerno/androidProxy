package com.six.group.listener.tasks;

import android.database.Cursor;
import android.util.Log;

import com.six.group.listener.activities.SenderActivity;
import com.six.group.listener.data.AdaptadorDB;
import com.six.group.listener.data.json.Datos;
import com.six.group.listener.data.json.Linea;
import com.six.group.listener.data.json.Tarea;
import com.six.group.listener.data.json.Usuario;
import com.six.group.listener.data.tables.TablaDatos;
import com.six.group.listener.data.tables.TablaTarea;
import com.six.group.listener.data.tables.TablaUsuario;
import com.six.group.listener.utils.WebServicesUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;

/**
 * Created by user on 3/04/18.
 */
@EBean
public class DbBackgroundTask {


    @RootContext
    SenderActivity activity;

    @Background
    public void doSomethingInBackground(String puerto, String ip) {

        AdaptadorDB db = new AdaptadorDB(activity);
        Datos datos = new Datos();
        db.abrir();

        Cursor cursor = db.obtenerUsuarios();
        if (cursor.moveToFirst()) {
            do {
                datos.getUsuarios().add(new Usuario(
                        cursor.getString(TablaUsuario.POS_CAMPO_KEY_USER),
                        cursor.getInt(TablaUsuario.POS_CAMPO_EDAD),
                        cursor.getString(TablaUsuario.POS_CAMPO_SEXO)));
            } while (cursor.moveToNext());
        }

        cursor = db.obtenerTareas();

        if (cursor.moveToFirst()) {
            do {
                datos.getTareas().add(new Tarea(
                        cursor.getString(TablaTarea.POS_CAMPO_KEY_TAREA),
                        cursor.getString(TablaTarea.POS_CAMPO_INS),
                        cursor.getString(TablaTarea.POS_CAMPO_URL_INIT),
                        cursor.getString(TablaTarea.POS_CAMPO_URL_FIN),
                        cursor.getString(TablaTarea.POS_CAMPO_TIEMPO)));
            } while (cursor.moveToNext());
        }

        cursor = db.obtenerRequests();

        if (cursor.moveToFirst()) {
            do {
                datos.getLineas().add(new Linea(
                        cursor.getString(TablaDatos.POS_CAMPO_KEY_USER),
                        cursor.getString(TablaDatos.POS_CAMPO_KEY_TAREA),
                        cursor.getString(TablaDatos.POS_CAMPO_ELEMENT),
                        cursor.getString(TablaDatos.POS_CAMPO_URL),
                        cursor.getString(TablaDatos.POS_CAMPO_EVENT),
                        cursor.getString(TablaDatos.POS_CAMPO_TIME),
                        cursor.getString(TablaDatos.POS_CAMPO_PCIP)));
            } while (cursor.moveToNext());
        }
        db.cerrar();
        updateUI(datos, puerto, ip);
    }

    // Notice that we manipulate the activity ref only from the UI thread
    @UiThread
    void updateUI(Datos datos, String puerto, String ip) {

        Datos auxilixar = new Datos();

        AdaptadorDB db = new AdaptadorDB(activity);
        db.abrir();

        WebServicesUtils webSw = new WebServicesUtils(puerto, ip);
        String isOk = "";

        if (!datos.getUsuarios().isEmpty()) {
            try {
                auxilixar.setUsuarios(datos.getUsuarios());
                isOk = webSw.invocaWebServiceHttp(auxilixar, "usuarios");
                auxilixar.setUsuarios(new ArrayList<Usuario>());
            } catch (Exception ex) {
                isOk = "";
                Log.e("Excepcion", ex.getMessage());
            }

            if ("isOk".equals(isOk)) {
                db.borrarUsurios();
                isOk = "";
            }
        }
        if (!datos.getTareas().isEmpty()) {
            try {
                auxilixar.setTareas(datos.getTareas());
                isOk = webSw.invocaWebServiceHttp(auxilixar, "tareas");
                auxilixar.setTareas(new ArrayList<Tarea>());
            } catch (Exception ex) {
                isOk = "";
                Log.e("Excepcion", ex.getMessage());
            }

            if ("isOk".equals(isOk)) {
                db.borrarTareas();
                isOk = "";
            }
        }
        if (!datos.getLineas().isEmpty()) {
            try {
                auxilixar.setLineas(datos.getLineas());
                isOk = webSw.invocaWebServiceHttp(auxilixar, "lineas");
                auxilixar.setLineas(new ArrayList<Linea>());
            } catch (Exception ex) {
                isOk = "";
                Log.e("Excepcion", ex.getMessage());
            }
        }
        activity.retorno(isOk);
    }
}
