package org.mediacat.torrent_engine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mediacat.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KickassTorrents implements TorrentEngine {
    static final KickassTorrents INSTANCE = new KickassTorrents();
    private static final String BASE_URL = "https://kickasstorrents.to/usearch/";

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

            return (long)size;
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
    }

    private static String extractName(Element row) {
        Element mainLink = row.selectFirst(SELECTORS.CELL_MAIN_LINK);
        String innerText = mainLink.text().trim();
        return innerText.replaceAll("\\s", ".");
    }

    private static String extractCellData(Element row, int cellPosition) {
        Element cell = row.select(SELECTORS.DATA_CELL).get(cellPosition);
        return cell.text().trim();
    }

    private static List<TorrentMeta> parseHtml(String html) {
        List<TorrentMeta> metas = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        List<Element> rows = doc.select(SELECTORS.DATA_ROWS_ODD);
        rows.addAll(doc.select(SELECTORS.DATA_ROWS_EVEN));

        for (Element row : rows) {
            String name = extractName(row).replace(".", " ");
            long size = Parser.parseSize(extractCellData(row, 0));
            int age = Parser.parseAge(extractCellData(row, 2));
            int seed = Parser.parseSeed(extractCellData(row, 3));
            int leech = Parser.parseLeech(extractCellData(row, 4));
            metas.add(new TorrentMeta(name, size, age, seed, leech));
        }

        return metas;
    }

    private static boolean hasResults(String html) {
        final String dummyHash = "E14B6EDF4AF7723D721342576D6CEC96A01C0247";
        return !html.contains(dummyHash);
    }

    private KickassTorrents() {
    }

    @Override
    public List<TorrentMeta> getTorrentMeta(String searchTerm) throws TorrentEngineFailedException {
        try {
            String fullUrl = BASE_URL + searchTerm;
            String html = new String(Utils.get(fullUrl));
            return hasResults(html) ? parseHtml(html) : Collections.emptyList();
        }
        catch (Exception ioe) {
           throw new TorrentEngineFailedException(ioe);
        }
    }
}
