package ogden.jake.http;//package com.example.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

public class Server {
    public static final int default_port = 80;
    public static final String default_root_directory = ".";
    public int port;
    public String rootDirectory;
    public boolean running;
    public Thread thread;
    public Handler handler;
    private ServerSocket serversocket;

    public Server(Handler handler, int port, String rootDirectory) {
        this.handler = handler;
        this.port = port;
        this.rootDirectory = rootDirectory;
    }

    public void start() {
        running = true;
        thread = new Thread(this::serve);
        thread.start();
    }

    public Handler getHandler() {
        return this.handler;
    }

    private void serve() {
        try {
            serversocket = new ServerSocket(port);
            while (running) {
                Socket clientSocket = serversocket.accept();
                Thread clientThread = new Thread(() -> {
                    try {
                        handler.handle(clientSocket);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                clientThread.start();
            }
        } catch (SocketException e) {
            if(running) {
                System.err.println("Socket error");
                e.printStackTrace(System.err);
            }
        }  catch (Exception e) {
           System.err.println("Server error");
           e.printStackTrace(System.err);
        }
    }

    public void stop() throws InterruptedException, IOException {
        if (running) {
            running = false;
            while(thread.isAlive()) {
                if(serversocket != null)
                    serversocket.close();
                Thread.yield();
            }
            thread = null;
        }
    }


    public static int getPort(String[] args, int port) {
        int index;
        if (Arrays.asList(args).contains("-p")) {
            index = Arrays.asList(args).indexOf("-p") + 1;
            port = Integer.parseInt(args[index]);
        }
        return port;
    }

    public static String getDirectory(String[] args, String rootDirectory) {
        int index;
        if (Arrays.asList(args).contains("-r")) {
            index = Arrays.asList(args).indexOf("-r") + 1;
            rootDirectory = args[index];
        }
        return rootDirectory;
    }


 public static Server commandParse(String[] args) throws URISyntaxException {
     int port = default_port;
     String rootDirectory = default_root_directory;
     HashMap<String, Serve> serveMap = new HashMap<>();
     port = getPort(args, port);
     rootDirectory = getDirectory(args, rootDirectory);
     Handler handler = new HttpHandler(rootDirectory,serveMap);
     return new Server(handler, port, rootDirectory);
 }

    public static void main(String[] args) throws URISyntaxException {
        Server server;
        server = commandParse(args);
        server.getHandler().addServe("/hello", new WelcomePage());
        server.getHandler().addServe("/game", new GuessingGameHtml());
        server.getHandler().addServe("/ping", new pingResponse());
        server.start();
        String message = "Running on port: " + server.port;
        System.out.println(message);

}}

