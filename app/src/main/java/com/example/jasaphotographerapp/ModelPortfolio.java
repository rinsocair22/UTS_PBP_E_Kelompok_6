package com.example.jasaphotographerapp;

public class ModelPortfolio {



    String pfID,pfInfo,pfLink1,pfLink2,pfLink3,pfLink4,pfLink5,pfLink6,pfName,pgID;

    public ModelPortfolio() {
    }

    public ModelPortfolio(String pfID, String pfInfo, String pfLink1, String pfLink2, String pfLink3, String pfLink4, String pfLink5, String pfLink6, String pfName, String pgID) {
        this.pfID = pfID;
        this.pfInfo = pfInfo;
        this.pfLink1 = pfLink1;
        this.pfLink2 = pfLink2;
        this.pfLink3 = pfLink3;
        this.pfLink4 = pfLink4;
        this.pfLink5 = pfLink5;
        this.pfLink6 = pfLink6;
        this.pfName = pfName;
        this.pgID = pgID;
    }

    public String getPfID() {
        return pfID;
    }

    public void setPfID(String pfID) {
        this.pfID = pfID;
    }

    public String getPfInfo() {
        return pfInfo;
    }

    public void setPfInfo(String pfInfo) {
        this.pfInfo = pfInfo;
    }

    public String getPfLink1() {
        return pfLink1;
    }

    public void setPfLink1(String pfLink1) {
        this.pfLink1 = pfLink1;
    }

    public String getPfLink2() {
        return pfLink2;
    }

    public void setPfLink2(String pfLink2) {
        this.pfLink2 = pfLink2;
    }

    public String getPfLink3() {
        return pfLink3;
    }

    public void setPfLink3(String pfLink3) {
        this.pfLink3 = pfLink3;
    }

    public String getPfLink4() {
        return pfLink4;
    }

    public void setPfLink4(String pfLink4) {
        this.pfLink4 = pfLink4;
    }

    public String getPfLink5() {
        return pfLink5;
    }

    public void setPfLink5(String pfLink5) {
        this.pfLink5 = pfLink5;
    }

    public String getPfLink6() {
        return pfLink6;
    }

    public void setPfLink6(String pfLink6) {
        this.pfLink6 = pfLink6;
    }

    public String getPfName() {
        return pfName;
    }

    public void setPfName(String pfName) {
        this.pfName = pfName;
    }

    public String getPgID() {
        return pgID;
    }

    public void setPgID(String pgID) {
        this.pgID = pgID;
    }
}
