module org.mediacat {
    requires org.jsoup;
    requires jdk.crypto.ec;
    exports org.mediacat.torrent;
    exports org.mediacat.settings;
    exports org.mediacat.utils;
    exports org.mediacat.filter;
}