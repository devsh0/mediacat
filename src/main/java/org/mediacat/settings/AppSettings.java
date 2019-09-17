package org.mediacat.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppSettings {
    private static final Object LOCK = new Object();
    private static AppSettings instance;

    private final Properties properties = new Properties();

    private interface Key {
        String name = "org.mediacat.app.name";
        String version = "org.mediacat.app.version";
    }

    public AppSettings(InputStream is) {
        try {
            this.properties.load(is);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public String getAppName() {
        synchronized (LOCK) {
            return properties.getProperty(Key.name);
        }
    }

    public String getVersion() {
        synchronized (LOCK) {
            return properties.getProperty(Key.version);
        }
    }

    public static AppSettings getInstance(InputStream is) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new AppSettings(is);
            }
        }
        return instance;
    }

    public static AppSettings getInstance(String filePath) {
        Path p = Paths.get(filePath);
        AppSettings appSettings;
        try (InputStream is = Files.newInputStream(p)) {
            appSettings = getInstance(is);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return appSettings;
    }
}
