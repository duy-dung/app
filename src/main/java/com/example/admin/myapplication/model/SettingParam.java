package com.example.admin.myapplication.model;

public class SettingParam {
    private int tdq;
    private int nat;
    private int nn;
    private int tgc;
    private int cl;

    public int getTdq() {
        return tdq;
    }

    public SettingParam() {
    }

    public SettingParam(int tdq, int nat, int nn, int tgc, int cl) {
        this.tdq = tdq;
        this.nat = nat;

        this.nn = nn;
        this.tgc = tgc;
        this.cl = cl;
    }

    public void setTdq(int tdq) {

        this.tdq = tdq;
    }

    public int getNat() {
        return nat;
    }

    public void setNat(int nam) {
        this.nat = nam;
    }

    public int getNn() {
        return nn;
    }

    public void setNn(int nn) {
        this.nn = nn;
    }

    public int getTgc() {
        return tgc;
    }

    public void setTgc(int tgc) {
        this.tgc = tgc;
    }

    public int getCl() {
        return cl;
    }

    public void setCl(int cl) {
        this.cl = cl;
    }
    //    private

}
