package hu.kiss.seeder.data;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.kiss.seeder.client.NCoreClient;
import hu.kiss.seeder.client.QbitorrentClient;

public class TorrentCompositTest {

	private NCoreClient nCoreClient;
	private QbitorrentClient qbitorrentClient; 

	@BeforeEach
	public void cleanUp() {
		nCoreClient = mock(NCoreClient.class);
		when(nCoreClient.getHrTorrents()).thenReturn(List.of(new Torrent("Test")));

		qbitorrentClient = mock(QbitorrentClient.class);
		when(qbitorrentClient.getSeededTorrents()).thenReturn(List.of(new BitTorrent("Name: Test\nID: 1\n State: Seeding")));
	}

	@Test
	public void testPairing() {

		var ncoreTorrents = nCoreClient.getHrTorrents();

		var bitTorrents = qbitorrentClient.getSeededTorrents();
		Assertions.assertTrue(bitTorrents.stream().filter(bt -> bt.getId() == null).findAny().isEmpty());

		List<TorrentComposite> torrents = new ArrayList<>();
		ncoreTorrents.stream().forEach(t -> {
			TorrentComposite tc = TorrentComposite.create(t, bitTorrents, nCoreClient);
			Assertions.assertNotNull(tc.getId(), t.getFajlNev());
			torrents.add(tc);
		});

		bitTorrents.stream()
				.filter(t -> torrents.stream()
						.filter(tc -> tc.getId().equals(t.getId()))
						.findAny()
						.isEmpty())
				.forEach(bt -> {
					TorrentComposite tc = new TorrentComposite(new Torrent(bt.getNev()), bt, null);
					Assertions.assertNotNull(tc.getNcoreTorrent(), bt.getNev());
					torrents.add(tc);
				});
		Assertions.assertEquals(bitTorrents.size(), torrents.size());
	}

}
