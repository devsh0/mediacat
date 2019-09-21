package org.mediacat.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TorrentEngineSettingsTest {
    private final String path = "src/main/resources/torrent.settings.properties";
    private final TorrentEngineSettings settings = TorrentEngineSettings.getInstance(path);

    @Test
    public void testSettingsLoaded() {
        assertTrue(settings.getEngineImplNames().size() > 0);
    }

    /*@Test
    public void testMultipleImplsLoaded() {
        assertTrue(settings.getEngineImplNames().size() > 1);
    }*/
}
