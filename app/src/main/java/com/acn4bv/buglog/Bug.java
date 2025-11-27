package com.acn4bv.buglog;

public class Bug {

    private String id;
    private String nombreJuego;
    private String plataforma;
    private String tipo;
    private String gravedad;
    private String descripcion;

    public Bug() {}

    public Bug(String nombreJuego, String plataforma, String tipo, String gravedad, String descripcion) {
        this.nombreJuego = nombreJuego;
        this.plataforma = plataforma;
        this.tipo = tipo;
        this.gravedad = gravedad;
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreJuego() { return nombreJuego; }
    public String getPlataforma()  { return plataforma; }
    public String getTipo()        { return tipo; }
    public String getGravedad()    { return gravedad; }
    public String getDescripcion() { return descripcion; }

    public void setNombreJuego(String nombreJuego) { this.nombreJuego = nombreJuego; }
    public void setPlataforma(String plataforma)   { this.plataforma = plataforma; }
    public void setTipo(String tipo)               { this.tipo = tipo; }
    public void setGravedad(String gravedad)       { this.gravedad = gravedad; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
