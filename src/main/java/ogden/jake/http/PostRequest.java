package ogden.jake.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;

public class PostRequest {

    public BufferedReader input;
    public int contentLength;
    public String queryResult;


    public PostRequest (BufferedReader input){
        this.input = input;
    }

    public void getPieces() throws IOException {
        String line;
        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder postHeader = new StringBuilder();
        while (!((line = input.readLine()).isEmpty())) {
            postHeader.append(line);
            if (line.contains("Content-Length:")) {
                String[] contentLengthArray = line.split(" ");
                this.contentLength = Integer.parseInt(contentLengthArray[1]);
            }

        }
        for (int i = 0; i < contentLength; i++){
            int contentByte = input.read();
            contentBuilder.append((char)contentByte);
        }
        this.queryResult = contentBuilder.toString();

    }
}
