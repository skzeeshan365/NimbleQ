package com.reiserx.nimbleq.Models.Announcements;

public class linkModel {
    String link, filename;
    String id;

    public linkModel(String link, String filename) {
        this.link = link;
        this.filename = filename;
    }

    public linkModel() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
