package com.example.android.cyclebuddy.model;

public class OfferedRoute {

    private String from;
    private String via;
    private String to;
    private int duration;
    private String userID;

    public OfferedRoute(){}

    public OfferedRoute(String from, String to, int duration, String userID){
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.userID = userID;
    }

    public OfferedRoute(String from, String via, String to, int duration, String userID){
        this.from = from;
        this.via = via;
        this.to = to;
        this.duration = duration;
        this.userID = userID;
    }

    public String getFrom(){
        return from;
    }

    public String getVia(){
        return via;
    }

    public String getTo(){
        return to;
    }

    public int getDuration(){return duration;}

    public String getUserID(){return userID;}

    public void setFrom(String from){
        this.from = from;
    }

    public void setVia(String via){
        this.via = via;
    }

    public void setTo(String to){
        this.to = to;
    }

    public void setDuration(int duration){this.duration = duration;}

    public void setUserID(String userID){
        this.userID = userID;
    }

}
