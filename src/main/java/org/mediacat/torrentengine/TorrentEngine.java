package org.mediacat.torrentengine;

import java.net.Proxy;
import java.util.List;

public interface TorrentEngine {
    List<TorrentMeta> getTorrentMeta (String searchTerm) throws TorrentEngineFailedException;

    String getBaseUrl();

    String getSearchPath();

    Proxy getProxy();

    void setBaseUrl(String baseUrl);

    void setSearchPath(String searchPath);

    void setProxy(Proxy proxy);

    default String getMagnet(String torrentUrl) throws TorrentEngineFailedException {
        throw new UnsupportedOperationException("Operation not supported");
    }
}
