package com.hadenwatne.shmames.enums;

public enum UserListType {
    PUBLIC,
    PRIVATE;

    public static UserListType parseOrPrivate(String type){
        for(UserListType v : UserListType.values()) {
            if(v.name().equalsIgnoreCase(type)) {
                return v;
            }
        }

        return UserListType.PRIVATE;
    }
}
