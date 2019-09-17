package org.mediacat.torrentengine;

class KatTest {
    /*private final Kat instance;

    KatTest() {
        try {
            var inputStream = Files.newInputStream(Paths.get("src/main/resources/config.properties"));
            Properties properties = new Properties();
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
    }*/
}
