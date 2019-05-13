package com.ericlam.mc.time.dungeon.papi;

import com.ericlam.mc.time.dungeon.main.TimeDungeon;
import com.ericlam.mc.time.dungeon.managers.DungeonManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class TDPlaceholder extends PlaceholderExpansion {

    private TimeDungeon timeDungeon;

    public TDPlaceholder(TimeDungeon timeDungeon) {
        this.timeDungeon = timeDungeon;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        String[] param = params.split("_");
        if (param.length < 2) return TimeDungeon.getMessage("placeholder.not-enough-arg");
        String id = param[0];
        String attr = param[1].toLowerCase();
        switch (attr){
            case "time":
                return DungeonManager.getInstance().requestTime(id);
            case "name":
                return DungeonManager.getInstance().requestName(id);
            case "stats":
                return DungeonManager.getInstance().requestStats(id);
            default:
                break;
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return timeDungeon.getName().toLowerCase();
    }

    @Override
    public String getAuthor() {
        return timeDungeon.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return timeDungeon.getDescription().getVersion();
    }
}
