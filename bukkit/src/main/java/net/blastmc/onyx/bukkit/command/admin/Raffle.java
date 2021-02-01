package net.blastmc.onyx.bukkit.command.admin;

import com.google.common.base.Joiner;
import me.clip.placeholderapi.PlaceholderAPI;
import net.blastmc.onyx.api.utils.Method;
import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.utils.NoteUtil;
import net.blastmc.onyx.bukkit.utils.interact.SoundUtil;
import net.blastmc.onyx.bukkit.utils.interact.TitleUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Raffle extends CommandManager {

    private static BukkitTask task;
    private static UUID first;
    private static UUID second;
    private static UUID third;

    public Raffle() {
        super("raffle", "抽彩", "/raffle <硬币数量> <颜色> <名字> <奖励指令>", "onyx.command.raffle", "抽彩");
    }

    @Cmd(arg = "<integer> <value> <value> <value...>", perm = "onyx.command.raffle", permMessage = "§c你需要 ADMIN 及以上的会员等级才能使用此指令！")
    public void raffle(CommandSender sender, String[] args){
        int count = Integer.parseInt(args[0]);
        String[] commandArg = new String[args.length - 3];
        System.arraycopy(args, 3, commandArg, 0, args.length - 3);
        String command = Joiner.on(" ").join(commandArg);
        try {
            ChatColor color = ChatColor.valueOf(args[1]);
            String name = args[2];
            if(task != null){
                sender.sendMessage("§c抽彩正在进行中！");
                return;
            }
            task = new BukkitRunnable(){
                int tick = 1;
                NoteUtil[] g1 = new NoteUtil[]{
                        NoteUtil.C4_SHARP, NoteUtil.F4_SHARP,
                        NoteUtil.F3_SHARP, NoteUtil.A3_SHARP,
                };
                NoteUtil[] g2 = new NoteUtil[]{
                        NoteUtil.A3, NoteUtil.A4,
                        NoteUtil.C4_SHARP, NoteUtil.E4,
                };
                NoteUtil[] g3 = new NoteUtil[]{
                        NoteUtil.D4_SHARP, NoteUtil.B4,
                        NoteUtil.D4, NoteUtil.F4
                };
                int i = 1;
                NoteUtil[] notes = g1;
                int sleep = 0;
                final Random r = new Random();
                int j = -1;
                @Override
                public synchronized void run() {
                    if(sleep > 0){
                        sleep--;
                        return;
                    }
                    try{
                        Player p = getRandomPlayer();
                        String displayName = getDisplayName(p.getUniqueId());
                        int amount = getAmount(count);
                        if(i == 1 && j == -1){
                            j = r.nextInt(6);
                            return;
                        }
                        if(tick == 16){
                            if(i == 3){
                                int order = getOrder(first, second, third);
                                for(Player online : Bukkit.getOnlinePlayers()){
                                    online.playSound(online.getLocation(), SoundUtil.ENDERDRAGON_GROWL.getSound(), 1F, 1F);
                                    TitleUtil.clearTitle(online);
                                    TitleUtil.sendTitle(online, 40, 40, 0, displayName, "§e赢得奖励 #" + order + ": " + color + Method.toTrisection(amount) + " " + name);
                                }
                                if(third == null){
                                    third = p.getUniqueId();
                                } else if(second == null){
                                    second = p.getUniqueId();
                                } else{
                                    first = p.getUniqueId();
                                    for(Player online : Bukkit.getOnlinePlayers()){
                                        int prize = getPrize(online, count);
                                        online.playSound(online.getLocation(), Sound.ENTITY_ENDERDRAGON_AMBIENT, 1F, 1F);
                                        TitleUtil.clearTitle(online);
                                        TitleUtil.sendTitle(online, 20, 10, 0, displayName, "§e赢得奖励 #" + getOrder(first, second, third) + ": " + color + Method.toTrisection(amount) + " " + name);
                                        online.sendMessage("§6§m----------------------");
                                        online.sendMessage("§6活动已结束: 抽彩");
                                        online.sendMessage("§6你的奖励: " + color + "+" + Method.toTrisection(prize) + " " + name);
                                        online.sendMessage("§6本次总奖金: " + color + Method.toTrisection(count) + " " + name);
                                        online.sendMessage("§e  #1 " + color + Method.toTrisection(count / 2) + " " + name + "§f: " + getDisplayName(first));
                                        online.sendMessage("§e  #2 " + color + Method.toTrisection((count / 2) / 3 * 2) + " " + name + "§f: " + getDisplayName(second));
                                        online.sendMessage("§e  #3 " + color + Method.toTrisection((count / 2) / 3) + " " + name + "§f: " + getDisplayName(third));
                                        online.sendMessage("§6§m----------------------");
                                        if(prize >= 0){
                                            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(online, command).replace("{amount}", String.valueOf(prize)));
                                            });
                                        }
                                    }
                                    this.cancel();
                                    first = null;
                                    second = null;
                                    third = null;
                                    task = null;
                                    return;
                                }
                                getRandomPlayer();
                                i = 1;
                                tick = 1;
                                j = -1;
                                notes = g1;
                                sleep = 60;
                                return;
                            }
                            i++;
                            if(i == 2){
                                notes = g2;
                            }else{
                                notes = g3;
                            }
                            tick = 0;
                        }
                        NoteUtil note1 = notes[0].getNextNote(j);
                        NoteUtil note2 = notes[1].getNextNote(j);
                        NoteUtil note3 = notes[2].getNextNote(j);
                        NoteUtil note4 = notes[3].getNextNote(j);
                        for(Player online : Bukkit.getOnlinePlayers()){
                            if(tick % 2 != 0){
                                online.playSound(online.getLocation(), SoundUtil.NOTE_PIANO.getSound(), 1F, note2.getPitch());
                                online.playSound(online.getLocation(), SoundUtil.NOTE_PIANO.getSound(), 1F, note1.getPitch());
                                online.playSound(online.getLocation(), SoundUtil.NOTE_BASS.getSound(), 1F, note2.getPitch());
                                online.playSound(online.getLocation(), SoundUtil.NOTE_BASS.getSound(), 1F, note1.getPitch());
                            }else{
                                online.playSound(online.getLocation(), SoundUtil.NOTE_PIANO.getSound(), 1F, note3.getPitch());
                                online.playSound(online.getLocation(), SoundUtil.NOTE_PIANO.getSound(), 1F, note4.getPitch());
                                online.playSound(online.getLocation(), SoundUtil.NOTE_BASS.getSound(), 1F, note3.getPitch());
                                online.playSound(online.getLocation(), SoundUtil.NOTE_BASS.getSound(), 1F, note4.getPitch());
                            }
                            TitleUtil.sendTitle(online, 0, 20, 0, displayName, "§e奖励 #" + getOrder(first, second, third) + ": " + color + Method.toTrisection(amount) + " " + name);
                        }
                        tick++;
                    }catch (IllegalArgumentException | IndexOutOfBoundsException ex){
                        for(Player online : Bukkit.getOnlinePlayers()){
                            int prize = getPrize(online, count);
                            online.sendMessage("§6§m----------------------");
                            online.sendMessage("§6活动已结束: 抽彩");
                            online.sendMessage("§6你的奖励: " + color + "+" + Method.toTrisection(prize) + " " + name);
                            online.sendMessage("§6本次总奖金: " + color  + Method.toTrisection(count) + " " + name);
                            if(first != null) {
                                online.sendMessage("§e  #1 " + color + Method.toTrisection(count / 2) + " " + name + "§f: " + getDisplayName(first));
                            }else{
                                online.sendMessage("§e  #1 §7未知");
                            }
                            if(second != null){
                                online.sendMessage("§e  #2 " + color + Method.toTrisection((count / 2) / 3 * 2) + " " + name + "§f: " + getDisplayName(second));
                            }else{
                                online.sendMessage("§e  #2 §7未知");
                            }
                            online.sendMessage("§e  #3 " + color + Method.toTrisection((count / 2) / 3) + " " + name + "§f: " + getDisplayName(third));
                            online.sendMessage("§6§m----------------------");
                            if(prize >= 0){
                                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(online, command).replace("{amount}", String.valueOf(prize)));
                                });
                            }
                        }
                        this.cancel();
                        first = null;
                        second = null;
                        third = null;
                        task = null;
                    }
                }
            }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 2L);
        }catch (IllegalArgumentException ex){
            sender.sendMessage("§c请输入颜色！ 可用颜色: ");
            sender.sendMessage(Joiner.on("§c, ").join(
                    Arrays.stream(ChatColor.values())
                            .map(chatColor -> chatColor.toString() + chatColor.name()).collect(Collectors.toList())));
        }
    }

    private Player getRandomPlayer(){
        List<Player> list = new ArrayList<Player>(Bukkit.getOnlinePlayers()).stream()
                .filter(p -> first != p.getUniqueId() && second != p.getUniqueId() && third != p.getUniqueId())
                .collect(Collectors.toList());
        return list.get(new Random().nextInt(list.size()));
    }

    private String getDisplayName(UUID uuid){
        Player p = Bukkit.getPlayer(uuid);
        return PlaceholderAPI.setPlaceholders(p, "%profile_prefix%" + p.getName() + "%profile_suffix%");
    }

    private int getPrize(Player p, int count){
        if(first == p.getUniqueId()){
            return count / 2;
        } else if(second == p.getUniqueId()){
            return (count / 2) / 3 * 2;
        } else if(third == p.getUniqueId()){
            return (count / 2) / 3;
        }
        return 0;
    }

    private int getOrder(UUID first, UUID second, UUID third){
        return third == null ? 3 : second == null ? 2 : first == null ? 1 : 0;
    }

    private int getAmount(int amount){
        if(third == null){
            return (amount / 2) / 3;
        }else if(second == null){
            return (amount / 2) / 3 * 2;
        } else if(first == null){
            return amount / 2;
        }
        return 0;
    }

}
