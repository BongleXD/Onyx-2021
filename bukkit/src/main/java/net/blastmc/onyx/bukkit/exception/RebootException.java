package net.blastmc.onyx.bukkit.exception;

public class RebootException extends Throwable {

    public RebootException(){
        super("§c正在执行服务器重启！");
    }
}
