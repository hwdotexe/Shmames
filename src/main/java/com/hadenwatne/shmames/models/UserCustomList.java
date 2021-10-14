package com.hadenwatne.shmames.models;

import com.hadenwatne.shmames.enums.UserListType;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserCustomList {
    private String ownerID;
    private UserListType type;
    private String name;
    private List<String> values;

    public UserCustomList(String ownerID, UserListType type, String name) {
        this.ownerID = ownerID;
        this.type = type;
        this.name = name;
        this.values = new ArrayList<>();
    }

    public String getOwnerID() {
        return this.ownerID;
    }

    public UserListType getType() {
        return this.type;
    }

    public void setType(UserListType newType) {
        this.type = newType;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getValues() {
        return this.values;
    }
}
