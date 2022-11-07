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

    public WarnedAction(QbitorrentClient qClient) {
        super(qClient);
    }

    @Override
    public void execute(TorrentComposite torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getStatus()+" id - "+torrent.getId());
        if(torrent.getStatus().equals(Status.QUEUED)){
            if( torrent.getNcoreTorrent().getId() != 0 && torrent.getNcoreTorrent().isWarn()){
                logger.info(formatLog("Set super seed",torrent));
                qClient.superSeed(torrent.getId());
                qClient.forceStart(torrent.getId());
            }

        }
    }
}
