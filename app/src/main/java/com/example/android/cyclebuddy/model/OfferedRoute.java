package com.example.android.cyclebuddy.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.cyclebuddy.R;

public class OfferedRoute implements Parcelable{

    private String from;
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

    public String getFrom(){
        return from;
    }

    public String getTo(){
        return to;
    }

    public String getToString(){
        return " to " + to;
    }

    public int getDuration(){return duration;}

    //public String getDurationString(){return .getString(R.string.journey_time) + String.valueOf(duration) + .getString(R.string.mins);}


    public String getUserID(){return userID;}

    public void setFrom(String from){
        this.from = from;
    }

    public void setTo(String to){
        this.to = to;
    }

    public void setDuration(int duration){this.duration = duration;}

    public void setUserID(String userID){
        this.userID = userID;
    }

    //parcellable methods
    public static final Creator CREATOR = new Creator() {
        @Override
        public OfferedRoute createFromParcel(Parcel parcel) {
            return new OfferedRoute(parcel);
        }

        @Override
        public OfferedRoute[] newArray(int i) {
            return new OfferedRoute[i];
        }
    };
    //override Parcelable methods
    private OfferedRoute(Parcel in) {
        from = in.readString();
        to = in.readString();
        duration = in.readInt();
        userID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(from);
        parcel.writeString(to);
        parcel.writeInt(duration);
        parcel.writeString(userID);
    }
}
