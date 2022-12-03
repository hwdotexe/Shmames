package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.enums.GachaRarity;

import java.util.UUID;

public class GachaCharacter {
    private String gachaCharacterID;
    private String gachaCharacterName;
    private String gachaCharacterDescription;
    private String gachaCharacterImageURL;
    private GachaRarity gachaCharacterRarity;

    public GachaCharacter(String name, String description, String imageURL, GachaRarity rarity) {
        this.gachaCharacterID = UUID.randomUUID().toString();
        this.gachaCharacterName = name;
        this.gachaCharacterDescription = description;
        this.gachaCharacterImageURL = imageURL;
        this.gachaCharacterRarity = rarity;
    }

    public String getGachaCharacterID() {
        return gachaCharacterID;
    }

    public String getGachaCharacterName() {
        return gachaCharacterName;
    }

    public String getGachaCharacterDescription() {
        return gachaCharacterDescription;
    }

    public String getGachaCharacterImageURL() {
        return gachaCharacterImageURL;
    }

    public GachaRarity getGachaCharacterRarity() {
        return gachaCharacterRarity;
    }
}
