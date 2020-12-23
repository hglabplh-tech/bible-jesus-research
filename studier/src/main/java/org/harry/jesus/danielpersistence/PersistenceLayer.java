package org.harry.jesus.danielpersistence;

import jesus.harry.org.devotional._1.Devotional;
import org.harry.jesus.danielpersistence.xmlpersist.DevotionalPersistence;

import java.io.InputStream;
import java.io.OutputStream;

public class PersistenceLayer {


    public static Devotional loadDevotional(InputStream stream) {
        return DevotionalPersistence.loadDevotional(stream);
    }




    public static void storeDevotional(Devotional root, OutputStream out) {
        DevotionalPersistence.storeDevotional(root, out);
    }
}
