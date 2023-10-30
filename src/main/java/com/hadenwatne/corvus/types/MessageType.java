package com.hadenwatne.corvus.types;

import java.awt.*;

public enum MessageType {
    INFORMATION(new Color(134, 187, 216)),
    PRIVILEGED(new Color(51, 102, 153)),
    SUCCESS(new Color(95, 173, 65)),
    WARNING(new Color(191, 174, 72)),
    ERROR(new Color(147, 3, 46)),
    EXPIRED(Color.lightGray);

    private final Color color;

    MessageType(Color color){
        this.color = color;
    }

    public Color getColor(){
        return this.color;
    }
}
