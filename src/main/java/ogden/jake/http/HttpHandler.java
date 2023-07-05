package ogden.jake.http;
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

    public HttpHandler(String rootdirectory, int port) {
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

    public void handleStreams(BufferedReader input, OutputStream output) throws IOException, InterruptedException {
        Request newRequest = new Request(input);
        try {
            newRequest.getPieces();
        } catch (NullPointerException ignored) {
        }
        try {
            this.resource = htmlDecode(newRequest.resource);
        } catch (NullPointerException ignored) {
        }
        if (newRequest.method.equals("GET")) {
            if (resource.contains("game?")) {
                GuessingGame.processGuess(resource, output);
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
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" +
                  "\r\n"
                + "<html><body><h1>Welcome!</h1></body></html>";
        out.write(response.getBytes());
    }
    
    public void fileHtml(String fileType, File file, OutputStream out) throws IOException {
        Path filePath = file.toPath();
        byte[] fileBytes = Files.readAllBytes(filePath);
       String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: " +fileType + "\r\n"
                + "Content-Length:" + fileBytes.length + "\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.write(fileBytes);
    }

    public void sendFileResponse(OutputStream out, File file) throws IOException {
        String fileType = Files.probeContentType(file.toPath());
        if (fileType == null){
            fileType = "application/octect-stream";
        }
        fileHtml(fileType, file, out);
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
            if (rootdirectory.length() > resource.length()) {
                resource = rootdirectory;
            }
            listFilesinRoot(out, new File(resource));
        }
    }
    
    public void serveIndexPageOrDirectory(OutputStream out) throws IOException {
        File indexFile = new File(resource, "index.html");
        File indexFile2 = new File(rootdirectory, "index.html");
        fileExists(out, indexFile, resource);
        fileExists(out, indexFile2, rootdirectory);
        File potentialDirectory = new File(rootdirectory + resource);
        fileOrDirectory(potentialDirectory, out);
    }

    public void listFilesinRoot(OutputStream out, File directory) throws IOException {
        File[] files = directory.listFiles();
        StringBuilder listing = new StringBuilder();
        listing.append("</ul></body></ul>");
        String parentDirectoryPath = directory.getParent();
        listing.append("<li><a href=\"").append(parentDirectoryPath).append("\">[Parent Directory]</a></li>");
        assert files != null;
        makeLinks(files, listing);
        sendLinkResponse(out, listing);
    }

    private static void sendLinkResponse(OutputStream out, StringBuilder listing) throws IOException {
        listing.append("</ul></body></html>");
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
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
        listing.append("<li><a href=\"http://localhost:" +  port + "/")
                .append(subdirectoryPath).append("\">").append(file.getName()).
                append("/</a></li>");
    }

    private static void fileLink(StringBuilder listing, File file) {
        String fileURL = file.toURI().toString();
        int index = fileURL.indexOf(":") + 1;
        fileURL = fileURL.substring(index);
        listing.append("<li><a target='_blank' href='").append("http://localhost:" + port).
                append(fileURL + "'>" + file.getName() + "</a></li>");
    }

    public void servePingResponse(OutputStream out) throws InterruptedException, IOException {
        Thread.sleep(1000);
        LocalDateTime currentTime =LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n\r\n"
                + "Current Time: " + currentTime.format(formatter);
        out.write(response.getBytes());

    }

    public static String htmlDecode(String resource) {
        String sub = "%20";
        if (resource.contains(sub)) {
            return resource.replaceAll("%20", " ");
        }
        return resource;
    }

    public void serveGuessingGame(OutputStream out) throws IOException {
        GuessingGame.initGameResponse(out);
    }

    }




