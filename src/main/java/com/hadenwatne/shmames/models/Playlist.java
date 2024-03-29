package com.hadenwatne.shmames.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Playlist {
    private String name;
    private List<String> urls;
    private HashMap<Integer, String> memos;

    public Playlist(String name) {
        this.urls = new ArrayList<>();
        this.name = name;
        memos = new HashMap<Integer, String>();
    }

    public String getName() {
        return name;
    }

    public List<String> getTracks() {
        return urls;
    }

    public void addTrack(String url, String memo) {
        urls.add(url);
        
        if(memo != null) {
            memos.put(urls.indexOf(url), memo);
        }else{
            memos.put(urls.indexOf(url), "");
        }
    }

    public boolean removeTrack(int position) {
        if(position >= 0 && urls.size() > position) {
            urls.remove(position);
            memos.remove(position);

            return true;
        }

        return false;
    }

    public String getMemo(String url) {
        if(urls.contains(url)) {
            return memos.get(urls.indexOf(url));
        }

        return null;
    }
}
