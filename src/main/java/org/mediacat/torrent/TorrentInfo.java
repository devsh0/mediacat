package org.mediacat.torrent;

import org.mediacat.filter.Quality;
import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.utils.Utils;

public class TorrentInfo implements Comparable<TorrentInfo> {
    private final String name;
    private final long sizeInBytes;
    private final int ageInDays;
    private final int seed;
    private final int leech;
    private final String torrentUrl;
    private final Quality quality;
    private final String engineName;

    private volatile boolean trustedUploader;
    private volatile String magnetUrl;

    TorrentInfo(String engineName, String torrentName, long sizeInBytes, int ageInDays, int seed,
                int leech, String torrentUrl) {
        this.engineName = engineName;
        this.name = torrentName;
        this.quality = Quality.fromName(torrentName);
        this.sizeInBytes = sizeInBytes;
        this.ageInDays = ageInDays;
        this.seed = seed;
        this.leech = leech;
        this.torrentUrl = torrentUrl;
    }

    TorrentInfo(String engineName, String torrentName, long sizeInBytes, int ageInDays,
                int seed, int leech, String torrentUrl, String magnetUrl) {
        this(engineName, torrentName, sizeInBytes, ageInDays, seed, leech, torrentUrl);
        this.magnetUrl = magnetUrl;
    }

    public String getName() {
        return name;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public int getAgeInDays() {
        return ageInDays;
    }

    public int getSeed() {
        return seed;
    }

    public int getLeech() {
        return leech;
    }

    public Quality getQuality() {
        return quality;
    }

    public boolean byTrustedUploader() {
        return trustedUploader;
    }

    public String getTorrentUrl() {
        return this.torrentUrl;
    }

    public String getMagnet() throws TorrentEngineFailedException {
        if (magnetUrl == null)
            magnetUrl = TorrentEngineManager
                    .getInstance(TorrentEngineSettings.getInstance())
                    .getMagnetOf(this);
        return magnetUrl;
    }

    public String getEngineName() {
        return this.engineName;
    }

    public TorrentInfo setMagnetUrl(String magnetUrl) {
        this.magnetUrl = magnetUrl;
        return this;
    }

    public TorrentInfo setTrustedUploader(boolean b) {
        trustedUploader = b;
        return this;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + "\n" +
                "Size: " + Utils.sizeInBytesToReadable(this.sizeInBytes) + "\n" +
                "Age: " + this.ageInDays + " days\n" +
                "Seed: " + this.seed + "\n" +
                "Leech: " + this.leech + "\n" +
                "URL: " + this.torrentUrl + "\n" +
                "Magnet: " + this.magnetUrl + "\n";
    }

    public boolean equals(TorrentInfo meta) {
        return this.torrentUrl.equals(meta.torrentUrl);
    }

    @Override
    public int compareTo(TorrentInfo o) {
        // sort by seed in descending order
        return o.getSeed() - seed;
    }
}
