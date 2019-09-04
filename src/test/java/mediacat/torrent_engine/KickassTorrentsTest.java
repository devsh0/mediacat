package mediacat.torrent_engine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KickassTorrentsTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    void testEmptyListReturnedWhenTorrentNotFoundForGivenSearchTerm() {
        try {
            String gibberish = "hfjbassafa";
            List<TorrentMeta> metas = KickassTorrents.INSTANCE.getTorrentMeta(gibberish);
            assertEquals(0, metas.size());
        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    void testTorrentMetaListReturnedWhenTorrentFoundForGivenSearchTerm() {
        try {
            String search = "Sacred Games";
            List<TorrentMeta> metas = KickassTorrents.INSTANCE.getTorrentMeta(search);
            System.out.println("Meta List Length: " + metas.size());
            for (TorrentMeta meta : metas) {
                System.out.println(meta);
            }
            assertTrue(metas.size() > 0);
        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
            fail("Exception thrown");
        }
    }
}