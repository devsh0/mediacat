package org.mediacat;

public interface PropKeys {
    interface app {
        String name = "app.name";
        String version = "app.version";
    }

    interface engine {
        String proxyIsSet = "engine.proxyIsSet";
        String proxyType = "engine.proxyType";
        String proxyHost = "engine.proxyHost";
        String proxyPort = "engine.proxyPort";

        interface kat {
            String url = "engine.kat.url";
            String searchPath = "engine.kat.searchPath";
            String proxyIsSet = "engine.kat.proxyIsSet";
            String proxyType = "engine.kat.proxyType";
            String proxyHost = "engine.kat.proxyHost";
            String proxyPort = "engine.kat.proxyPort";
        }
    }
}
