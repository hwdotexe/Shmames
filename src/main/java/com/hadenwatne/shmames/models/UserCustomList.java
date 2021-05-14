package com.hadenwatne.shmames.models;

import java.util.ArrayList;
import java.util.List;

public class UserCustomList {
    private String name;
    private List<String> values;

    public UserCustomList(String name) {
        this.name = name;
        this.values = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getValues() {
        return this.values;
    }
}
