package org.mediacat.torrent;

import org.mediacat.filter.FilterCriteria;
import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.utils.Observer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TorrentEngineManager implements Observer {
    private static final java.lang.Object LOCK = new java.lang.Object();
    private static TorrentEngineManager instance;

    private TorrentEngineSettings engineSettings;
    private final List<TorrentEngine> engineImpls = new ArrayList<>();
    private final String methodName = "getInstance";
    private int engineIndex = 0;

    private TorrentEngineManager(TorrentEngineSettings engineSettings) {
        this.engineSettings = engineSettings;
        setupEngines();
    }

    private void setupEngines() {
        List<String> engineImplNames = engineSettings.getEngineImplNames();
        engineImpls.clear();
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
            Object temp = method.invoke(null, baseUrl, searchPath, proxy);
            return (TorrentEngine) temp;
        } catch (ClassNotFoundException |
                NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TorrentInfo> filter(List<TorrentInfo> infos, FilterCriteria c) {
        List<TorrentInfo> filtered = new ArrayList<>();
        for (var torrent : infos) {
            if (Arrays.asList(c.allowedQualities).contains(torrent.getQuality())) {
                if (!torrent.byTrustedUploader() && !c.includeUntrusted)
                    continue;
                filtered.add(torrent);
            }
        }
        return filtered;
    }

    private TorrentEngine getCurrentEngine() {
        return engineImpls.get(engineIndex);
    }

    private TorrentEngine incrementIndexAndGetEngine() throws TorrentEngineFailedException {
        while (true) {
            if (++engineIndex == engineImpls.size())
                throw new TorrentEngineFailedException("ran out of engines");
            var engine = engineImpls.get(engineIndex);
            if (!engine.isFailing())
                return engine;
        }
    }

    private List<TorrentInfo> infoListHelper(TorrentEngine engine, String searchTerm, FilterCriteria fc)
            throws TorrentEngineFailedException {
        List<TorrentInfo> infos = new ArrayList<>();
        int fetchCount = engineSettings.getFetchCount();
        int page = 0;
        do {
            ++page;
            var temp = engine.getTorrentInfoList(searchTerm, page);
            temp = filter(temp, fc);

            // we're outta results
            if (temp.isEmpty())
                break;

            // we need this many
            int needed = fetchCount - infos.size();
            int size = temp.size();
            infos.addAll(temp.subList(0, Math.min(needed, size)));
        } while (infos.size() < fetchCount);
        return infos;
    }

    public List<TorrentInfo> getTorrentInfoList(String searchTerm, FilterCriteria fc)
            throws TorrentEngineFailedException {
        synchronized (LOCK) {
            List<TorrentInfo> infoList;
            var engine = getCurrentEngine();
            int workingEngine = engineIndex;

            do {
                infoList = infoListHelper(engine, searchTerm, fc);
                if (infoList.isEmpty()) {
                    try {
                        engine = incrementIndexAndGetEngine();
                    } catch (TorrentEngineFailedException e) {
                        // since all the engines have failed and engineIndex
                        //  is in an invalid state, we reset the index where it
                        //  was before processing this search
                        engineIndex = workingEngine;
                        throw e;
                    }
                }
            } while (infoList.isEmpty());

            return infoList;
        }
    }

    String getMagnetOf(TorrentInfo info) throws TorrentEngineFailedException {
        synchronized (LOCK) {
            String magnet = info.getMagnetUrl();
            if (magnet != null && magnet.startsWith("magnet:"))
                return magnet;

            String engineName = info.getEngineName();
            TorrentEngine usedEngine = engineImpls.stream()
                    .filter(e -> e.getClass()
                            .getCanonicalName()
                            .equals(engineName))
                    .findFirst().orElse(null);

            magnet = Objects.requireNonNull(usedEngine)
                    .getMagnet(info.getTorrentUrl());

            info.setMagnetUrl(magnet);
            return magnet;
        }
    }

    private void resetEngineIndex() {
        engineIndex = 0;
    }

    private void reset(TorrentEngineSettings settings) {
        // reset engine settings and index
        engineSettings = settings;
        resetEngineIndex();

        /* setupEngines must not be called here because we don't
         * want to break singleton-ness of engines via reflection */
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

    @Override
    public void update(Object object) {
        synchronized (LOCK) {
            // when updates arrive, reset the state of manager
            reset((TorrentEngineSettings) object);
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
