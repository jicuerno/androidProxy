package com.six.group.listener.data;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.six.group.listener.data.tables.TablaDatos;
import com.six.group.listener.data.tables.TablaTarea;
import com.six.group.listener.data.tables.TablaUsuario;

/**
 * Created by user on 22/03/18.
 */

class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "";
    private static final String DB_NAME = "listener";// Database name

    public DataBaseHelper(Context context) {
        super(context, DB_PATH + DB_NAME, null, 2);// 1? its Database Version

        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TablaUsuario.SQL_CREA_TABLA);
        db.execSQL(TablaTarea.SQL_CREA_TABLA);
        db.execSQL(TablaDatos.SQL_CREA_TABLA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String TAG = "DataBaseHelper";
        Log.w(TAG, "Actualizando base de datos de version " + oldVersion + " a "
                + newVersion + ", lo que destruira todos los viejos datos");
        db.execSQL(TablaUsuario.SQL_CREA_TABLA);
        db.execSQL(TablaTarea.SQL_CREA_TABLA);
        db.execSQL(TablaDatos.SQL_CREA_TABLA);
        onCreate(db);
    }
}