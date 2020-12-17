package org.harry.jesus.jesajautils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class BibleTextUtilTest {

    static BibleTextUtils utils;

    @BeforeClass
    public static void beforeClass () {
        utils = new BibleTextUtils();
    }

    @Test
    public void testWriteBooksCsv() throws Exception {
        List<String> result = utils.getBibleBookInfo(utils.getBibles().get(0));
        File out = File.createTempFile("booksinfo", ".csv");

        FileWriter writer = new FileWriter(out);
        for (String str : result) {
            writer.write(str + "\n");
        }
        writer.close();
    }
}
