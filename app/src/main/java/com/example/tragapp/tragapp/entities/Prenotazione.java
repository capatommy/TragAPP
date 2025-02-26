package com.example.tragapp.tragapp.entities;

import androidx.annotation.Nullable;

public class Prenotazione {
    private int oraAndata;
    private int oraRitorno;
    private int minutiRitorno;
    private int minutiAndata;
    private String data;
    private long tsAndata;
    private long tsRitorno;
    private int numAd;
    private String email;

    public Prenotazione(){};

    public Prenotazione(int oraAndata, int minutiAndata, int oraRitorno, int minutiRitorno, String data, long tsAndata, long tsRitorno, int numAd){
        this.oraAndata= oraAndata;
        this.minutiAndata= minutiAndata;
        this.minutiRitorno = minutiRitorno;
        this.oraRitorno = oraRitorno;
        this.data = data;
        this.tsAndata = tsAndata;
        this.tsRitorno = tsRitorno;
        this.numAd = numAd;
    }


    public Prenotazione(int oraAndata, int minutiAndata, int oraRitorno, int minutiRitorno, String data, long tsAndata, long tsRitorno, int numAd, @Nullable String email){
        this.oraAndata= oraAndata;
        this.minutiAndata= minutiAndata;
        this.minutiRitorno = minutiRitorno;
        this.oraRitorno = oraRitorno;
        this.data = data;
        this.tsAndata = tsAndata;
        this.tsRitorno = tsRitorno;
        this.numAd = numAd;
        this.email = email;
    }



    public int getOraAndata(){return  oraAndata;}
    public int getOraRitorno(){return  oraRitorno;}
    public int getMinutiAndata(){ return  minutiAndata; }
    public int getMinutiRitorno(){ return  minutiRitorno; }
    public String getData() { return data; }
    public long getTsAndata() { return tsAndata; }
    public long getTsRitorno() { return tsRitorno; }
    public int getNumAd() { return numAd; }
    public String getEmail() { return email; }
}
