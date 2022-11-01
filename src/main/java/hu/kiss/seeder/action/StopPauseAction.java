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

    @Override
    public void execute(TorrentComposite torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getBitTorrent().getStatus()+" id - "+torrent.getId());

        if(deletableStatuses.contains(torrent.getBitTorrent().getStatus()) || torrent.getBitTorrent().getStatus().equals(Status.PAUSED)){

            if (deletableStatuses.contains(torrent.getBitTorrent().getStatus())
                    && !qClient.getTartosIds().contains(torrent.getId())
                    && torrent.getNcoreTorrent().getStatus() == null
            ) {
                logger.info("Remove - "+torrent.getNev());
                qClient.removeTorrent(torrent.getId());
            }
            else if(!torrent.getBitTorrent().getStatus().equals(Status.PAUSED)
                    && deletableStatuses.contains(torrent.getBitTorrent().getStatus())
                    && torrent.getNcoreTorrent().getStatus() == null
            ){
                logger.info("Stop - "+torrent.getNev());
                qClient.pauseTorrent(torrent.getId());
            }
            else if(torrent.getBitTorrent().getStatus().equals(Status.PAUSED)
                    && torrent.getNcoreTorrent().getStatus() != null ){
                logger.info("Resume - "+torrent.getNev());
                qClient.resumeTorrent(torrent.getId());
            }
            else {
                logger.debug("Torrent not changed: " + torrent.getNev());
            }
        }
        else {
            logger.debug("Torrent not changed: " + torrent.getNev());
        }

    }

}
