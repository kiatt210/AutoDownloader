/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autodownloader;

import hu.kiss.seeder.data.Torrent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import hu.kiss.seeder.run.Runner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

/**
 *
 * @author KICSI
 */
public class AutoDownloader {

    private static String LOGIN_URL = "https://ncore.cc/login.php";
    private static String HR_URL = "https://ncore.cc/hitnrun.php";
    private static String HTML_COMMENT_REGEXP = "<![ \\r\\n\\t]*(--([^\\-]|[\\r\\n]|-[^\\-])*--[ \\r\\n\\t]*)>";
    private static String USERNAME = "Test";
    private static String PWD = "Test";
    private static String phpSessioId = "";
    private static HttpClient client;
    private static List<Torrent> hrTorrents = new ArrayList<>();
    private static Log logger = LogFactory.getLog(AutoDownloader.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

        String urlParameters = "nev=" + USERNAME + "&pass=" + PWD + "&set_lang=hu&submitted=1";

        client = HttpClientBuilder.create().build();

        HttpPost loginP = new HttpPost(LOGIN_URL);

        ArrayList<NameValuePair> postParameters;
        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("nev", USERNAME));
        postParameters.add(new BasicNameValuePair("pass", PWD));
        postParameters.add(new BasicNameValuePair("set_lang", "hu"));
        postParameters.add(new BasicNameValuePair("submitted", "1"));

        loginP.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

        HttpResponse response = client.execute(loginP);

        logger.debug("Status code: " + response.getStatusLine().getStatusCode());

        setPhpSession(response);

        logger.debug("Start get");
        response = doGet(HR_URL);
        String content = getContent(response);
        logger.debug("Content: \n" + content);
        
        Document hrDoc = getDocument(content);
        findTorrents(hrDoc);
        
    }

    private static HttpResponse doGet(String url) throws IOException {
        logger.debug("Start get url=" + url);
        HttpGet get = new HttpGet(url);
        get.setHeader("Cookie", "PHPSESSID=" + phpSessioId + "; nyelv=hu; stilus=default");

        logger.debug("Header setted");

        HttpResponse response = client.execute(get);
        printHeaders(response);

        logger.debug("Request executed");

        return response;
    }

    private static void printHeaders(HttpResponse response) {
        //get all headers
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            logger.debug("Key: " + header.getName() + " = Value:" + header.getValue());
        }
    }

    private static void setPhpSession(HttpResponse response) {
        //get all headers
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            if (header.getName().equals("Set-Cookie") && header.getValue().startsWith("PHPSESSID=")) {
                phpSessioId = header.getValue().split("PHPSESSID=")[1].replace("; path=/", "");
                logger.debug("PHPSESSID=" + phpSessioId);
            }
        }
    }

    private static String getContent(HttpResponse response) throws IOException {
        logger.debug("Get content");
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseStr = new StringBuilder();
        String line;
        logger.debug("Start read");
        while ((line = rd.readLine()) != null) {
            responseStr.append(line);
            responseStr.append('\r');
        }
        rd.close();

        String content = responseStr.toString();

        Pattern p = Pattern.compile(HTML_COMMENT_REGEXP, Pattern.MULTILINE);

        Matcher m = p.matcher(content);
        int i = 1;
        while (m.find()) {
            String comment = m.group(0);
            logger.debug("#" + i + "Comment:" + comment);
            content = content.replace(comment, "");
            i++;

        }

        //Lezáratlan tagek eltávolítása
        content = content.replaceAll("<link.*>","").replaceAll("<meta.*>","");
        return content;
    }

    private static Document getDocument(String content) throws IOException {
        logger.debug("Get document");

        return Jsoup.parse(content);
    }

    private static void findTorrents(Document hrDocument) throws XPathExpressionException {
        logger.debug("Find torrents");
        Elements torrentsE = hrDocument.select("div[class^=hnr_all]");
        logger.debug("Torrents size: "+torrentsE.size());
        for (org.jsoup.nodes.Element element : torrentsE) {
            Torrent t = new Torrent(element);
            hrTorrents.add(t);
        }
        
        Collections.sort(hrTorrents);
        logger.debug(hrTorrents);
        
    }

}
