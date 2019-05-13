package com.ericlam.mc.time.dungeon.exceptions;

public class DungeonSetUpNotFinishException extends DungeonException{

    public DungeonSetUpNotFinishException(String id) {
        super(id,"§4Dungeon #"+id+" 的設置尚未完成。");
    }
}
