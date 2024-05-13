package hu.kiss.seeder.action;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.kiss.seeder.client.EmbyClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.data.TorrentComposite;

public class DeleteEpisodeAction extends BaseAction {

	private static Logger logger = LogManager.getLogger();
	private QbitorrentClient qClient;
	private EmbyClient embyClient;
	private final String TAG = "tartós";
	private static final List<String> VIDEO_EXTENSIONS = List.of("mkv", "mp4", "avi", "wmv");
	private static final List<String> CATEGORIES = List.of("Álommeló","Capak kozott");

	public DeleteEpisodeAction(QbitorrentClient qClient, EmbyClient embyClient) {
		super(qClient);
		this.qClient = qClient;
		this.embyClient = embyClient;
	}

	@Override
	public void execute(TorrentComposite torrent) {
		logger.debug("Start handle: nev - " + torrent.getNev() + " status - " + torrent.getBitStatus() + " id - "+ torrent.getId());
		if (CATEGORIES.contains(torrent.getCategory()) && torrent.getTags().contains(TAG)) {
			Boolean allWatched = true;
			for (String episode : qClient.getFiles(torrent.getId())) {

				var extension = episode.split("\\.")[episode.split("\\.").length-1];
;
				if (VIDEO_EXTENSIONS.contains(extension) && !episode.endsWith("sample."+extension) && !embyClient.isWatched(episode.replace("downloads", "mnt/share1"))) {
					allWatched = false;
				}
			}

			if (allWatched) {
				logger.info("Remove tartos tag from" + torrent.getNev());
				qClient.removeTag(torrent.getId(), TAG);
			}
		}
	}

}
