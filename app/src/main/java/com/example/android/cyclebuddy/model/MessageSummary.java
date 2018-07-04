package com.example.android.cyclebuddy.model;

import android.support.annotation.Nullable;

public class MessageSummary {

    private String convoUID;
    private String buddyOneID;
    @Nullable private String buddyTwoID;
    @Nullable private String lastMessage;
    @Nullable private String timestamp;


    public MessageSummary(){}

    public MessageSummary(String convoUID, String buddyOneID, @Nullable String buddyTwoID){
        this.convoUID = convoUID;
        this.buddyOneID = buddyOneID;
        this.buddyTwoID = buddyTwoID;
    }

    public MessageSummary(String convoUID, String buddyOneID, @Nullable String buddyTwoID, @Nullable String lastMessage,
                          @Nullable String timestamp){
        this.convoUID = convoUID;
        this.buddyOneID = buddyOneID;
        this.buddyTwoID = buddyTwoID;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getConvoUID(){return convoUID;}
    public String getbuddyOneID(){return buddyOneID;}
    @Nullable public String getbuddyTwoID(){return buddyTwoID;}
    @Nullable public String getLastMessage(){
        return lastMessage;
    }
    @Nullable public String getTimestamp(){
        return timestamp;
    }

    public void setConvoUID(String convoUID){
        this.convoUID = convoUID;
    }
    public void setBuddyOneID(String buddyOneID){
        this.buddyOneID = buddyOneID;
    }
    public void setBuddyTwoID(@Nullable String buddyTwoID){
        this.buddyTwoID = buddyTwoID;
    }
    public void setLastMessage(@Nullable String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setTimestamp(@Nullable String timestamp){
        this.timestamp = timestamp;
    }
}
