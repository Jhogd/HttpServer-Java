package ogden.jake.http;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.Socket;


public class HttpHandler implements Handler{
    public String rootdirectory;
    public static int port;
    public Socket socket;
    public String resource;

    public HttpHandler(String rootdirectory, int port) throws URISyntaxException {
        this.rootdirectory = htmlDecode(rootdirectory);
        this.port = port;

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
        if ("GET".equals(newRequest.method)) {
            if (newRequest.resource.contains("game?")) {
                GuessingGameHtml.processGameHtml(newRequest.resource, output);
            } else {
                switch (resource) {
                    case "/hello" -> serveWelcomePage(output);
                    case "/ping" -> servePingResponse(output);
                    case "/game" -> serveGuessingGame(output);
                    default -> serveIndexPageOrDirectory(output);
                }
            }

        }
    }

    public void serveWelcomePage(OutputStream out) throws IOException {
        String response = htmlHeader("text/html") +
                  "\r\n"
                + "<html><body><h1>Welcome!</h1></body></html>";
        out.write(response.getBytes());
    }
    
    public void readFile(String fileType, File file, OutputStream out) throws IOException {
        Path filePath = file.toPath();
        byte[] fileBytes = Files.readAllBytes(filePath);
       String response = htmlHeader(fileType) +
                 "Content-Length:" + fileBytes.length + "\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.write(fileBytes);
    }

    public void sendFileResponse(OutputStream out, File file) throws IOException {
        String fileType = Files.probeContentType(file.toPath());
        if (fileType == null){
            fileType = "application/octect-stream";
        }
        readFile(fileType, file, out);
    }

    public void fileExists(OutputStream out, File indexFile, String path) throws IOException {
        if (indexFile.exists()){
            String indexPath = path + "/index.html";
            sendFileResponse(out, new File(indexPath));
        }
    }

    public void fileOrDirectory (File potentialDirectory, OutputStream out) throws IOException {
        if (potentialDirectory.isFile()) {
            sendFileResponse(out, potentialDirectory);
        } else {

            listFilesinRoot(out, new File(resource));
        }
    }
    
    public void serveIndexPageOrDirectory(OutputStream out) throws IOException {
      if (rootdirectory.length() > resource.length())  {
          resource = rootdirectory + resource;
      }
      if (rootdirectory.equals(".")){
          resource = rootdirectory;
      }
        System.out.println(resource);
        File indexFile = new File(resource ,"index.html");
        fileExists(out, indexFile, resource);
        File fileOrDirectory = new File(resource);
        fileOrDirectory(fileOrDirectory, out);
    }

    public void listFilesinRoot(OutputStream out, File directory) throws IOException {
        File[] files = directory.listFiles();
        StringBuilder listing = new StringBuilder();
        listing.append("</ul></body></ul>");
        String parentDirectoryPath = directory.getParent();
        listing.append("<li><a href=\"").append(parentDirectoryPath).append("\">[Parent Directory]</a></li>");
        assert  files != null;
        makeLinks(files, listing);
        sendLinkResponse(out, listing);
    }

    private static void sendLinkResponse(OutputStream out, StringBuilder listing) throws IOException {
        listing.append("</ul></body></html>");
        String response = htmlHeader("text/html")
                + "Content-Length: " + listing.length() + "\r\n" + "\r\n" +
                listing;
        out.write(response.getBytes());
    }

    private static void makeLinks(File[] files, StringBuilder listing) {
        try {
        for (File file : files){
            if (file.isDirectory()){
                directoryLink(listing, file);
            }
            else {
                fileLink(listing, file);
            }
        } }
        catch (NullPointerException ignored){}
    }

    private static void directoryLink(StringBuilder listing, File file) {
        String subdirectoryPath = file.getPath();
        listing.append("<li><a href=\"")
                .append(subdirectoryPath).append("\">").append(file.getName()).
                append("/</a></li>");
    }

    private static void fileLink(StringBuilder listing, File file) {
        listing.append("<li><a target='_blank' href='").append(file.getPath()).
                append("'>").append(file.getName()).append("</a></li>");
    }

    public void servePingResponse(OutputStream out) throws InterruptedException, IOException {
        Thread.sleep(1000);
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String response =  htmlHeader("text/plain") + "\r\n"
                + "Current Time: " + currentTime.format(formatter);
        out.write(response.getBytes());

    }

    public static String htmlDecode(String resource) throws URISyntaxException {
    return new URI(resource).getPath();
    }

    public void serveGuessingGame(OutputStream out) throws IOException {
        GuessingGameHtml.initGameHtml(out);
    }

    }




