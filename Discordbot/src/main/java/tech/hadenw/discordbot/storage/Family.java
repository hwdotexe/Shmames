package tech.hadenw.discordbot.storage;

import java.util.ArrayList;
import java.util.List;

public class Family {
    private String famName;
    private List<Long> memberGuilds;
    private long familyOwner;

    public Family(String name, long owner){
        this.famName = name;
        this.familyOwner = owner;
        this.memberGuilds = new ArrayList<Long>();
    }

    public String getFamName(){
        return this.famName;
    }

    public List<Long> getMemberGuilds(){
        return this.memberGuilds;
    }

    private Long getFamilyOwner(){
        return this.familyOwner;
    }
}
