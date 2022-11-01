package hu.kiss.seeder.data;

import java.time.LocalDateTime;
import java.util.List;

public class TorrentComposite {

    private Torrent ncoreTorrent;
    private BitTorrent bitTorrent;

    public TorrentComposite(Torrent ncoreTorrent, BitTorrent bitTorrent) {
        this.ncoreTorrent = ncoreTorrent;
        this.bitTorrent = bitTorrent;
    }

    public static TorrentComposite create(Torrent t, List<BitTorrent> bitTorrents){
        return new TorrentComposite(t,
                bitTorrents.stream()
                        .filter( q -> q.getNev().equals(t.getTorrentNev()))
                        .findAny()
                        .orElse(new BitTorrent()));
    }

    public Torrent getNcoreTorrent() {
        return ncoreTorrent;
    }

    public BitTorrent getBitTorrent() {
        return bitTorrent;
    }

    public String getId(){
        return bitTorrent.getId();
    }

    public String getNev(){
        return bitTorrent.getNev();
    }

    public LocalDateTime getAdditionDate(){
        return bitTorrent.getAdditionDate();
    }

    public String getCategory(){
        return bitTorrent.getCategory();
    }
}
