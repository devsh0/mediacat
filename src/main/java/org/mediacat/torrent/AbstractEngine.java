package org.mediacat.torrent;

import java.net.Proxy;

abstract public class AbstractEngine implements TorrentEngine {
    protected volatile String baseUrl;
    protected volatile String searchPath;
    protected volatile Proxy proxy;

    public AbstractEngine (String baseUrl, String searchPath, Proxy proxy) {
        this.baseUrl = baseUrl;
        this.searchPath = searchPath;
        this.proxy = proxy;
    }

    // Getters
    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getSearchPath() {
        return searchPath;
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    // Setters
    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    @Override
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
}
