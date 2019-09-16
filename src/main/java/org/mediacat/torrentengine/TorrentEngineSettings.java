package org.mediacat.torrentengine;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

class TorrentEngineSettings {
    private static final Object LOCK = new Object();
    private static TorrentEngineSettings instance;

    private interface Key {
        String impls = "org.mediacat.torrentengine.impls";

        // settings that apply to all engines
        interface global {
            interface proxy {
                String isSet = "org.mediacat.torrentengine.*.proxy.isSet";
                String type = "org.mediacat.torrentengine.*.proxy.type";
                String host = "org.mediacat.torrentengine.*.proxy.host";
                String port = "org.mediacat.torrentengine.*.proxy.port";
            }
        }

        // settings that apply to individual engines
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
    private final List<String> engineImpls = new ArrayList<>();

    TorrentEngineSettings(InputStream is) {
        load(is);
    }

    private boolean validateImpl(String impl) {
        if (!engineImpls.contains(impl))
            throw new IllegalArgumentException("Engine implementation does not exist");

        return true;
    }

    private void loadEngineImpls() {
        String[] all = props.getProperty(Key.impls).split(",");
        engineImpls.addAll(Arrays.asList(all));
    }

    void load(InputStream is) {
        synchronized (LOCK) {
            try {
                this.props.clear();
                this.props.load(is);
                this.loadEngineImpls();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private boolean isProxySetFor(String impl) {
        synchronized (LOCK) {
            if (impl.equals("*"))
                return props.getProperty(Key.global.proxy.isSet).equals("true");

            validateImpl(impl);
            String propertyKey = impl + Key.engine.proxy.isSet;
            return props.getProperty(propertyKey).equals("true");
        }
    }

    Proxy getProxyFor(String impl) {
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

                SocketAddress address = new InetSocketAddress(host, port);
                return new Proxy(type, address);
            }

            return Proxy.NO_PROXY;
        }
    }

    String getUrlFor(String impl) {
        synchronized (LOCK) {
            validateImpl(impl);
            return props.getProperty(impl + Key.engine.url);
        }
    }

    String getSearchPathFor(String impl) {
        synchronized (LOCK) {
            validateImpl(impl);
            return props.getProperty(impl + Key.engine.searchPath);
        }
    }

    List<String> getEngineImpls() {
        synchronized (LOCK) {
            return new ArrayList<>(engineImpls);
        }
    }

    static TorrentEngineSettings instance(InputStream is) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new TorrentEngineSettings(is);
            }
        }
        return instance;
    }

    static TorrentEngineSettings instance(String filePath) {
        Path p = Paths.get(filePath);
        TorrentEngineSettings tes;
        try (InputStream is = Files.newInputStream(p)) {
            tes = instance(is);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return tes;
    }
}
