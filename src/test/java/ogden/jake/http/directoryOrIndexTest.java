package ogden.jake.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class directoryOrIndexTest {
    //@BeforeEach
    directoryOrIndex newDirectory = new directoryOrIndex();
    @Test
    void listdirectory() throws IOException {
        OutputStream output = new ByteArrayOutputStream();
        File path = new File("/Users/jakeogden/httpTests");
        newDirectory.listFilesinRoot(output, path);
        assertTrue(output.toString().contains("/Users/jakeogden/httpTests"));
    }

    @Test
    void serveIndex() throws IOException, InterruptedException {
        OutputStream output = new ByteArrayOutputStream();
        File index = new File("/Users/jakeogden/Documents", "index.html");
        newDirectory.fileExists(output, index,"/Users/jakeogden/Documents");
        String out = output.toString();
        System.out.println(out);
        String subOut = "Welcome to the index.html file";
        assertTrue(out.contains(subOut));
    }

    @Test
    void serveRoot() throws IOException, InterruptedException {
        OutputStream output = new ByteArrayOutputStream();
        newDirectory.sendResponse(output, "/Users/jakeogden/httpTests", "");
        assertTrue(output.toString().contains("/Users/jakeogden/httpTests"));
    }


}