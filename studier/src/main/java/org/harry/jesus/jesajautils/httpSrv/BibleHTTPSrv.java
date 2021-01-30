package org.harry.jesus.jesajautils.httpSrv;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;


import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BibleHTTPSrv {

    public static final String HOST = "localhost";

    public static final Integer PORT = 8980;
    private HttpServer server;

    public BibleHTTPSrv(XMLBIBLE bible, Boolean verseRandom) {
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
            server.createContext("/dayVerse", new VersPerDayHandler(bible, verseRandom));
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

        private int sequence = 0;

        private final XMLBIBLE bible;

        private  ThreadLocalRandom randomizer;

        private final Boolean verseRandom;

        public VersPerDayHandler(XMLBIBLE bible, Boolean verseRandom) {
            this.bible = bible;
             randomizer = ThreadLocalRandom.current();
             this.verseRandom = verseRandom;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException{
            BibleTextUtils utils = BibleTextUtils.getInstance();
            BibleTextUtils.BookLink link = getRandomVerse();
            String htmlText = HTMLRendering.renderLink(
                    utils, this.bible, Arrays.asList(link));
            System.out.println(htmlText);
            getLengthAndSendText(exchange, htmlText);
        }

        /**
         * Get a verse either by random or sequential
         * @return the verse link
         */
        public BibleTextUtils.BookLink getRandomVerse() {
            BibleTextUtils.BookLink link;
            if (verseRandom) {
                Integer index = randomizer.nextInt(DayVerses.verses.size());
                link = DayVerses.verses.get(index);
            } else {
                link = DayVerses.verses.get(sequence);
            }
            System.out.println(link);
            if (sequence == (DayVerses.verses.size() - 1)) {
                sequence = 0;
            } else {
                sequence++;
            }
            return link;
        }
    }

    /**
     * Be careful with changes here.. This method calculates the length in bytes
     * of a UTF-8 encoded String and sends the String as request answer
     * @param exchange the exchange context
     * @param textToSend the text
     * @throws IOException exception if the data cannot be transmitted
     */
    private static void getLengthAndSendText(HttpExchange exchange, String textToSend) throws IOException {
        int length = textToSend.getBytes(StandardCharsets.UTF_8).length;
        exchange.sendResponseHeaders(200, length);
        OutputStream os = exchange.getResponseBody();
        os.write(textToSend.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();
        exchange.close();
    }


}
