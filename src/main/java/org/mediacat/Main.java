package org.mediacat;

import org.mediacat.filter.Quality;

public class Main {
    public static void main(String[] args) {
        String name = "Seth Meyers 2019 09 18 Chelsea Handler WEB x264 TRUMP [eztv]";
        System.out.println(Quality.fromName(name));
    }
}
