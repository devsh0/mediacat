package org.mediacat.torrent;

import org.junit.jupiter.api.Test;
import org.mediacat.filter.Filter;
import org.mediacat.filter.Quality;
import org.mediacat.settings.TorrentEngineSettings;

import static org.junit.jupiter.api.Assertions.*;

public class TorrentEngineManagerTest {
    private final String filePath = "src/main/resources/torrent.settings.properties";
    private final TorrentEngineSettings settings = TorrentEngineSettings.getInstance(filePath);
    private final TorrentEngineManager manager = TorrentEngineManager.getInstance(settings);
    private final Quality[] allQualities = new Quality[]{Quality.THEATRE, Quality.HD};
    private final String search = "Game of Thrones";
    private final int defaultFetchCount = settings.getFetchCount();

    @Test
    public void torrentMetaListContainsNElementsTest() {
        try {
            var filter = Filter.builder()
                    .includeUntrusted(false)
                    .allowQualities(Quality.HD)
                    .build();

            var infoList = manager.getTorrentInfoList(search, filter);
            assertEquals(defaultFetchCount, infoList.size());
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    public void gotRestrictedResultsBasedOnQualityTest() {
        try {
            String search = "Aladdin";
            Quality allowed = Quality.HD;
            var filter = Filter.builder()
                    .allowQualities(allowed)
                    .build();
            var infoList = manager.getTorrentInfoList(search, filter);
            infoList.forEach(i -> {
                System.out.println(i);
                assertEquals(i.getQuality(), allowed);
            });
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    /*@Test
    public void httpClientProxyTest() {
        try {
            String url = "https://api.ipify.org";
            String proxyHost = "103.81.77.13";
            InetSocketAddress addr = new InetSocketAddress(proxyHost, 82);

            HttpClient client = HttpClient.newBuilder()
                    .proxy(ProxySelector.of(addr))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            String ip = client.send(request, BodyHandlers.ofString()).body();
            assertTrue(ip.contains(proxyHost));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Test
    public void resetEngineIndexWhenAllEnginesFailTest() {
        String search = "total gibberiasl;nkajkfbas";
        var filter = Filter.builder().build();
        try {
            manager.getTorrentInfoList(search, filter);
        } catch (TorrentEngineFailedException e1) {
            assertEquals("ran out of engines", e1.getMessage());
            torrentMetaListContainsNElementsTest();
        }
    }

    @Test
    public void fetchMagnetsTest() {
        try {
            var filter = Filter.builder().build();
            var infoList = manager.getTorrentInfoList(search, filter);
            for (var info : infoList) {
                String mag = info.getMagnet();
                assertNotEquals(null, mag);
                System.out.println(info);
            }
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("exception thrown");
        }
    }
}
