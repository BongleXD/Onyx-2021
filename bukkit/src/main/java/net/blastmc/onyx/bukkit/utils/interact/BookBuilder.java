package net.blastmc.onyx.bukkit.utils.interact;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.utils.ReflectUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class BookBuilder {

    private String title;
    private List<TextComponent> texts = Lists.newArrayList();

    public BookBuilder(String title){
        this.title = title;
    }

    public BookBuilder addText(TextComponent text){
        this.texts.add(text);
        return this;
    }

    public ItemStack create() {
        ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta m = (BookMeta) is.getItemMeta();
        m.setTitle(title);
        m.setAuthor("");
        try {
            List<Object> list = (List<Object>) ReflectUtils.getField(ReflectUtils.getCraftClass("inventory.CraftMetaBook"), "pages").get(m);
            TextComponent text = new TextComponent("");
            for (TextComponent tc : texts)
                text.addExtra(tc);
            list.add(getAsIChatBaseComponent(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
        is.setItemMeta(m);
        return is;
    }

    public Object getAsIChatBaseComponent(TextComponent textComponent) {
        try {
            Class<?> chatSerializer = Main.getBukkitVer().equals("v1_8_R1") ? ReflectUtils.getNMSClass("ChatSerializer") : ReflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
            return chatSerializer.getMethod("a", String.class).invoke(chatSerializer, ComponentSerializer.toString(textComponent));
        } catch (Exception e) {
            return null;
        }
    }

}
