package com.hadenwatne.shmames.models.data;

import java.util.ArrayList;
import java.util.List;

public class GachaBanner {
    private String URL;
    private List<String> characters;

    public GachaBanner() {
        this.characters = new ArrayList<>();
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void addCharacter(String id) {
        this.characters.add(id);
    }

    public List<String> getCharacters() {
        return characters;
    }
}
