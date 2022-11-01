package hu.kiss.seeder.mongo;

import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.Torrent;

import java.util.Observable;
import java.util.Observer;

public class DBSynchronizer implements Observer {

    private TorrentDb db;
    private int ind = 0;

    public DBSynchronizer() {
        this.db = new TorrentDb();
        db.truncate();
    }

    public void update(Observable o, Object arg) {
        BitTorrent torrent = (BitTorrent) arg;
        Torrent t = new Torrent(torrent.getNev());
        t.setInforBarImg("https://pngimage.net/wp-content/uploads/2018/06/why-icon-png-4.png");
        t.setId(ind++);
        db.insert(t);
        System.out.println("New torrent " + arg);
    }

}
