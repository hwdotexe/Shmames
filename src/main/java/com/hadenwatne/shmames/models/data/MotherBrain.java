package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.models.Family;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MotherBrain {
    public String botName;
    private HashMap<String, ActivityType> statuses;
    private HashMap<String, Integer> commandStats;
    private List<Family> serverFamilies;
    private String tenorAPIKey;
    private String wolframAPIKey;

    public MotherBrain(String botName) {
        this.botName = botName;
        this.statuses = new HashMap<String, ActivityType>();
        this.commandStats = new HashMap<String, Integer>();
        this.serverFamilies = new ArrayList<Family>();
        this.tenorAPIKey = "API_KEY_HERE";
        this.wolframAPIKey = "API_KEY_HERE";
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
        if(this.serverFamilies == null)
            this.serverFamilies = new ArrayList<Family>();

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
        if(tenorAPIKey == null)
            tenorAPIKey = "API_KEY_HERE";

        return tenorAPIKey;
    }

    public String getWolframAPIKey() {
        if(wolframAPIKey == null)
            wolframAPIKey = "API_KEY_HERE";

        return wolframAPIKey;
    }

    /**
     * Loads default settings into the system.
     */
    public void loadDefaults() {
        statuses.put("Bagpipes", ActivityType.PLAYING);
        statuses.put("Netflix", ActivityType.WATCHING);
        statuses.put("Game Soundtracks", ActivityType.LISTENING);
    }
}