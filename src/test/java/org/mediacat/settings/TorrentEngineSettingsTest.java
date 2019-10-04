package org.mediacat.settings;

import org.junit.jupiter.api.Test;
import org.mediacat.filter.Filter;
import org.mediacat.torrent.TorrentEngineFailedException;
import org.mediacat.torrent.TorrentEngineManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class TorrentEngineSettingsTest {
    private final String path = "src/main/resources/torrent.settings.properties";
    private final TorrentEngineSettings settings = TorrentEngineSettings.getInstance(path);

    @Test
    public void loadSettingsTest() {
        assertTrue(settings.getEngineImplNames().size() > 0);
    }

    @Test
    public void fetchCountUpdatedTest() {
        try {
            TorrentEngineManager manager = TorrentEngineManager.getInstance(settings);
            int fetchCount = settings.getFetchCount();
            Filter filter = Filter.builder().build();
            int infoListSize = manager.getTorrentInfoList("Friends", filter).size();
            assertEquals(infoListSize, fetchCount);
            settings.setFetchCount(40);
            assertEquals(40, manager.getTorrentInfoList("Breaking bad", filter).size());

        } catch (TorrentEngineFailedException exc) {
            exc.printStackTrace();
            fail();
        }
    }

    @Test
    public void saveSettingsTest() {
        assertTrue(settings.getFetchCount() < 100);
        settings.setFetchCount(100);
        Path p = Paths.get(path);

        try (var os = Files.newOutputStream(p)) {
            settings.saveCurrentAsDefault(os);
            assertEquals(100, settings.getFetchCount());
        } catch (IOException exc) {
            exc.printStackTrace();
            fail();
        }

    }
}
