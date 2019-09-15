package org.mediacat.torrentengine;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class KatTest {
    private static final Properties properties = new Properties();
    private static Kat instance;

    static {
        try {
            var inputStream = Files.newInputStream(Paths.get("src/main/resources/config.properties"));
            properties.load(inputStream);
            instance = Kat.getInstance(properties);
        } catch (IOException ioe) {
            throw new RuntimeException("Error reading props");
        }
    }

    @Test
    void testEmptyListReturnedWhenTorrentNotFoundForGivenSearchTerm() {
        try {
            String gibberish = "kfahfjaskd";
            List<TorrentMeta> metas = instance.getTorrentMeta(gibberish);
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
            List<TorrentMeta> metas = instance.getTorrentMeta(search);
            for (TorrentMeta meta : metas)
                System.out.println(meta);
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
            List<TorrentMeta> metas = instance.getTorrentMeta(search);
            assertTrue(metas.size() > 0);
            assertTrue(metas.get(0).getName().contains(search));
        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
            fail("Exception thrown");
        }
    }
}