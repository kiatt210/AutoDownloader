package hu.kiss.seeder.client;

import com.fasterxml.jackson.core.type.TypeReference;

import hu.kiss.seeder.client.qbit.FileInfo;
import hu.kiss.seeder.client.qbit.GeneralInfo;
import hu.kiss.seeder.client.qbit.HTTPUtils;
import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class QbitorrentClient implements TorrentClientI {

	private static Logger logger = LogManager.getLogger();

	private String sessionId;
	private final String urlString = "http://192.168.0.20:8112";

	public static final ArrayList<String> states = new ArrayList<>(
			Arrays.asList("all", "downloading", "completed", "seeding", "pause", "active", "inactive"));
	private List<String> tartosIds;
	List<BitTorrent> seededTorrents;

	public QbitorrentClient() {
		login();
	}

	private void login() {
		String username = "admin";
		String password = "Agi123";
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);

		Map<String, String> parameters = new HashMap<>();
		parameters.put("username", username);
		parameters.put("password", password);

		HTTPUtils.postRequest(urlString + "/api/v2/auth/login", null, parameters);

		List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
		for (HttpCookie cookie : cookies) {
			if (cookie.getName().equals("SID")) {
				sessionId = cookie.getValue();
			}
		}
	}

	@Override
	public void populateSeededTorrents(String filter) {
		List<GeneralInfo> torrents = HTTPUtils.getRequest(urlString + "/api/v2/torrents/info?filter=" + filter,
				sessionId, new TypeReference<List<GeneralInfo>>() {
				}, true);

		seededTorrents = Collections.synchronizedList(new ArrayList<>());
		logger.debug("Start populate torrents");
		var executor = Executors.newCachedThreadPool();

		torrents.stream().forEach(
				t -> {
					executor.execute(() -> {
						BitTorrent torrent = new BitTorrent();
						torrent.setNev(t.getName());
						torrent.setId(t.getHash());
						torrent.setAdditionDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(t.getAdded_on()),
								ZoneId.systemDefault()));
						torrent.setCategory(t.getCategory());
						switch (t.getState()) {
							case "uploading":
							case "stalledUP":
							case "forcedUP":
								torrent.setStatus(Status.SEED);
								break;
							case "pausedUP":
							case "pausedDL":
							case "moving":
							case "pause":
								torrent.setStatus(Status.PAUSED);
								break;
							case "queuedUP":
							case "queuedDL":
								torrent.setStatus(Status.QUEUED);
								break;
							case "checkingUP":
							case "checkingDL":
							case "checkingResumeData":
								torrent.setStatus(Status.CHECKING);
								break;
							case "metaDL":
							case "stalledDL":
							case "forcedDL":
							case "downloading":
								torrent.setStatus(Status.DOWNLOAD);
								break;
							case "error":
							case "missingFiles":
							default:
								torrent.setStatus(Status.ERROR);
								break;
						}
						seededTorrents.add(torrent);
					});
				});
		logger.debug(
				"Client torrents: " + this.seededTorrents.size() + " : Rest response torrents: " + torrents.size());
	}

	@Override
	public void populateSeededTorrents() {
		populateSeededTorrents("all");
	}

	@Override
	public void addTorrent(String torrentFile) {
		addTorrent(torrentFile, Collections.emptyMap());
	}

	@Override
	public void addTorrent(String torrentFile, Map<String, String> params) {
		File file = new File(torrentFile);
		HTTPUtils.postFileRequest(urlString + "/api/v2/torrents/add", sessionId, file, params);
	}

	@Override
	public void removeTorrent(String id) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("hashes", id);
		parameters.put("deleteFiles", "true");
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/delete", sessionId, parameters);
	}

	@Override
	public void pauseTorrent(String id) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("hashes", id);
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/pause", sessionId, parameters);
	}

	@Override
	public List<BitTorrent> getSeededTorrents() {
		return seededTorrents;
	}

	@Override
	public Boolean getIsUpdatable() {
		return sessionId != null;
	}

	@Override
	public List<String> getTartosIds() {

		if (tartosIds == null) {

			List<GeneralInfo> info = HTTPUtils.getRequest(urlString + "/api/v2/torrents/info?filter=all", sessionId,
					new TypeReference<List<GeneralInfo>>() {
					}, true);
			tartosIds = info.stream().filter(t -> t.getTags().contains("tartós")).map(f -> f.getHash())
					.collect(Collectors.toList());

		}

		return tartosIds;
	}

	@Override
	public int getRunningSize() {

		List<GeneralInfo> info = HTTPUtils.getRequest(urlString + "/api/v2/torrents/info?filter=seeding", sessionId,
				new TypeReference<List<GeneralInfo>>() {
				}, true);

		return info.size();
	}

	@Override
	public void resumeTorrent(String id) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("hashes", id);
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/resume", sessionId, parameters);
	}

	@Override
	public void superSeed(String id) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("hashes", id);
		parameters.put("value", "true");
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/setSuperSeeding", sessionId, parameters);
	}

	@Override
	public void forceStart(String id) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("hashes", id);
		parameters.put("value", "true");
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/setForceStart", sessionId, parameters);
	}

	@Override
	public int getCountByCategory(String category) {
		long count = this.seededTorrents.stream().filter(t -> t.getCategory().equals(category)).count();

		return Long.valueOf(count).intValue();
	}

	public void addTag(String id, String tag) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("tags", tag);
		parameters.put("hashes", id);
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/addTags", sessionId, parameters);
	}

	public void removeTag(String id, String tag) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("tags", tag);
		parameters.put("hashes", id);
		HTTPUtils.postRequest(urlString + "/api/v2/torrents/removeTags", sessionId, parameters);
	}

	public List<String> getFiles(String id) {
		logger.debug("Id:"+id);
		List<FileInfo> result = HTTPUtils.getRequest(urlString + "/api/v2/torrents/files?hash=" + id, sessionId,new TypeReference<List<FileInfo>>() {
				}, true);
		if (result == null) {
		    return List.of();
		}
		return result.stream().map(FileInfo::getName).collect(Collectors.toList());

	}

	public boolean isConnected() {
		return sessionId != null;
	}
}
