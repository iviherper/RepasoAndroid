package com.example.repasoandroid;

import java.util.ArrayList;

public class Ruta {
    private String nombre;
    private ArrayList<Punto> puntos;

    public Ruta(String nombre, ArrayList<Punto> puntos) {
        this.nombre = nombre;
        this.puntos = puntos;
    }

    public Ruta() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Punto> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
