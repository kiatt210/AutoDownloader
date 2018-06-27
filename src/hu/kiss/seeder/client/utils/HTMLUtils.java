/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client.utils;

import hu.kiss.seeder.data.Torrent;
import java.util.Collections;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author KICSI
 */
public class HTMLUtils {
    
    public static void findTorrents(Document hrDocument, List<Torrent> hrTorrents) {
        System.out.println("Find torrents");
        Elements torrentsE = hrDocument.select("div[class^=hnr_all]");
        System.out.println("Torrents size: "+torrentsE.size());;
        for(int i=0; i<torrentsE.size(); i++){
            Torrent t = new Torrent(torrentsE.get(i));
            hrTorrents.add(t);
        }
        
        Collections.sort(hrTorrents);
        
    }
    
}
