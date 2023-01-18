package com.reiserx.nimbleq.Models;

import com.google.firebase.firestore.PropertyName;
import com.reiserx.nimbleq.Models.Announcements.linkModel;

import java.util.List;

public class remoteLinks {
    @PropertyName("links")
    private List<linkModel> dungeonGroup;

    public remoteLinks() {
        // Must have a public no-argument constructor
    }

    // Initialize all fields of a dungeon
    public remoteLinks(List<linkModel> dungeonGroup) {
        this.dungeonGroup = dungeonGroup;
    }

    @PropertyName("links")
    public List<linkModel> getDungeonGroup() {
        return dungeonGroup;
    }

    @PropertyName("links")
    public void setDungeonGroup(List<linkModel> dungeonGroup) {
        this.dungeonGroup = dungeonGroup;
    }
}
