package com.hadenwatne.shmames.enums;

import java.awt.*;

public enum EmbedType {
    INFO(new Color(219, 181, 28)),
    SUCCESS(new Color(0, 132, 2)),
    ERROR(new Color(140, 5, 19));

    private final Color color;

    EmbedType(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }
}
