package com.arr.enruta.models;

public class History {

    public String fecha;
    public String origen;
    public String destino;
    public String estado;
    public String hora;

    public History(String fecha, String origen, String destino, String estado, String hora) {
        this.fecha = fecha;
        this.origen = origen;
        this.destino = destino;
        this.estado = estado;
        this.hora = hora;
    }
}
