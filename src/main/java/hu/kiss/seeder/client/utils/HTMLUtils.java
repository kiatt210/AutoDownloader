/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client.utils;

import hu.kiss.seeder.data.Torrent;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author KICSI
 */
public class HTMLUtils {

    private static Logger logger = LogManager.getLogger();
    
    public static void findTorrents(Document hrDocument, List<Torrent> hrTorrents) {
        Elements torrentsE = hrDocument.select("div[class^=hnr_all]");
        logger.info("Torrents size: "+torrentsE.size());
        for (org.jsoup.nodes.Element element : torrentsE) {
            Torrent t = new Torrent(element);
            hrTorrents.add(t);
        }
        
        Collections.sort(hrTorrents);
        
    }
    
}
