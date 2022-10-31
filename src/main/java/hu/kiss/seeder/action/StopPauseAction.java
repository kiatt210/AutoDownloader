package hu.kiss.seeder.action;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.Torrent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StopPauseAction implements Action{

    private static Logger logger = LogManager.getLogger();
    private static final List<Status> deletableStatuses = Collections.unmodifiableList(Arrays.asList(Status.SEED,Status.PAUSED,Status.ERROR));

    private NCoreClient nClientKiatt;
    private NCoreClient nClientDake;
    private QbitorrentClient qClient;
    private Boolean kiattHrContains;
    private Boolean dakeHrContains;

    @Override
    public void init(Object... params) {
        Assert.assertEquals("StopPauseAction init error, missing parameter!",3, params.length);
        nClientKiatt = (NCoreClient) params[0];
        nClientDake = (NCoreClient) params[1];
        qClient = (QbitorrentClient) params[2];
    }

    @Override
    public void execute(DelugeTorrent torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getStatus()+" id - "+torrent.getId());

        if(deletableStatuses.contains(torrent.getStatus()) || torrent.getStatus().equals(Status.PAUSED)){

            if (deletableStatuses.contains(torrent.getStatus())
                    && !qClient.getTartosIds().contains(torrent.getId())
                    && !checkKiattHr(torrent)
                    && !checkDakeHr(torrent)
            ) {
                logger.info("Remove - "+torrent.getNev());
                qClient.removeTorrent(torrent.getId());
            }
            else if(!torrent.getStatus().equals(Status.PAUSED)
                    && deletableStatuses.contains(torrent.getStatus())
                    && !checkKiattHr(torrent)
                    && !checkDakeHr(torrent)
            ){
                logger.info("Stop - "+torrent.getNev());
                qClient.pauseTorrent(torrent.getId());
            }
            else if(torrent.getStatus().equals(Status.PAUSED)
                    && ( checkKiattHr(torrent) || checkDakeHr(torrent))){
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

        dakeHrContains =null;
        kiattHrContains=null;

    }

    private boolean checkDakeHr(DelugeTorrent torrent) {
        if(dakeHrContains == null){
            dakeHrContains = nClientDake.getHrTorrents().contains(new Torrent(torrent.getNev()));
        }

        return dakeHrContains;
    }

    private boolean checkKiattHr(DelugeTorrent torrent) {
        if(kiattHrContains == null){
            kiattHrContains = nClientKiatt.getHrTorrents().contains(new Torrent(torrent.getNev()));
        }

        return kiattHrContains;
    }
}
