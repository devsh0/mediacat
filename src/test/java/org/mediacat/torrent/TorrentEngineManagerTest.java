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
    private final Quality[] allQualities = new Quality[]{Quality.THEATRE, Quality.HD, Quality.BLURAY};
    private final String search = "Game of Thrones";
    private final int defaultFetchCount = 20;

    @Test
    public void torrentMetaListIncludedNElementsTest() {
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

    /*@Test
    public void goToNextPageIfEnoughResultsNotFoundInPage1Test() {
        try {
            int moreThan1stPageReturns = 20 + 10;
            var filter = Filter.builder()
                    .allowQualities(Quality.HD)
                    .build();

            var infoList = manager.getTorrentInfoList(search, filter);

            for (int i = 0; i < infoList.size(); i++)
                System.out.println(i + 1 + ". " + infoList.get(i));

            assertEquals(moreThan1stPageReturns, infoList.size());
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }*/

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
                assertTrue(i.getQuality().equals(allowed));
            });
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    public void resetEngineIndexWhenAllEnginesFailTest() {
        String search = "total gibberiasl;nkajkfbas";
        var filter = Filter.builder().build();
        try {
            manager.getTorrentInfoList(search, filter);
        } catch (TorrentEngineFailedException e1) {
            assertTrue(e1.getMessage().equals("ran out of engines"));
            torrentMetaListIncludedNElementsTest();
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
