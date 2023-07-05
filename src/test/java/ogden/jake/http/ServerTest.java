package ogden.jake.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    private Server server;
    private MockHandler handler;
    private MockReader mockinput;
    @BeforeEach
    void setup() {
        handler = new MockHandler();
        server = new Server(handler, 123, "/");
    }

    @AfterEach
    void teardown() throws IOException, InterruptedException {
        server.stop();
    }

    @Test
    void creation(){
        assertEquals(handler, server.handler);
        assertEquals(123, server.port);
        assertEquals("/", server.rootDirectory);
    }


    @Test
    void start() throws InterruptedException {
        assertEquals(false, server.running);
        assertEquals(null, server.thread);
        server.start();
        assertEquals(true, server.running);
        assertNotNull(server.thread);
        assertTrue(server.thread.isAlive());
    }

    @Test
    void stop() throws InterruptedException, IOException {
        server.start();
        Thread thread = server.thread;
        Thread.yield();
        server.stop();
        assertEquals(false, server.running);
        assertNull(server.thread);
        assertFalse(thread.isAlive());
    }

    @Test
    void connection () throws IOException, InterruptedException {
        server.start();
        try (Socket socket = new Socket("localhost", 123)) {
        }
        Thread.sleep(10);
        assertNotNull(handler.socket);
        assertTrue(handler.wasCalled);
    }

    @Test
    void multipleConnection() throws IOException, InterruptedException {
        server.start();
        for (int i = 0; i < 4; i++){
            Socket socket = new Socket("localhost", 123);
            Thread.sleep(10);
            assertNotNull(handler.socket);
            assertTrue(handler.wasCalled);
        }
    }

    @Test
    void fill_input() throws IOException {
        mockinput = new MockReader(new FileInputStream("src/test/testFile.txt"));
        assertNotNull(mockinput.in);
        mockinput.bufferData();
        assertEquals("Hi I am Jake", mockinput.read);
    }

    @Test
    void commandLine() {
        String[] args = new String[]{"-p", "124", "-r", "/src/test"};
        String[] newArgs = new String[]{"-p", "127", "-r", "/src/main"};
        Server newserver = server.commandParse(args);
        Server thirdServer = server.commandParse(newArgs);
        assertEquals(124, newserver.port);
        assertEquals("/src/test", newserver.rootDirectory);
        assertEquals("/src/main", thirdServer.rootDirectory);

    }

}