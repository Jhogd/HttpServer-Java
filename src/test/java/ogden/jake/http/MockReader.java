package ogden.jake.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MockReader implements Reader {

    public InputStream in;
    public String read;

    public  MockReader (InputStream in){
        this.in = in;
    }

    @Override
    public void bufferData() throws IOException {
        BufferedReader read = new BufferedReader(new InputStreamReader(in));
        String strCurrentLine;
        while ((strCurrentLine = read.readLine()) != null) {
            this.read = strCurrentLine;
        }
}}
