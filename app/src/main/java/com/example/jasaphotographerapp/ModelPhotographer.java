package com.example.jasaphotographerapp;

public class ModelPhotographer {

    String name;
    String email;
    String phoneNo;
    String pass;
    String type;
    String pgID;
    String pgIcon;

    public ModelPhotographer(){
        this.name = "";
        this.email = "";
        this.phoneNo = "";
        this.pass = "";
        this.pgID="";
        this.type = "";
        this.pgIcon="";


    }


    public ModelPhotographer(String name, String email, String phoneNo, String pass, String type, String pgID,String pgIcon ) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.pass = pass;
        this.type = type;
        this.pgID=pgID;
        this.pgIcon=pgIcon;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPgID() { return pgID; }

    public void setPgID(String pgID) { this.pgID = pgID; }


    public String getPgIcon() {
        return pgIcon;
    }

    public void setPgIcon(String pgIcon) {
        this.pgIcon = pgIcon;
    }

}
