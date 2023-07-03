package ogden.jake.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

class HttpHandlerTest {
    private HttpHandler httpHandler;
    private Request newRequest;
    private BufferedReader bRead;
    private MockHandler mockhandler;
    @BeforeEach
    void setup() {
        httpHandler = new HttpHandler("/Users/jakeogden/httpTests/testStuff", 123);
        String requestString = "GET " + "/" + " HTTP/1.1\r\n\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(requestString.getBytes());
        bRead = new BufferedReader(new InputStreamReader(input));
        newRequest = new Request(bRead);
    }

    @Test
    void creation(){
        assertNotNull(httpHandler.rootdirectory);
        assertEquals(123, httpHandler.port);
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
        System.out.println(out);
        assertTrue(out.contains(subOut));
    }

    @Test
    void handleStream2() throws IOException, InterruptedException {
        HttpHandler otherHandler = new HttpHandler("/", 123);
        OutputStream output = new ByteArrayOutputStream();
        String request = "GET " + "/hello" + " HTTP/1.1\r\n\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        otherHandler.handleStreams(buffer, output);
        String out = output.toString();
        System.out.println(out);
        String sub = "Welcome!";
        assertTrue(out.contains(sub));
    }

    @Test
    void handleStream3() throws IOException, InterruptedException {
        HttpHandler otherHandler = new HttpHandler("/", 123);
        OutputStream output = new ByteArrayOutputStream();
        String request = "GET " + "/ping" + " HTTP/1.1\r\n\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        otherHandler.handleStreams(buffer, output);
        String out = output.toString();
        LocalDateTime currentTime =LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = currentTime.format(formatter);
        assertTrue(output.toString().contains(time));
    }


    @Test
    void
    fileresponse() throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        File myfile = new File("src/test/testFile.txt");
        httpHandler.sendFileResponse(output, myfile);
        String expectedString = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length:12\r\n\r\nHi I am Jake";
        assertEquals(expectedString, output.toString());
    }

   @Test
    void listdirectory() throws IOException {
       OutputStream output = new ByteArrayOutputStream();
       File path = new File("/Users/jakeogden/httpTests");
       httpHandler.listFilesinRoot(output, path);
       assertTrue(output.toString().contains("/Users/jakeogden/httpTests"));
   }

   @Test
    void serveIndexorRoot() throws IOException {
       OutputStream output = new ByteArrayOutputStream();
       httpHandler.serveIndexPageOrDirectory(output);
       String out = output.toString();
       String subOut = "This is the idex.html file";
        //System.out.println(out);
       assertTrue(out.contains(subOut));
   }

   @Test
    void servewelcome() throws IOException {
       OutputStream output = new ByteArrayOutputStream();
       httpHandler.serveWelcomePage(output);
       String out = output.toString();
       String expected = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n"
               + "<html><body><h1>Welcome!</h1></body></html>";
       assertEquals(expected, out);
   }

   @Test
    void pingResponse() throws IOException, InterruptedException {
       OutputStream output = new ByteArrayOutputStream();
       httpHandler.servePingResponse(output);
       LocalDateTime currentTime =LocalDateTime.now();
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       String time = currentTime.format(formatter);
       System.out.println(output.toString());
       assertTrue(output.toString().contains(time));

       //;; https://developer.mozilla.org/en-US/docs/Learn/HTML/Introduction_to_HTML/Getting_started#anatomy_of_an_html_document
   }

   @Test
    void
    decode(){
        assertEquals("Photo Booth Library", HttpHandler.htmlDecode("Photo%20Booth%20Library"));
        assertEquals("Abstract factory.key", HttpHandler.htmlDecode("Abstract%20factory.key"));
        assertEquals("Screenshot 2023-06-19 at 8.08.38 AM.png", HttpHandler.htmlDecode("Screenshot%202023-06-19%20at%208.08.38%20AM.png"));
        assertEquals("camNewton.gif", HttpHandler.htmlDecode("camNewton.gif"));
   }



}