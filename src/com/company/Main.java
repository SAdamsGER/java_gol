package com.company;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port),0);
        System.out.println("server started at: " + port);
        server.createContext("/", new RootHandler());
        server.setExecutor(null);
        server.start();

    }
}
