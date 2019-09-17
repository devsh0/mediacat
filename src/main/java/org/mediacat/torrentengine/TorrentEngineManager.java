package org.mediacat.torrentengine;

import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.utils.Object;
import org.mediacat.utils.Observer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TorrentEngineManager implements Observer {
    private static final java.lang.Object LOCK = new java.lang.Object();
    private static TorrentEngineManager instance;

    private TorrentEngineSettings engineSettings;
    private final List<TorrentEngine> engineImpls = new ArrayList<>();
    private final String methodName = "getInstance";

    private TorrentEngineManager(TorrentEngineSettings engineSettings) {
        this.engineSettings = engineSettings;
        setupEngines();
    }

    private void setupEngines() {
        List<String> engineImplNames = engineSettings.getEngineImplNames();
        engineImpls.clear();
        engineImplNames.clear();
        engineImplNames.addAll(engineSettings.getEngineImplNames());
        for (String implName : engineImplNames) {
            TorrentEngine temp = getEngineInstanceFor(implName);
            engineImpls.add(temp);
        }
    }

    private TorrentEngine getEngineInstanceFor(String implName) {
        try {
            Class<?> clazz = Class.forName(implName);
            Method method = clazz.getDeclaredMethod(methodName, String.class, String.class, Proxy.class);
            String baseUrl = engineSettings.getBaseUrlFor(implName);
            String searchPath = engineSettings.getSearchPathFor(implName);
            Proxy proxy = engineSettings.getProxyFor(implName);
            java.lang.Object temp = method.invoke(null, baseUrl, searchPath, proxy);
            return (TorrentEngine) temp;
        } catch (ClassNotFoundException |
                NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    // todo: caller must also specify how to filter the torrents
    public List<TorrentMeta> getTorrentMeta(String searchTerm) {
        synchronized (LOCK) {
            try {
                // todo: more complex logic to determine which engine to use
                TorrentEngine engine = engineImpls.get(0);
                String engineName = engine.getClass().getCanonicalName();
                return engine.getTorrentMeta(searchTerm).stream()
                        .map(meta -> meta.setEngineName(engineName))
                        .collect(Collectors.toList());
            } catch (TorrentEngineFailedException exc) {
                // todo: cycle through engines
            }
        }

        // todo: must not return null
        return null;
    }

    public String getMagnetOf(TorrentMeta meta) throws TorrentEngineFailedException {
        synchronized (LOCK) {
            String magnet = meta.getMagnetUrl();
            if (magnet != null && magnet.startsWith("magnet:"))
                return magnet;

            String engineName = meta.getEngineName();
            TorrentEngine usedEngine = engineImpls.stream()
                    .filter(e -> e.getClass().getCanonicalName().equals(engineName))
                    .findFirst().orElse(null);

            return Objects.requireNonNull(usedEngine)
                    .getMagnet(meta.getTorrentUrl());
        }
    }

    @Override
    public void update(Object object) {
        synchronized (LOCK) {
            engineSettings = (TorrentEngineSettings) object;

            // updates state of all impls, without analyzing the changes..duh!
            for (TorrentEngine engine : engineImpls) {
                String implName = engine.getClass().getCanonicalName();
                String baseUrl = engineSettings.getBaseUrlFor(implName);
                String searchPath = engineSettings.getSearchPathFor(implName);
                Proxy proxy = engineSettings.getProxyFor(implName);

                engine.setBaseUrl(baseUrl);
                engine.setSearchPath(searchPath);
                engine.setProxy(proxy);
            }
        }
    }

    public static TorrentEngineManager getInstance(TorrentEngineSettings settings) {
        synchronized (LOCK) {
            if (instance == null)
                instance = new TorrentEngineManager(settings);

            return instance;
        }
    }
}
