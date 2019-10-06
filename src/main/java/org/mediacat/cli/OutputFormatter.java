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
            // this call is important because first call to getMagnet
            //  triggers the search for magnet URL
            info.getMagnet();

            if (parameters.magnetOnly) {
                System.out.println(info.getMagnet() + "\n");
                if (parameters.best)
                    break;
                else continue;
            }

            System.out.println(info);
            if (parameters.best)
                break;
        }
    }
}
