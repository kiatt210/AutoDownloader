package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.client.TorrentClientI;
import hu.kiss.seeder.data.TorrentComposite;

public abstract class BaseAction implements Action {

    protected QbitorrentClient qClient;


    /*Delete action-hez sqlite query
    
    API-ból lekérdezhetőek a fájl nevek:
    http://192.168.0.20:8112/api/v2/torrents/files?hash=e3f9413c71e2842c24668febad129189b8adac72
    */
    public BaseAction(QbitorrentClient qClient){
        this.qClient = qClient;
    }

    protected String formatLog(String msg, TorrentComposite torrent){
        return msg+" - "+torrent.getNev();
    }
}
