package com.example.android.cyclebuddy.model;

public class OfferedRoute {

    private String from;
    private String via;
    private String to;
    private int duration;

    public OfferedRoute(){}

    public OfferedRoute(String from, String to, int duration){
        this.from = from;
        this.to = to;
        this.duration = duration;
    }

    public OfferedRoute(String from, String via, String to, int duration){
        this.from = from;
        this.via = via;
        this.to = to;
        this.duration = duration;
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
}
