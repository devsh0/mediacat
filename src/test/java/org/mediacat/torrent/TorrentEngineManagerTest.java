package org.mediacat.torrent;

import org.junit.jupiter.api.Test;
import org.mediacat.filter.FilterCriteria;
import org.mediacat.filter.Quality;
import org.mediacat.settings.TorrentEngineSettings;

import static org.junit.jupiter.api.Assertions.*;

public class TorrentEngineManagerTest {
    private final String filePath = "src/main/resources/torrent.settings.properties";
    private final TorrentEngineSettings settings = TorrentEngineSettings.getInstance(filePath);
    private final TorrentEngineManager manager = TorrentEngineManager.getInstance(settings);
    private final Quality[] allQualities = new Quality[]{Quality.THEATRE, Quality.HD, Quality.BLURAY};
    private final String search = "Game of Thrones";

    @Test
    public void torrentMetaListIncludedNElementsTest() {
        try {
            int fetchCount = 10;
            var fc = new FilterCriteria(true, allQualities, fetchCount);
            var infoList = manager.getTorrentInfoList(search, fc);
            assertEquals(fetchCount, infoList.size());
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    public void goToNextPageIfEnoughResultsNotReturnedFromPage1Test() {
        try {
            int moreThan1stPageReturns = 20 + 10;
            var fc = new FilterCriteria(true, allQualities, moreThan1stPageReturns);
            var infoList = manager.getTorrentInfoList(search, fc);

            for (int i = 0; i < infoList.size(); i++)
                System.out.println(i + 1 + ". " + infoList.get(i));

            assertEquals(moreThan1stPageReturns, infoList.size());
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    public void infoListRestrictsResultsBasedOnQualityTest() {
        try {
            String search = "Aladdin";
            int fetchCount = 20;
            Quality allowed = Quality.HD;
            var fc = new FilterCriteria(true, new Quality[]{allowed}, fetchCount);
            var infoList = manager.getTorrentInfoList(search, fc);
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
        int fetch = 10;
        var fc = new FilterCriteria(true, allQualities, fetch);
        try {
            manager.getTorrentInfoList(search, fc);
        } catch (TorrentEngineFailedException e1) {
            assertTrue(e1.getMessage().equals("ran out of engines"));
            torrentMetaListIncludedNElementsTest();
        }
    }

    @Test
    public void fetchMagnetsTest() {
        try {
            var fc = new FilterCriteria(false, allQualities, 5);
            var infoList = manager.getTorrentInfoList(search, fc);
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
