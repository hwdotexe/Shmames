package com.hadenwatne.shmames.models;

public class Story {
    private final String title;
    private final String author;
    private final String text;

    public Story(String title, String author, String text) {
        this.title = title;
        this.author = author;
        this.text = text;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getText() {
        return this.text;
    }
}
