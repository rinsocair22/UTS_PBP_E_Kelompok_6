package com.example.jasaphotographerapp;

public class ModelBooking {



    String bookDate;
    String BookDescription;
    String bookID;
    String bookLocation;
    String bookStatus;
    String bookTime;
    String clientID;
    String pgID;
    String packageID;
    String bookPrice;

    public ModelBooking() {

    }

    public ModelBooking(String bookDate, String bookDescription, String bookID, String bookLocation, String bookStatus, String bookTime,String bookPrice, String clientID, String pgID, String packageID) {
        this.bookDate = bookDate;
        this.BookDescription = bookDescription;
        this.bookID = bookID;
        this.bookLocation = bookLocation;
        this.bookStatus = bookStatus;
        this.bookTime = bookTime;
        this.bookPrice = bookPrice;
        this.clientID = clientID;
        this.pgID = pgID;
        this.packageID = packageID;
    }
    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getBookDescription() {
        return BookDescription;
    }

    public void setBookDescription(String bookDescription) {
        BookDescription = bookDescription;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getBookLocation() {
        return bookLocation;
    }

    public void setBookLocation(String bookLocation) {
        this.bookLocation = bookLocation;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getPgID() {
        return pgID;
    }

    public void setPgID(String pgID) {
        this.pgID = pgID;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }
    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

}
