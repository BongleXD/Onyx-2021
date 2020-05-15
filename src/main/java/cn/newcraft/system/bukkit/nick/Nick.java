package cn.newcraft.system.bukkit.nick;

import cn.newcraft.system.bukkit.config.SkinConfig;
import cn.newcraft.system.shared.PlayerData;
import cn.newcraft.system.bukkit.api.PlayerProfile;
import cn.newcraft.system.bukkit.Main;
import cn.newcraft.system.bukkit.command.CommandManager;
import cn.newcraft.system.bukkit.util.interact.BookBuilder;
import cn.newcraft.system.bukkit.util.interact.SignGUI;
import cn.newcraft.system.bukkit.util.interact.TextBuilder;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class Nick extends CommandManager {

    public Nick() {
        super("nick", "昵称修改", "/nick", "昵称修改");
        this.setPermission("ncs.command.nick");
    }

    @Cmd(coolDown = 5000, perm = "ncs.command.nick", only = CommandOnly.PLAYER)
    public void rank(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getGameManager() != null || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        if(p.hasPermission("ncs.nick.staff")){
            openAdminMenu(p);
        }else{
            openNormalMenu(p);
        }
    }

    @Cmd(coolDown = 5000, arg = "<value>", perm = "ncs.command.nick", only = CommandOnly.PLAYER)
    public void skin(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getGameManager() != null || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        boolean b = !p.hasPermission("ncs.nick.staff") && (args[0].equalsIgnoreCase("self") || args[0].equalsIgnoreCase("mvp_plus_plus"));
        List<String> ranks = Lists.newArrayList();
        ranks.add("default");
        ranks.add("vip");
        ranks.add("vip_plus");
        ranks.add("mvp");
        ranks.add("mvp_plus");
        ranks.add("mvp_plus_plus");
        ranks.add("self");
        if(ranks.contains(args[0]) && !b){
            switch (args[0]){
                case "default":
                    p.sendMessage("§a你的昵称会员等级以设置为 §7默认§a！");
                    break;
                case "vip":
                    p.sendMessage("§a你的昵称会员等级以设置为 §aVIP！");
                    break;
                case "vip_plus":
                    p.sendMessage("§a你的昵称会员等级以设置为 §aVIP§6+§a！");
                    break;
                case "mvp":
                    p.sendMessage("§a你的昵称会员等级以设置为 §bMVP§a！");
                    break;
                case "mvp_plus":
                    p.sendMessage("§a你的昵称会员等级以设置为 §bMVP§c+§a！");
                    break;
                case "mvp_plus_plus":
                    p.sendMessage("§a你的昵称会员等级以设置为 §6MVP§c++§a！");
                    break;
                case "self":
                    p.sendMessage("§a你的昵称会员等级将继承于你目前的会员等级！");
                    break;
            }
            Main.getNMS().openBookMenu(p,
                    new BookBuilder("skin")
                            .addText(new TextBuilder("哇哦！显示昵称时，你想要哪个皮肤？\n").build())
                            .addText(new TextBuilder("\n§l➤ §0我的皮肤")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + PlayerData.getDataFromUUID(p.getUniqueId()).getName())
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用你的皮肤")
                                    .build())
                            .addText(new TextBuilder("\n§l➤ §0Steve/Alex 皮肤")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " Steve")
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用 Steve 的皮肤")
                                    .build())
                            .addText(new TextBuilder("\n§l➤ §0随机皮肤")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + randomSkin())
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用随机皮肤")
                                    .build())
                            .create());
        }else{
            p.sendMessage("§c会员等级不存在！");
        }
    }

    @Cmd(coolDown = 5000, arg = "<value> <value>", perm = "ncs.command.nick", only = CommandOnly.PLAYER)
    public void name(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (Main.getGameManager() != null || Main.getInstance().getConfig().getBoolean("disable-nick")) {
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        switch (args[1]) {
            case "Steve":
                p.sendMessage("§a你的皮肤已设置为 Steve/Alex！");
                break;
            default:
                p.sendMessage("§a你的皮肤已设置为 " + args[1] + "！");
                break;
        }
        if (!p.hasPermission("ncs.nick.staff")) {
            p.chat("/nick " + args[0] + " " + args[1] + " random");
            return;
        }
        Main.getNMS().openBookMenu(p,
                new BookBuilder("name_select")
                        .addText(new TextBuilder("现在，请选择你要是用的§l昵称！\n").build())
                        .addText(new TextBuilder("\n§l➤ §0输入昵称")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 输入要使用的昵称")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §0使用随机昵称")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " random")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用随机生成的昵称")
                                .build())
                        .addText(new TextBuilder("\n\n想要恢复平常状态，请输入:\n§l/unnick").build())
                        .create());
    }

    @Cmd(arg = "<value> <value> enter", perm = "ncs.nick.staff", only = CommandOnly.PLAYER)
    public void enter(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getGameManager() != null){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        new SignGUI().open(p, "", "^^^^^^^^^^^^^^^", "在此输入你", "喜欢的昵称", new SignGUI.EditCompleteListener() {
            @Override
            public void onEditComplete(SignGUI.EditCompleteEvent e) {
                p.chat("/nick " + args[0] + " " + args[1] + " enter " + e.getLines()[0]);
            }
        });
    }

    @Cmd(arg = "<value> <value> enter <value>", perm = "ncs.command.nick", only = CommandOnly.PLAYER)
    public void checkName(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getGameManager() != null || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        String name = args[3];
        if(p.hasPermission("ncs.nick.staff")){
            boolean match = name.matches("^[_a-zA-Z0-9]+$");
            if(!match || name.length() < 4 || name.length() > 16){
                Main.getNMS().openBookMenu(p,
                        new BookBuilder("name_error")
                                .addText(new TextBuilder("注意！你不能将中文或非下划线符号用作你的昵称！\n").build())
                                .addText(new TextBuilder("\n昵称长度为 4-16 个字符！\n").build())
                                .addText(new TextBuilder("\n        §c§n点击重试")
                                        .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter")
                                        .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 输入要使用的昵称")
                                        .build())
                                .create());
                return;
            }
            if(Main.getSQL().checkDataExists("player_data", "player_name", name)){
                Main.getNMS().openBookMenu(p,
                        new BookBuilder("name_error")
                                .addText(new TextBuilder("注意！你不能将" + name + "用作你的昵称！\n").build())
                                .addText(new TextBuilder("\n这个昵称是 NewCraft 玩家的哦！\n").build())
                                .addText(new TextBuilder("\n        §c§n点击重试")
                                        .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter")
                                        .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 输入要使用的昵称")
                                        .build())
                                .create());
                return;
            }
            if(Main.getSQL().checkDataExists("player_profile", "nick_name", name)){
                Main.getNMS().openBookMenu(p,
                        new BookBuilder("name_error")
                                .addText(new TextBuilder("注意！你不能将" + name + "用作你的昵称！\n").build())
                                .addText(new TextBuilder("\n这个昵称有人已经作为昵称使用了哦！\n").build())
                                .addText(new TextBuilder("\n        §c§n点击重试")
                                        .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter")
                                        .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 输入要使用的昵称")
                                        .build())
                                .create());
            }
            boolean b = true;
            for(Player online : Bukkit.getOnlinePlayers()){
                if(online.getName().equalsIgnoreCase(name)){
                    b = false;
                    break;
                }
            }
            if(!b) {
                Main.getNMS().openBookMenu(p,
                        new BookBuilder("name_error")
                                .addText(new TextBuilder("注意！你不能将" + name + "用作你的昵称！\n").build())
                                .addText(new TextBuilder("\n这个昵称有人已经使用了哦！\n").build())
                                .addText(new TextBuilder("\n        §c§n点击重试")
                                        .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter")
                                        .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 输入要使用的昵称")
                                        .build())
                                .create());
            }
        }
        String prefix = null;
        switch (args[0]){
            case "default":
                prefix = "§7";
                break;
            case "vip":
                prefix = "§a[VIP] ";
                break;
            case "vip_plus":
                prefix = "§a[VIP§6+§a] ";
                break;
            case "mvp":
                prefix = "§b[MVP] ";
                break;
            case "mvp_plus":
                prefix = "§b[MVP§c+§b] ";
                break;
            case "mvp_plus_plus":
                prefix = "§6[MVP§c++§6] ";
                break;
            case "self":
                prefix = "self";
                break;
        }
        PlayerProfile prof = PlayerProfile.getDataFromUUID(p.getUniqueId());
        if(p.hasPermission("ncs.nick.staff")){
            prof.setNickSkin(args[1]);
            prof.setNickName(name);
            prof.setNickPrefix(prefix);
            Main.getNMS().changeName(p, name);
            p.sendMessage("§a你已完成昵称设置！");
        }else{
            prof.setNickSkin(args[1]);
            prof.setNickName(name);
            prof.setNickPrefix(prefix);
            prof.setNicked(true);
            p.sendMessage("§a你已完成昵称设置！即将于游戏内生效！");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                prof.saveData(false);
            }
        }.runTaskAsynchronously(Main.getInstance());
    }
    @Cmd(arg = "<value> <value> random", perm = "ncs.command.nick", only = CommandOnly.PLAYER)
    public void random(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getGameManager() != null || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        String name = randomName();
        if(Main.getSQL().checkDataExists("player_data", "player_name", name)){
            name = randomName();
        }
        boolean b = true;
        for(Player online : Bukkit.getOnlinePlayers()){
            if(online.getName().equalsIgnoreCase(name)){
                b = false;
                break;
            }
        }
        if(!b){
            name = randomName();
        }
        if(p.hasPermission("ncs.nick.staff")){
            Main.getNMS().openBookMenu(p,
                    new BookBuilder("name_error")
                            .addText(new TextBuilder("我们为你随机生成了一个昵称:\n").build())
                            .addText(new TextBuilder("§l" + name).build())
                            .addText(new TextBuilder("\n\n        §a§n点击使用")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter " + name)
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用此昵称")
                                    .build())
                            .addText(new TextBuilder("\n        §c§n点击重试")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " random")
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 重新生成昵称")
                                    .build())
                            .addText(new TextBuilder("\n\n§n或者输入要使用的昵称。")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter")
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 输入要使用的昵称")
                                    .build())
                            .create());
        }else{
            Main.getNMS().openBookMenu(p,
                    new BookBuilder("name_error")
                            .addText(new TextBuilder("我们为你随机生成了一个昵称:\n").build())
                            .addText(new TextBuilder("§l" + name).build())
                            .addText(new TextBuilder("\n\n        §a§n点击使用")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " enter " + name)
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用此昵称")
                                    .build())
                            .addText(new TextBuilder("\n        §c§n点击重试")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + args[1] + " random")
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 重新生成昵称")
                                    .build())
                            .addText(new TextBuilder("\n\n§n昵称不会马上生效而是在§l游§l戏§0中生效。")
                                    .build())
                            .create());
        }
    }

    private void openNormalMenu(Player p){
        Main.getNMS().openBookMenu(p,
                new BookBuilder("rank")
                        .addText(new TextBuilder("帮助你设置昵称！\n首先，你需要选择你想要显示\n的§l会员等级§8。\n").build())
                        .addText(new TextBuilder("\n§l➤ §7默认")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick default")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §7默认")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §aVIP")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick vip")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §aVIP")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §aVIP§6+")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick vip_plus")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §aVIP§6+")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §bMVP")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick mvp")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §bMVP")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §bMVP§c+")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick mvp_plus")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §bMVP§c+")
                                .build())
                        .create());
    }

    private void openAdminMenu(Player p){
        Main.getNMS().openBookMenu(p,
                new BookBuilder("rank")
                        .addText(new TextBuilder("帮助你设置昵称！\n首先，你需要选择你想要显示\n的§l会员等级§8。\n").build())
                        .addText(new TextBuilder("\n§l➤ §7默认")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick default")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §7默认")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §aVIP")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick vip")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §aVIP")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §aVIP§6+")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick vip_plus")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §aVIP§6+")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §bMVP")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick mvp")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §bMVP")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §bMVP§c+")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick mvp_plus")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §bMVP§c+")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §6MVP§c++")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick mvp_plus_plus")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §6MVP§c++")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §0不更换会员等级")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick self")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 跳过更换会员等级")
                                .build())
                        .create());
    }

    private String randomName(){
        return NickNames.getNickNames().get(new Random().nextInt(NickNames.getNickNames().size()));
    }

    private String randomSkin(){
        List<String> list = SkinConfig.cfg.getYml().getStringList("skin");
        return list.get(new Random().nextInt(list.size()));
    }

}
