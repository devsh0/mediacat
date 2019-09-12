package org.mediacat.torrent_engine;

public class TorrentMeta {
    private final String name;
    private final long sizeInBytes;
    private final int ageInDays;
    private final int seed;
    private final int leech;

    TorrentMeta(String name, long sizeInBytes, int ageInDays, int seed, int leech) {
        this.name = name;
        this.sizeInBytes = sizeInBytes;
        this.ageInDays = ageInDays;
        this.seed = seed;
        this.leech = leech;
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

    @Override
    public String toString () {
        return "[" +
                "Name: " + this.name + ", " +
                "Size: " + this.sizeInBytes + " bytes, " +
                "Age: " + this.ageInDays + " days, " +
                "Seed: " + this.seed + ", " +
                "Leech: " + this.leech +
                "]";
    }
}
