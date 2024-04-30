package hu.kiss.seeder.client.deluge;

import hu.kiss.seeder.client.QbitorrentClient;
import static org.junit.jupiter.api.Assertions.*;

import hu.kiss.seeder.data.BitTorrent;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelugeRPCClientTest {

	private String sessionId;

	@Test
	public void connectTest() throws IOException {
		QbitorrentClient client = new QbitorrentClient();
		assertTrue(client.isConnected());
	}

	@Test
	public void testRunningSize() {
		QbitorrentClient client = new QbitorrentClient();
		assertTrue(client.getRunningSize() > 0);
	}

	@Test
	public void testTartosIds() {
		QbitorrentClient client = new QbitorrentClient();
		assertTrue(client.getTartosIds().size() > 0);
	}

	@Test
	public void testSeedingTorrents() {
		QbitorrentClient client = new QbitorrentClient();
		client.populateSeededTorrents();
		assertTrue(client.getSeededTorrents().size() > 0);
	}

	@Test
	public void testDelete() {
		QbitorrentClient client = new QbitorrentClient();
		client.removeTorrent("31ccd934e214d3c30a575631b616a165a55d563d");
	}

	@Test
	public void testAddTorrent() {
		QbitorrentClient client = new QbitorrentClient();
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("category", "Filmek Atinak");
		parameters.put("tags", "tartós");
		parameters.put("paused", "true");
		client.addTorrent(
				"C:\\Work\\AutoDownloader\\torrents\\[nCore][xvid_hun]Bridget.Joness.Baby.2016.BDRip.x264.HuN-No1.torrent",
				parameters);
	}

	@Test
	public void testPauseTorrent() {
		QbitorrentClient client = new QbitorrentClient();
		client.pauseTorrent("baf02fff60f82dbb342bb26ffcad2925a0843349");
	}

	@Test
	public void testAddTag() {
		QbitorrentClient client = new QbitorrentClient();
		client.addTag("789b7af5c773e0579bf3814b32e7787963fb5060", "tartós");
	}

	@Test
	public void testResumeTorrent() {
		QbitorrentClient client = new QbitorrentClient();
		client.resumeTorrent("0b4648741e2f8b4e676182a5cafbc28cac08122d");
	}

	@Test
	public void testCategoryCount() {
		QbitorrentClient client = new QbitorrentClient();
		client.populateSeededTorrents();
		int count = client.getCountByCategory("Filmek Atinak");
		Assert.assertTrue(count > 1);
	}

	@Test
	public void testSuperSeed() {
		QbitorrentClient client = new QbitorrentClient();
		client.populateSeededTorrents();
		BitTorrent t = client.getSeededTorrents().get(0);
		client.superSeed(t.getId());
		client.forceStart(t.getId());
		System.out.println(t);
	}

	@Test
	public void testGetFiles() {
		QbitorrentClient client = new QbitorrentClient();
		client.populateSeededTorrents();
		BitTorrent t = client.getSeededTorrents().get(0);
		List<String> result = client.getFiles(t.getId());
		Assert.assertFalse(result.isEmpty());
	}

	/*
	 * @Test
	 * public void testGet() {
	 * QbitorrentClient client = new QbitorrentClient();
	 * client.populateSeededTorrents();
	 * BitTorrent t = client.getSeededTorrents().get(0);
	 * System.out.println(t);
	 * }
	 */
}
