/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client.utils;

import autodownloader.AutoDownloader;
import hu.kiss.seeder.data.Torrent;
import java.util.Collections;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author KICSI
 */
public class HTMLUtils {

    private static Log logger = LogFactory.getLog(HTMLUtils.class);
    
    public static void findTorrents(Document hrDocument, List<Torrent> hrTorrents) {
        logger.debug("Find torrents");
        Elements torrentsE = hrDocument.select("div[class^=hnr_all]");
        logger.debug("Torrents size: "+torrentsE.size());;
        for(int i=0; i<torrentsE.size(); i++){
            Torrent t = new Torrent(torrentsE.get(i));
            hrTorrents.add(t);
        }
        
        Collections.sort(hrTorrents);
        
    }
    
}
