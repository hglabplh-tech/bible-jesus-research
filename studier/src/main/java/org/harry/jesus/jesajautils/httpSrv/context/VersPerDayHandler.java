package org.harry.jesus.jesajautils.httpSrv.context;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import generated.XMLBIBLE;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.harry.jesus.jesajautils.HTMLRendering;
import org.harry.jesus.jesajautils.httpSrv.DayVerses;
import org.harry.jesus.jesajautils.httpSrv.HttpSrvUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The type Vers per day handler.
 */
public class VersPerDayHandler implements HttpHandler {

    private int sequence = 0;

    private final XMLBIBLE bible;

    private ThreadLocalRandom randomizer;

    private final Boolean verseRandom;

    /**
     * Instantiates a new Vers per day handler.
     *
     * @param bible       the bible
     * @param verseRandom the verse random
     */
    public VersPerDayHandler(XMLBIBLE bible, Boolean verseRandom) {
        this.bible = bible;
        randomizer = ThreadLocalRandom.current();
        this.verseRandom = verseRandom;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BibleTextUtils utils = BibleTextUtils.getInstance();
        BibleTextUtils.BookLink link = getRandomVerse();
        XMLBIBLE oldBible = BibleTextUtils.getInstance().getSelected();
        BibleTextUtils.getInstance().setSelected(this.bible);
        String htmlText = HTMLRendering.renderLink(
                utils, this.bible, Arrays.asList(link));
        System.out.println(htmlText);
        BibleTextUtils.getInstance().setSelected(oldBible);
        HttpSrvUtils.getLengthAndSendText(exchange, htmlText);
    }

    /**
     * Get a verse either by random or sequential
     *
     * @return the verse link
     */
    public BibleTextUtils.BookLink getRandomVerse() {
        DayVerses versesObj = DayVerses.getInstance();
        List<BibleTextUtils.BookLink> linkList = versesObj.loadVerses();
        BibleTextUtils.BookLink link;
        if (verseRandom) {
            Integer index = randomizer.nextInt(linkList.size());
            link = linkList.get(index);
        } else {
            link = linkList.get(sequence);
        }
        System.out.println(link);
        if (sequence == (linkList.size() - 1)) {
            sequence = 0;
        } else {
            sequence++;
        }
        return link;
    }
}
