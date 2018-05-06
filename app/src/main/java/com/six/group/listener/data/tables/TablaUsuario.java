package com.six.group.listener.data.tables;

/**
 * Created by user on 22/03/18.
 * URL, ID del elemento HTML que general el evento, Evento generado, Tiempo en milisegundos.
 */

public class TablaUsuario {

    public static final String NOMBRE_TABLA = "user";
    public static final String SQL_BORRA_TABLA = "drop table " + NOMBRE_TABLA + ";";
    public static final String KEY_CAMPO_ID = "id";
    public static final int POS_KEY_CAMPO_ID = 0;
    public static final String CAMPO_KEY_USER = "keyUser";
    public static final int POS_CAMPO_KEY_USER = 1;
    public static final String CAMPO_EDAD = "edad";
    public static final int POS_CAMPO_EDAD = 2;
    public static final String CAMPO_SEXO = "sexo";
    public static final int POS_CAMPO_SEXO = 3;

    public static final int NUM_CAMPOS = 4;

    public static final String SQL_CREA_TABLA = "create table " + NOMBRE_TABLA + " \n" +
            "( \n" +
            KEY_CAMPO_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
            CAMPO_KEY_USER + " TEXT  NOT NULL, \n" +
            CAMPO_EDAD + " INTEGER NOT NULL, \n" +
            CAMPO_SEXO + " TEXT NOT NULL);";
}
