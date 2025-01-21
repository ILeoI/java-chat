package Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageReceiver {

    private final BufferedReader reader;

    public MessageReceiver(final InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public final String getLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
