package ogden.jake.http;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class HttpHandler implements Handler{
    public final String rootdirectory;
    public static int port;
    public Socket socket;
    private String resource;

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
        newRequest.getPieces();
        String method = newRequest.method;
        String resource = newRequest.resource;
        this.resource = htmlDecode(resource);
        System.out.println(resource);
        if (method.equals("GET")) {
            switch (resource) {
                case "/hello" -> serveWelcomePage(output);
                case "/ping" -> servePingResponse(output);
                case "/game" -> serveGuessingGame(output);
                default -> serveIndexPageOrDirectory(output);
            }

        }

    }

    public void serveWelcomePage(OutputStream out) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" +
                "Content-Length: 7" + "\r\n"
                + "<html><body><h1>Welcome!</h1></body></html>";
        out.write(response.getBytes());
    }

    public void sendFileResponse(OutputStream out, File file) throws IOException {
        String fileType = Files.probeContentType(file.toPath());
        if (fileType == null){
            fileType = "application/octect-stream";
        }
        Path filePath = file.toPath();
        byte[] fileBytes = Files.readAllBytes(filePath);
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: " +fileType + "\r\n"
                + "Content-Length:" + fileBytes.length + "\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.write(fileBytes);
    }

  public void serveIndexPageOrDirectory(OutputStream out) throws IOException {
        File indexFile = new File(resource, "index.html");
        File indexFile2 =  new File(rootdirectory, "index.html");
      if (indexFile.exists()){
          String indexPath = resource + "/index.html";
          sendFileResponse(out, new File(indexPath));
      }
      if (indexFile2.exists()){
          String indexPath = rootdirectory + "/index.html";
          sendFileResponse(out, new File(indexPath));
      }
      else {
          File notDirectory = new File(resource);
          System.out.println(Paths.get(resource));
       //   System.out.println(notDirectory.isFile());
          if (notDirectory.isFile()){
              sendFileResponse(out, notDirectory);
          }
          else {
              if (rootdirectory.length() > resource.length()) {
                  resource = rootdirectory;
              }
                listFilesinRoot(out, new File(resource));}
      }
  }

    public void listFilesinRoot(OutputStream out, File directory) throws IOException {
        File[] files = directory.listFiles();
        StringBuilder listing = new StringBuilder();
        listing.append("</ul></body></ul>");
        String parentDirectoryPath = directory.getParent();
        listing.append("<li><a href=\"").append(parentDirectoryPath).append("\">[Parent Directory]</a></li>");
        extracted(files, listing);
        listing.append("</ul></body></html>");
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n"
                + "Content-Length: " + listing.length() + "\r\n" + "\r\n" +
                listing;
        out.write(response.getBytes());
    }

    private static void extracted(File[] files, StringBuilder listing) {
        for (File file : files){
            if (file.isDirectory()){
                String subdirectoryPath = file.getPath();
                listing.append("<li><a href=\"http://localhost:" + String.valueOf(port) + "/")
                        .append(subdirectoryPath).append("\">").append(file.getName()).
                        append("/</a></li>");
            }
            else {
                String fileURL = file.toURI().toString();
                int index = fileURL.indexOf(":") + 1;
                fileURL = fileURL.substring(index);
               // System.out.println(fileURL);
                listing.append("<li><a target='_blank' href='").append("http://localhost:" + String.valueOf(port)).
                        append(fileURL + "'>" + file.getName() + "</a></li>");
            }
        }
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
        if (resource.contains(sub)){
        return resource.replaceAll("%20", " ");
    }
    return resource;
    }

    public void serveGuessingGame(OutputStream out){

    }

    }




