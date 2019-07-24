package com.example.dell.mypadclock.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 音乐数据信息
 */
public class MusicDatas implements Parcelable {

    private String path;// 路径
    private int musicId;// 歌曲的id
    private String name; // 歌曲名
    private String album; // 专辑
    private String artist; // 作者
    private long size;// 大小
    private int duration;// 时长

    public MusicDatas() {

    }

    public MusicDatas(String path, int musicId, String name, String album, String artist, long size, int duration) {
        this.path = path;
        this.musicId = musicId;
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.size = size;
        this.duration = duration;
    }

    protected MusicDatas(Parcel in) {
        path = in.readString();
        musicId = in.readInt();
        name = in.readString();
        album = in.readString();
        artist = in.readString();
        size = in.readLong();
        duration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(musicId);
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeLong(size);
        dest.writeInt(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicDatas> CREATOR = new Creator<MusicDatas>() {
        @Override
        public MusicDatas createFromParcel(Parcel in) {
            return new MusicDatas(in);
        }

        @Override
        public MusicDatas[] newArray(int size) {
            return new MusicDatas[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MusicDatas{" +
                "path='" + path + '\'' +
                ", musicId=" + musicId +
                ", name='" + name + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }
}
