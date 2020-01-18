package org.mediacat.torrent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;
import java.net.http.HttpClient;

abstract public class AbstractEngine implements TorrentEngine {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
    private static final int MAX_RETRIES = 50;

    private volatile HttpClient httpClient;
    protected volatile String baseUrl;
    protected volatile String searchPath;
    protected volatile Proxy proxy;
    protected volatile boolean isFailing;

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

    @Override
    public boolean isFailing() {
        return isFailing;
    }

    private Document documentHelper (String url, int triesLeft) throws IOException {
        try {
            return Jsoup.connect(url)
                    .proxy(proxy)
                    .userAgent(USER_AGENT)
                    .get();
        } catch (IOException ioe) {
            String message = ioe.getMessage().toLowerCase();
            if (message.contains("connection reset") && triesLeft > 0) {
                return documentHelper(url, triesLeft - 1);
            } else
                throw ioe;
        }
    }

    protected Document getDocument (String url) throws IOException {
        return documentHelper(url, MAX_RETRIES);
    }
}
