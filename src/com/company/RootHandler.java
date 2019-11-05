package com.company;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RootHandler implements HttpHandler {

    GameOfLife gol;

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response =
            "<html>"+
            "<head>"+
            "<meta http-equiv=\"refresh\" content=\"1\" />"+
            "</head>"+
            "<h4>Small Game of Life Example in Java</h4>";
        gol.Grow();
        response += gol.GetBoardAsHTML();
        response += "</body></html>";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public RootHandler(){
        gol = new GameOfLife(30, 200);
    }
}
