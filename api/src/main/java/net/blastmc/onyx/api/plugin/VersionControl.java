package net.blastmc.onyx.api.plugin;

public interface VersionControl {

    boolean isLatest(String ver);

    void download(String version, String path);

}
