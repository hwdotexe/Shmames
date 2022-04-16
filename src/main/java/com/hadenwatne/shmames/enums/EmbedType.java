package com.hadenwatne.shmames.enums;

import java.awt.*;

public enum EmbedType {
    INFO(new Color(237, 198, 42)),
    SUCCESS(new Color(26, 206, 29)),
    ERROR(new Color(209, 5, 19));

    private final Color color;

    EmbedType(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }
}
