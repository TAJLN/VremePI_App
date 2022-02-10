package com.tajln.vremenarapp.data;

import java.sql.Timestamp;

public class Vreme {
    private Integer id;

    private Timestamp time;

    private float vlaga;
    private float pritisk;
    private float temperatura;
    private float svetloba;
    private float oxid;
    private float redu;
    private float nh3;
    private int postaja;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getVlaga() {
        return vlaga;
    }

    public void setVlaga(float vlaga) {
        this.vlaga = vlaga;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public float getOxid() {
        return oxid;
    }

    public void setOxid(float oxid) {
        this.oxid = oxid;
    }

    public float getRedu() {
        return redu;
    }

    public void setRedu(float redu) {
        this.redu = redu;
    }

    public float getNh3() {
        return nh3;
    }

    public void setNh3(float nh3) {
        this.nh3 = nh3;
    }

    public float getSvetloba() {
        return svetloba;
    }

    public float getPritisk() {
        return pritisk;
    }

    public void setPritisk(float pritisk) {
        this.pritisk = pritisk;
    }

    public void setSvetloba(float svetloba) {
        this.svetloba = svetloba;
    }

    public void setPostaja(int postaja){
        this.postaja = postaja;
    }

    public int getPostaja(){
        return postaja;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Timestamp getTime(){
        return time;
    }
}