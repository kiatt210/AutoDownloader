package hu.kiss.seeder.action;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.kiss.seeder.client.EmbyClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.config.ConfigStore;
import hu.kiss.seeder.data.TorrentComposite;

public class DeleteEpisodeAction extends BaseAction {

	private static Logger logger = LogManager.getLogger();
	private QbitorrentClient qClient;
	private EmbyClient embyClient;
	private ConfigStore configStore;
	private final String TAG = "tartós";

	public DeleteEpisodeAction(QbitorrentClient qClient, EmbyClient embyClient, ConfigStore configStore) {
		super(qClient);
		this.qClient = qClient;
		this.embyClient = embyClient;
		this.configStore = configStore;
	}

	@Override
	public void execute(TorrentComposite torrent) {
		logger.debug("Start handle: nev - " + torrent.getNev() + " status - " + torrent.getBitStatus() + " id - "+ torrent.getId());
		if (configStore.getEpisodeDeleteCategories().contains(torrent.getCategory()) && torrent.getTags().contains(TAG)) {
			Boolean allWatched = true;
			for (String episode : qClient.getFiles(torrent.getId())) {

				var extension = episode.split("\\.")[episode.split("\\.").length-1];

				if (configStore.getEpisodeDeleteExtensions().contains(extension) && !episode.endsWith("sample."+extension) && !embyClient.isWatched(episode.replace(configStore.getEpisodeDeleteFolderTorrent(), configStore.getEpisodeDeleteFolderEmby()))) {
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
