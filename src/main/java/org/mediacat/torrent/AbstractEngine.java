package org.mediacat.torrent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

import static java.net.http.HttpResponse.BodyHandlers;

abstract public class AbstractEngine implements TorrentEngine {
    private volatile HttpClient httpClient;
    protected volatile String baseUrl;
    protected volatile String searchPath;
    protected volatile Proxy proxy;
    protected volatile boolean isFailing;

    public AbstractEngine (String baseUrl, String searchPath, Proxy proxy) {
        this.baseUrl = baseUrl;
        this.searchPath = searchPath;
        this.proxy = proxy;
        setupClient();
    }

    private void setupClient() {
        var selector = ProxySelector.of((InetSocketAddress)proxy.address());
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(30))
                .proxy(selector)
                .build();
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
        setupClient();
    }

    @Override
    public boolean isFailing() {
        return isFailing;
    }

    protected Document getDocument (String url) throws IOException, InterruptedException {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            String body = httpClient.send(req, BodyHandlers.ofString()).body();
            return Jsoup.parse(body);
    }
}
