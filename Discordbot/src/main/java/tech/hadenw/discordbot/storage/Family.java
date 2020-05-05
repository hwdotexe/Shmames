package tech.hadenw.discordbot.storage;

import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Family {
    private int famID;
    private String famName;
    private List<Long> memberGuilds;
    private long familyOwner;
    private String joinCode;

    public Family(int id, String name, long owner){
        this.famID = id;
        this.famName = name;
        this.familyOwner = owner;
        this.memberGuilds = new ArrayList<Long>();
        this.joinCode = "";
    }

    public int getFamID(){
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

    public boolean validateCode(String attempt){
        if(attempt.length()>0)
            return this.joinCode.equals(attempt);

        return false;
    }

    public void addToFamily(Guild g){
        if(!memberGuilds.contains(g.getIdLong()))
            memberGuilds.add(g.getIdLong());

        this.joinCode = "";
    }
}
