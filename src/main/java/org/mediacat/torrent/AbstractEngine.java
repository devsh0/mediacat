package org.mediacat.torrent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

import static java.net.http.HttpResponse.BodyHandlers;

abstract public class AbstractEngine implements TorrentEngine {
    private static final Object LOCK = new Object();
    private static final Map<String, Proxy> proxyMap = new HashMap<>();
    private static final ProxySelector proxySelector = new TorrentProxySelector(proxyMap);

    protected String baseUrl;
    protected String searchPath;
    protected Proxy proxy;
    protected boolean isFailing;

    public AbstractEngine (String baseUrl, String searchPath, Proxy proxy) {
        synchronized (LOCK) {
            this.baseUrl = baseUrl;
            this.searchPath = searchPath;
            this.proxy = proxy;
            proxyMap.put(baseUrl, proxy);
        }
    }

    // Getters
    @Override
    public String getBaseUrl() {
        synchronized (LOCK) {
            return baseUrl;
        }
    }

    @Override
    public String getSearchPath() {
        synchronized (LOCK) {
            return searchPath;
        }
    }

    @Override
    public Proxy getProxy() {
        synchronized (LOCK) {
            return proxy;
        }
    }

    // Setters
    @Override
    public void setBaseUrl(String baseUrl) {
        synchronized (LOCK) {
            Proxy proxy = proxyMap.remove(this.baseUrl);
            this.baseUrl = baseUrl;
            proxyMap.put(baseUrl, proxy);
        }
    }

    @Override
    public void setSearchPath(String searchPath) {
        synchronized (LOCK) {
            this.searchPath = searchPath;
        }
    }

    @Override
    public void setProxy(Proxy proxy) {
        synchronized (LOCK) {
            this.proxy = proxy;
            proxyMap.put(baseUrl, proxy);
        }
    }

    @Override
    public boolean isFailing() {
        synchronized (LOCK) {
            return isFailing;
        }
    }

    protected Document getDocument (String url) throws IOException, InterruptedException {
        synchronized (LOCK) {
            HttpClient client = HttpClient.newBuilder()
                    .proxy(proxySelector)
                    .build();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            String body = client.send(req, BodyHandlers.ofString()).body();
            return Jsoup.parse(body);
        }
    }
}
