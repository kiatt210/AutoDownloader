package hu.kiss.seeder.client.deluge;

import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.mongo.TorrentDb;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DTorrentStore extends Observable {

    private List<DelugeTorrent> torrents;

    public DTorrentStore(){
        this.torrents = new ArrayList<>();
    }

    public void add(DelugeTorrent torrent){
        this.torrents.add(torrent);
        setChanged();
        notifyObservers(torrent);
    }

    public List<DelugeTorrent> getTorrents(){
        return torrents;
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }
}
