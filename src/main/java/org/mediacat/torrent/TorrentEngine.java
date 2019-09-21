package org.mediacat.torrent;

import java.net.Proxy;
import java.util.List;

public interface TorrentEngine {
    List<TorrentInfo> getTorrentInfoList(String searchTerm, int fromPage) throws TorrentEngineFailedException;

    String getBaseUrl();

    String getSearchPath();

    Proxy getProxy();

    void setBaseUrl(String baseUrl);

    void setSearchPath(String searchPath);

    void setProxy(Proxy proxy);

    boolean isFailing();

    default String getMagnet(String torrentUrl) throws TorrentEngineFailedException {
        throw new UnsupportedOperationException("Operation not supported");
    }
}
