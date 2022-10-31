package hu.kiss.seeder.client.deluge;

import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.mongo.DBSynchronizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DTorrentStoreTest {

    private static DTorrentStore store;

    @BeforeAll
    public static void init(){
        store = new DTorrentStore();
    }

    @Test
    public void when_add_then_observer_notify(){
        DBSynchronizer synchronizer = mock(DBSynchronizer.class);
        store.addObserver(synchronizer);
        Assertions.assertEquals(1,store.countObservers());
        DelugeTorrent torrent = new DelugeTorrent();
        store.add(torrent);

        verify(synchronizer,times(1)).update(store,torrent);
    }

}
