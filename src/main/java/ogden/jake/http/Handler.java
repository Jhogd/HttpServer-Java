package ogden.jake.http;

import java.io.IOException;
import java.net.Socket;

public interface Handler {
    void handle(Socket socket) throws IOException, InterruptedException;
    void addServe(String resource, Serve app);
}
