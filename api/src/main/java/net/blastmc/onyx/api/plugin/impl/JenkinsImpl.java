package net.blastmc.onyx.api.plugin.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import net.blastmc.onyx.api.plugin.VersionControl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JenkinsImpl implements VersionControl {

    private JobWithDetails job;
    private final String url;
    private final String user;
    private final String passwd;
    private JenkinsHttpClient client;
    private JenkinsServer server;

    public JenkinsImpl(String job, String url, String user, String passwd){
        this.url = url;
        this.user = user;
        this.passwd = passwd;
        try {
            this.client = new JenkinsHttpClient(new URI(this.url), this.user, this.passwd);
            this.server = new JenkinsServer(new URI(this.url), this.user, this.passwd);
            this.job = server.getJob(job);
        }catch (URISyntaxException | IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isLatest(String version) {
        return false;
    }

    @Override
    public void download(String version, String path) {
        Build stable = job.getLastStableBuild();
    }

}
