package com.company;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RootHandler implements HttpHandler {

    private GameOfLife gol;
    private GameOfLife gol2;
    private Map<String, String> params;
    private boolean AutoStep;
    private boolean _useGol2;

    @Override
    public void handle(HttpExchange he) throws IOException {
        String queryString = he.getRequestURI().getQuery();
        String urlPath = he.getRequestURI().getPath();
        if (!"/".equals(urlPath) && "/".equals(urlPath.substring(0,1))){
            serveStaticRessource(he, urlPath);
            return;
        }


        params = parseQueryString(queryString);
        String boardOutput = "";
        String boardOutputHex = "";

        String action = getQueryString("action");
        switch (action){
            case "autoon":
                AutoStep = true;
                break;
            case "autooff":
                AutoStep = false;
                break;
            case "restart":
                gol.Restart();
                gol2.SetBoardString(gol.GetBoardString());
                break;
            case "clear":
                gol.Clear();
                gol2.Clear();
                break;
            case "set":
                int row = getQueryInt("r");
                int column = getQueryInt("c");
                gol.SetPoint(Integer.parseInt(params.get("r")),Integer.parseInt(params.get("c")));
                gol2.SetPoint(Integer.parseInt(params.get("r")),Integer.parseInt(params.get("c")));
                break;
            case "getboard":
                boardOutput = gol.GetBoardString();
                boardOutputHex = "<br>"+gol.GetBoardHexString();
                break;
            case "setboard":
                String boardString = getQueryString("board");
                if (boardString.length() > 0){
                    gol.SetBoardString(boardString);
                    gol2.SetBoardString(boardString);
                }
                break;
            default:
                gol.Grow();
                gol2.Grow();
                break;
        }

        String response =
            "<html>"+
            "<head>";
        response += "<link rel=\"stylesheet\" type=\"text/css\" href=\"/src/my.css\">";
        response += "</head><body><center>"+
            "<h4>Small Game of Life Example in Java</h4>"+
            "<div>"+
            "<a href=\"?action=restart\" class=\"button\"><button>restart</button></a>&nbsp;"+
            "<a href=\"?action=clear\" class=\"button\"><button>clear</button></a>&nbsp;"+
            "<a href=\"?action=getboard\" class=\"button\"><button>get board</button></a>&nbsp;"+
            "<a href=\"?action=step\" class=\"button\"><button>step</button></a>&nbsp;"+
            "<a href=\"?action=autoon\" class=\"button\"><button>autostep on</button></a>&nbsp;"+
            "<a href=\"?action=autooff\" class=\"button\"><button>autostep off</button></a>&nbsp;"+
            "</div>";


        response += gol.GetBoardAsHTML();
        if (_useGol2) {
            response += gol2.GetBoardAsHTML();
        }
        response += "</center>";
        response += "<div style=\"width: 90%;margin: auto;word-wrap: break-word;\">"+boardOutput+"</div>";
        response += "<div style=\"width: 90%;margin: auto;word-wrap: break-word;\">"+boardOutputHex+"</div>";
        if(AutoStep){
            response += "<script>setTimeout('window.location.href=\"?action=none\";', 10);</script>";
        }
        response += "</body></html>";

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private int getQueryInt(String key) {
        String value = getQueryString(key);
        int result = Integer.parseInt(value);
        return result;
    }

    private String getQueryString(String key){
        String result = params.get(key);
        if (result == null) result = "";
        return result;
    }

    private static Map<String, String> parseQueryString(String qs) {
        Map<String, String> result = new HashMap<>();
        if (qs == null)
            return result;

        int last = 0, next, l = qs.length();
        while (last < l) {
            next = qs.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = qs.indexOf('=', last);
                try {
                    if (eqPos < 0 || eqPos > next)
                        result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                    else
                        result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e); // will never happen, utf-8 support is mandatory for java
                }
            }
            last = next + 1;
        }
        return result;
    }

    private void serveStaticRessource(HttpExchange he, String urlPath) throws IOException{
        // serve static resource
        urlPath = urlPath.substring(1);
        File file = new File(urlPath);

        Headers h = he.getResponseHeaders();

        String line;
        String resp = "";

        try {
            File newFile = new File(urlPath);
            System.out.println("file: " + newFile.getName());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(newFile)));
            while ((line = bufferedReader.readLine()) != null) {
                resp += line;
            }
            bufferedReader.close();
            h.add("Content-Type", "text/css");
            he.sendResponseHeaders(200, resp.length());
        } catch (IOException e) {
//                e.printStackTrace();
            System.out.println("file not found: "+urlPath);
            resp = "file not found";
            he.sendResponseHeaders(404,resp.length());
        }
        OutputStream os = he.getResponseBody();
        os.write(resp.getBytes());
        os.close();

    }

    public RootHandler(){
        _useGol2 = false;

        gol2 = new GameOfLife(10, 50);
        gol2.SetRules("23", "3");

        if (_useGol2) {
            gol = new GameOfLife(10, 50);
            gol2.SetBoardString(gol.GetBoardString());
        } else {
            gol = new GameOfLife(40, 40 * 40 / 2);
        }
        gol.SetRules("23", "3");
    }
}
