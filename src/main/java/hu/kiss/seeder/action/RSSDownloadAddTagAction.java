package hu.kiss.seeder.action;

import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.TorrentComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

import java.time.LocalDateTime;

public class RSSDownloadAddTagAction extends BaseAction{

    private static Logger logger = LogManager.getLogger();
    private final String TAG="tartós";

    @Override
    public void execute(TorrentComposite torrent) {
        if(!qClient.getTartosIds().contains(torrent.getId()) //Nem tartós
                && torrent.getAdditionDate().isAfter(LocalDateTime.now().minusDays(1)) //1 napon belül lett létrehozva
                && torrent.getCategory() != null
                && !torrent.getCategory().isEmpty()//Van kategóriája
                ){
            logger.info(formatLog("Add tartós tag",torrent));
            qClient.addTag(torrent.getId(),TAG);
        }
    }
}
