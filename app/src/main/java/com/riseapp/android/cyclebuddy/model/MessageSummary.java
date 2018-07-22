package com.riseapp.android.cyclebuddy.model;


public class MessageSummary {

    private String convoUID;
    private String buddyOneID;
    private String buddyTwoID;
    private String lastMessage;
    private String timestamp;


    public MessageSummary(){}

    public MessageSummary(String convoUID, String buddyOneID, String buddyTwoID){
        this.convoUID = convoUID;
        this.buddyOneID = buddyOneID;
        this.buddyTwoID = buddyTwoID;
    }

    public MessageSummary(String convoUID, String buddyOneID, String buddyTwoID, String lastMessage,
                          String timestamp){
        this.convoUID = convoUID;
        this.buddyOneID = buddyOneID;
        this.buddyTwoID = buddyTwoID;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getConvoUID(){return convoUID;}
    public String getbuddyOneID(){return buddyOneID;}
    public String getbuddyTwoID(){return buddyTwoID;}
    public String getLastMessage(){
        return lastMessage;
    }
    public String getTimestamp(){
        return timestamp;
    }

    public void setConvoUID(String convoUID){
        this.convoUID = convoUID;
    }
    public void setBuddyOneID(String buddyOneID){
        this.buddyOneID = buddyOneID;
    }
    public void setBuddyTwoID(String buddyTwoID){
        this.buddyTwoID = buddyTwoID;
    }
    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
}
