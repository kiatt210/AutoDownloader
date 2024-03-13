package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.TorrentComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WarnedAction extends BaseAction{

    private static Logger logger = LogManager.getLogger();

    public WarnedAction(QbitorrentClient qClient) {
        super(qClient);
    }

    @Override
    public void execute(TorrentComposite torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getBitStatus()+" id - "+torrent.getId());
        if(torrent.getBitStatus().equals(Status.QUEUED)){
            if( torrent.getNcoreTorrent() != null && torrent.getNcoreTorrent().getId() != 0 && torrent.getNcoreTorrent().isWarn()){
                logger.info(formatLog("Set super seed",torrent));
                qClient.superSeed(torrent.getId());
                qClient.forceStart(torrent.getId());
            }

        }
    }
}
