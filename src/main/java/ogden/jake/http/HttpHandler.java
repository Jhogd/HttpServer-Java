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

    public static String splitResource(String resource, HashMap<String, Serve> serveMap){
        String partialResource = "";
        for (String key : serveMap.keySet()){
          if (resource.contains(key)){
             partialResource = key;
          }
      }
        return partialResource;
    }

    public void handleStreams(BufferedReader input, OutputStream output) throws IOException, InterruptedException {
        Request newRequest = new Request(input);

        try {
            newRequest.getPieces();
        } catch (NullPointerException ignored) {
        }
        try {
            this.resource = htmlDecode(newRequest.resource);
        } catch (NullPointerException ignored) {
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String partialResource = splitResource(resource, serveMap);
        if ("GET".equals(newRequest.method)) {
            if (serveMap.get(partialResource) == null) {
                if (rootDirectory.length() > resource.length()) {
                    resource = rootDirectory;
                }
                if (rootDirectory.equals(".")) {
                    resource = rootDirectory + resource;
                }
                new directoryOrIndex().sendResponse(output, resource);
            } else {
                serveMap.get(partialResource).sendResponse(output, resource);

            }
        }
    }


    public static String htmlDecode(String resource) throws URISyntaxException {
        if (resource.contains("?")){
            return resource;
        }
    return new URI(resource).getPath();
    }


    }




