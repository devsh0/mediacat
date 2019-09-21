module org.mediacat {
    requires transitive org.jsoup;
    requires transitive jdk.crypto.ec;
    exports org.mediacat.torrent;
    exports org.mediacat.settings;
    exports org.mediacat.utils;
    exports org.mediacat.filter;
}