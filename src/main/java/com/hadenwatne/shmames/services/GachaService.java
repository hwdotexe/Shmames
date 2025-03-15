package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.enums.GachaRarity;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaUser;
import net.dv8tion.jda.api.entities.User;

public class GachaService {
    public static final int SOFT_PITY_THRESHOLD = 50;
    public static final int HARD_PITY = 250;
    public static final int ROLL_COST = 5;
    public static final int AUTOMATIC_MAXIMUM = 100;
    public static final double BANNER_ODDS_BUFF = 1d - 0.2d; // 20%, 0.8

    public static GachaUser GetGachaUser(Brain brain, User user) {
        for(GachaUser gu : brain.getGachaUsers()) {
            if(gu.getUserID() == user.getIdLong()) {
                return gu;
            }
        }

        GachaUser ngu = new GachaUser(user.getIdLong());

        // New users get 10 free rolls. Yay!
        ngu.addUserPoints(ROLL_COST * 10);

        brain.getGachaUsers().add(ngu);

        return ngu;
    }

    public static String GetRarityEmoji(GachaRarity rarity) {
        switch (rarity) {
            case UNCOMMON:
                return ":green_circle:";
            case RARE:
                return ":blue_circle:";
            case VERY_RARE:
                return ":purple_circle:";
            case LEGENDARY:
                return ":orange_circle:";
            default:
                return ":white_circle:";
        }
    }

    public static int GetRarityDuplicateRefund(GachaRarity rarity) {
        switch (rarity) {
            case UNCOMMON:
                return 3;
            case RARE:
                return 5;
            case VERY_RARE:
                return 10;
            case LEGENDARY:
                return 15;
            default:
                return 0;
        }
    }
}
