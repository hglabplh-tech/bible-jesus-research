package org.harry.jesus.jesajautils;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import generated.XMLBIBLE;
import org.w3c.dom.stylesheets.LinkStyle;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

public class BibleHTTPSrv {

    public static final String HOST = "localhost";

    public static final Integer PORT = 8980;
    private HttpServer server;

    public BibleHTTPSrv(XMLBIBLE bible) {
        try {

            ResponseCache responseCache = new ResponseCache() {
                //calling the abstract methods
                @Override
                public CacheResponse get(URI uri, String rqstMethod, Map<String, List<String>> rqstHeaders) throws IOException {
                    return null;
                }

                @Override
                public CacheRequest put(URI uri, URLConnection conn) throws IOException {
                    return null;
                }
            };
            ResponseCache.setDefault(responseCache);
            server = HttpServer.create();
            server.bind(new InetSocketAddress(
                    InetAddress.getByName(HOST),
                    PORT), 0);
            server.createContext("/dayVerse", new VersPerDayHandler(bible));
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopServer() {
        server.removeContext("/dayVerse");
        server.stop(0);
    }



    private static class VersPerDayHandler  implements HttpHandler {

        private int rand;

        private final XMLBIBLE bible;

        public VersPerDayHandler(XMLBIBLE bible) {
            rand = 0;
            this.bible = bible;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException{
            BibleTextUtils utils = BibleTextUtils.getInstance();
            BibleTextUtils.BookLink link = getRandomVerse();
            String htmlText = HTMLRendering.renderLink(
                    utils, this.bible, Arrays.asList(link));
            System.out.println(htmlText);
            getLengthAndSend(exchange, htmlText);
        }

        public BibleTextUtils.BookLink getRandomVerse() {
            BibleTextUtils.BookLink link = DayVerses.verses.get(rand);
            System.out.println(link);
            if (rand == (DayVerses.verses.size() - 1)) {
                rand = 0;
            } else {
                rand++;
            }
            return link;
        }
    }

    private static void getLengthAndSend(HttpExchange exchange, String htmlText) throws IOException {
        int length = htmlText.getBytes(StandardCharsets.UTF_8).length;
        exchange.sendResponseHeaders(200, length);
        OutputStream os = exchange.getResponseBody();
        os.write(htmlText.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        exchange.close();
    }


}
