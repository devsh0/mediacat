package org.mediacat.torrent;

import org.mediacat.filter.Filter;
import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.utils.Observer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final public class TorrentEngineManager implements Observer {
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

    private List<TorrentInfo> infoListHelper(TorrentEngine engine, String search, Filter filter)
            throws TorrentEngineFailedException {
        List<TorrentInfo> infos = new ArrayList<>();
        int fetchCount = engineSettings.getFetchCount();
        int page = 0;
        do {
            ++page;
            var temp = engine.getTorrentInfoList(search, page);

            /* To lower the chances of getting banned, we first apply the
            * filters and then check the filtered list for emptiness. It
            * is assumed that if the Nth page does not return any "valid"
            * result, subsequent pages won't either */
            temp = filter.filter(temp);

            if (temp.isEmpty())
                break;

            // more $needed TorrentInfo required
            int needed = fetchCount - infos.size();
            int size = temp.size();
            infos.addAll(temp.subList(0, Math.min(needed, size)));
        } while (infos.size() < fetchCount);
        return infos;
    }

    public List<TorrentInfo> getTorrentInfoList(String searchTerm, Filter filter)
            throws TorrentEngineFailedException {
        synchronized (LOCK) {
            var engine = getCurrentEngine();
            List<TorrentInfo> infoList = infoListHelper(engine, searchTerm, filter);
            int activeEngine = engineIndex;

            while (infoList.isEmpty()) {
                try {
                    engine = incrementIndexAndGetEngine();
                    infoList = infoListHelper(engine, searchTerm, filter);
                } catch (TorrentEngineFailedException e) {
                    // since all the engines have failed and engineIndex
                    //  is in an invalid state, we reset the index where it
                    //  was before processing this search
                    engineIndex = activeEngine;
                    throw e;
                }
            }

            Collections.sort(infoList);
            return infoList;
        }
    }

    private TorrentEngine getUsedEngine(TorrentInfo info) {
        String engineName = info.getEngineName();
        var usedEngine = engineImpls.stream()
                .filter(e -> e.getClass()
                        .getCanonicalName()
                        .equals(engineName))
                .findFirst().orElse(null);

        return Objects.requireNonNull(usedEngine);
    }

    String getMagnetOf(TorrentInfo info) throws TorrentEngineFailedException {
        // Some websites don't expose magnet urls in search results
        //  This is a helper method that is invoked by TorrentInfo class
        //  when the magnet is finally queried
        synchronized (LOCK) {
            TorrentEngine usedEngine = getUsedEngine(info);
            String magnet = usedEngine.getMagnet(info.getTorrentUrl());
            info.setMagnetUrl(magnet);
            return magnet;
        }
    }

    private void resetEngineIndex() {
        engineIndex = 0;
    }

    private void reset(TorrentEngineSettings settings) {
        engineSettings = settings;
        //resetEngineIndex();

        // setupEngines must not be called here because we don't
        //  want to break singleton-ness of engines via reflection
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
