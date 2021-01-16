package org.harry.jesus.synchjeremia;

import jesus.harry.org.highlights._1.Highlights;
import jesus.harry.org.versnotes._1.Versnotes;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.configjaxbser.BibleAppConfig;
import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;

import java.util.*;

public class BibleThreadPool {

    public static final ThreadLocal<ThreadBean> contexts = new ThreadLocal<>();


    static  {
        ThreadBean context = new ThreadBean();
        contexts.set(context);
    }

    public void doGet(ThreadBean context) {

        contexts.set(context); // save that context to our thread-local - other threads
        // making this call don't overwrite ours
        try {
            // business logic
        } finally {
            contexts.remove(); // 'ensure' removal of thread-local variable
        }
    }

    public static ThreadBean getContext() {
        return contexts.get(); // get returns the variable unique to this thread
    }

    public static class ThreadBean {


        final Properties settings = new Properties();

        List<BibleRef> bibleRefList = new ArrayList<>();

        List<BibleFulltextEngine.BibleTextKey> verseKeys = new ArrayList<>();

        Versnotes noteList = new Versnotes();

        Highlights highlights = new Highlights();

        Map<Tuple<Integer, Integer>, Map<Integer, String>> renderMap = new LinkedHashMap<>();

        List<HistoryEntry> history = new ArrayList<>();

        BibleAppConfig appSettings = new BibleAppConfig();

        public List<BibleFulltextEngine.BibleTextKey> getVerseKeys() {
            return verseKeys;
        }

        public ThreadBean setVerseKeys(List<BibleFulltextEngine.BibleTextKey> verseKeys) {
            this.verseKeys = verseKeys;
            return this;
        }

        public Versnotes getNoteList() {
            return noteList;
        }

        public Map<Tuple<Integer, Integer>, Map<Integer, String>> getRenderMap() {
            return renderMap;
        }

        public ThreadBean setRenderMap(Map<Tuple<Integer, Integer>, Map<Integer, String>> renderMap) {
            this.renderMap = renderMap;
            return this;
        }

        public ThreadBean addRenderElement(Tuple<Integer, Integer> key, Map<Integer, String> value) {
            this.renderMap.put(key, value);
            return this;
        }

        public Highlights getHighlights() {
            return highlights;
        }

        public Properties getSettings() {
            return settings;
        }

        public Properties addSetting(String key, String value) {
            settings.setProperty(key, value);
            return settings;
        }

        public BibleAppConfig getAppSettings() {
            return appSettings;
        }

        public ThreadBean setAppSettings(BibleAppConfig appSettings) {
            this.appSettings = appSettings;
            return this;
        }

        public List<BibleRef> getBibleRefList() {
            return bibleRefList;
        }

        public List<HistoryEntry> getHistory() {
            return history;
        }

        public void addHistoryEntry(HistoryEntry entry) {
            history.add(0, entry);
        }
    }
}
