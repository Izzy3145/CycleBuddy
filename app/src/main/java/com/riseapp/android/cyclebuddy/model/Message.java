package com.riseapp.android.cyclebuddy.model;


public class Message {

    private String userID;
    private String message;
    private String timestamp;

    public Message(){}

    public Message(String userID, String message, String timestamp){
        this.userID = userID;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserID(){
        return userID;
    }
    public String getMessage(){
        return message;
    }
    public String getTimestamp(){
        return timestamp;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

}
