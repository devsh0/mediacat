package org.mediacat.torrentengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.torrent.TorrentEngineFailedException;
import org.mediacat.torrent.TorrentEngineManager;
import org.mediacat.torrent.TorrentInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TorrentEngineManagerTest {
    private final String filePath = "src/main/resources/torrentengine.settings.properties";
    private final TorrentEngineSettings settings = TorrentEngineSettings.getInstance(filePath);
    private final TorrentEngineManager instance = TorrentEngineManager.getInstance(settings);
    private List<TorrentInfo> metas;

    @BeforeEach
    public void setUp() {
        String searchTerm = "Mirzapur";
        metas = instance.getTorrentInfoList(searchTerm);
    }

    @Test
    public void testTorrentMetaListReturned() {
        assertTrue(metas.size() > 0);
    }

    @Test
    public void testMagnetLinkFetched() {
        TorrentInfo meta = metas.get(0);
        try {
            instance.getMagnetOf(meta);
        } catch (TorrentEngineFailedException e) {
            e.printStackTrace();
            fail();
        }
    }
}
