package com.six.group.listener.activities;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.six.group.R.id;
import com.six.group.R.layout;
import com.six.group.listener.data.AdaptadorDB;
import com.six.group.listener.data.conf.ArchivoXml;
import com.six.group.listener.utils.ReadXMLFile;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.InputStream;

@EActivity(layout.main)
public class MainActivity extends Activity {

    @ViewById(id.puerto)
    EditText puerto;

    @ViewById(id.ip)
    EditText ip;

    @ViewById(id.radioGrupo)
    RadioGroup radioGrupo;

    @ViewById(id.masc)
    RadioButton masc;

    @ViewById(id.edad)
    EditText edad;

    public Integer port;
    public Integer age;
    public String sex;

    ArchivoXml datosXml;

    @AfterViews
    void init() {

        try {
            InputStream is = getAssets().open("properties.xml");
            ReadXMLFile file = new ReadXMLFile(is);
            datosXml = file.getDatosXml();
            masc.setChecked(true);
        } catch (Exception e) {
            Log.e("Excepcion: ", e.getMessage());
        }
    }

    @Click(id.aceptarMain)
    void initProxy() {

        if (!puerto.getText().toString().equals(""))
            port = Integer.parseInt(puerto.getText().toString());
        else
            port = 9090;

        if (!edad.getText().toString().equals(""))
            age = Integer.parseInt(edad.getText().toString());
        else
            age = 0;

        sex = (((int) 2) == radioGrupo.getCheckedRadioButtonId()) ? "Femen                .extra(\"edad\", age)\nino" : "Masculino";

        realizarInsercion(datosXml.getIdUsuario(), age, sex);

        SnifferActivity_.intent(this)
                .extra("puerto", port)
                .extra("sexo", sex)
                .extra("datosXml", datosXml)
                .extra("direccion", ip.getText().toString()).start();
    }


    @Click(id.enviarMain)
    void initSender() {
       SenderActivity_.intent(this).start();
    }

    @Click(id.salirMain)
    void exitProxy() {
        this.finish();
    }

    private void realizarInsercion(final String keyUser, final int edad, final String sexo) {
        AdaptadorDB db = new AdaptadorDB(this);
        db.abrir();
        db.insertarUsuario(keyUser, edad, sexo);
        db.cerrar();
    }
}

