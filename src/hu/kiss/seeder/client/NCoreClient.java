/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client;

import hu.kiss.seeder.client.utils.HTMLUtils;
import hu.kiss.seeder.client.utils.HTTPUtils;
import hu.kiss.seeder.data.Torrent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static Log logger = LogFactory.getLog(NCoreClient.class);
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
            logger.debug("Error while logging:" + ex.getMessage());
        }
    }

    private void doLogin(String password) throws UnsupportedEncodingException, IOException {

        HashMap params = new HashMap<String, String>();
        params.put("nev", userName);
        params.put("pass", password);
        params.put("set_lang", "hu");
        params.put("submitted", "1");

        HttpResponse response = httpUtils.doPost(LOGIN_URL, params);

        logger.debug("Status code: " + response.getStatusLine().getStatusCode());

        setPhpSession(response);

        //Elmentjük a kijelentkezés linkjét, mert ez sessiononként egyedi     
        response = httpUtils.doGet(HOME_URL, getDefaultHeader());
        String responseStr = httpUtils.getContent(response);
        Document doc = Jsoup.parse(responseStr);
        logoutUrl = doc.select("a#menu_11").attr("href");
        logger.debug("Logout url:" + logoutUrl);
        logger.debug(userName+" has logged in");
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

    public void populateHrTorrents() {
        logger.debug("Populate hit&run torrents");
        HttpResponse response = httpUtils.doGet(HR_URL, getDefaultHeader());

        String content = httpUtils.getContent(response);

        Document doc = Jsoup.parse(content);
        HTMLUtils.findTorrents(doc, hrTorrents);
    }

    public String download(Torrent torrent) {
        InputStream is = null;
        try {
            HttpResponse response = httpUtils.doGet(BASE_URL + downloadLink.replace("${id}", torrent.getId() + ""), getDefaultHeader());
            Header[] heads = response.getHeaders("content-disposition");
            String filename = heads[0].getValue().replace("attachment; filename=\"","").replace("\"","");
            is = response.getEntity().getContent();
            String filePath = DOWNLOAD_LOCATION+filename;
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            int inByte;
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
            }   
            is.close();
            fos.close();
            return filename;
        } catch (IOException ex) {
            Logger.getLogger(NCoreClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(NCoreClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(NCoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "";
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
