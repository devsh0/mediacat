package org.mediacat.torrentengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mediacat.PropKeys;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

class Kat implements TorrentEngine {
    private volatile static Kat instance;

    private interface SELECTORS {
        String DATA_ROWS_ODD = "table.data tr.odd";
        String DATA_ROWS_EVEN = "table.data tr.even";
        String CELL_MAIN_LINK = "a.cellMainLink";
        String DATA_CELL = "td.center";
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

        static int parseLeech(String leech) {
            return Integer.parseInt(leech);
        }

        static String parseTorrentUrl(String url) {
            return url;
        }
    }

    private volatile String baseUrl;
    private volatile String searchPath;
    private volatile Proxy proxy;

    private Kat(Properties props) {
        baseUrl = props.getProperty(PropKeys.torrentengine_kat_url)
                .trim().toLowerCase();
        searchPath = props.getProperty(PropKeys.torrentengine_kat_searchPath)
                .trim().toLowerCase();

        if (props.getProperty(PropKeys.torrentengine_kat_proxyIsSet).
                trim().toLowerCase().equals("true")) {

            String host = props.getProperty(PropKeys.torrentengine_kat_proxyHost)
                    .trim().toLowerCase();
            int port = Integer.parseInt(props.getProperty(
                    PropKeys.torrentengine_kat_proxyPort).trim().toLowerCase());
            String type = props.getProperty(PropKeys.torrentengine_kat_proxyType)
                    .trim().toLowerCase();
            SocketAddress address = new InetSocketAddress(host, port);
            boolean isHttp = type.equals("http");
            proxy = new Proxy(isHttp ? Proxy.Type.HTTP : Proxy.Type.SOCKS, address);
        }
    }

    // Getters
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public Proxy getProxy() {
        return proxy;
    }

    // Setters
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

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
        return mainLink.attr("href").trim().toLowerCase();
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
            Document document = Jsoup.connect(fullUrl)
                    .proxy(this.proxy)
                    .get();
            return hasResults(document.html()) ? parseHtml(document) : Collections.emptyList();
        } catch (Exception ioe) {
            throw new TorrentEngineFailedException(ioe);
        }
    }

    synchronized static Kat getInstance(Properties properties) {
        if (instance == null)
            instance = new Kat(properties);

        return instance;
    }
}
