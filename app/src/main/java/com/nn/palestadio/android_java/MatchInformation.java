package com.nn.palestadio.android_java;

public class MatchInformation {

    private String fecha;
    private String tribuna;
    private String asiento;
    private String hora;
    private String equipo1;
    private String equipo2;
    private String info;

    public MatchInformation(String nAsiento, String nEquipo1, String nEquipo2, String nFecha, String nHora, String nTribuna, String nInfo) {
        fecha = nFecha;
        tribuna = nTribuna;
        asiento = nAsiento;
        hora = nHora;
        equipo1 = nEquipo1;
        equipo2 = nEquipo2;
        info = nInfo;

    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTribuna() {
        return tribuna;
    }

    public void setTribuna(String tribuna) {
        this.tribuna = tribuna;
    }

    public String getAsiento() {
        return asiento;
    }

    public void setAsiento(String asiento) {
        this.asiento = asiento;
    }

    public String getEquipo1() {
        return equipo1;
    }

    public void setEquipo1(String equipo1) {
        this.equipo1 = equipo1;
    }

    public String getEquipo2() {
        return equipo2;
    }

    public void setEquipo2(String equipo2) {
        this.equipo2 = equipo2;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}


