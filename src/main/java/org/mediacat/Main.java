package org.mediacat;

import org.mediacat.filter.Quality;

public class Main {
    public static void main(String[] args) {
        String name = "Kabir Singh 2019 Hindi 720p V2 PreDVD Rip x264 AAC 1 2GB[MB]";
        System.out.println(Quality.fromName(name));
    }
}
