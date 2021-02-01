package net.blastmc.onyx.api.plugin;

public interface VersionControl {

    boolean isLatest(String version);

    void download(String version, String path);

}
