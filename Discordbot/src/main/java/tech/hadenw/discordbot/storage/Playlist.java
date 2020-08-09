package tech.hadenw.discordbot.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Playlist {
    private String name;
    private List<String> urls;

    public Playlist(String name) {
        this.urls = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getTracks() {
        return urls;
    }

    public void addTrack(String url) {
        urls.add(url);
    }

    public boolean removeTrack(int position) {
        if(urls.size() > position) {
            urls.remove(position);
            return true;
        }

        return false;
    }
}
