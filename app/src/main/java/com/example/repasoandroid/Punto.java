package com.example.repasoandroid;

public class Punto {
    private Double lat,lng;

    public Punto() {
    }

    public Punto(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
