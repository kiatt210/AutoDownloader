package hu.kiss.seeder.data;

import hu.kiss.seeder.client.NCoreClient;

import java.time.LocalDateTime;
import java.util.List;

public class TorrentComposite {

    private Torrent ncoreTorrent;
    private BitTorrent bitTorrent;

    private NCoreClient nCoreClient;

    public TorrentComposite(Torrent ncoreTorrent, BitTorrent bitTorrent, NCoreClient nCoreClient) {
        this.ncoreTorrent = ncoreTorrent;
        this.bitTorrent = bitTorrent;
        this.nCoreClient = nCoreClient;
    }

    public static TorrentComposite create(Torrent t, List<BitTorrent> bitTorrents, NCoreClient nCoreClient){
        return new TorrentComposite(t,
                bitTorrents.stream()
                        .filter( q -> q.getNev().equals(t.getTorrentNev()))
                        .findAny()
                        .orElse(null)
        ,nCoreClient);
    }

    public Torrent getNcoreTorrent() {
        return ncoreTorrent;
    }

    public String getId(){
        if(bitTorrent != null){
            return bitTorrent.getId();
        }
        return "";
    }

    public String getNev(){
        if(bitTorrent != null){
            return bitTorrent.getNev();
        }
        return "";
    }

    public LocalDateTime getAdditionDate(){
        if(bitTorrent != null){
            return bitTorrent.getAdditionDate();
        }
        return LocalDateTime.MIN;
    }

    public String getCategory(){
        if(bitTorrent != null){
            return bitTorrent.getCategory();
        }
        return "";
    }

    public Status getBitStatus() {
        if(bitTorrent != null){
            return bitTorrent.getStatus();
        }
        return Status.NOT_EXIST;
    }

    public String getNcoreStatus(){
        if(ncoreTorrent != null){
            return ncoreTorrent.getStatus();
        }

        return null;
    }

    public String download() {
        return nCoreClient.download(ncoreTorrent);
    }

    public List<String> getTags() {
	return bitTorrent.getTags();
    }
}
