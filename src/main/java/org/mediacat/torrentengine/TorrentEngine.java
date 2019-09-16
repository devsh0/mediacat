package org.mediacat.torrentengine;

import java.net.Proxy;
import java.util.List;
import java.util.Objects;

public interface TorrentEngine {
    List<TorrentMeta> getTorrentMeta (String searchTerm) throws TorrentEngineFailedException;

    String getBaseUrl();

    String getSearchPath();

    Proxy getProxy();

    void setBaseUrl(String baseUrl);

    void setSearchPath(String searchPath);

    void setProxy(Proxy proxy);

    void registerSelf();

    static void registerEngine(TorrentEngine engine) {
        Objects.requireNonNull(engine);
        TorrentEngineManager.registerEngine(engine);
    }
}
