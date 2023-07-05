package ogden.jake.http;//package com.example.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class Server {
    public static final int default_port = 80;
    public static final String default_root_directory = ".";
    public int port;
    public String rootDirectory;
    public boolean running;
    public Thread thread;
    public Handler handler;
    private ServerSocket serversocket;

    public Server(Handler handler, int port) {
        this.handler = handler;
        this.port = port;
    }

    public void start() {
        running = true;
        thread = new Thread(this::serve);
        thread.start();
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

    public static Server commandParse(String[] args) {
        Server server;
        Handler handler;
        int port = default_port;
        String rootDirectory = default_root_directory;
        port = getPort(args, port);
        rootDirectory = getDirectory(args, rootDirectory);
        handler = new HttpHandler(rootDirectory, port);
        server = new Server(handler, port);
        return server;
    }

    public static void main(String [] args){
       Server server;
       server = commandParse(args);
       server.start();
       String message = "Running on port: " + server.port;
       System.out.println(message);

}}
