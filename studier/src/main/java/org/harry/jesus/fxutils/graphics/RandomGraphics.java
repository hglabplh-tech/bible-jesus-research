package org.harry.jesus.fxutils.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RandomGraphics {

    static final Integer MAX_GRAPHICS = 25;
    static List<Color> colorList = Arrays.asList(Color.WHITE, Color.YELLOW,
            Color.RED, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.WHITE, Color.LIGHT_GRAY,
            Color.GRAY, Color.DARK_GRAY, Color.ORANGE);
    static int width = 700;
    static int height = 500;
    static int minimum = 1;
    static int maximum = 8;
    static int colIndex = 0;


    static {
        colIndex = rnNum();
    }

    public static void paint() {
        int x = 0;
        int y = 0;

        for (int index = 0; index < MAX_GRAPHICS; index++) {
            BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bimage.createGraphics();
            graphics.setColor(getColByIndex());
            graphics.drawRect(x, y, width, height);
            // fill all the image with white
            graphics.setColor(getColByIndex());
            graphics.fillRect(0, 0, width, height);


            graphics.setColor(getColByIndex());
            graphics.fillOval(0, height / rnNum(), width / rnNum(), height / rnNum());

            // create a circle with black
            graphics.setColor(getColByIndex());
            graphics.fillOval(width / rnNum(), 0, width / rnNum(), height / rnNum());
            graphics.setColor(getColByIndex());
            graphics.fillOval(width / rnNum(), height / rnNum(),
                    width / rnNum(),
                    height / rnNum());
            graphics.setColor(getColByIndex());
            graphics.fillOval(width / rnNum(), height / rnNum(),
                    width / rnNum(), height / rnNum());
            graphics.setColor(getColByIndex());
            Shape shape = new Ellipse2D.Double((double) rnNum(), (double) rnNum(),
                    (double) height / rnNum(), (double) height / rnNum());
            ;
            graphics.draw(shape);


            // Disposes of this graphics context and releases any system resources that it is using.
            graphics.dispose();
            Image img = SwingFXUtils.toFXImage(bimage, null);
            ImageMaker.saveToFile(img, "samples");
        }
    }

    private static Color getColByIndex() {
        colIndex++;
        if (colIndex ==  colorList.size()) {
            colIndex = 0;
        }
        return colorList.get(colIndex);
    }

    private static int rnNum() {
        return ThreadLocalRandom.current().nextInt(minimum, maximum + 1);
    }

}
