package com.example.android.cyclebuddy.model;

public class Buddies {

    private String buddyOne;
    private String buddyTwo;

    public Buddies(){};

    public Buddies(String buddyOne, String buddyTwo){
        this.buddyOne = buddyOne;
        this.buddyTwo = buddyTwo;
    }

    public String getBuddyOne(){
        return buddyOne;
    }

    public String getBuddyTwo(){
        return buddyTwo;
    }

    public void setBuddyOne(String buddyOne){
        this.buddyOne = buddyOne;
    }

    public void setBuddyTwo(String buddyTwo){
        this.buddyTwo = buddyTwo;
    }
}
