package com.ericlam.mc.time.dungeon.exceptions;

public class DungeonNonExistException extends DungeonException{

    public DungeonNonExistException(String id) {
        super(id,"§4Dungeon #"+id+" 不存在！");
    }
}
