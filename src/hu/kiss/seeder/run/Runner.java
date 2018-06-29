/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.run;

import hu.kiss.seeder.client.DelugeClient;
import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.Torrent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author KICSI
 */
public class Runner {

    private static int limit = 16;
    private static Log logger = LogFactory.getLog(Runner.class);

    /**
     * Működés: Kigyűjtjük a seedelendő torrenteket, majd a deluge-ban jelenleg
     * futtatott torrenteket mindkét felhasználóhoz.
     *
     * Végig megyónk az összes deluge torrenten, és ha valamelyik seed
     * státuszban van, de nincs benne az ncore-ról leszedettek között, akkor
     * töröljük, ezt mindkét felhasználóra megtesszük. Közben megszámoljuk, hány
     * torrent maradt az adott felhasználótól a deluge-ban. Végig megyünk az
     * ncore-os torrenteken, hátra lévő idő szerint növekvőben és ha valamelyik
     * nincs a deluges-ek között, és felhasználóhoz tartozó torrentek száma
     * kisebb mint 8 akkor azt hozzáadjuk, ezt megismételjük mindkét
     * felhasználóra.
     *
     * Ezután ha az összes torrent száma kisebb mint 8 akkor végig megyünk
     * még1szer mindkét felhasznáéó torrentjein és feltöltjük 18-ig a
     * torrenteket.
     *
     * @param args
     */
    public static void main(String[] args) {

        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$s] %5$s %n");

        logger.info("-----------------------------------------------");
        logger.info("Start runner");
        logger.info("-----------------------------------------------");

        NCoreClient ncClientKiatt = new NCoreClient();
        ncClientKiatt.login("test", "test", "test");
        //Ki gyűjtkük az ncore-ban lévő torrenteket
        ncClientKiatt.populateHrTorrents();
        //Ki gyűjtjük a deluge-ban lévő torrenteket.
        DelugeClient dClient = new DelugeClient();
        dClient.populateSeededTorrents();

        NCoreClient ncClientDake = new NCoreClient();
        ncClientDake.login("test", "test", "test");
        //Ki gyűjtkük az ncore-ban lévő torrenteket
        ncClientDake.populateHrTorrents();

        logger.info("Seeded torrent size: " + dClient.getSeededTorrents().size());
        logger.info("Running torrent size: " + dClient.getRunningSize());

        stopAndPause(ncClientKiatt, ncClientDake, dClient);

        if (dClient.getIsUpdatable() && dClient.getRunningSize() < limit) {

            int addable = 18 - dClient.getRunningSize();
            logger.debug("Addable: " + addable);
            int maxAddable = addable / 2;
            logger.debug("Maxaddable: " + maxAddable);
            addDownloads(ncClientKiatt, dClient);
            addDownloads(ncClientDake, dClient);

        }

        logger.info("Seeded torrent size: " + dClient.getSeededTorrents().size());
        logger.info("Running torrent size: " + dClient.getRunningSize());

        logData(dClient);

        ncClientKiatt.logout();
        ncClientDake.logout();

        logger.info("-----------------------------------------------");
        logger.info("Finish runner");
        logger.info("-----------------------------------------------");

    }

    private static void stopAndPause(NCoreClient nClientKiatt, NCoreClient nClientDake , DelugeClient dClient) {

        dClient.getSeededTorrents().stream().forEach(torrent -> {
            logger.debug("Start handle: " + torrent.getNev());
            if (torrent.getStatus().equals(Status.SEED) && !nClientKiatt.getHrTorrents().contains(new Torrent(torrent.getNev()))
                    && !nClientDake.getHrTorrents().contains(new Torrent(torrent.getNev()))
                    && !dClient.getTartosIds().contains(torrent.getId())) {
                dClient.removeTorrent(torrent.getId());
            } else if (torrent.getStatus().equals(Status.SEED) && !nClientKiatt.getHrTorrents().contains(new Torrent(torrent.getNev()))
                    && !nClientDake.getHrTorrents().contains(new Torrent(torrent.getNev()))
                    && !dClient.getTartosIds().contains(torrent.getId())) {
                dClient.pauseTorrent(torrent.getId());
            } else {
                logger.debug("Torrent not changed: " + torrent.getNev());
            }
        });
    }

    private static void addDownloads(NCoreClient nClient, DelugeClient dClient) {
        logger.debug("Start addDownloads for " + nClient.getUserName());
        DelugeTorrent tmpTorrent = new DelugeTorrent();
        int runnedBefore = dClient.getRunningSize();
        nClient.getHrTorrents().stream().forEach(torrent -> {
            tmpTorrent.setNev(torrent.getNev());
            if (!dClient.getSeededTorrents().contains(tmpTorrent)) {
                String fileName = nClient.download(torrent);
                dClient.addTorrent(NCoreClient.DOWNLOAD_LOCATION + fileName);
            }
            
        });
    }

    private static void logData(DelugeClient dClient){
        SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd. HH:mm");
        String msg = df.format(new Date())+";"+dClient.getSeededTorrents().size()+";"+dClient.getRunningSize();
        try{
            PrintWriter pw = new PrintWriter(new FileOutputStream("torrents.csv",true));
            pw.println(msg);
            pw.close();
        }
        catch (FileNotFoundException ex){
            logger.error("File not found torrents.csv");
        }

    }

}
