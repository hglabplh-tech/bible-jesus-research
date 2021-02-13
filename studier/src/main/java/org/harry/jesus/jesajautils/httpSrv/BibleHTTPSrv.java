package org.harry.jesus.jesajautils.httpSrv;


import com.github.markusbernhardt.proxy.selector.pac.PacProxySelector;
import com.github.markusbernhardt.proxy.selector.pac.UrlPacScriptSource;
import com.sun.net.httpserver.HttpServer;
import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.httpSrv.context.*;


import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;


/**
 * The type Bible http srv.
 */
public class BibleHTTPSrv {

    /**
     * The constant HOST.
     */
    public static final String HOST = "localhost";



    private static final Integer PORT = 55777;
    private HttpServer server;

    /**
     * Instantiates a new Bible http srv.
     *
     * @param bible       the bible
     * @param verseRandom the verse random
     */
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
                    getPORT()), 0);
            createContextPoints(bible, verseRandom);
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createContextPoints(XMLBIBLE bible, Boolean verseRandom) {
        server.createContext("/dayVerse", new VersPerDayHandler(bible, verseRandom));
        server.createContext("/retrieveChapter", new RetrieveChapterHandler(bible));
        server.createContext("/retrieveVerse", new RetrieveVerseHandler(bible));
        server.createContext("/dictionary", new RetrieveDictionaryHandler());
        server.createContext("/searchInput", new SearchHandler.SearchForm());
        server.createContext("/searchExecute", new SearchHandler.SearchExecute());
    }

    private void removeContextPoints() {
        server.removeContext("/dayVerse");
        server.removeContext("/retrieveChapter");
        server.removeContext("/retrieveVerse");
        server.removeContext("/dictionary");
        server.removeContext("/searchInput");
        server.removeContext("/searchExecute");
    }

    /**
     * Stop server.
     */
    public void stopServer() {
        removeContextPoints();
        server.stop(0);
    }

    /**
     * Proxy select.
     *
     * @param url the url
     * @throws Exception the exception
     */
    public static void proxySelect(String url) throws  Exception {
        PacProxySelector selector = new PacProxySelector(new UrlPacScriptSource("file:///C:/Users/haral/proxy/proxy.pac"));
        List<Proxy> pList = selector.select(new URI(url));
        System.out.println(pList.get(0).toString());

    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public static Integer getPORT() {
        return PORT;
    }


}
