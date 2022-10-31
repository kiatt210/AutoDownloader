package hu.kiss.seeder.action;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.DelugeTorrent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.time.LocalDateTime;

public class RSSDownloadAddTagAction implements Action{

    private static Logger logger = LogManager.getLogger();
    private QbitorrentClient qClient;
    private final String TAG="tartós";

    @Override
    public void init(Object... params) {
        Assert.assertEquals("StopPauseAction init error, missing parameter!",1, params.length);
        qClient = (QbitorrentClient) params[0];
    }

    @Override
    public void execute(DelugeTorrent torrent) {
        if(!qClient.getTartosIds().contains(torrent.getId()) //Nem tartós
                && torrent.getAdditionDate().isAfter(LocalDateTime.now().minusDays(1)) //1 napon belül lett létrehozva
                && torrent.getCategory() != null
                && !torrent.getCategory().isEmpty()//Van kategóriája
                ){
            logger.info("Add tartós tag - "+torrent.getNev());
            qClient.addTag(torrent.getId(),TAG);
        }
    }
}
