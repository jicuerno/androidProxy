package com.six.group.listener.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.six.group.listener.data.tables.TablaDatos;
import com.six.group.listener.data.tables.TablaTarea;
import com.six.group.listener.data.tables.TablaUsuario;

public class AdaptadorDB {

    private final DataBaseHelper DBHelper;
    private SQLiteDatabase db;

    public AdaptadorDB(final Context contexto) {
        DBHelper = new DataBaseHelper(contexto);
    }

    public void abrir() throws SQLException {
        db = DBHelper.getWritableDatabase();
    }

    public void cerrar() {
        DBHelper.close();
    }

    public void insertarRequest(final String keyUser, final String keyTarea, final String element, final String url, final String event, final String time, final String pcIp) {

        final ContentValues valoresIniciales = new ContentValues();

        valoresIniciales.put(TablaDatos.CAMPO_KEY_USER, keyUser);
        valoresIniciales.put(TablaDatos.CAMPO_KEY_TAREA, keyTarea);
        valoresIniciales.put(TablaDatos.CAMPO_ELEMENT, element);
        valoresIniciales.put(TablaDatos.CAMPO_URL, url);
        valoresIniciales.put(TablaDatos.CAMPO_EVENT, event);
        valoresIniciales.put(TablaDatos.CAMPO_TIME, time);
        valoresIniciales.put(TablaDatos.CAMPO_PCIP, pcIp);
        try {
            db.insertOrThrow(TablaDatos.NOMBRE_TABLA, null, valoresIniciales);
        } catch (Exception ex) {
            Log.e("error:", ex.getLocalizedMessage(), ex);
        }
    }

    public void borrarRequests() {
        db.delete(TablaDatos.NOMBRE_TABLA, null, null);
    }

    public Cursor obtenerRequests() {
        return db.query(TablaDatos.NOMBRE_TABLA,
                new String[]{TablaDatos.KEY_CAMPO_ID,TablaDatos.CAMPO_ELEMENT, TablaDatos.CAMPO_URL, TablaDatos.CAMPO_EVENT, TablaDatos.CAMPO_TIME, TablaDatos.CAMPO_PCIP},
                null, null, null, null, TablaDatos.KEY_CAMPO_ID, null);
    }

    public void insertarUsuario(final String keyUser, final int edad, final String sexo) {

        final ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(TablaUsuario.CAMPO_KEY_USER, keyUser);
        valoresIniciales.put(TablaUsuario.CAMPO_EDAD, edad);
        valoresIniciales.put(TablaUsuario.CAMPO_SEXO, sexo);

        try {
            db.insertOrThrow(TablaUsuario.NOMBRE_TABLA, null, valoresIniciales);
        } catch (Exception ex) {
            Log.e("error:", ex.getLocalizedMessage(), ex);
        }
    }

    public void borrarUsurios() {
        db.delete(TablaUsuario.NOMBRE_TABLA, null, null);
    }

    public Cursor obtenerUsuarios() {
        return db.query(TablaUsuario.NOMBRE_TABLA,
                new String[]{TablaUsuario.KEY_CAMPO_ID,TablaUsuario.CAMPO_KEY_USER, TablaUsuario.CAMPO_EDAD, TablaUsuario.CAMPO_SEXO},
                null, null, null, null, TablaUsuario.KEY_CAMPO_ID, null);
    }

    public void insertarTarea(final String keyTarea, final String instruc, final String urlInit, final String urlFin, final int tiempo) {

        final ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(TablaTarea.CAMPO_KEY_TAREA, keyTarea);
        valoresIniciales.put(TablaTarea.CAMPO_INS, instruc);
        valoresIniciales.put(TablaTarea.CAMPO_URL_INIT, urlInit);
        valoresIniciales.put(TablaTarea.CAMPO_URL_FIN, urlFin);
        valoresIniciales.put(TablaTarea.CAMPO_TIEMPO, tiempo);

        try {
            db.insertOrThrow(TablaTarea.NOMBRE_TABLA, null, valoresIniciales);
        } catch (Exception ex) {
            Log.e("error:", ex.getLocalizedMessage(), ex);
        }
    }

    public void borrarTareas() {
        db.delete(TablaTarea.NOMBRE_TABLA, null, null);
    }

    public Cursor obtenerTareas() {
        return db.query(TablaTarea.NOMBRE_TABLA,
                new String[]{TablaTarea.KEY_CAMPO_ID,TablaTarea.CAMPO_KEY_TAREA, TablaTarea.CAMPO_INS, TablaTarea.CAMPO_URL_INIT, TablaTarea.CAMPO_URL_FIN, TablaTarea.CAMPO_TIEMPO},
                null, null, null, null, TablaTarea.KEY_CAMPO_ID, null);
    }

    public boolean hayDatos() {
        String count = "SELECT count(*) FROM " + TablaDatos.NOMBRE_TABLA;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        return mcursor.getInt(0) > 0;
    }

}