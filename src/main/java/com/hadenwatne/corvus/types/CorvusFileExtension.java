package com.hadenwatne.corvus.types;

public enum CorvusFileExtension {
    GIF("gif"),
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg");

    private final String extension;

    CorvusFileExtension(String ext){
        this.extension = ext;
    }

    public String getExtension(){
        return this.extension;
    }
}
