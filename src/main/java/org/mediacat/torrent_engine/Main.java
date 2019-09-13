package org.mediacat.torrent_engine;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String search = "Witcher 3";
            List<TorrentMeta> metas = KickassTorrents.INSTANCE.getTorrentMeta(search);
            for (TorrentMeta meta : metas)
                System.out.println(meta.getName());

        } catch (TorrentEngineFailedException exc) {
            exc.getCause().printStackTrace();
        }
    }
}
