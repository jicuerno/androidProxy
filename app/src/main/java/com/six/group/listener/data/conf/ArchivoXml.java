package com.six.group.listener.data.conf;

import android.support.annotation.NonNull;

import com.six.group.listener.data.json.Tarea;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ArchivoXml implements Serializable {
    private static volatile SecureRandom numberGenerator = null;
    private static final long MSB = 0x8000000000000000L;

    private String idUsuario;
    private String uuid;

    private List<Tarea> datos;

    public ArchivoXml() {
        this.datos = new ArrayList<>();
        this.uuid = generaUuid();
    }

    public ArchivoXml(String idUsuario) {
        super();
        this.uuid = generaUuid();
        this.idUsuario = idUsuario + "|" + uuid;
        this.datos = new ArrayList<>();
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        if (uuid == null)
            uuid = generaUuid();
        this.idUsuario = idUsuario + "|" + uuid;
    }

    public List<Tarea> getDatos() {
        return datos;
    }

    public void setDatos(List<Tarea> datos) {
        this.datos = datos;
    }

    public void addDato(Tarea tarea) {
        if (uuid == null)
            uuid = generaUuid();
        tarea.setKeyTarea(tarea.getKeyTarea() + "|" + uuid);
        datos.add(tarea);
    }

    public String getUuid() {
        return uuid;
    }

    @NonNull
    private String generaUuid() {
        SecureRandom ng = numberGenerator;
        if (ng == null)
            numberGenerator = ng = new SecureRandom();
        return Long.toHexString(MSB | ng.nextLong()) + Long.toHexString(MSB | ng.nextLong());
    }
}