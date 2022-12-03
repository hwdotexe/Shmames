package com.hadenwatne.shmames.enums;

public enum GachaRarity {
    COMMON(0d) ,            // 100%, 0.0
    UNCOMMON(1d-0.3d),      // 30%, 0.7
    RARE(1d-0.1d),          // 10%, 0.9
    VERY_RARE(1d-0.05d),    // 5%, 0.95
    LEGENDARY(1d-0.01d);    // 1%, 0.99

    /*
    This value serves as a minimum value required to attain an item of that rarity.
    For example, LEGENDARY's value is 0.99.
    Let x = the user's roll.
    A LEGENDARY item can only be attained if x >= 0.99; else, VERY_RARE is evaluated, etc., down to COMMON
     */
    private final double rarityValue;

    GachaRarity(double rarityValue){
        this.rarityValue = rarityValue;
    }

    public double getRarityValue() {
        return rarityValue;
    }

    public static GachaRarity matchRarity(double input) {
        if(input >= LEGENDARY.getRarityValue()) {
            return LEGENDARY;
        } else if(input >= VERY_RARE.getRarityValue()) {
            return VERY_RARE;
        } else if(input >= RARE.getRarityValue()) {
            return RARE;
        } else if(input >= UNCOMMON.getRarityValue()) {
            return UNCOMMON;
        } else {
            return COMMON;
        }
    }
}
