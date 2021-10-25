package com.example.jasaphotographerapp;

public class ModelClient {

    String name;
    String email;
    String phoneNo;
    String pass;



    String clientID;

    public ModelClient() {
    }

    public ModelClient(String name, String email, String phoneNo, String pass, String clientID) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.pass = pass;
        this.clientID=clientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getClientID() { return clientID; }

    public void setClientID(String clientID) { this.clientID = clientID; }
}

