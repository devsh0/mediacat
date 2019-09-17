package org.mediacat.torrentengine;

import org.mediacat.settings.TorrentEngineSettings;
import org.mediacat.utils.Object;
import org.mediacat.utils.Observer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class TorrentEngineManager implements Observer {
    private static final java.lang.Object LOCK = new java.lang.Object();
    private static TorrentEngineManager instance;

    private TorrentEngineSettings engineSettings;
    private final List<TorrentEngine> engineImples = new ArrayList<>();
    private final String methodName = "getInstance";

    private TorrentEngineManager(TorrentEngineSettings engineSettings) {
        this.engineSettings = engineSettings;
        setupEngines();
    }

    private void setupEngines() {
        List<String> engineImplNames = engineSettings.getEngineImplNames();
        engineImples.clear();
        engineImplNames.clear();
        engineImplNames.addAll(engineSettings.getEngineImplNames());
        for (String implName : engineImplNames) {
            TorrentEngine temp = getEngineInstanceFor(implName);
            engineImples.add(temp);
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

    public List<TorrentMeta> getTorrentMeta(String searchTerm) throws TorrentEngineFailedException {
        return engineImples.get(0).getTorrentMeta(searchTerm);
    }

    @Override
    public void update(Object object) {
        synchronized (LOCK) {
            this.engineSettings = (TorrentEngineSettings) object;

            // updates state of all impls, without analyzing the changes..duh!
            for (TorrentEngine engine : engineImples) {
                String implName = engine.getClass().getCanonicalName();
                String baseUrl = this.engineSettings.getBaseUrlFor(implName);
                String searchPath = this.engineSettings.getSearchPathFor(implName);
                Proxy proxy = this.engineSettings.getProxyFor(implName);

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
