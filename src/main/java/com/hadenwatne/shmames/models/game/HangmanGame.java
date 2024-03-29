package com.hadenwatne.shmames.models.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HangmanGame {
    private String word;
    private String hint;
    private String dictionary;
    private List<Character> correctGuesses;
    private List<Character> incorrectGuesses;
    private int lives;
    private long channelID;
    private long messageID;
    private boolean isFinished;

    public HangmanGame(String answer, String hint, String dictionary){
        this.word = answer.toLowerCase();
        this.hint = hint;
        this.dictionary = dictionary;
        this.correctGuesses = new ArrayList<Character>();
        this.incorrectGuesses = new ArrayList<Character>();
        this.lives = 6;
        this.isFinished = false;

        // If there are any spaces in the word, add them to the correct guesses.
        if(this.word.contains(" "))
            this.correctGuesses.add(' ');
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public void finish() {
        this.isFinished = true;
    }

    public boolean didWin() {
        for(char wc : word.toCharArray()) {
            if(!correctGuesses.contains(wc)) {
                return false;
            }
        }

        if(lives == 0) {
            return false;
        }

        return true;
    }

    public void deleteMessage(Guild g){
        try {
            TextChannel tc = g.getTextChannelById(this.channelID);
            tc.deleteMessageById(this.messageID).queue();
        }catch (Exception e){
            // Do nothing
        }
    }

    public void updateMessage(Guild g, EmbedBuilder embedBuilder){
        try {
            TextChannel tc = g.getTextChannelById(this.channelID);
            tc.editMessageEmbedsById(this.messageID, embedBuilder.build()).queue();
        }catch (Exception e){
            // Do nothing
        }
    }

    public void setChannel(long channel) {
        this.channelID = channel;
    }

    public void setMessage(long message){
        this.messageID = message;
    }

    public String getWord(){
        return this.word;
    }

    public String getHint(){
        if(this.hint != null)
            return this.hint;

        return "<Hint Unknown>";
    }

    public String getDictionary(){
        if(this.dictionary != null)
            return this.dictionary;

        return "<Hint Unknown>";
    }

    public List<Character> getCorrectGuesses(){
        Collections.sort(correctGuesses);
        return this.correctGuesses;
    }

    public List<Character> getIncorrectGuesses(){
        Collections.sort(incorrectGuesses);
        return this.incorrectGuesses;
    }

    public int getLivesRemaining(){
        return this.lives;
    }

    public void removeLife(){
        this.lives -= 1;
    }

    public long getChannelID(){
        return this.channelID;
    }

    public long getMessageID(){
        return this.messageID;
    }
}
