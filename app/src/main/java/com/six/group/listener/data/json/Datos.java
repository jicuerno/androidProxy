package com.six.group.listener.data.json;

import java.io.Serializable;
import java.util.ArrayList;

public class Datos implements Serializable {
    private ArrayList<Linea> lineas = null;
    private ArrayList<Usuario> usuarios = null;
    private ArrayList<Tarea> tareas = null;

    public Datos() {
        super();
        lineas = new ArrayList<>();
        usuarios = new ArrayList<>();
        tareas = new ArrayList<>();
    }

    public Datos(ArrayList<Linea> lineas) {
        super();
        this.lineas = lineas;
    }

    public ArrayList<Linea> getLineas() {
        return lineas;
    }

    public void setLineas(ArrayList<Linea> lineas) {
        this.lineas = lineas;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public ArrayList<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(ArrayList<Tarea> tareas) {
        this.tareas = tareas;
    }

}
