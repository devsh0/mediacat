package org.mediacat.settings;

import org.mediacat.utils.Observable;
import org.mediacat.utils.Observer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

// todo: make ports to edit settings
final public class TorrentEngineSettings implements Observable {
    private static final Object LOCK = new java.lang.Object();
    private static TorrentEngineSettings instance;

    private interface Key {
        String impls = "org.mediacat.torrent.impls";
        String fetchCount = "org.mediacat.torrent.fetchCount";

        // settings that apply to all engines
        interface global {
            interface proxy {
                String isSet = "org.mediacat.torrent.*.proxy.isSet";
                String type = "org.mediacat.torrent.*.proxy.type";
                String host = "org.mediacat.torrent.*.proxy.host";
                String port = "org.mediacat.torrent.*.proxy.port";
            }
        }

        // trailers of settings that apply to individual engines
        interface engine {
            String url = ".url";
            String searchPath = ".searchPath";

            interface proxy {
                String isSet = ".proxy.isSet";
                String type = ".proxy.type";
                String host = ".proxy.host";
                String port = ".proxy.port";
            }
        }
    }

    private final Properties props = new Properties();
    private final List<String> engineImplNames = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();

    private TorrentEngineSettings(InputStream is) {
        reload(is);
    }

    public void reload(InputStream is) {
        synchronized (LOCK) {
            try {
                props.clear();
                props.load(is);
                loadEngineImpls();
                broadcast();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    public void reload(String filePath) {
        Path p = Paths.get(filePath);
        try (InputStream is = Files.newInputStream(p)) {
            reload(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadEngineImpls() {
        String[] all = props.getProperty(Key.impls).split(",");
        engineImplNames.addAll(Arrays.asList(all));
    }

    private void checkImpl(String implName) {
        if (!engineImplNames.contains(implName))
            throw new IllegalArgumentException("engine implementation does not exist");
    }

    public boolean isProxySetFor(String impl) {
        synchronized (LOCK) {
            if (impl.equals("*"))
                return props.getProperty(Key.global.proxy.isSet).equals("true");

            checkImpl(impl);
            String propertyKey = impl + Key.engine.proxy.isSet;
            return props.getProperty(propertyKey).equals("true");
        }
    }

    // todo: proxy is broken
    public Proxy getProxyFor(String impl) {
        synchronized (LOCK) {
            if (isProxySetFor(impl)) {
                boolean isGlobal = impl.equals("*");

                String typeStr = isGlobal ? props.getProperty(Key.global.proxy.type)
                        : props.getProperty(impl + Key.engine.proxy.type);
                Proxy.Type type = typeStr.equals("http")
                        ? Proxy.Type.HTTP : Proxy.Type.SOCKS;
                String host = isGlobal ? props.getProperty(Key.global.proxy.host)
                        : props.getProperty(impl + Key.engine.proxy.host);
                int port = isGlobal ? Integer.parseInt(props.getProperty(Key.global.proxy.port))
                        : Integer.parseInt(props.getProperty(impl + Key.engine.proxy.port));

                InetSocketAddress address = new InetSocketAddress(host, port);
                return new Proxy(type, address);
            }

            return Proxy.NO_PROXY;
        }
    }

    public String getBaseUrlFor(String impl) {
        synchronized (LOCK) {
            checkImpl(impl);
            return props.getProperty(impl + Key.engine.url);
        }
    }

    public String getSearchPathFor(String impl) {
        synchronized (LOCK) {
            checkImpl(impl);
            return props.getProperty(impl + Key.engine.searchPath);
        }
    }

    public List<String> getEngineImplNames() {
        synchronized (LOCK) {
            return new ArrayList<>(engineImplNames);
        }
    }

    public int getFetchCount() {
        synchronized (LOCK) {
            return Integer.parseInt(props.getProperty(Key.fetchCount));
        }
    }

    @Override
    public void register(Observer observer) {
        synchronized (LOCK) {
            observers.add(observer);
        }
    }

    @Override
    public void broadcast() {
        synchronized (LOCK) {
            for (Observer o : observers) {
                o.update(this);
            }
        }
    }

    public static TorrentEngineSettings getInstance(InputStream... is) {
        synchronized (LOCK) {
            if (instance == null) {
                if (is.length == 0)
                    throw new IllegalStateException("settings not loaded yet..." +
                            "input stream is required");
                instance = new TorrentEngineSettings(is[0]);
            }
        }
        return instance;
    }

    public static TorrentEngineSettings getInstance(String filePath) {
        Path p = Paths.get(filePath);
        TorrentEngineSettings tes;
        try (InputStream is = Files.newInputStream(p)) {
            tes = getInstance(is);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return tes;
    }
}
