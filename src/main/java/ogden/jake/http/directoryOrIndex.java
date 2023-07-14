package ogden.jake.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static ogden.jake.http.HttpHandler.htmlHeader;

public class directoryOrIndex implements  Serve{


    @Override
    public void sendResponse(OutputStream out, String resource, String data) throws InterruptedException, IOException {
        File indexFile = new File(resource ,"index.html");
        fileExists(out, indexFile, resource);
        File fileOrDirectory = new File(resource);
        fileOrDirectory(fileOrDirectory, out, resource);
    }

    public static void readFile(String fileType, File file, OutputStream out) throws IOException {
        Path filePath = file.toPath();
        byte[] fileBytes = Files.readAllBytes(filePath);
        String response = htmlHeader(fileType) +
                "Content-Length:" + fileBytes.length + "\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.write(fileBytes);
    }

    public static void sendFileResponse(OutputStream out, File file) throws IOException {
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

    public void fileOrDirectory (File potentialDirectory, OutputStream out, String resource) throws IOException {
        if (potentialDirectory.isFile()) {
            sendFileResponse(out, potentialDirectory);
        } else {

            listFilesinRoot(out, new File(resource));
        }
    }


    public void listFilesinRoot(OutputStream out, File directory) throws IOException {
        File[] files = directory.listFiles();
        StringBuilder listing = new StringBuilder();
        listing.append("</ul></body></ul>");
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
}
