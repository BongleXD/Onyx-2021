package net.blastmc.onyx.bukkit.command.admin;

import net.blastmc.onyx.bukkit.Main;
import net.blastmc.onyx.bukkit.command.CommandManager;
import net.blastmc.onyx.bukkit.utils.NoteUtil;
import net.blastmc.onyx.bukkit.utils.interact.SoundUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayNote extends CommandManager {

    public PlayNote() {
        super("playnote", "测试音效", "/playnote", "onyx.command.playnote");
        this.setPermissionMessage("§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！");
    }

    @Cmd(perm = "onyx.command.playnote", permMessage = "§c你需要 §cADMIN §c及以上的会员等级才能使用此指令！", only = CommandOnly.PLAYER)
    public void playNote(CommandSender sender, String[] args){
        Player p = (Player) sender;
        new BukkitRunnable() {
            NoteUtil note = NoteUtil.A3;
            @Override
            public void run() {
                if(note == null){
                    this.cancel();
                }
                p.playSound(p.getLocation(), SoundUtil.NOTE_PIANO.getSound(), 1F, note.getPitch());
                note = note.getNextNote();
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 0L, 5L);
    }

}
