package com.ericlam.mc.time.dungeon.main;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
/*
    This builder created by Eric Lam at 2019/1
 */
public class AdvMessageBuilder {
    private TextComponent textComponent;

    public AdvMessageBuilder(String omsg) {
        String msg = ChatColor.translateAlternateColorCodes('&', omsg);
        textComponent = new TextComponent(msg);
    }

    public AdvMessageBuilder addMessage(String omsg) {
        String msg = ChatColor.translateAlternateColorCodes('&', omsg);
        for (BaseComponent component : TextComponent.fromLegacyText(msg)) {
            textComponent.addExtra(component);
        }
        return this;
    }

    public AdvMessageBuilder addMessage(String[] msgs) {
        for (String msg : msgs) {
            addMessage(msg);
        }
        return this;
    }

    public AdvMessageBuilder addHover(String omsg, HoverEvent.Action action, BaseComponent[] bases) {
        String msg = ChatColor.translateAlternateColorCodes('&', omsg);
        TextComponent component = new TextComponent(msg);
        component.setHoverEvent(new HoverEvent(action, bases));
        textComponent.addExtra(component);
        return this;
    }

    public AdvMessageBuilder addClick(String omsg, ClickEvent.Action action, String value) {
        String msg = ChatColor.translateAlternateColorCodes('&', omsg);
        TextComponent component = new TextComponent(msg);
        component.setClickEvent(new ClickEvent(action, value));
        textComponent.addExtra(component);
        return this;
    }

    public TextComponent build() {
        return textComponent;
    }

    public void sendPlayer(Player player) {
        player.spigot().sendMessage(textComponent);
    }
}