module org.mediacat {
    requires transitive org.jsoup;
    requires transitive jdk.crypto.ec;
    requires transitive java.net.http;
    requires info.picocli;
    exports org.mediacat.torrent;
    exports org.mediacat.settings;
    exports org.mediacat.utils;
    exports org.mediacat.filter;
    exports org.mediacat.cli;
    opens org.mediacat.cli;
}