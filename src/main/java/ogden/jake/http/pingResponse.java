package ogden.jake.http;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ogden.jake.http.HttpHandler.htmlHeader;

public class pingResponse implements Serve{

    @Override
    public void sendResponse(OutputStream out, String resource) throws InterruptedException, IOException {
        Thread.sleep(1000);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String response =  htmlHeader("text/plain") + "\r\n"
                + "Current Time: " + currentTime.format(formatter);
        out.write(response.getBytes());

    }

}

