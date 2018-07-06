package com.example.android.cyclebuddy.model;

import android.support.annotation.Nullable;

public class Message {

    @Nullable private String userID;
    @Nullable private String message;
    @Nullable private String timestamp;

    public Message(){}

    public Message(@Nullable String userID, @Nullable String message, @Nullable String timestamp){
        this.userID = userID;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Nullable public String getUserID(){
        return userID;
    }
    @Nullable public String getMessage(){
        return message;
    }
    @Nullable public String getTimestamp(){
        return timestamp;
    }

    public void setUserID(@Nullable String userID){
        this.userID = userID;
    }

    public void setMessage(@Nullable String message){
        this.message = message;
    }

    public void setTimestamp(@Nullable String timestamp){
        this.timestamp = timestamp;
    }

}
