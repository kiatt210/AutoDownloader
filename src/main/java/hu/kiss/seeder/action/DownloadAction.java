package hu.kiss.seeder.action;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.TorrentComposite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadAction extends BaseAction{

    private static Logger logger = LogManager.getLogger();
    public static final int MAX_ADDABLE_PER_USER = 18;
    private static int limit = 35;

    public DownloadAction(QbitorrentClient qClient) {
        super(qClient);

        int addable = MAX_ADDABLE_PER_USER - qClient.getRunningSize();
        addable = Math.max(addable,0);
        logger.info("Addable: " + addable);
        int maxAddable = addable == 0 ? 0 : addable / 2;
        logger.info("Max addable: " + maxAddable);
    }

    @Override
    public void execute(TorrentComposite torrent) {
        logger.debug("Start handle: nev - " + torrent.getNev()+" status - "+torrent.getBitStatus()+" id - "+torrent.getId());
        if(torrent.getNcoreTorrent() != null && torrent.getId().isEmpty()){

            if (qClient.getIsUpdatable() && qClient.getRunningSize() < limit) {

                String fileName = torrent.download();
                if(fileName != ""){
                    qClient.addTorrent(NCoreClient.DOWNLOAD_LOCATION + fileName);
                    logger.info(formatLog("Download",torrent));
                }
            }


        }
    }
}
