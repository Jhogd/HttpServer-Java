package ogden.jake.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MockHandler implements Handler {
    public Socket socket;
    public boolean wasCalled;
    public String resource;


    //  @Override
    public void handle(Socket socket) {
        this.socket = socket;
        this.wasCalled = true;

    }

    @Override
    public void addServe(String resource, Serve app) {

    }

    public void handleStreams(BufferedReader input, OutputStream output) throws IOException, InterruptedException {
        GetRequest newGetRequest = new GetRequest(input);
        try {
            newGetRequest.getPieces();
        } catch (NullPointerException | IOException ignored) {
        }
        try {
            this.resource = newGetRequest.resource;
        } catch (NullPointerException ignored) {
        }
        if ("GET".equals(newGetRequest.method)) {
            if (resource.equals("/ping")) {
                servePingResponse(output);
            }

        }
    }

    public void servePingResponse(OutputStream out) throws InterruptedException, IOException {
        LocalDateTime currentTime =LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n\r\n"
                + "Current Time: " + currentTime.format(formatter);
        out.write(response.getBytes());

    }}
