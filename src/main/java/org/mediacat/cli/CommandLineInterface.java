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
        if (params.includeAllQualities) {
            qualityList.add(Quality.THEATRE);
            qualityList.add(Quality.HD);
            qualityList.add(Quality.UNKNOWN);
        } else {
            if (params.includeTheatre)
                qualityList.add(Quality.THEATRE);
            if (params.includeHd)
                qualityList.add(Quality.HD);
        }

        Quality[] qualities = new Quality[qualityList.size()];
        qualityList.toArray(qualities);
        return qualities;
    }

    public static void main(String[] args) throws TorrentEngineFailedException {
        CmdParameters params = new CmdParameters();
        CommandLine cmdLine = new CommandLine(params);
        cmdLine.parseArgs(args);
        if (cmdLine.isUsageHelpRequested()) {
            cmdLine.usage(System.out);
            return;
        } else if(cmdLine.isVersionHelpRequested()) {
            cmdLine.printVersionHelp(System.out);
            return;
        }
        params.postProcess();
        try {
            var infoList = getTorrents(params);
            new OutputFormatter(params, infoList).print();
        } catch (TorrentEngineFailedException e) {
            String msg = e.getMessage();
            if (msg != null && msg.equals("ran out of engines"))
                System.out.println("No results found! :(\n" +
                        "Try to be more specific with your keyword and double check the spellings.");
            else throw e;
        }
    }
}
