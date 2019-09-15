package org.mediacat.torrentengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        /*try {
            String search = "Terrified";
            List<TorrentMeta> metas = KickassTorrents.INSTANCE.getTorrentMeta(search);
            for (TorrentMeta meta : metas)
                System.out.println(meta.getName());

        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
        }*/
        System.out.println(System.getProperty("user.dir"));
        System.out.println(Files.readString(Paths.get("config.properties")));
    }
}
