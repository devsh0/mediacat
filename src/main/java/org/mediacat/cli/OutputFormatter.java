package org.mediacat.cli;

import org.mediacat.torrent.TorrentEngineFailedException;
import org.mediacat.torrent.TorrentInfo;

import java.util.List;

public class OutputFormatter {
    private final List<TorrentInfo> infoList;
    private final CmdParameters parameters;
    public OutputFormatter(CmdParameters params, List<TorrentInfo> list) {
        parameters = params;
        infoList = list;
    }

    public void print() throws TorrentEngineFailedException {
        for (var info : infoList) {
            // This call is important because first call to getMagnet
            // triggers the search for magnet URLs
            info.getMagnet();
            String str = parameters.magnetOnly ? info.getMagnet() + "\n" : info.toString();
            System.out.println(str);

            if (parameters.best)
                return;
        }
    }
}
