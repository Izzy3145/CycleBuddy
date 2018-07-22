package com.riseapp.android.cyclebuddy.model;


public class UserProfile {

    private String userID;
    private String user;
    private String buddyType;
    private String yearsCycling;
    private String cyclingFrequency;
    private String miniBio;
    private String photoUrl;

    public UserProfile(){}

    public UserProfile(String userID, String user, String buddyType, String yearsCycling,
                       String cyclingFrequency, String miniBio){
        this.userID = userID;
        this.user = user;
        this.buddyType = buddyType;
        this.yearsCycling = yearsCycling;
        this.cyclingFrequency = cyclingFrequency;
        this.miniBio = miniBio;
    }

    public UserProfile(String userID, String user, String buddyType, String yearsCycling,
                       String cyclingFrequency, String miniBio, String photoUrl){
        this.userID = userID;
        this.user = user;
        this.buddyType = buddyType;
        this.yearsCycling = yearsCycling;
        this.cyclingFrequency = cyclingFrequency;
        this.miniBio = miniBio;
        this.photoUrl = photoUrl;
    }

    public String getUserID(){
        return userID;
    }

    public String getUser(){
        return user;
    }

    public String getYearsCycling(){
        return yearsCycling;
    }

    public String getCyclingFrequency(){
        return cyclingFrequency;
    }

    public String getBuddyType(){
        return buddyType;
    }

    public String getMiniBio(){
        return miniBio;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void setBuddyType(String buddyType){
        this.buddyType = buddyType;
    }

    public void setYearsCycling(String yearsCycling){
        this.yearsCycling = yearsCycling;
    }
    public void setCyclingFrequency(String cyclingFrequency){
        this.cyclingFrequency = cyclingFrequency;
    }

    public void setMiniBio(String miniBio){
        this.miniBio = miniBio;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
