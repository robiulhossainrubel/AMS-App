package com.rhr.ams.Model;

public class ClassItems {
    int id;
    String session, cc, ct;

    public ClassItems(int id, String session, String cc, String ct) {
        this.id = id;
        this.session = session;
        this.cc = cc;
        this.ct = ct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }
}

