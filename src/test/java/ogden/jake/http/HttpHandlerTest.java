package ogden.jake.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HttpHandlerTest {
    private HttpHandler httpHandler;
    private HttpHandler otherHandler;
    private Request newRequest;
    private BufferedReader bRead;
    private MockHandler mockhandler;
    @BeforeEach
    void setup() throws URISyntaxException {
        HashMap<String, Serve> serveMap = new HashMap<>();
        otherHandler  = new HttpHandler("/Users/jakeogden", serveMap);
        httpHandler = new HttpHandler("/Users/jakeogden/httpTests/testStuff",serveMap);
        String requestString = "GET " + "/" + " HTTP/1.1\r\n\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(requestString.getBytes());
        bRead = new BufferedReader(new InputStreamReader(input));
        newRequest = new Request(bRead);
    }

    @Test
    void creation(){
        assertEquals("/Users/jakeogden/httpTests/testStuff",httpHandler.rootDirectory);
    }

    @Test
    void HttpHandler() throws IOException, InterruptedException {
        mockhandler = new MockHandler();
        Socket socket = new Socket();
        Thread.sleep(10);
        mockhandler.handle(socket);
        assertNotNull(mockhandler.socket);
    }
    @Test
    void
    addServeMap(){
        HashMap<String, Serve> testMap = new HashMap<>();
        httpHandler.addServe("/game", new GuessingGameHtml());
        assertEquals(GuessingGameHtml.class, httpHandler.serveMap.get("/game").getClass());


    }


    @Test
    void readRequest() throws IOException {
        newRequest.getPieces();
        assertEquals("GET" , newRequest.method);
        assertEquals("/", newRequest.resource);
        assertEquals("HTTP/1.1", newRequest.version);
    }

    @Test
    void handleStream() throws IOException, InterruptedException {
        OutputStream output = new ByteArrayOutputStream();
        httpHandler.handleStreams(bRead, output);
        String out = output.toString();
        String subOut = "This is the idex.html file";
        assertTrue(out.contains(subOut));
    }

    @Test
    void handleStream2() throws IOException, InterruptedException, URISyntaxException {
        OutputStream output = new ByteArrayOutputStream();
        otherHandler.addServe("/hello", new WelcomePage());
        otherHandler.addServe("/game", new GuessingGameHtml());
        otherHandler.addServe("/ping", new pingResponse());
        String request = "GET " + "/hello" + " HTTP/1.1\r\n\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        otherHandler.handleStreams(buffer, output);
        String out = output.toString();
        String sub = "Welcome!";
        assertTrue(out.contains(sub));
    }

    @Test
    void handleStream3() throws IOException, InterruptedException {
        MockHandler otherHandler = new MockHandler();
        OutputStream output = new ByteArrayOutputStream();
        String request = "GET " + "/ping" + " HTTP/1.1\r\n\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        otherHandler.handleStreams(buffer, output);
        LocalDateTime currentTime =LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = currentTime.format(formatter);
        assertTrue(output.toString().contains(time));
    }



   @Test
    void
    decode() throws URISyntaxException {
        assertEquals("Photo Booth Library", HttpHandler.htmlDecode("Photo%20Booth%20Library"));
        assertEquals("Abstract factory.key", HttpHandler.htmlDecode("Abstract%20factory.key"));
        assertEquals("Screenshot 2023-06-19 at 8.08.38 AM.png", HttpHandler.htmlDecode("Screenshot%202023-06-19%20at%208.08.38%20AM.png"));
        assertEquals("camNewton.gif", HttpHandler.htmlDecode("camNewton.gif"));
        assertEquals("/game?numGuesses", HttpHandler.htmlDecode("/game?numGuesses"));
   }



}