package com.hadenwatne.shmames.models;

import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Family {
    private String famID;
    private String famName;
    private List<Long> memberGuilds;
    private long familyOwner;
    private String joinCode;

    public Family(String id, String name, long owner){
        this.famID = id;
        this.famName = name;
        this.familyOwner = owner;
        this.memberGuilds = new ArrayList<Long>();
        this.joinCode = "";
    }

    public String getFamID(){
        return this.famID;
    }

    public String getFamName(){
        return this.famName;
    }

    public List<Long> getMemberGuilds(){
        return this.memberGuilds;
    }

    public Long getFamilyOwner(){
        return this.familyOwner;
    }

    public String getNewJoinCode(){
        this.joinCode = UUID.randomUUID().toString();

        return this.joinCode;
    }

    public void clearCode(){
        this.joinCode = "";
    }

    public boolean validateCode(String attempt){
        if(attempt.length()>0)
            return this.joinCode.equals(attempt);

        return false;
    }

    public void addToFamily(long guildID){
        if(!memberGuilds.contains(guildID))
            memberGuilds.add(guildID);

        this.joinCode = "";
    }
}
