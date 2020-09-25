/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client;

import hu.kiss.seeder.client.utils.HTMLUtils;
import hu.kiss.seeder.client.utils.HTTPUtils;
import hu.kiss.seeder.data.Torrent;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import jBittorrentAPI.BDecoder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author KICSI
 */
public class NCoreClient {
    
    private static final String LOGIN_URL = "https://ncore.cc/login.php";
    private static final String HOME_URL = "https://ncore.cc/index.php";
    private static final String HR_URL = "https://ncore.cc/hitnrun.php";
    private static final String BASE_URL = "https://ncore.cc/";
    public static final String DOWNLOAD_LOCATION = "/home/seeder/torrents/";
    private String downloadLink = "torrents.php?action=download&id=${id}&key=${passkey}";

    private HttpClient client;
    private HTTPUtils httpUtils;
    private String phpSessionId;
    private String logoutUrl;
    private String userName;
    
    private int running = 0;

    private List<Torrent> hrTorrents = new ArrayList<Torrent>();

    public void login(String userName, String password, String passKey) {
        this.userName = userName;
        this.downloadLink = downloadLink.replace("${passkey}", passKey);
        this.httpUtils = new HTTPUtils();
        try {
            doLogin(password);
        } catch (IOException ex) {
            System.out.println("Error while logging:" + ex.getMessage());
        }
    }

    private void doLogin(String password) throws UnsupportedEncodingException, IOException {

        HashMap params = new HashMap<String, String>();
        params.put("nev", userName);
        params.put("pass", password);
        params.put("set_lang", "hu");
        params.put("submitted", "1");

        HttpResponse response = httpUtils.doPost(LOGIN_URL, params);

        System.out.println("Status code: " + response.getStatusLine().getStatusCode());

        setPhpSession(response);

        //Elmentjük a kijelentkezés linkjét, mert ez sessiononként egyedi     
        response = httpUtils.doGet(HOME_URL, getDefaultHeader());
        String responseStr = httpUtils.getContent(response);

        Document doc = Jsoup.parse(responseStr);
        logoutUrl = doc.select("a#menu_11").attr("href");
        System.out.println("Logout url:" + logoutUrl);
        System.out.println(userName+" has logged in");
    }

    private void setPhpSession(HttpResponse response) {
        //get all headers
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equals("Set-Cookie") && header.getValue().startsWith("PHPSESSID=")) {
                phpSessionId = header.getValue().split("PHPSESSID=")[1].replace("; path=/", "");
                System.out.println("PHPSESSID=" + phpSessionId);
            }
        }
    }

    public void logout() {
        System.out.println(userName+" has logged out");
        httpUtils.doGet(BASE_URL + logoutUrl, getDefaultHeader());
    }

    private HashMap<String, String> getDefaultHeader() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Cookie", "PHPSESSID=" + phpSessionId + "; nyelv=hu; stilus=default");
        return headers;
    }

    public void populateHrTorrents() throws IOException, InterruptedException {
        System.out.println("Populate hit&run torrents");
        HttpResponse response = httpUtils.doGet(HR_URL, getDefaultHeader());

        String content = httpUtils.getContent(response);

        Document doc = Jsoup.parse(content);
        HTMLUtils.findTorrents(doc, hrTorrents);
        setTorrentName(hrTorrents);
        setInfoBarImg(hrTorrents);
    }

    private void setInfoBarImg(List<Torrent> hrTorrents) {
        System.out.println("setInfoBarImg");
        for (Torrent torrent: hrTorrents) {
            HttpResponse response = httpUtils.doGet(BASE_URL+torrent.getPageHREF(),getDefaultHeader());
            String content = httpUtils.getContent(response);

            torrent.setInforBarImg(Jsoup.parse(content).selectFirst(".inforbar_img img").attr("src"));
        }

    }

    private void setTorrentName(List<Torrent> hrTorrents) throws IOException, InterruptedException {
        for(Torrent t : hrTorrents){
            String fajlNev = "";
            try{
                fajlNev = download(t);
            }
            catch (Exception e){
                e.printStackTrace();
                continue;
            }

            if(fajlNev == "") continue;
            String name = getTorrentName(DOWNLOAD_LOCATION+fajlNev);

            t.setTorrentNev(name);
        }
    }

    public String download(Torrent torrent) {
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
            fos = new FileOutputStream(new File(filePath));
            int inByte;
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
            }
            return filename;
        } catch (IOException ex) {
            Logger.getLogger(NCoreClient.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(NCoreClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (fos != null) {
                    fos.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(NCoreClient.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.println("name: " + name);
        return name;
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

}
