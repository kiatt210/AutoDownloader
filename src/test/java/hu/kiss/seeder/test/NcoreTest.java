/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.test;

import hu.kiss.seeder.action.StopPauseAction;
import hu.kiss.seeder.auth.Secret;
import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Torrent;
import hu.kiss.seeder.mongo.TorrentDb;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 *
 * @author KICSI
 */
public class NcoreTest {

    private static NCoreClient ncClient;

    @BeforeAll
    public static void init(){
        Configurator.setRootLevel(Level.DEBUG);
        ncClient = new NCoreClient();
        ncClient.login(Secret.all().get(0));
        NCoreClient.DOWNLOAD_LOCATION = "target/";
    }
    @AfterAll
    static public void cleanUp(){
        ncClient.logout();
    }

    @Test
    public void testPopulateTorrents(){
        assertTimeout(Duration.ofSeconds(20), () -> ncClient.populateHrTorrents());
    }

    @Test
    public void testTorrents() throws IOException, InterruptedException {
        ncClient.populateHrTorrents();
        long heavenTorrents = ncClient.getHrTorrents().stream().filter(t -> t.getTorrentNev().contains("Heaven")).count();
        assertEquals(1,heavenTorrents);
    }

    @Test
    public void testSearch(){
        List<Torrent> torrents = ncClient.search("The Birds 1963");
        Assert.assertEquals(7,torrents.size());

        Optional<Torrent> best = ncClient.getBest(torrents);
        Assert.assertEquals("The.Birds.1963.720p.BluRay.DTS.x264.Hun-HDGirL",best.get().getTorrentNev());

    }

    @Test
    public void testSearchByImdb(){
        List<Torrent> torrents = ncClient.searchByImd("tt0111161");
        Assert.assertEquals(22,torrents.size());

        Optional<Torrent> best = ncClient.getBest(torrents);
        Assert.assertEquals("The.Shawshank.Redemption.1994.REMASTERED.720p.BluRay.x264-CiNEPHiLiA",best.get().getTorrentNev());

    }

}