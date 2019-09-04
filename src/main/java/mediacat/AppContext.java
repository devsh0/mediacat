package mediacat;

import okhttp3.OkHttpClient;

import java.time.Duration;

public class AppContext {
    // todo: load from configs
    private static final long CALL_TIMEOUT = 10000;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .callTimeout(Duration.ofMillis(CALL_TIMEOUT))
            .build();
}
