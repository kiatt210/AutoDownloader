package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.TorrentComposite;

public interface Action {

    public void execute(TorrentComposite torrent);
}
