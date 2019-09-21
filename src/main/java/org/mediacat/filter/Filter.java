package org.mediacat.filter;

import org.mediacat.torrent.TorrentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filter {
    private boolean includeUntrusted;
    private Quality[] allowedQualities = new Quality[]{
            Quality.THEATRE,
            Quality.HD,
            Quality.BLURAY
    };

    private Filter() {
    }

    public List<TorrentInfo> filter(List<TorrentInfo> infoList) {
        List<TorrentInfo> filtered = new ArrayList<>();

        for (var info : infoList) {
            if (Arrays.asList(allowedQualities).contains(info.getQuality())) {
                if (!info.byTrustedUploader() && !includeUntrusted)
                    continue;
                filtered.add(info);
            }
        }
        return filtered;
    }

    public static class FilterBuilder {
        private final Filter instance = new Filter();

        public FilterBuilder includeUntrusted(boolean b) {
            instance.includeUntrusted = b;
            return this;
        }

        public FilterBuilder allowQualities(Quality... qualities) {
            instance.allowedQualities = qualities;
            return this;
        }

        public Filter build() {
            return instance;
        }
    }

    public static FilterBuilder builder() {
        return new FilterBuilder();
    }
}
