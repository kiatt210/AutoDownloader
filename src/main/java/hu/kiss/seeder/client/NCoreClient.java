/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client;

import hu.kiss.seeder.auth.Secret;
import hu.kiss.seeder.client.utils.HTMLUtils;
import hu.kiss.seeder.client.utils.HTTPUtils;
import hu.kiss.seeder.data.Torrent;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jBittorrentAPI.BDecoder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author KICSI
 */
public class NCoreClient {

    private static final String LOGIN_URL = "https://ncore.pro/login.php";
    private static final String HOME_URL = "https://ncore.pro/index.php";
    private static final String HR_URL = "https://ncore.pro/hitnrun.php";
    private static final String BASE_URL = "https://ncore.pro/";
    public static /*final*/ String DOWNLOAD_LOCATION = File.separatorChar+"home"+File.separatorChar+"seeder"+File.separatorChar+"torrents"+File.separatorChar;
    private String downloadLink = "torrents.php?action=download&id=${id}&key=${passkey}";

    private static Logger logger = LogManager.getLogger();

    private HttpClient client;
    private HTTPUtils httpUtils;
    private String phpSessionId;
    private String logoutUrl;
    private String userName;

    private int running = 0;

    private List<Torrent> hrTorrents = new ArrayList<Torrent>();

    public void login(Secret secret) {
        this.userName = secret.getUsername();
        this.downloadLink = downloadLink.replace("${passkey}", secret.getKey());
        this.httpUtils = new HTTPUtils();
        try {
            doLogin(secret.getPassword());
        } catch (IOException ex) {
            logger.error("Error while logging:" + ex.getMessage());
        }
    }

    private void doLogin(String password) throws UnsupportedEncodingException, IOException {

        HashMap params = new HashMap<String, String>();
        params.put("nev", userName);
        params.put("pass", password);
        params.put("set_lang", "hu");
        params.put("submitted", "1");

        HttpResponse response = httpUtils.doPost(LOGIN_URL, params);
        logger.debug(httpUtils.getContent(response));
        logger.debug("Status code: " + response.getStatusLine().getStatusCode());

        setPhpSession(response);

        logger.info(userName+" has logged in");
    }

    private void setPhpSession(HttpResponse response) {
        //get all headers
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equals("Set-Cookie") && header.getValue().startsWith("PHPSESSID=")) {
                phpSessionId = header.getValue().split("PHPSESSID=")[1].replace("; path=/", "");
                logger.debug("PHPSESSID=" + phpSessionId);
            }
        }
    }

    public void logout() {
        logger.info(userName+" has logged out");
        httpUtils.doGet(BASE_URL + logoutUrl, getDefaultHeader());
    }

    private HashMap<String, String> getDefaultHeader() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", "PHPSESSID=" + phpSessionId + "; nyelv=hu; stilus=default");
        return headers;
    }

    public void populateHrTorrents(){
        logger.debug("Populate hit&run torrents");
        HttpResponse response = httpUtils.doGet(HR_URL, getDefaultHeader());

        String content = httpUtils.getContent(response);

        Document doc = Jsoup.parseBodyFragment(content);

        //Elmentjük a kijelentkezés linkjét, mert ez sessiononként egyedi
        logoutUrl = doc.select("a#menu_11").attr("href");
        logger.debug("Logout url:" + logoutUrl);

        logger.info("Find torrents of "+this.userName);
        HTMLUtils.findTorrents(doc, hrTorrents);
        setTorrentName(hrTorrents);
        //setInfoBarImg(hrTorrents);
    }

    private void setInfoBarImg(List<Torrent> hrTorrents) {
        logger.info("setInfoBarImg");
        for (Torrent torrent: hrTorrents) {
            HttpResponse response = httpUtils.doGet(BASE_URL+torrent.getPageHREF(),getDefaultHeader());
            String content = httpUtils.getContent(response);

            try{
                torrent.setInforBarImg(Jsoup.parse(content).selectFirst(".inforbar_img img").attr("src"));
            }
            catch(Exception e){
                logger.error("Infobar image not found!");
            }

        }

    }

    private void setTorrentName(List<Torrent> hrTorrents) {

        try(var executor = Executors.newVirtualThreadPerTaskExecutor()){

            hrTorrents
                    .parallelStream()
                    .forEach( t ->{

                        executor.execute(() -> {

                            String fajlNev = "";
                            try{
                                logger.debug("Start download");
                                fajlNev = download(t);
                                logger.debug("Finish download");
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                return;
                            }

                            if(fajlNev == "") return;
                            logger.debug("Start get name");
                            String name = null;
                            try {
                                name = getTorrentName(DOWNLOAD_LOCATION+fajlNev);
                            } catch (IOException e) {
                            }
                            logger.debug("Finish get name");

                            t.setTorrentNev(name);
                        });

                    });

            executor.shutdown();

        }

    }

    public synchronized String download(Torrent torrent) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            HttpResponse response = httpUtils.doGet(BASE_URL + downloadLink.replace("${id}", torrent.getId() + ""), getDefaultHeader());
            Header[] heads = response.getHeaders("content-disposition");
            if (heads.length < 1) {
                response.getEntity().getContent().close();
                return "";
            }
            String filename = heads[0].getValue().replace("attachment; filename=\"", "").replace("\"", "");
            is = response.getEntity().getContent();
            String filePath = DOWNLOAD_LOCATION + filename;

            if(!new File(filePath).exists()){
                fos = new FileOutputStream(new File(filePath));
                int inByte;
                while ((inByte = is.read()) != -1) {
                    fos.write(inByte);
                }
            }

            return filename;
        } catch (IOException | UnsupportedOperationException ex) {
            logger.error(ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (fos != null) {
                    fos.close();
                }

            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return "";
    }

    private String getTorrentName(String fileLocation) throws IOException {
        BufferedInputStream torrentFilteStream = new BufferedInputStream(new FileInputStream(fileLocation));
        Map torrentInfo = BDecoder.decode(torrentFilteStream);
        Map infoMap = (Map) torrentInfo.get("info");
        String name = new String((byte[])infoMap.get("name"));
        torrentFilteStream.close();
        logger.debug("name: " + name);
        return name;
    }

    public List<Torrent> search(String query) {
        logger.info("Search by query:"+query);
        Map<String,String> params = new HashMap<>();
        params.put("nyit_filmek_resz","true");
        params.put("nyit_sorozat_resz","true");
        params.put("mire",query);
        params.put("miben","name");
        params.put("tipus","all_own");
        HttpResponse response = httpUtils.doPost(BASE_URL+"torrents.php",params);

        String content = httpUtils.getContent(response);

        Document doc = Jsoup.parseBodyFragment(content);
        Elements torrentBoxes = doc.select(".box_torrent");
        logger.info("Torrent size:"+torrentBoxes.size());

        ArrayList<Torrent> result = new ArrayList<>(torrentBoxes.size());
        for(Element element : torrentBoxes){
            Element link = element.selectFirst(".torrent_txt>a");
            String idStr = element.select(".box_nev2>div").attr("id").replace("borito","");
            Torrent t = new Torrent(link.attr("title"));
            t.setId(Integer.parseInt(idStr));
            t.setPageHREF(link.attr("href"));
            logger.debug("Found:"+t);
            result.add(t);

        }

        return result;
    }

    public List<Torrent> searchByImd(String imdbId){
        logger.info("Search by query:"+imdbId);
        Map<String,String> params = new HashMap<>();
        params.put("nyit_filmek_resz","true");
        params.put("nyit_sorozat_resz","true");
        params.put("mire",imdbId);
        params.put("miben","imdb");
        params.put("tipus","all_own");
        HttpResponse response = httpUtils.doPost(BASE_URL+"torrents.php",params);

        String content = httpUtils.getContent(response);

        Document doc = Jsoup.parseBodyFragment(content);
        Elements torrentBoxes = doc.select(".box_torrent");
        logger.info("Torrent size:"+torrentBoxes.size());

        ArrayList<Torrent> result = new ArrayList<>(torrentBoxes.size());
        for(Element element : torrentBoxes){
            Element link = element.selectFirst(".torrent_txt>a");
            String idStr = element.select(".box_nev2>div").attr("id").replace("borito","");
            Torrent t = new Torrent(link.attr("title"));
            t.setId(Integer.parseInt(idStr));
            t.setPageHREF(link.attr("href"));
            logger.debug("Found:"+t);
            result.add(t);

        }

        return result;
    }


    public List<Torrent> getHrTorrents() {
        return hrTorrents;
    }

    public void addRun(){
        running++;
    }

    public void removeRun(){
        running--;
    }

    public int getRunning(){
        return running;
    }

    public String getUserName(){
        return userName;
    }

    public Optional<Torrent> getBest(List<Torrent> torrents) {

        Optional<Torrent> result;

        result = find(torrents,"720p");

        if(!result.isPresent()){
            result = find(torrents,"1080p");
            if(!result.isPresent()){
                return torrents.size() >0 ? Optional.of(torrents.get(0)) : Optional.empty();
            }
        }

        return result;
    }

    private Optional<Torrent> find(List<Torrent> torrents, String resolution){
        for (Torrent t: torrents) {
            if(t.getTorrentNev().contains(resolution)){
                return Optional.of(t);
            }
        }

        return Optional.empty();
    }

}