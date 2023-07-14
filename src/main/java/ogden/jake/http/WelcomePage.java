package ogden.jake.http;

import java.io.IOException;
import java.io.OutputStream;

import static ogden.jake.http.HttpHandler.htmlHeader;

public class WelcomePage implements Serve{

    @Override
    public void sendResponse(OutputStream output, String resource, String data) throws InterruptedException, IOException {
        String response = htmlHeader("text/html") +
                "\r\n"
                + "<html><body><h1>Welcome!</h1></body></html>";
        output.write(response.getBytes());
    }
    }

