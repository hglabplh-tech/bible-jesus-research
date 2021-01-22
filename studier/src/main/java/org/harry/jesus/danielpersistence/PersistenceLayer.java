package org.harry.jesus.danielpersistence;

import jesus.harry.org.devotional._1.Devotional;
import jesus.harry.org.highlights._1.Highlights;
import jesus.harry.org.plan._1.Plan;
import jesus.harry.org.versnotes._1.Versnotes;
import org.harry.jesus.danielpersistence.xmlpersist.DevotionalPersistence;
import org.harry.jesus.danielpersistence.xmlpersist.HighlightsPersistence;
import org.harry.jesus.danielpersistence.xmlpersist.PlanPersistence;
import org.harry.jesus.danielpersistence.xmlpersist.VersNotesPersistence;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * The type Persistence layer.
 */
public class PersistenceLayer {


    /**
     * Load devotional devotional.
     *
     * @param stream the stream
     * @return the devotional
     */
    public static Devotional loadDevotional(InputStream stream) {
        return DevotionalPersistence.loadDevotional(stream);
    }

    /**
     * Store devotional.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storeDevotional(Devotional root, OutputStream out) {
        DevotionalPersistence.storeDevotional(root, out);
    }

    /**
     * Load notes versnotes.
     *
     * @param stream the stream
     * @return the versnotes
     */
    public static Versnotes loadNotes(InputStream stream) {
        return VersNotesPersistence.loadNotes(stream);
    }

    /**
     * Store notes.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storeNotes(Versnotes root, OutputStream out) {
        VersNotesPersistence.storeNotes(root, out);
    }

    /**
     * Load plan plan.
     *
     * @param stream the stream
     * @return the plan
     */
    public static Plan loadPlan(InputStream stream) {
        return PlanPersistence.loadPlan(stream);
    }

    /**
     * Store plan.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storePlan(Plan root, OutputStream out) {
        PlanPersistence.storePlan(root, out);
    }

    /**
     * Load highlights highlights.
     *
     * @param stream the stream
     * @return the highlights
     */
    public static Highlights loadHighlights(InputStream stream) {
        return HighlightsPersistence.loadHighlights(stream);
    }

    /**
     * Store highligts.
     *
     * @param root the root
     * @param out  the out
     */
    public static void storeHighligts(Highlights root, OutputStream out) {
        HighlightsPersistence.storeHighlights(root, out);
    }
}
