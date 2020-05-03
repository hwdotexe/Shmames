package tech.hadenw.discordbot.storage;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HangmanGame {
    private String word;
    private List<Character> correctGuesses;
    private List<Character> incorrectGuesses;
    private int lives;
    private long channelID;
    private long messageID;

    public HangmanGame(String answer){
        this.word = answer.toLowerCase();
        this.correctGuesses = new ArrayList<Character>();
        this.incorrectGuesses = new ArrayList<Character>();
        this.lives = 6;

        // If there are any spaces in the word, add them to the correct guesses.
        if(this.word.contains(" "))
            this.correctGuesses.add(' ');
    }

    public void deleteMessage(Guild g){
        try {
            TextChannel tc = g.getTextChannelById(this.channelID);
            tc.deleteMessageById(this.messageID).queue();
        }catch (Exception e){
            // Do nothing
        }
    }

    public void setMessage(long channel, long message){
        this.channelID = channel;
        this.messageID = message;
    }

    public String getWord(){
        return this.word;
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
