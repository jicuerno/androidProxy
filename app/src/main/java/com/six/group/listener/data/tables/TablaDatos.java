package com.six.group.listener.data.tables;

/**
 * Created by user on 22/03/18.
 * URL, ID del elemento HTML que general el evento, Evento generado, Tiempo en milisegundos.
 */

public class TablaDatos {

    public static final String NOMBRE_TABLA = "request";
    public static final String SQL_BORRA_TABLA = "drop table " + NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID = "id";
    public static final int POS_KEY_CAMPO_ID = 0;
    public static final String CAMPO_KEY_USER = "keyUser";
    public static final int POS_CAMPO_KEY_USER = 1;
    public static final String CAMPO_KEY_TAREA = "keyTarea";
    public static final int POS_CAMPO_KEY_TAREA = 2;
    public static final String CAMPO_ELEMENT = "element";
    public static final int POS_CAMPO_ELEMENT = 3;
    public static final String CAMPO_URL = "url";
    public static final int POS_CAMPO_URL = 4;
    public static final String CAMPO_EVENT = " event";
    public static final int POS_CAMPO_EVENT = 5;
    public static final String CAMPO_TIME = "time";
    public static final int POS_CAMPO_TIME = 6;
    public static final String CAMPO_PCIP = "pcIp";
    public static final int POS_CAMPO_PCIP = 7;

    public static final int NUM_CAMPOS = 8;

    public static final String SQL_CREA_TABLA = "create table " + NOMBRE_TABLA + " \n" +
            "( \n" +
            KEY_CAMPO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
            CAMPO_ELEMENT + " TEXT, \n" +
            CAMPO_KEY_USER + " TEXT NOT NULL, \n" +
            CAMPO_KEY_TAREA + " TEXT NOT NULL, \n" +
            CAMPO_URL + " TEXT NOT NULL, \n" +
            CAMPO_EVENT + " TEXT, \n" +
            CAMPO_TIME + " TEXT NOT NULL, \n" +
            CAMPO_PCIP + " TEXT NOT NULL);";
}
