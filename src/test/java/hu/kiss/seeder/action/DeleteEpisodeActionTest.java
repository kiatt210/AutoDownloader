package hu.kiss.seeder.action;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import hu.kiss.seeder.client.EmbyClient;
import hu.kiss.seeder.client.QbitorrentClient;
import hu.kiss.seeder.config.ConfigStore;
import hu.kiss.seeder.data.TorrentComposite;

public class DeleteEpisodeActionTest {

	private static EmbyClient embyClient;
	private static QbitorrentClient qClient;
	private static ConfigStore configStore;

	@BeforeAll
	public static void setup() {
		embyClient = mock(EmbyClient.class);
		when(embyClient.isWatched(eq("watched.mkv"))).thenReturn(true);
		when(embyClient.isWatched(eq("unwatched.mkv"))).thenReturn(false);

		qClient = mock(QbitorrentClient.class);
		when(qClient.getFiles(eq("watched"))).thenReturn(List.of("watched.mkv"));
		when(qClient.getFiles(eq("unwatched"))).thenReturn(List.of("unwatched.mkv"));

		configStore = mock(ConfigStore.class);
		when(configStore.getEpisodeDeleteCategories()).thenReturn(List.of("Álommeló"));
		when(configStore.getEpisodeDeleteExtensions()).thenReturn(List.of("mkv"));
		when(configStore.getEpisodeDeleteFolderEmby()).thenReturn("");
		when(configStore.getEpisodeDeleteFolderTorrent()).thenReturn("");
	}

	@Test
	public void testWatched() {
		DeleteEpisodeAction action = new DeleteEpisodeAction(qClient, embyClient,configStore);
		TorrentComposite t = mock(TorrentComposite.class);
		when(t.getCategory()).thenReturn("Álommeló");
		when(t.getId()).thenReturn("watched");
		when(t.getTags()).thenReturn(List.of("tartós"));
		action.execute(t);
		verify(qClient).removeTag(eq("watched"), any());
	}

	@Test
	public void testUnWatched() {
		DeleteEpisodeAction action = new DeleteEpisodeAction(qClient, embyClient,configStore);
		TorrentComposite t = mock(TorrentComposite.class);
		when(t.getCategory()).thenReturn("Álommeló");
		when(t.getId()).thenReturn("unwatched");
		when(t.getTags()).thenReturn(List.of("tartós"));
		action.execute(t);
		verify(qClient,atLeast(0)).removeTag(eq("unwatched"), any());
	}
}
