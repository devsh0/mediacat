package org.mediacat.torrentengine;

import org.junit.jupiter.api.Test;
import org.mediacat.settings.TorrentEngineSettings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TorrentEngineManagerTest {
    private final String filePath = "src/main/resources/torrentengine.settings.properties";
    private final TorrentEngineSettings settings = TorrentEngineSettings.getInstance(filePath);
    private final TorrentEngineManager instance = TorrentEngineManager.getInstance(settings);

    @Test
    public void testTorrentMetaListReturned() {
        try {
            String searchTerm = "Mirzapur";
            List<TorrentMeta> metas = instance.getTorrentMeta(searchTerm);
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail("Exception thrown");
        }
    }
}
