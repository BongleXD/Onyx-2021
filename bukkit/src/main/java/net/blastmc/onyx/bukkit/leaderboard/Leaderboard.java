package net.blastmc.onyx.bukkit.leaderboard;

import com.google.common.collect.Lists;

import java.util.List;

public class Leaderboard {

    private String title;
    private List<Page> pageList;
    private BoardType type;

    public Leaderboard(String title) {
        this.title = title;
        this.pageList = Lists.newArrayList();
    }

    public List<Page> getPageList() {
        return pageList;
    }

    public void addPage(Page page) {
        this.pageList.add(page);
    }

    public BoardType getType() {
        return type;
    }

    public void setType(BoardType type) {
        this.type = type;
    }

}
