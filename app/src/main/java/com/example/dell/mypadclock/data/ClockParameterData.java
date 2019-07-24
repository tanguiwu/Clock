package com.example.dell.mypadclock.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class ClockParameterData implements Parcelable{

    private int id;
    private String clockTime;
    private String clockPeriod;
    private String isAm;
    private String clockTag;
    private String clockMusic;
    private int clockMusicId;
    private int clockRemain;
    private int clockIsOpen;

    public ClockParameterData() {

    }

    public ClockParameterData(int id, String clockTime, String clockPeriod, String isAm, String clockTag, String clockMusic, int clockMusicId, int clockRemain, int clockIsOpen) {
        this.id = id;
        this.clockTime = clockTime;
        this.clockPeriod = clockPeriod;
        this.isAm = isAm;
        this.clockTag = clockTag;
        this.clockMusic = clockMusic;
        this.clockMusicId = clockMusicId;
        this.clockRemain = clockRemain;
        this.clockIsOpen = clockIsOpen;
    }

    protected ClockParameterData(Parcel in) {
        id = in.readInt();
        clockTime = in.readString();
        clockPeriod = in.readString();
        isAm = in.readString();
        clockTag = in.readString();
        clockMusic = in.readString();
        clockMusicId = in.readInt();
        clockRemain = in.readInt();
        clockIsOpen = in.readInt();
    }

    public static final Creator<ClockParameterData> CREATOR = new Creator<ClockParameterData>() {
        @Override
        public ClockParameterData createFromParcel(Parcel in) {
            return new ClockParameterData(in);
        }

        @Override
        public ClockParameterData[] newArray(int size) {
            return new ClockParameterData[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClockTime() {
        return clockTime;
    }

    public void setClockTime(String clockTime) {
        this.clockTime = clockTime;
    }

    public String getClockPeriod() {
        return clockPeriod;
    }

    public void setClockPeriod(String clockPeriod) {
        this.clockPeriod = clockPeriod;
    }

    public String getIsAm() {
        return isAm;
    }

    public void setIsAm(String isAm) {
        this.isAm = isAm;
    }

    public String getClockTag() {
        return clockTag;
    }

    public void setClockTag(String clockTag) {
        this.clockTag = clockTag;
    }

    public String getClockMusic() {
        return clockMusic;
    }

    public void setClockMusic(String clockMusic) {
        this.clockMusic = clockMusic;
    }

    public int getClockMusicId() {
        return clockMusicId;
    }

    public void setClockMusicId(int clockMusicId) {
        this.clockMusicId = clockMusicId;
    }

    public int getClockRemain() {
        return clockRemain;
    }

    public void setClockRemain(int clockRemain) {
        this.clockRemain = clockRemain;
    }

    public int getClockIsOpen() {
        return clockIsOpen;
    }

    public void setClockIsOpen(int clockIsOpen) {
        this.clockIsOpen = clockIsOpen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(clockTime);
        dest.writeString(clockPeriod);
        dest.writeString(isAm);
        dest.writeString(clockTag);
        dest.writeString(clockMusic);
        dest.writeInt(clockMusicId);
        dest.writeInt(clockRemain);
        dest.writeInt(clockIsOpen);
    }


    @Override
    public String toString() {
        return "ClockParameterData{" +
                "id=" + id +
                ", clockTime='" + clockTime + '\'' +
                ", clockPeriod='" + clockPeriod + '\'' +
                ", isAm='" + isAm + '\'' +
                ", clockTag='" + clockTag + '\'' +
                ", clockMusic='" + clockMusic + '\'' +
                ", clockMusicId=" + clockMusicId +
                ", clockRemain=" + clockRemain +
                ", clockIsOpen=" + clockIsOpen +
                '}';
    }
}
