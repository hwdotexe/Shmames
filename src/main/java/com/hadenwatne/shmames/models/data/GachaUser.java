package com.hadenwatne.shmames.models.data;

import java.util.HashMap;

public class GachaUser {
    private long userID;
    private int userPoints;
    private int globalPityCounter;
    private int bannerPityCounter;
    private HashMap<String, Integer> userGachaInventory;

    public GachaUser(long userID) {
        this.userID = userID;
        this.userPoints = 0;
        this.globalPityCounter = 0;
        this.bannerPityCounter = 0;
        this.userGachaInventory = new HashMap<>();
    }

    public long getUserID() {
        return userID;
    }

    public int getGlobalPityCounter() {
        return globalPityCounter;
    }

    public int getBannerPityCounter() {
        return bannerPityCounter;
    }

    public void incrementPityCounter() {
        this.globalPityCounter++;
        this.bannerPityCounter++;
    }

    public void resetPityCounter() {
        this.globalPityCounter = 0;
        this.bannerPityCounter = 0;
    }

    public void resetBannerPityCounter() {
        this.bannerPityCounter = 0;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(int newPoints) {
        this.userPoints = newPoints;
    }

    public void addUserPoints(int points) {
        if(points > 0) {
            this.userPoints += points;
        }
    }

    public void subtractUserPoints(int points) {
        if(points > 0) {
            if(this.userPoints - points >= 0) {
                this.userPoints -= points;
            } else {
                this.userPoints = 0;
            }
        }
    }

    public HashMap<String, Integer> getUserGachaInventory() {
        return userGachaInventory;
    }

    public void addCharacterToInventory(GachaCharacter character) {
        int rolls = this.userGachaInventory.getOrDefault(character.getGachaCharacterID(), 0);

        this.userGachaInventory.put(character.getGachaCharacterID(), rolls + 1);
    }

    public boolean hasCharacter(GachaCharacter character) {
        return this.getUserGachaInventory().containsKey(character.getGachaCharacterID());
    }
}
