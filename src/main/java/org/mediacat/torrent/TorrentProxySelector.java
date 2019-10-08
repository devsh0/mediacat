package org.mediacat.torrent;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class TorrentProxySelector extends ProxySelector {
    private final Map<String, Proxy> proxyMap;
    TorrentProxySelector(Map<String, Proxy> proxyMap) {
        this.proxyMap = proxyMap;
    }

    @Override
    public List<Proxy> select(URI uri) {
        for (var key : proxyMap.keySet()) {
            String uriStr = uri.toString();
            if (uriStr.contains(key))
                return List.of(proxyMap.get(key));
        }

        return List.of(Proxy.NO_PROXY);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        ioe.printStackTrace(System.err);
    }
}
