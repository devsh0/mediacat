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
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static java.net.http.HttpResponse.BodyHandlers;

abstract public class AbstractEngine implements TorrentEngine {
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36";
    private static final int MAX_RETRIES = 20;

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
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    private String makeRequest(String url, int triesLeft) throws IOException, InterruptedException {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .uri(URI.create(url))
                    .build();

            var response = httpClient.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.body();
        } catch(IOException exc) {
            String causeMessage = exc
                    .getCause()
                    .getMessage()
                    .toLowerCase();

            if (causeMessage.contains("connection reset")) {
                if (triesLeft > 0) {
                    System.out.println("Retrying...");
                    makeRequest(url, triesLeft - 1);
                    return "";
                }
                else
                    throw exc;
            }
        }

        throw new IOException("Request returned unexpected response!");
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
            String body = makeRequest(url, MAX_RETRIES);
            return Jsoup.parse(body);
    }
}
