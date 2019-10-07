package org.mediacat.torrent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mediacat.utils.Utils;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Kat extends AbstractEngine {
    private static final Object LOCK = new Object();
    private static Kat instance;

    private interface SELECTORS {
        String DATA_ROWS_ODD = "table.data tr.odd";
        String DATA_ROWS_EVEN = "table.data tr.even";
        String CELL_MAIN_LINK = "a.cellMainLink";
        String DATA_CELL = "td.center";
        String MAGNET_LINK = "a[title='Magnet link']";
        String TRUSTED_UPLOADED = "i[title='Verified Uploader']";
    }

    private final String engineName;
    private volatile boolean isFailing;

    private Kat(String baseUrl, String searchPath, Proxy proxy) {
        super(baseUrl, searchPath, proxy);
        this.engineName = this.getClass().getCanonicalName();
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

    @Override
    public boolean isFailing() {
        return isFailing;
    }

    //todo: proxy is broken
    private Document getDocument(String url) throws IOException {
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

    private boolean isTrustedUploader(Element row) {
        return row.select(SELECTORS.TRUSTED_UPLOADED).size() > 0;
    }

    private List<TorrentInfo> collectTorrentInfo(Document doc) {
        List<TorrentInfo> infoList = new ArrayList<>();
        List<Element> rows = doc.select(SELECTORS.DATA_ROWS_ODD);
        rows.addAll(doc.select(SELECTORS.DATA_ROWS_EVEN));

        for (Element row : rows) {
            String torrentName = extractName(row).replace(".", " ");
            long size = Utils.parseMediaSize(extractSimpleCellData(row, 0));
            int age = Utils.parseMediaAge(extractSimpleCellData(row, 2));
            int seed = Integer.parseInt(extractSimpleCellData(row, 3));
            int leech = Integer.parseInt(extractSimpleCellData(row, 4));
            String torUrl = extractTorrentUrl(row);
            String fullTorUrl = baseUrl + (torUrl.startsWith("/") ? "" : "/") + torUrl;
            boolean trustedUploader = isTrustedUploader(row);

            TorrentInfo meta = new TorrentInfo(engineName,
                    torrentName, size, age, seed, leech, fullTorUrl)
                    .setTrustedUploader(trustedUploader);

            infoList.add(meta);
        }

        return infoList;
    }

    private boolean hasResults(String html) {
        final String dummyHash = "E14B6EDF4AF7723D721342576D6CEC96A01C0247";
        return !html.contains(dummyHash);
    }

    @Override
    public List<TorrentInfo> getTorrentInfoList(String searchTerm, int pageNo)
            throws TorrentEngineFailedException {
        try {
            String fullUrl = baseUrl + searchPath + searchTerm + "/" + pageNo;
            Document document = getDocument(fullUrl);
            return hasResults(document.html()) ?
                    collectTorrentInfo(document) : Collections.emptyList();
        } catch (Exception e) {
            isFailing = true;
            throw new TorrentEngineFailedException(e);
        }
    }

    static TorrentEngine getInstance(String baseUrl, String searchPath, Proxy proxy) {
        synchronized (LOCK) {
            if (instance == null)
                instance = new Kat(baseUrl, searchPath, proxy);
        }
        return instance;
    }
}
