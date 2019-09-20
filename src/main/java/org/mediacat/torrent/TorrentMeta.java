package org.mediacat.torrent;

import org.mediacat.filter.Quality;
import org.mediacat.settings.TorrentEngineSettings;

public class TorrentMeta {
    private final String name;
    private final long sizeInBytes;
    private final int ageInDays;
    private final int seed;
    private final int leech;
    private final String torrentUrl;
    private final Quality quality;

    private volatile boolean trustedUploader;
    private volatile String magnetUrl;
    private volatile String engineName;

    TorrentMeta(String name, long sizeInBytes, int ageInDays, int seed,
                int leech, String torrentUrl) {
        this.name = name;
        this.quality = Quality.fromName(name);
        this.sizeInBytes = sizeInBytes;
        this.ageInDays = ageInDays;
        this.seed = seed;
        this.leech = leech;
        this.torrentUrl = torrentUrl;
    }

    TorrentMeta(String name, long sizeInBytes, int ageInDays, int seed, int leech,
                String torrentUrl, String magnetUrl) {
        this(name, sizeInBytes, ageInDays, seed, leech, torrentUrl);
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

    public boolean isTrustedUploader() {
        return trustedUploader;
    }

    public String getTorrentUrl() {
        return this.torrentUrl;
    }

    public String getMagnetUrl() throws TorrentEngineFailedException {
        if (magnetUrl == null)
            magnetUrl = TorrentEngineManager
                    .getInstance(TorrentEngineSettings.getInstance())
                    .getMagnetOf(this);
        return magnetUrl;
    }

    public String getEngineName() {
        return this.engineName;
    }

    public TorrentMeta setMagnetUrl(String magnetUrl) {
        this.magnetUrl = magnetUrl;
        return this;
    }

    public TorrentMeta setEngineName(String name) {
        this.engineName = name;
        return this;
    }

    public TorrentMeta setTrustedUploader(boolean b) {
        trustedUploader = b;
        return this;
    }

    @Override
    public String toString() {
        return "[" +
                "Name: " + this.name + ", " +
                "Size: " + this.sizeInBytes + " bytes, " +
                "Age: " + this.ageInDays + " days, " +
                "Seed: " + this.seed + ", " +
                "Leech: " + this.leech + ", " +
                "URL: " + this.torrentUrl + ", " +
                "Magnet: " + this.magnetUrl +
                "]";
    }

    public boolean equals(TorrentMeta meta) {
        return this.torrentUrl.equals(meta.torrentUrl);
    }
}
