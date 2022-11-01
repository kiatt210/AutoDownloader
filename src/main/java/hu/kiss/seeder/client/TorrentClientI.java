package hu.kiss.seeder.client;

import hu.kiss.seeder.data.BitTorrent;

import java.util.List;
import java.util.Map;

public interface TorrentClientI {
    public void populateSeededTorrents(String filter);
    public void populateSeededTorrents();
    public void addTorrent(String torrentFile);
    public void addTorrent(String torrentFile, Map<String,String> params);
    public void removeTorrent(String id);
    public void pauseTorrent(String id);
    public List<BitTorrent> getSeededTorrents();
    public Boolean getIsUpdatable();
    public List<String> getTartosIds();
    public int getRunningSize();
    public void resumeTorrent(String id);

    public void superSeed(String id);

    public void forceStart(String id);

    int getCountByCategory(String category);
}
