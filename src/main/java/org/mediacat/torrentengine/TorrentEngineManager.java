package org.mediacat.torrentengine;

import org.mediacat.PropKeys;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.net.Proxy.Type;

public class TorrentEngineManager {
    private static final List<TorrentEngineState> engineStates = new CopyOnWriteArrayList<>();

    TorrentEngineManager(Properties props) {
        configureEngines(props);
    }

    private void configureEngines(Properties props) {
        Proxy globalProxy;
        boolean proxyIsSet = props.getProperty(PropKeys.engine.proxyIsSet).equals("true");
        if (proxyIsSet) {
            String typeStr = props.getProperty(PropKeys.engine.proxyType);
            Type type = typeStr.equals("http") ? Type.HTTP : Type.SOCKS;

            String host = props.getProperty(PropKeys.engine.proxyHost);
            int port = Integer.parseInt(props.getProperty(PropKeys.engine.proxyPort));

            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            globalProxy = new Proxy(type, socketAddress);
        }
    }

    synchronized static void registerEngine(TorrentEngine e) {
        for (TorrentEngineState state : engineStates) {
            TorrentEngine t = state.getEngine();
            if (t.getClass().getName().equals(e.getClass().getName()))
                return;

            engineStates.add(new TorrentEngineState(e));
        }
    }
}
