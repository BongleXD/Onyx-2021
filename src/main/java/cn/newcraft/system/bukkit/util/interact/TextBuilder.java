package cn.newcraft.system.bukkit.util.interact;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextBuilder {

    private final TextComponent text;

    public TextBuilder(String text){
        this.text = new TextComponent(text);
    }

    public TextBuilder setClick(ClickEvent.Action action, String value){
        this.text.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public TextBuilder setHover(HoverEvent.Action action, String value){
        this.text.setHoverEvent(new HoverEvent(action, new ComponentBuilder(value).create()));
        return this;
    }

    public TextComponent build(){
        return this.text;
    }

}
