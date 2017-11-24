package com.rso.streaming.ententies;

import java.util.List;

public class Album {

    private Long ID;

    private String title;
    private String artist;

    private List<Clip> clips;

    public Album(String title, String artist) {
        super();
        this.artist = artist;
        this.title = title;
    }

    public Album() {
        super();
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Clip> getClips() {
        return clips;
    }

    public void setClips(List<Clip> clips) {
        this.clips = clips;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Album{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", clips=" + clips +
                '}';
    }
}
