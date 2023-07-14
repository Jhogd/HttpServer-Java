package ogden.jake.http;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;


public class HttpHandler implements Handler{
    public String rootDirectory;
    public Socket socket;
    public String resource;
    public final HashMap<String, Serve> serveMap;
    public String queryResult;

    public HttpHandler(String rootdirectory, HashMap<String, Serve> serveMap) throws URISyntaxException {
        this.serveMap = serveMap;
        this.rootDirectory = rootdirectory;

    }

    public void addServe(String resource, Serve app) {
        serveMap.put(resource, app);
    }

    @Override
    public void handle(Socket socket) throws IOException, InterruptedException {
        this.socket = socket;
        try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream output = socket.getOutputStream()){
        handleStreams(inputReader, output);
    }
    }
    public static String htmlHeader(String contentType){
        return "HTTP/1.1 200 OK\r\n" + "Content-Type: " + contentType + "\r\n";
    }

    public void handleStreams(BufferedReader input, OutputStream output) throws IOException, InterruptedException {
        GetRequest newGetRequest = new GetRequest(input);

        try {
            newGetRequest.getPieces();
        } catch (NullPointerException ignored) {
        }
        if (newGetRequest.method.equals("GET")) {
            try {
                this.resource = htmlDecode(newGetRequest.resource);
            } catch (NullPointerException ignored) {
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            PostRequest postRequest = new PostRequest(input);
            postRequest.getPieces();
            this.resource = newGetRequest.resource;
            this.queryResult = postRequest.queryResult;
            System.out.println(queryResult);

        }
            if (serveMap.get(resource) == null) {
                if (rootDirectory.length() > resource.length()) {
                    resource = rootDirectory;
                }
                if (rootDirectory.equals(".")) {
                    resource = rootDirectory + resource;
                }
                new directoryOrIndex().sendResponse(output, resource, queryResult);
            } else {
                serveMap.get(resource).sendResponse(output, resource, queryResult);

            }

    }

    public static String htmlDecode(String resource) throws URISyntaxException {
     if (resource.contains("?")){
         return resource;
     }
    return new URI(resource).getPath();
    }


    }




