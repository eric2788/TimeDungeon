package com.ericlam.mc.time.dungeon.exceptions;

public abstract class DungeonException extends Exception {
    private String id;

    DungeonException(String id) {
        this.id = id;
    }

    DungeonException(String id, String messages){
        super(messages);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
