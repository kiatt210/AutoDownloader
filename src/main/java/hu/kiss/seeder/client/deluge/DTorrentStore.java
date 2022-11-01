package hu.kiss.seeder.client.deluge;

import hu.kiss.seeder.data.BitTorrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class DTorrentStore extends Observable {

    private List<BitTorrent> torrents;

    public DTorrentStore(){
        this.torrents = new ArrayList<>();
    }

    public void add(BitTorrent torrent){
        this.torrents.add(torrent);
        setChanged();
        notifyObservers(torrent);
    }

    public List<BitTorrent> getTorrents(){
        return torrents;
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }
}
