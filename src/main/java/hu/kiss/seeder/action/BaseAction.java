package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.client.TorrentClientI;
import hu.kiss.seeder.data.TorrentComposite;

public abstract class BaseAction implements Action {

    protected QbitorrentClient qClient;

    @Override
    public void init(QbitorrentClient client) {
        qClient = client;
    }

    protected String formatLog(String msg, TorrentComposite torrent){
        return msg+" - "+torrent.getNev();
    }
}
