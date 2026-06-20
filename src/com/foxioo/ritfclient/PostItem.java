package com.foxioo.ritfclient;

import java.io.Serializable;

public class PostItem implements Serializable {
    public int id;
    public String author;
    public String description;
    public String url;
    public String thumb;
    public int width;
    public int height;
    public String type;
    public String[] tags;
    public int duration;
    public Boolean deleted;

    public PostItem(int id, String author, String description, String type, String url, String thumb, String[] tags, int width, int height, int duration, Boolean deleted)
    {
        this.id = id;
        this.author = author;
        this.description = description;
        this.type = type;
        this.url = url;
        this.thumb = thumb;
        this.tags = tags;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.deleted = deleted;
    }

    public String gettags() {
        StringBuilder sb = new StringBuilder();
        for (String t : tags)
        {
            if (sb.length() > 0) sb.append(", ");
            sb.append(t);
        }
        return sb.toString();
    }
}
