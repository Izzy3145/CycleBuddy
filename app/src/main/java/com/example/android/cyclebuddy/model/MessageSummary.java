package com.example.android.cyclebuddy.model;

import android.support.annotation.Nullable;

public class MessageSummary {

    @Nullable private String photoUrl;
    private String username;
    private String lastMessage;
    //TODO: add timestamp


    public MessageSummary(){}

    public MessageSummary(@Nullable String photoUrl, String username, String lastMessage){
        this.photoUrl = photoUrl;
        this.username = username;
        this.lastMessage = lastMessage;
    }

    @Nullable public String getPhotoUrl(){
        return photoUrl;
    }

    public String getUsername(){
        return username;
    }

    public String getLastMessage(){
        return lastMessage;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }

}
