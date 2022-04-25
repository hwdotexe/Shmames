package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.models.Story;

import java.util.ArrayList;
import java.util.List;

public class StorytimeStories {
    private List<Story> stories;

    public StorytimeStories() {
        stories = new ArrayList<>();
    }

    public List<Story> getStories() {
        return this.stories;
    }

    public void loadDefaults() {
        Story def = new Story("The adventures of Shmames", "Shmames", "This bot is super cool!");

        stories.add(def);
    }
}
