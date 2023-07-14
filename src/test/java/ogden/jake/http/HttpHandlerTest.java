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
    private GetRequest newGetRequest;
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
        newGetRequest = new GetRequest(bRead);
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
        newGetRequest.getPieces();
        assertEquals("GET" , newGetRequest.method);
        assertEquals("/", newGetRequest.resource);
        assertEquals("HTTP/1.1", newGetRequest.version);
    }

    @Test
    void postReader() throws IOException {
        String input = "Host: localhost\r\nConnection: keep-alive\r\nContent-Length: 35\r\nCache-Control: max-age=0\r\nsec-ch-ua: \"Not.A/Brand\";v=\"8\"\r\n\"Chromium\";v=\"114\"\r\n \"Google Chrome\";v=\"114\"\r\n sec-ch-ua-mobile: ?0\r\nsec-ch-ua-platform: \"macOS\"\r\nUpgrade-Insecure-Requests: 1\r\nOrigin: http://localhost\r\nContent-Type: application/x-www-form-urlencoded\r\nUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML like Gecko) Chrome/114.0.0.0 Safari/537.36\r\n Accept: text/html\r\napplication/xhtml+xml\r\napplication/xml;q=0.9\r\nimage/avif\r\nimage/webp\r\nimage/apng\r\n*/*;q=0.8\r\napplication/signed-exchange;v=b3;q=0.7\r\n Sec-Fetch-Site: same-origin\r\nSec-Fetch-Mode: navigate\r\nSec-Fetch-User: ?1\r\nSec-Fetch-Dest: document\r\nReferer: http://localhost/game\r\nAccept-Encoding: gzip\r\n deflate\r\n br\r\nAccept-Language: en-US\r\nen;\r\n\r\nq=0.9numGuesses=1&gameId=0&guess=10";
        ByteArrayInputStream byteInput = new ByteArrayInputStream(input.getBytes());
        BufferedReader newBuffer = new BufferedReader(new InputStreamReader(byteInput));
        PostRequest another = new PostRequest(newBuffer);
        another.getPieces();
        assertEquals(35 , another.contentLength);
        assertEquals("q=0.9numGuesses=1&gameId=0&guess=10" , another.queryResult);
 }

    @Test
    void handleStream() throws IOException,  InterruptedException {
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