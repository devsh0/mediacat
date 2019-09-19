package org.mediacat.torrentengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Kat implements TorrentEngine {
    private volatile static Kat instance;

    private interface SELECTORS {
        String DATA_ROWS_ODD = "table.data tr.odd";
        String DATA_ROWS_EVEN = "table.data tr.even";
        String CELL_MAIN_LINK = "a.cellMainLink";
        String DATA_CELL = "td.center";
        String MAGNET_LINK = "a[title='Magnet link']";
    }

    private static class Parser {
        static long parseSize(String sizeStr) {
            String[] pieces = sizeStr.split("\\s");
            double size = Double.parseDouble(pieces[0]);
            switch (pieces[1].toUpperCase()) {
                case "KB":
                    size *= 1000L;
                    break;
                case "MB":
                    size *= 1000_000L;
                    break;
                case "GB":
                    size *= 1000_000_000L;
                    break;
                case "TB":
                    size *= 1000_000_000_000L;
                    break;
            }

            return (long) size;
        }

        static int parseAge(String ageStr) {
            String[] pieces = ageStr.split("\\s");
            int age = Integer.parseInt(pieces[0]);
            switch (pieces[1].toLowerCase()) {
                case "hour":
                case "hours":
                case "min.":
                    age = 0;
                    break;
                case "month":
                case "months":
                    age *= 30;
                    break;
                case "year":
                case "years":
                    age *= 365;
                    break;
            }

            return age;
        }

        static int parseSeed(String seedStr) {
            return Integer.parseInt(seedStr);
        }

        static int parseLeech(String leechStr) {
            return Integer.parseInt(leechStr);
        }

        static String parseTorrentUrl(String url) {
            return url;
        }
    }

    private String baseUrl;
    private String searchPath;
    private Proxy proxy;

    private Kat(String baseUrl, String searchPath, Proxy proxy) {
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

    @Override
    public String getMagnet(String torrentUrl) throws TorrentEngineFailedException {
        try {
            Document doc = getDocument(torrentUrl);
            return doc.select(SELECTORS.MAGNET_LINK).attr("href");
        } catch (Exception e) {
            throw new TorrentEngineFailedException(e);
        }
    }

    //todo: proxy is broken
    private Document getDocument(String url) throws IOException {
        System.out.println("Proxy: " + proxy);
        return Jsoup.connect(url)
                .proxy(proxy)
                .followRedirects(true)
                .get();
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

    private String extractName(Element row) {
        Element mainLink = row.selectFirst(SELECTORS.CELL_MAIN_LINK);
        String innerText = mainLink.text().trim();
        return innerText.replaceAll("\\s", ".");
    }

    private String extractSimpleCellData(Element row, int cellPosition) {
        Element cell = row.select(SELECTORS.DATA_CELL).get(cellPosition);
        return cell.text().trim();
    }

    private String extractTorrentUrl(Element row) {
        Element mainLink = row.selectFirst(SELECTORS.CELL_MAIN_LINK);
        return mainLink.attr("href").trim();
    }

    private List<TorrentMeta> parseHtml(Document doc) {
        List<TorrentMeta> metas = new ArrayList<>();
        List<Element> rows = doc.select(SELECTORS.DATA_ROWS_ODD);
        rows.addAll(doc.select(SELECTORS.DATA_ROWS_EVEN));

        for (Element row : rows) {
            String name = extractName(row).replace(".", " ");
            long size = Parser.parseSize(extractSimpleCellData(row, 0));
            int age = Parser.parseAge(extractSimpleCellData(row, 2));
            int seed = Parser.parseSeed(extractSimpleCellData(row, 3));
            int leech = Parser.parseLeech(extractSimpleCellData(row, 4));
            String torUrl = Parser.parseTorrentUrl(extractTorrentUrl(row));
            String fullTorUrl = this.baseUrl + (torUrl.startsWith("/") ? "" : "/") + torUrl;

            metas.add(new TorrentMeta(name, size, age, seed, leech, fullTorUrl));
        }

        return metas;
    }

    private boolean hasResults(String html) {
        final String dummyHash = "E14B6EDF4AF7723D721342576D6CEC96A01C0247";
        return !html.contains(dummyHash);
    }

    @Override
    public List<TorrentMeta> getTorrentMeta(String searchTerm)
            throws TorrentEngineFailedException {
        try {
            String fullUrl = baseUrl + searchPath + searchTerm;
            Document document = getDocument(fullUrl);
            return hasResults(document.html()) ? parseHtml(document) : Collections.emptyList();
        } catch (Exception e) {
            throw new TorrentEngineFailedException(e);
        }
    }

    static TorrentEngine getInstance(String baseUrl, String searchPath, Proxy proxy) {
        if (instance == null)
            instance = new Kat(baseUrl, searchPath, proxy);

        return instance;
    }
}
