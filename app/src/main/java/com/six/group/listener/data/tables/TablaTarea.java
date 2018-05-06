package com.six.group.listener.data.tables;

/**
 * Created by user on 22/03/18.
 * URL, ID del elemento HTML que general el evento, Evento generado, Tiempo en milisegundos.
 */

public class TablaTarea {

    public static final String NOMBRE_TABLA = "tarea";
    public static final String SQL_BORRA_TABLA = "drop table " + NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID = "id";
    public static final int POS_KEY_CAMPO_ID = 0;
    public static final String CAMPO_KEY_TAREA = "keyTarea";
    public static final int POS_CAMPO_KEY_TAREA = 1;
    public static final String CAMPO_INS = "instrucciones";
    public static final int POS_CAMPO_INS = 2;
    public static final String CAMPO_URL_INIT = "urlInit";
    public static final int POS_CAMPO_URL_INIT = 3;
    public static final String CAMPO_URL_FIN = "urlFin";
    public static final int POS_CAMPO_URL_FIN = 4;
    public static final String CAMPO_TIEMPO = "tiempo";
    public static final int POS_CAMPO_TIEMPO = 5;
    public static final int NUM_CAMPOS = 6;

    public static final String SQL_CREA_TABLA = "create table " + NOMBRE_TABLA + " \n" +
            "( \n" +
            KEY_CAMPO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
            CAMPO_INS + " TEXT, \n" +
            CAMPO_KEY_TAREA + " TEXT NOT NULL, \n" +
            CAMPO_URL_INIT+ " TEXT NOT NULL, \n" +
            CAMPO_URL_FIN+ " TEXT NOT NULL, \n" +
            CAMPO_TIEMPO+ " INTEGER NOT NULL);";
}
