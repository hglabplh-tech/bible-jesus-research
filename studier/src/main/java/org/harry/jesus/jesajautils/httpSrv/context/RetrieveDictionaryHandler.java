package org.harry.jesus.jesajautils.httpSrv.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.harry.jesus.fxutils.LinkHandler;
import org.harry.jesus.jesajautils.configjaxbser.AppSettingsPersistence;
import org.harry.jesus.jesajautils.configjaxbser.BibleAppConfig;
import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;
import org.harry.jesus.jesajautils.httpSrv.HttpSrvUtils;
import org.harry.jesus.jesajautils.judaerrmsg.BibleStudyException;
import org.harry.jesus.synchjeremia.BibleThreadPool;
import org.harry.jesus.synchjeremia.SynchThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class RetrieveDictionaryHandler  implements HttpHandler  {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            FileInputStream stream = new FileInputStream(SynchThread.appSettings);
            BibleAppConfig bibleAppConfig = AppSettingsPersistence.loadAppSettings(stream);
            Map<String, String> query = LinkHandler.splitURIQuery(httpExchange.getRequestURI());
            String bible = query.get(LinkHandler.BIBLE_PARAM);
            if (bible != null) {
                Optional<Map.Entry<DictionaryRef, BibleRef>> result = bibleAppConfig.getDictConfig()
                        .getDictBibleMapping()
                        .entrySet().stream()
                        .filter(e -> e.getValue().getBibleID().equals(bible))
                        .findFirst();
                if (result.isPresent()) {
                    DictionaryRef ref = result.get().getKey();
                    String htmlFilePath = ref.getPathToBook().replace(".xml", ".html");
                    File file = new File(htmlFilePath);
                    httpExchange.sendResponseHeaders(200, file.length());
                    IOUtils.copy(new FileInputStream(file), httpExchange.getResponseBody());
                    httpExchange.getResponseBody().close();
                    httpExchange.close();
                } else {
                    throw new BibleStudyException("no dictionary found for bible: " + bible);
                }
            } else {
                throw new BibleStudyException("no dictionary found for bible: " + bible);
            }
        } catch (BibleStudyException ex ) {
            HttpSrvUtils.getLengthAndSendException(httpExchange, ex);
        }

    }
}
