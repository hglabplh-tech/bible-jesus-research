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
        utils = BibleTextUtils.getInstance();
    }

    @Test
    public void testWriteBooksCsv() throws Exception {
        List<String> result = utils.getBibleBookInfo(utils.getBibleInstances().get(0).getBible());
        File out = File.createTempFile("booksinfo", ".csv");

        FileWriter writer = new FileWriter(out);
        for (String str : result) {
            writer.write(str + "\n");
        }
        writer.close();
    }
}
