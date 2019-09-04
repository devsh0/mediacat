import mediacat.AppContext;
import mediacat.Utils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String body = new String(Utils.get("https://kickasstorrents.to"));
        System.out.println(body);
        AppContext.cleanup();
    }
}
