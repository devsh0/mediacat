package mediacat;

import okhttp3.OkHttpClient;

import java.time.Duration;

public class AppContext {
    // todo: load this from configs
    private static final long CALL_TIMEOUT = 10000;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .callTimeout(Duration.ofMillis(CALL_TIMEOUT))
            .build();

    public static void cleanup () {
        HTTP_CLIENT.dispatcher().executorService().shutdownNow();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
