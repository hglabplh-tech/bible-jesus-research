package org.harry.jesus.synchjeremia;

import jesus.harry.org.highlights._1.Highlights;
import jesus.harry.org.versnotes._1.Versnotes;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.jesajautils.configjaxbser.BibleAppConfig;
import org.harry.jesus.jesajautils.configjaxbser.BibleRef;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;
import org.harry.jesus.jesajautils.graphicsjaxb.VerseImageRoot;

import java.util.*;

/**
 * The type Bible thread pool.
 */
public class BibleThreadPool {

    /**
     * The constant contexts.
     */
    public static final ThreadLocal<ThreadBean> contexts = new ThreadLocal<>();


    static  {
        ThreadBean context = new ThreadBean();
        contexts.set(context);
    }

    /**
     * Do get.
     *
     * @param context the context
     */
    public void doGet(ThreadBean context) {

        contexts.set(context); // save that context to our thread-local - other threads
        // making this call don't overwrite ours
        try {
            // business logic
        } finally {
            contexts.remove(); // 'ensure' removal of thread-local variable
        }
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public static ThreadBean getContext() {
        return contexts.get(); // get returns the variable unique to this thread
    }

    /**
     * The type Thread bean.
     */
    public static class ThreadBean {


        /**
         * The Settings.
         */
        final Properties settings = new Properties();

        /**
         * The Bible ref list.
         */
        List<BibleRef> bibleRefList = new ArrayList<>();

        /**
         * The Verse keys.
         */
        List<BibleFulltextEngine.BibleTextKey> verseKeys = new ArrayList<>();

        /**
         * The Note list.
         */
        Versnotes noteList = new Versnotes();

        /**
         * The Highlights.
         */
        Highlights highlights = new Highlights();

        /**
         * The Render map.
         */
        Map<Tuple<Integer, Integer>, Map<Integer, String>> renderMap = new LinkedHashMap<>();

        /**
         * The History.
         */
        List<HistoryEntry> history = new ArrayList<>();

        /**
         * The App settings.
         */
        BibleAppConfig appSettings = new BibleAppConfig();

        /**
         * The Verse images.
         */
        VerseImageRoot verseImages = new VerseImageRoot();

        /**
         * Gets verse keys.
         *
         * @return the verse keys
         */
        public List<BibleFulltextEngine.BibleTextKey> getVerseKeys() {
            return verseKeys;
        }

        /**
         * Sets verse keys.
         *
         * @param verseKeys the verse keys
         * @return the verse keys
         */
        public ThreadBean setVerseKeys(List<BibleFulltextEngine.BibleTextKey> verseKeys) {
            this.verseKeys = verseKeys;
            return this;
        }

        /**
         * Gets note list.
         *
         * @return the note list
         */
        public Versnotes getNoteList() {
            return noteList;
        }

        /**
         * Gets render map.
         *
         * @return the render map
         */
        public Map<Tuple<Integer, Integer>, Map<Integer, String>> getRenderMap() {
            return renderMap;
        }

        /**
         * Sets render map.
         *
         * @param renderMap the render map
         * @return the render map
         */
        public ThreadBean setRenderMap(Map<Tuple<Integer, Integer>, Map<Integer, String>> renderMap) {
            this.renderMap = renderMap;
            return this;
        }

        /**
         * Add render element thread bean.
         *
         * @param key   the key
         * @param value the value
         * @return the thread bean
         */
        public ThreadBean addRenderElement(Tuple<Integer, Integer> key, Map<Integer, String> value) {
            this.renderMap.put(key, value);
            return this;
        }

        /**
         * Gets highlights.
         *
         * @return the highlights
         */
        public Highlights getHighlights() {
            return highlights;
        }

        /**
         * Gets settings.
         *
         * @return the settings
         */
        public Properties getSettings() {
            return settings;
        }

        /**
         * Add setting properties.
         *
         * @param key   the key
         * @param value the value
         * @return the properties
         */
        public Properties addSetting(String key, String value) {
            settings.setProperty(key, value);
            return settings;
        }

        /**
         * Gets app settings.
         *
         * @return the app settings
         */
        public BibleAppConfig getAppSettings() {
            return appSettings;
        }

        /**
         * Sets app settings.
         *
         * @param appSettings the app settings
         * @return the app settings
         */
        public ThreadBean setAppSettings(BibleAppConfig appSettings) {
            this.appSettings = appSettings;
            return this;
        }

        /**
         * Gets bible ref list.
         *
         * @return the bible ref list
         */
        public List<BibleRef> getBibleRefList() {
            return bibleRefList;
        }

        /**
         * Gets history.
         *
         * @return the history
         */
        public List<HistoryEntry> getHistory() {
            return history;
        }

        /**
         * Add history entry.
         *
         * @param entry the entry
         */
        public void addHistoryEntry(HistoryEntry entry) {
            history.add(0, entry);
        }

        /**
         * Gets verse images.
         *
         * @return the verse images
         */
        public VerseImageRoot getVerseImages() {
            return verseImages;
        }

        /**
         * Sets verse images.
         *
         * @param verseImages the verse images
         * @return the verse images
         */
        public ThreadBean setVerseImages(VerseImageRoot verseImages) {
            this.verseImages = verseImages;
            return this;
        }
    }
}
