package net.blastmc.onyx.api.plugin;

public interface VersionControl {

    boolean isLatest(String md5);

    void download(String version, String path);

}
