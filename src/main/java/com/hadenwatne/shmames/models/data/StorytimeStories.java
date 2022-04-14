package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;

import java.util.ArrayList;
import java.util.List;

public class StorytimeStories {
    private List<String> stories;

    public StorytimeStories() {
        stories = new ArrayList<>();
    }

    public List<String> getStories() {
        return this.stories;
    }

    public void loadDefaults() {
        stories.add("The bot admin should add some stories here!");
        stories.add("When " + App.Shmames.getBotName() + " tells a story, everyone listens.");
    }
}
