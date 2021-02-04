package org.harry.jesus.jesajautils.httpSrv;


import com.github.markusbernhardt.proxy.selector.pac.PacProxySelector;
import com.github.markusbernhardt.proxy.selector.pac.PacScriptSource;
import com.github.markusbernhardt.proxy.selector.pac.UrlPacScriptSource;
import com.sun.deploy.net.proxy.BrowserProxyInfo;
import com.sun.deploy.net.proxy.ProxyInfo;
import com.sun.deploy.net.proxy.ProxyType;
import com.sun.deploy.net.proxy.SunAutoProxyHandler;
import com.sun.net.httpserver.HttpServer;
import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.httpSrv.context.RetrieveChapterHandler;
import org.harry.jesus.jesajautils.httpSrv.context.VersPerDayHandler;


import java.io.IOException;
import java.net.*;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    }

    private void removeContextPoints() {
        server.removeContext("/dayVerse");
        server.removeContext("/retrieveChapter");
    }

    public void stopServer() {
        removeContextPoints();
        server.stop(0);
    }

    public static void proxySelect(String url) throws  Exception {
        PacProxySelector selector = new PacProxySelector(new UrlPacScriptSource("file:///C:/Users/haral/proxy/proxy.pac"));
        List<Proxy> pList = selector.select(new URI(url));
        System.out.println(pList.get(0).toString());

    }


}
