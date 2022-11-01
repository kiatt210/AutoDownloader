package hu.kiss.seeder.action;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.client.TorrentClientI;
import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.Torrent;
import hu.kiss.seeder.data.TorrentComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.stream.Stream;

public class WarnedAction extends BaseAction{

    private static Logger logger = LogManager.getLogger();

    private TorrentClientI qClient;

    @Override
    public void execute(TorrentComposite torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getBitTorrent().getStatus()+" id - "+torrent.getId());
        if(torrent.getBitTorrent().getStatus().equals(Status.QUEUED)){
            if( torrent.getNcoreTorrent().getId() != 0 && torrent.getNcoreTorrent().isWarn()){
                logger.info("Set super seed - "+torrent.getNev());
                qClient.superSeed(torrent.getId());
                qClient.forceStart(torrent.getId());
            }

        }
    }
}
