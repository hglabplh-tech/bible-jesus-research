package org.harry.jesus.danielpersistence;

import jesus.harry.org.devotional._1.Devotional;
import jesus.harry.org.versnotes._1.Versnotes;
import org.harry.jesus.danielpersistence.xmlpersist.DevotionalPersistence;
import org.harry.jesus.danielpersistence.xmlpersist.VersNotesPersistence;

import java.io.InputStream;
import java.io.OutputStream;

public class PersistenceLayer {


    public static Devotional loadDevotional(InputStream stream) {
        return DevotionalPersistence.loadDevotional(stream);
    }




    public static void storeDevotional(Devotional root, OutputStream out) {
        DevotionalPersistence.storeDevotional(root, out);
    }

    public static Versnotes loadNotes(InputStream stream) {
        return VersNotesPersistence.loadNotes(stream);
    }




    public static void storeNotes(Versnotes root, OutputStream out) {
        VersNotesPersistence.storeNotes(root, out);
    }
}
