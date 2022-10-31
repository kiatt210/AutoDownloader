package hu.kiss.seeder.action;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.client.TorrentClientI;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.Torrent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.util.stream.Stream;

public class WarnedAction implements Action{

    private static Logger logger = LogManager.getLogger();
    private NCoreClient nClientKiatt;
    private NCoreClient nClientDake;
    private TorrentClientI qClient;

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
        if(torrent.getStatus().equals(Status.QUEUED)){
            Stream.concat(nClientKiatt.getHrTorrents().stream(),nClientDake.getHrTorrents().stream())
                    .filter( Torrent::isWarn)
                    .filter( t -> t.getTorrentNev() != null && t.getTorrentNev().contentEquals(torrent.getNev()))
                    .forEach(t -> {
                        logger.info("Set super seed - "+torrent.getNev());
                        qClient.superSeed(torrent.getId());
                        qClient.forceStart(torrent.getId());
                    });
        }
    }
}
