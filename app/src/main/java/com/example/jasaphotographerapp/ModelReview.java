package com.example.jasaphotographerapp;

public class ModelReview {

    String pgID,clientID,bookID,rateValue,rateComment,timestamp,rateID;

    public ModelReview() {
    }
    public ModelReview(String pgID, String clientID, String bookID, String rateValue, String rateComment, String timestamp, String rateID) {
        this.pgID = pgID;
        this.clientID = clientID;
        this.bookID = bookID;
        this.rateValue = rateValue;
        this.rateComment = rateComment;
        this.timestamp = timestamp;
        this.rateID = rateID;
    }

    public String getPgID() {
        return pgID;
    }

    public void setPgID(String pgID) {
        this.pgID = pgID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getRateComment() {
        return rateComment;
    }

    public void setRateComment(String rateComment) {
        this.rateComment = rateComment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRateID() {
        return rateID;
    }

    public void setRateID(String rateID) {
        this.rateID = rateID;
    }
}
