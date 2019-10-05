package org.mediacat.cli;


import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

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
            names = "--hd",
            description = "Optional. If specified, high quality (those extracted from" +
                    " a hd media disk) torrent media files will be included in the" +
                    " results. By default, this type of torrents are included."
    )
    boolean includeHd;

    @Option(
            names = "--bluray",
            description = "Optional. If specified, bluray (those extracted from" +
                    " a bluray disk) torrent media files will be included in the" +
                    " results. By default, this type of torrents are included."
    )
    boolean includeBluray;

    @Option(
            names = {"-f", "--fetch-count"},
            description = "Optional. Number of torrents to capture. Defaults to 5." +
                    " It is recommended to keep this number lower than 40 to prevent" +
                    " bans from torrent sites."
    )
    int fetchCount = 5;

    @Option(
            names = {"-s", "--save"},
            description = "Optional. Save the specified settings as default."
    )
    boolean saveAsDefault;

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

    public void wrapUp() {
        if (!includeBluray && !includeHd && !includeTheatre)
            includeBluray = includeHd = includeTheatre = true;
    }
}
