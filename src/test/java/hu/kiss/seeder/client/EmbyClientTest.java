package hu.kiss.seeder.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.HttpVersion;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import hu.kiss.seeder.client.utils.HTTPUtils;

public class EmbyClientTest {
	private String t = "as\"}";
	private static final String RESPONSE_STR = """
	{
  "Items": [
    {
      "Name": "11. epizód",
      "ServerId": "0dc6f3fd0ff643be823bc2efc0ae2861",
      "Id": "261507",
      "RunTimeTicks": 43666130000,
      "IndexNumber": 11,
      "ParentIndexNumber": 7,
      "IsFolder": false,
      "Type": "Episode",
      "ParentLogoItemId": "261500",
      "ParentBackdropItemId": "261500",
      "ParentBackdropImageTags": [
        "9b68d19cf164b739fd4a4486e3e90b9b"
      ],
      "UserData": {
        "PlaybackPositionTicks": 0,
        "PlayCount": 0,
        "IsFavorite": false,
        "Played": true
      },
      "SeriesName": "Cápák között",
      "SeriesId": "261500",
      "SeasonId": "261502",
      "SeriesPrimaryImageTag": "f474cadf5ed160367e471ebf70080d24",
      "SeasonName": "Season 7",
      "ImageTags": {
        "Primary": "2fa06015979ddf037c372b19bdd35e2d"
      },
      "BackdropImageTags": [],
      "ParentLogoImageTag": "d3b90b9deb6ac40ddec19cf08a83e33c",
      "MediaType": "Video"
    }
  ],
  "TotalRecordCount": 1
}""";

    @Test
    public void testSelectPlayed() throws IOException{
	Assert.assertNotNull(System.getenv("API_KEY"));
	HTTPUtils httpUtils = mock(HTTPUtils.class);
	HttpResponse response = mock(HttpResponse.class);
	HttpEntity entity = mock(HttpEntity.class);
	when(entity.getContent()).thenReturn(new ByteArrayInputStream(RESPONSE_STR.getBytes()));
	when(httpUtils.doGet(any(), any())).thenReturn(response);
	when(response.getEntity()).thenReturn(entity);
	when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));

	EmbyClient client = new EmbyClient(httpUtils);

	Boolean result = client.isWatched("/mnt/share1/Capak.Kozott/Season.7/Capak.kozott.S07E10.720p.RTLP.WEB-DL.AAC2.0.H.264.HUN-FULCRUM/fulcrum-capak.kozott.s07e10.720p.web.mkv");
	Assert.assertTrue(result);
    }

}
