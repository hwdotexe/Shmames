package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.models.Family;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotherBrain {
    public String botName;
    public HashMap<String, ActivityType> statuses;
    public HashMap<String, Integer> commandStats;
    public List<Family> serverFamilies;
    public String tenorAPIKey;
    public String wolframAPIKey;

    public MotherBrain(){}

    public MotherBrain(String botName) {
        this.botName = botName;
        this.statuses = new HashMap<>();
        this.commandStats = new HashMap<>();
        this.serverFamilies = new ArrayList<>();
        this.tenorAPIKey = "API_KEY_HERE";
        this.wolframAPIKey = "API_KEY_HERE";

        loadDefaults();
    }

    public HashMap<String, ActivityType> getStatuses(){
        return statuses;
    }

    public  HashMap<String, Integer> getCommandStats(){
        if(commandStats == null)
            commandStats = new HashMap<String, Integer>();

        return commandStats;
    }

    public List<Family> getServerFamilies(){
        return this.serverFamilies;
    }

    public Family getFamilyByID(String famID){
        for(Family f : getServerFamilies()){
            if(f.getFamID().equals(famID)){
                return f;
            }
        }

        return null;
    }

    public String getTenorAPIKey() {
        return tenorAPIKey;
    }

    public String getWolframAPIKey() {
        return wolframAPIKey;
    }

    private void loadDefaults() {
        statuses.put("Bagpipes", ActivityType.PLAYING);
        statuses.put("Netflix", ActivityType.WATCHING);
        statuses.put("Game Soundtracks", ActivityType.LISTENING);
    }
}