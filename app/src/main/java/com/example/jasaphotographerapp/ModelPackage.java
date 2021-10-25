package com.example.jasaphotographerapp;

public class ModelPackage {

    private String packageID,packageName,packagePrice,packageType,packageDescription,packageIcon,PgID;

    public ModelPackage() {
    }

    public ModelPackage(String packageID, String packageName, String packagePrice, String packageType, String packageDescription, String packageIcon, String pgID) {
        this.packageID = packageID;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.packageType = packageType;
        this.packageDescription = packageDescription;
        this.packageIcon = packageIcon;
        this.PgID = pgID;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }

    public String getPackageIcon() {
        return packageIcon;
    }

    public void setPackageIcon(String packageIcon) {
        this.packageIcon = packageIcon;
    }

    public String getPgID() {
        return PgID;
    }

    public void setPgID(String pgID) {
        PgID = pgID;
    }
}
