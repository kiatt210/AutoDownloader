/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.run;

import hu.kiss.seeder.action.Action;
import hu.kiss.seeder.action.RSSDownloadAddTagAction;
import hu.kiss.seeder.action.StopPauseAction;
import hu.kiss.seeder.action.WarnedAction;
import hu.kiss.seeder.auth.Secret;
import hu.kiss.seeder.client.IMDBClient;
import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.client.TorrentClientI;
import hu.kiss.seeder.client.mqtt.PahoClient;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Torrent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;


/**
 *
 * @author KICSI
 */
public class Runner {

    public static final int MAX_ADDABLE_PER_USER = 18;
    private static int limit = 35;
    private static int MY_MOVIES_COUNT = 6;
    private static final String MY_MOVIES_CATEGORY="Filmek Atinak";
    private NCoreClient ncClientKiatt;
    private NCoreClient ncClientDake;
    private TorrentClientI dClient;
    private PahoClient pahoClient;

    private PahoClient client;
    private List<Action> actions;

    private static Logger logger = LogManager.getLogger();
    public Runner() {
    }


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
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        logger.info("-----------------------------------------------");
        logger.info("Start runner");
        logger.info("-----------------------------------------------");
        new Runner().run();
        logger.info("-----------------------------------------------");
        logger.info("Finish runner");
        logger.info("-----------------------------------------------");

        System.exit(0);

    }

    private void run(){

        try {
            init();
            initActions();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        logger.info("Seeded torrent size: " + dClient.getSeededTorrents().size());
        logger.info("Running torrent size: " + dClient.getRunningSize());

//        stopAndPause(ncClientKiatt, ncClientDake, dClient);

        if (dClient.getIsUpdatable() && dClient.getRunningSize() < limit) {

            int addable = MAX_ADDABLE_PER_USER - dClient.getRunningSize();
            addable = Math.max(addable,0);
            logger.info("Addable: " + addable);
            int maxAddable = addable == 0 ? 0 : addable / 2;
            logger.info("Max addable: " + maxAddable);
            addDownloads(ncClientKiatt, dClient);
            addDownloads(ncClientDake, dClient);

        }

        refreshMyMovies();

        logger.info("Seeded torrent size: " + dClient.getSeededTorrents().size());
        logger.info("Running torrent size: " + dClient.getRunningSize());

        logData(dClient);

        ncClientKiatt.logout();
        ncClientDake.logout();
        sendMqttStop();
    }

    private void init() throws IOException, InterruptedException {
        pahoClient = new PahoClient();
        sendMqttStart();
        ncClientKiatt = new NCoreClient();
        ncClientDake = new NCoreClient();
        dClient = new QbitorrentClient();
        try(var executor = Executors.newVirtualThreadPerTaskExecutor()){
            executor.execute(()-> {
                        ncClientKiatt.login(Secret.all().get(0));
                        //Ki gyűjtkük az ncore-ban lévő torrenteket
                        ncClientKiatt.populateHrTorrents();
                    }
            );
            executor.execute(() ->{
                    ncClientDake.login(Secret.all().get(1));
                    //Ki gyűjtkük az ncore-ban lévő torrenteket
                    ncClientDake.populateHrTorrents();
                    }
            );

            //Ki gyűjtjük a torrent kliensben lévő torrenteket.
            executor.execute(() -> dClient.populateSeededTorrents());
            executor.shutdown();
        }

    }

    private void initActions(){
        actions = new ArrayList<Action>();

        Action stopPauseAction = new StopPauseAction();
        stopPauseAction.init(ncClientKiatt,ncClientDake,dClient);
        actions.add(stopPauseAction);

        Action addTagAction = new RSSDownloadAddTagAction();
        addTagAction.init(dClient);
        actions.add(addTagAction);

        Action warnAction = new WarnedAction();
        warnAction.init(ncClientKiatt,ncClientDake,dClient);
        actions.add(warnAction);
    }

    private void stopAndPause(NCoreClient nClientKiatt, NCoreClient nClientDake , TorrentClientI dClient) {

        dClient.getSeededTorrents().stream().forEach(torrent -> {

            actions.forEach( a -> a.execute(torrent));
        });
    }

    private void addDownloads(NCoreClient nClient, TorrentClientI dClient) {
        logger.info("Start addDownloads for " + nClient.getUserName());
        DelugeTorrent tmpTorrent = new DelugeTorrent();

        nClient.getHrTorrents().stream().forEach(torrent -> {
            tmpTorrent.setNev(torrent.getTorrentNev());
            if (!dClient.getSeededTorrents().contains(tmpTorrent)) {
                String fileName = nClient.download(torrent);
                    if(fileName != ""){
                        dClient.addTorrent(NCoreClient.DOWNLOAD_LOCATION + fileName);
                    }
            }
            
        });
    }

    private void logData(TorrentClientI dClient){
        SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd. HH:mm");
        String msg = df.format(new Date())+";"+dClient.getSeededTorrents().size()+";"+dClient.getRunningSize();
        try{
            PrintWriter pw = new PrintWriter(new FileOutputStream("torrents.csv",true));
            pw.println(msg);
            pw.close();
        }
        catch (FileNotFoundException ex){
            logger.info("File not found torrents.csv");
        }

    }

    private void sendMqttStart(){
        pahoClient.send("home/state/auto-downloader","on");
    }

    private void sendMqttStop(){
        pahoClient.send("home/state/auto-downloader","off");
    }

    private void refreshMyMovies(){
        logger.info("Refresh my movies");
        int currentMoviesCount = this.dClient.getCountByCategory(MY_MOVIES_CATEGORY);
        logger.info("Current count:"+currentMoviesCount);

        Map<String,String> parameters = new HashMap<String, String>();
        parameters.put("category","Filmek Atinak");
        parameters.put("tags","tartós");

        if (currentMoviesCount < MY_MOVIES_COUNT) {
            IMDBClient imdbClient = new IMDBClient();
            Collection<String> movies = imdbClient.getRandom(MY_MOVIES_COUNT-currentMoviesCount);
            //Add new torrents
            for (String movie : movies) {
                List<Torrent> t = ncClientKiatt.searchByImd(movie);
                Optional<Torrent> torrent = ncClientKiatt.getBest(t);
                if(torrent.isPresent()){
                    String fileName = ncClientKiatt.download(torrent.get());
                    if(fileName != ""){
                        dClient.addTorrent(NCoreClient.DOWNLOAD_LOCATION + fileName,parameters);
                    }
                }
            }
        }
    }

}
