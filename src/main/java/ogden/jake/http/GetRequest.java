package ogden.jake.http;

import java.io.BufferedReader;
import java.io.IOException;

public class GetRequest {

    public BufferedReader input;
    public String method;
    public String resource;
    public String version;

    public GetRequest(BufferedReader input){
        this.input = input;
    }

    public void getPieces () throws IOException {
        String requestline = input.readLine();
        String[] requestparts = requestline.split("\\s+");
        this.method = requestparts[0];
        this.resource = requestparts[1];
        this.version = requestparts[2];
    }

}
