package com.example.android.cyclebuddy.model;

public class UserProfile {

    private String userID;
    private String user;
    private String buddyType;
    private String miniBio;
    private String photoUrl;

    public UserProfile(String userID, String user, String buddyType, String miniBio){
        this.userID = userID;
        this.user = user;
        this.buddyType = buddyType;
        this.miniBio = miniBio;
    }

    public UserProfile(String userID, String user, String buddyType, String miniBio, String photoUrl){
        this.userID = userID;
        this.user = user;
        this.buddyType = buddyType;
        this.miniBio = miniBio;
        this.photoUrl = photoUrl;
    }

    public String getUserID(){
        return userID;
    }

    public String getUser(){
        return user;
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

    public void setMiniBio(String miniBio){
        this.miniBio = miniBio;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }
}
