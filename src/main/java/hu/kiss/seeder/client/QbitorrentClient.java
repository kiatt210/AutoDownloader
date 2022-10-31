package hu.kiss.seeder.client;

import com.fasterxml.jackson.core.type.TypeReference;
import hu.kiss.seeder.client.qbit.GeneralInfo;
import hu.kiss.seeder.client.qbit.HTTPUtils;
import hu.kiss.seeder.client.qbit.TrackerInfo;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Status;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class QbitorrentClient implements TorrentClientI{

    private String sessionId;
    private final String urlString = "http://192.168.0.20:8112";

    public static final ArrayList<String> states = new ArrayList<>(Arrays.asList("all", "downloading", "completed", "seeding", "pause", "active", "inactive"));
    private List<String> tartosIds;
    List<DelugeTorrent> seededTorrents;

    public QbitorrentClient(){
        login();
    }

    private void login() {
        String username="admin";
        String password="Agi123";
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
        List<GeneralInfo> torrents = HTTPUtils.getRequest(urlString + "/api/v2/torrents/info?filter=" + filter, sessionId, new TypeReference<List<GeneralInfo>>() {
        }, true);

        seededTorrents = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            torrents.parallelStream().forEach(
                    t -> {
                        executor.execute(() -> {
                            DelugeTorrent torrent = new DelugeTorrent();
                            torrent.setNev(t.getName());
                            torrent.setId(t.getHash());
                            torrent.setAdditionDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(t.getAdded_on()), ZoneId.systemDefault()));
                            torrent.setCategory(t.getCategory());
                            switch (t.getState()) {
                                case "uploading":
                                    torrent.setStatus(Status.SEED);
                                    break;
                                case "pausedUP":
                                    torrent.setStatus(Status.PAUSED);
                                    break;
                                case "queuedUP":
                                    torrent.setStatus(Status.QUEUED);
                                    break;
                                case "stalledUP":
                                    torrent.setStatus(Status.SEED);
                                    break;
                                case "checkingUP":
                                    torrent.setStatus(Status.CHECKING);
                                    break;
                                case "forcedUP":
                                    torrent.setStatus(Status.SEED);
                                    break;
                                case "metaDL":
                                    torrent.setStatus(Status.DOWNLOAD);
                                    break;
                                case "pausedDL":
                                    torrent.setStatus(Status.PAUSED);
                                    break;
                                case "queuedDL":
                                    torrent.setStatus(Status.QUEUED);
                                    break;
                                case "stalledDL":
                                    torrent.setStatus(Status.DOWNLOAD);
                                    break;
                                case "checkingDL":
                                    torrent.setStatus(Status.CHECKING);
                                    break;
                                case "forcedDL":
                                    torrent.setStatus(Status.DOWNLOAD);
                                    break;
                                case "checkingResumeData":
                                    torrent.setStatus(Status.CHECKING);
                                    break;
                                case "moving":
                                    torrent.setStatus(Status.PAUSED);
                                    break;
                                case "downloading":
                                    torrent.setStatus(Status.DOWNLOAD);
                                    break;
                                case "pause":
                                    torrent.setStatus(Status.PAUSED);
                                    break;
                                case "error":
                                    torrent.setStatus(Status.ERROR);
                                    break;
                                case "missingFiles":
                                    torrent.setStatus(Status.ERROR);
                                    break;
                                default:
                                    torrent.setStatus(Status.ERROR);
                                    break;
                            }
                            seededTorrents.add(torrent);
                        });
                    }
            );
            executor.shutdown();
    }
    }

    @Override
    public void populateSeededTorrents() {
        populateSeededTorrents("all");
    }

    @Override
    public void addTorrent(String torrentFile) {
        addTorrent(torrentFile,Collections.emptyMap());
    }

    @Override
    public void addTorrent(String torrentFile, Map<String, String> params) {
        File file = new File(torrentFile);
        HTTPUtils.postFileRequest(urlString + "/api/v2/torrents/add",sessionId,file,params);
    }

    @Override
    public void removeTorrent(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("hashes",id);
        parameters.put("deleteFiles", "true");
        HTTPUtils.postRequest(urlString + "/api/v2/torrents/delete", sessionId, parameters);
    }

    @Override
    public void pauseTorrent(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("hashes",id);
        HTTPUtils.postRequest(urlString + "/api/v2/torrents/pause", sessionId, parameters);
    }

    @Override
    public List<DelugeTorrent> getSeededTorrents() {
        return seededTorrents;
    }

    @Override
    public Boolean getIsUpdatable() {
        return sessionId != null;
    }

    @Override
    public List<String> getTartosIds() {

        if(tartosIds == null){

            List<GeneralInfo> info = HTTPUtils.getRequest(urlString + "/api/v2/torrents/info?filter=all", sessionId, new TypeReference<List<GeneralInfo>>() {},true);
            tartosIds = info.stream().filter(t -> t.getTags().contains("tartós")).map( f -> f.getHash()).collect(Collectors.toList());

        }

        return tartosIds;
    }

    @Override
    public int getRunningSize() {

        List<GeneralInfo> info = HTTPUtils.getRequest(urlString + "/api/v2/torrents/info?filter=seeding", sessionId, new TypeReference<List<GeneralInfo>>() {},true);

        return info.size();
    }

    @Override
    public void resumeTorrent(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("hashes",id);
        HTTPUtils.postRequest(urlString + "/api/v2/torrents/resume", sessionId, parameters);
    }

    @Override
    public void superSeed(String id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("hashes",id);
        parameters.put("value","true");
        HTTPUtils.postRequest(urlString + "/api/v2/torrents/setSuperSeeding", sessionId, parameters);
    }

    @Override
    public void forceStart(String id){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("hashes",id);
        parameters.put("value","true");
        HTTPUtils.postRequest(urlString + "/api/v2/torrents/setForceStart", sessionId, parameters);
    }

    @Override
    public int getCountByCategory(String category) {
        long count = this.seededTorrents.stream().filter(t -> t.getCategory().equals(category)).count();

        return Long.valueOf(count).intValue();
    }

    public void addTag(String id, String tag){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("tags",tag);
        parameters.put("hashes",id);
        HTTPUtils.postRequest(urlString + "/api/v2/torrents/addTags", sessionId, parameters);
    }

    public boolean isConnected(){
        return sessionId != null;
    }
}
