package net.blastmc.onyx.bukkit.nick;

import net.blastmc.onyx.api.Onyx;
import net.blastmc.onyx.api.bukkit.PlayerProfile;
import net.blastmc.onyx.api.bukkit.server.ServerType;
import net.blastmc.onyx.bukkit.util.Method;
import net.blastmc.onyx.bukkit.util.interact.BookBuilder;
import net.blastmc.onyx.bukkit.util.interact.SignGUI;
import net.blastmc.onyx.bukkit.util.interact.TextBuilder;
import net.blastmc.onyx.bukkit.config.SkinConfig;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
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
        this.setPermission("onyx.command.nick");
    }

    @Cmd(arg = "rank", coolDown = 5000, perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void select(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        if(p.hasPermission("onyx.nick.staff")){
            openAdminMenu(p);
        }else{
            openNormalMenu(p);
        }
    }

    @Cmd(coolDown = 5000, perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void rank(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        if(p.hasPermission("onyx.nick.staff")){
            openAdminMenu(p);
        }else{
            openNotificationMenu(p);
        }
    }

    @Cmd(coolDown = 5000, arg = "<value>", perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void skin(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME  || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        boolean b = !p.hasPermission("onyx.nick.staff") && (args[0].equalsIgnoreCase("self") || args[0].equalsIgnoreCase("svip_plus"));
        List<String> ranks = Lists.newArrayList();
        ranks.add("default");
        ranks.add("vip");
        ranks.add("vip_plus");
        ranks.add("svip");
        ranks.add("svip_plus");
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
                case "svip":
                    p.sendMessage("§a你的昵称会员等级以设置为 §bSVIP§a！");
                    break;
                case "svip_plus":
                    p.sendMessage("§a你的昵称会员等级以设置为 §6SVIP§c+§a！");
                    break;
                case "self":
                    p.sendMessage("§a你的昵称会员等级将继承于你目前的会员等级！");
                    break;
            }
            Main.getNMS().openBookMenu(p,
                    new BookBuilder("skin")
                            .addText(new TextBuilder("哇哦！显示昵称时，你想要哪个皮肤？\n").build())
                            .addText(new TextBuilder("\n§l➤ §0我的皮肤")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " " + Onyx.getPlayerData(p.getUniqueId()).getName())
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用你的皮肤")
                                    .build())
                            .addText(new TextBuilder("\n§l➤ §0Steve/Alex 皮肤")
                                    .setClick(ClickEvent.Action.RUN_COMMAND, "/nick " + args[0] + " Steve")
                                    .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 使用 Steve/Alex 的皮肤")
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

    @Cmd(coolDown = 5000, arg = "<value> <value>", perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void name(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME  || Main.getInstance().getConfig().getBoolean("disable-nick")) {
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
        Method.setSkin(p, args[1]);
        if (!p.hasPermission("onyx.nick.staff")) {
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

    @Cmd(arg = "<value> <value> enter", perm = "onyx.nick.staff", only = CommandOnly.PLAYER)
    public void enter(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME ){
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

    @Cmd(arg = "<value> <value> enter <value>", perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void checkName(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME  || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        String name = args[3];
        if(p.hasPermission("onyx.nick.staff")){
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
            if(Main.getSQL().checkDataExists("player_data", "name", name)){
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
                return;
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
            case "svip":
                prefix = "§b[SVIP] ";
                break;
            case "svip_plus":
                prefix = "§6[SVIP§c+§6] ";
                break;
            case "self":
                prefix = "self";
                break;
        }
        PlayerProfile prof = Onyx.getPlayerProfile(p.getUniqueId());
        if(p.hasPermission("onyx.nick.staff")){
            prof.setNickSkin(args[1]);
            prof.setNickName(name);
            prof.setNickPrefix(prefix);
            Main.getNMS().changeName(p, name);
            p.sendMessage("§a你已完成昵称设置！");
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Main.getNMS().reloadPlayer(p);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
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
    @Cmd(arg = "<value> <value> random", perm = "onyx.command.nick", only = CommandOnly.PLAYER)
    public void random(CommandSender sender, String[] args){
        Player p = (Player) sender;
        if(Main.getType() == ServerType.GAME || Main.getType() == ServerType.ENDLESS_GAME  || Main.getInstance().getConfig().getBoolean("disable-nick")){
            p.sendMessage("§c请移步至大厅进行昵称修改！");
            return;
        }
        String name = randomName();
        if(Main.getSQL().checkDataExists("player_data", "name", name)){
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
        if(p.hasPermission("onyx.nick.staff")){
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
                            .addText(new TextBuilder("\n\n§n昵称不会马上生效而是在§0§l游§0§l戏§0中生效。")
                                    .build())
                            .create());
        }
    }

    private void openNotificationMenu(Player p){
        Main.getNMS().openBookMenu(p,
                new BookBuilder("rank")
                        .addText(new TextBuilder("昵称功能将允许你使用不同的用户名，使得其他玩家无法立即认出你。\n\n所有规则仍将适用，你仍可以被举报并且你的昵称记录将被保存。\n").build())
                        .addText(new TextBuilder("\n§0§n➤ 我明白了，开始设定我的\n§0§n昵称吧！")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick rank")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 进行昵称修改")
                                .build())
                        .create());
    }

    private void openNormalMenu(Player p){
        Main.getNMS().openBookMenu(p,
                new BookBuilder("rank")
                        .addText(new TextBuilder("帮助你设置昵称！\n首先，你需要选择你想要显示\n的§l会员等级§0。\n").build())
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
                        .addText(new TextBuilder("\n§l➤ §bSVIP")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick svip")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §bSVIP")
                                .build())
                        .create());
    }

    private void openAdminMenu(Player p){
        Main.getNMS().openBookMenu(p,
                new BookBuilder("rank")
                        .addText(new TextBuilder("帮助你设置昵称！\n首先，你需要选择你想要显示\n的§l会员等级§0。\n").build())
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
                        .addText(new TextBuilder("\n§l➤ §bSVIP")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick svip")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §bSVIP")
                                .build())
                        .addText(new TextBuilder("\n§l➤ §6SVIP§c+")
                                .setClick(ClickEvent.Action.RUN_COMMAND, "/nick svip_plus")
                                .setHover(HoverEvent.Action.SHOW_TEXT, "点击这里, 显示为 §6SVIP§c+")
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
