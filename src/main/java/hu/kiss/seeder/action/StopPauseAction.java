package hu.kiss.seeder.action;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.Torrent;
import hu.kiss.seeder.data.TorrentComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StopPauseAction extends BaseAction{

    private static Logger logger = LogManager.getLogger();
    private static final List<Status> deletableStatuses = Collections.unmodifiableList(Arrays.asList(Status.SEED,Status.PAUSED,Status.ERROR));

    public StopPauseAction(QbitorrentClient qClient) {
        super(qClient);
    }

    @Override
    public void execute(TorrentComposite torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getStatus()+" id - "+torrent.getId());

        if(deletableStatuses.contains(torrent.getStatus()) || torrent.getStatus().equals(Status.PAUSED)){

            if (deletableStatuses.contains(torrent.getStatus())
                    && !qClient.getTartosIds().contains(torrent.getId())
                    && torrent.getNcoreTorrent().getStatus() == null
            ) {
                logger.info(formatLog("Remove",torrent));
                qClient.removeTorrent(torrent.getId());
            }
            else if(!torrent.getStatus().equals(Status.PAUSED)
                    && deletableStatuses.contains(torrent.getStatus())
                    && torrent.getNcoreTorrent().getStatus() == null
            ){
                logger.info(formatLog("Stop",torrent));
                qClient.pauseTorrent(torrent.getId());
            }
            else if(torrent.getStatus().equals(Status.PAUSED)
                    && torrent.getNcoreTorrent().getStatus() != null ){
                logger.info(formatLog("Resume",torrent));
                qClient.resumeTorrent(torrent.getId());
            }
            else {
                logger.debug(formatLog("Torrent not changed",torrent));
            }
        }
        else {
            logger.debug(formatLog("Torrent not changed",torrent));
        }

    }

}
