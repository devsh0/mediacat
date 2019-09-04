package mediacat;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Utils {
    @SuppressWarnings("ConstantConditions")
    public static byte[] get (String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = AppContext.HTTP_CLIENT.newCall(request).execute();

        if (response.body() == null)
            throw new RuntimeException("no body returned in response");

        if (!response.isSuccessful())
            throw new RuntimeException("bad response received");

        return response.body().bytes();
    }
}
