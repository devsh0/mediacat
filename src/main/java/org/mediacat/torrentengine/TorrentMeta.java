package org.mediacat.torrentengine;

public class TorrentMeta {
    private final String name;
    private final long sizeInBytes;
    private final int ageInDays;
    private final int seed;
    private final int leech;
    private final String torrentUrl;
    private volatile String magnetUrl;

    private volatile String engineName;

    TorrentMeta(String name, long sizeInBytes, int ageInDays, int seed,
                int leech, String torrentUrl) {
        this.name = name;
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

    public String getTorrentUrl() {
        return this.torrentUrl;
    }

    public String getMagnetUrl() {
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

    @Override
    public String toString () {
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
