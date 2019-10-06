package org.mediacat.cli;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(mixinStandardHelpOptions = true)
public class CmdParameters {
    @Parameters
    String searchTerm;

    @Option(
            names = "--theatre",
            description = "Optional. If specified, low quality (those recorded in a" +
                    " cinema theatre) torrent media files will be included in the" +
                    " results. By default, this type of torrents are included."
    )
    boolean includeTheatre;

    @Option(
            names = {"-a", "--all"},
            description = "Optional. If specified, other quality parameters will be ignored" +
                    " and torrents of any quality (or no quality at all, as in case of non-media" +
                    " files) will be included. If no quality is specified, this option will be" +
                    " set to true."
    )
    boolean includeAllQualities;

    @Option(
            names = "--hd",
            description = "Optional. If specified, high quality (those extracted from" +
                    " a hd media disk) torrent media files will be included in the" +
                    " results. By default, this type of torrents are included."
    )
    boolean includeHd;

    @Option(
            names = {"-f", "--fetch-count"},
            description = "Optional. Number of torrents to capture. Defaults to 5." +
                    " It is recommended to keep this number lower than 40 to prevent" +
                    " bans from torrent sites."
    )
    int fetchCount = 5;

    @Option(
            names = {"-m", "--magnet-only"},
            description = "Optional. If specified, only the magnet urls will be sent" +
                    " to standard output. Set to false by default. This option is" +
                    " helpful when used with `--best` to allow easy piping of the magnet" +
                    " to a torrent client."
    )
    boolean magnetOnly;

    @Option(
            names = {"-b", "--best"},
            description = "Optional. If specified, `fetchCount` will be ignored and" +
                    " only the best (one having the highest seed count) will be sent" +
                    " to standard output."
    )
    boolean best;

    @Option(
            names = {"-u", "--allow-untrusted"},
            description = "Optional. If specified, torrents uploaded by untrusted (non-vip)" +
                    " members will be included. Depending on the torrent engine being used," +
                    " this option may not have any effect in the search results."
    )
    boolean allowUntrusted;

    public void postProcess() {
        if (!includeHd && !includeTheatre)
            includeAllQualities = true;
    }
}
