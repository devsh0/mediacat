package org.mediacat.torrentengine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KickassTorrentsTest {
    @Test
    void testEmptyListReturnedWhenTorrentNotFoundForGivenSearchTerm() {
        try {
            String gibberish = "kfahfjaskd";
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
            assertTrue(metas.size() > 0);
        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
            fail("Exception thrown");
        }
    }

    @Test
    void testInvalidUrlCharsCarriedToSearch() {
        try {
            String search = "O'Reilly";
            List<TorrentMeta> metas = KickassTorrents.INSTANCE.getTorrentMeta(search);
            assertTrue(metas.size() > 0);
            assertTrue(metas.get(0).getName().contains(search));
        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
            fail("Exception thrown");
        }
    }
}