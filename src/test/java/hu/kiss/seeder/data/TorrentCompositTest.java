package hu.kiss.seeder.data;

import hu.kiss.seeder.auth.Secret;
import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TorrentCompositTest {

    private NCoreClient nCoreClient;

    @AfterEach
    public void cleanUp(){
        nCoreClient.logout();
    }

    @Test
    public void testPairing(){
        NCoreClient.DOWNLOAD_LOCATION = "target/";
        nCoreClient = new NCoreClient();
        nCoreClient.login(Secret.all().get(0));
        nCoreClient.populateHrTorrents();

        var ncoreTorrents = nCoreClient.getHrTorrents();

        QbitorrentClient qbitorrentClient = new QbitorrentClient();
        qbitorrentClient.populateSeededTorrents();
        var bitTorrents = qbitorrentClient.getSeededTorrents();
        Assertions.assertTrue(bitTorrents.stream().filter(bt -> bt.getId() == null).findAny().isEmpty());

        List<TorrentComposite> torrents = new ArrayList<>();
        ncoreTorrents.stream().forEach( t ->{
            TorrentComposite tc = TorrentComposite.create(t,bitTorrents);
            Assertions.assertNotNull(tc.getBitTorrent().getId(),t.getFajlNev());
            torrents.add(tc);
        });

        bitTorrents.stream()
                .filter(t ->
                        torrents.stream()
                        .filter(tc -> tc.getBitTorrent().getId().equals(t.getId()))
                        .findAny()
                        .isEmpty())
                .forEach(bt ->{
                    TorrentComposite tc = new TorrentComposite(new Torrent(bt.getNev()),bt);
                    torrents.add(tc);
                });
        Assertions.assertEquals(bitTorrents.size(),torrents.size());
    }


}
