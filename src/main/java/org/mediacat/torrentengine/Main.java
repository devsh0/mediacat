package org.mediacat.torrentengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get("config.properties")));
        System.out.println(properties.getProperty("torrentengine.proxy"));
    }
}
