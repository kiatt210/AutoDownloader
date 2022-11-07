package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.TorrentComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getBitStatus()+" id - "+torrent.getId());

        if(deletableStatuses.contains(torrent.getBitStatus()) || torrent.getBitStatus().equals(Status.PAUSED)){

            if (deletableStatuses.contains(torrent.getBitStatus())
                    && !qClient.getTartosIds().contains(torrent.getId())
                    && torrent.getNcoreStatus() == null
            ) {
                logger.info(formatLog("Remove",torrent));
                qClient.removeTorrent(torrent.getId());
            }
            else if(!torrent.getBitStatus().equals(Status.PAUSED)
                    && deletableStatuses.contains(torrent.getBitStatus())
                    && torrent.getNcoreStatus() == null
            ){
                logger.info(formatLog("Stop",torrent));
                qClient.pauseTorrent(torrent.getId());
            }
            else if(torrent.getBitStatus().equals(Status.PAUSED) &&
                    torrent.getNcoreStatus() != null ){
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
