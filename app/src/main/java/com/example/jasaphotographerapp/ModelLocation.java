package com.example.jasaphotographerapp;

public class ModelLocation {

    String locationID;
    String locationName;
    String pgID;

    public ModelLocation() {
    }

    public ModelLocation(String locationID, String locationName, String pgID) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.pgID = pgID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getPgID() {
        return pgID;
    }

    public void setPgID(String pgID) {
        this.pgID = pgID;
    }
}
