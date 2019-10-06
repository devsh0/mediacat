package org.mediacat.cli;

import org.mediacat.filter.Filter;
import org.mediacat.filter.Quality;
import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.torrent.TorrentEngineFailedException;
import org.mediacat.torrent.TorrentEngineManager;
import org.mediacat.torrent.TorrentInfo;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class CommandLineInterface {
    private static final String engineSettingsFile = "configs/torrent.settings.properties";

    private static List<TorrentInfo> getTorrents(CmdParameters params)
            throws TorrentEngineFailedException {
        TorrentEngineSettings settings = TorrentEngineSettings.getInstance(engineSettingsFile);
        TorrentEngineManager manager = TorrentEngineManager.getInstance(settings);
        settings.setFetchCount(params.fetchCount);

        Quality[] allowedQualities = getQualitiesInArray(params);
        Filter filter = Filter.builder()
                .includeUntrusted(params.allowUntrusted)
                .allowQualities(allowedQualities)
                .build();

        return manager.getTorrentInfoList(params.searchTerm, filter);
    }

    private static Quality[] getQualitiesInArray (CmdParameters params) {
        List<Quality> qualityList = new ArrayList<>();
        if (params.includeTheatre)
            qualityList.add(Quality.THEATRE);
        if (params.includeHd)
            qualityList.add(Quality.HD);
        if(params.includeBluray)
            qualityList.add(Quality.BLURAY);

        Quality[] qualities = new Quality[qualityList.size()];
        qualityList.toArray(qualities);
        return qualities;
    }

    public static void main(String[] args) throws TorrentEngineFailedException {
        CmdParameters params = new CmdParameters();
        new CommandLine(params).parseArgs(args);
        params.wrapUp();
        var infoList = getTorrents(params);
        new OutputFormatter(params, infoList).print();
    }
}
