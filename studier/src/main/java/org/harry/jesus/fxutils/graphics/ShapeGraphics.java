package org.harry.jesus.fxutils.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * The type Shape graphics.
 */
public class ShapeGraphics {

    /**
     * The Max graphics.
     */
    static final Integer MAX_GRAPHICS = 25;
    /**
     * The Color list.
     */
    static List<Color> colorList = Arrays.asList(Color.WHITE, Color.YELLOW,
            Color.RED, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.WHITE, Color.LIGHT_GRAY,
            Color.GRAY, Color.DARK_GRAY, Color.ORANGE);
    /**
     * The Width.
     */
    static int width = 700;
    /**
     * The Height.
     */
    static int height = 500;
    /**
     * The Minimum.
     */
    static int minimum = 1;
    /**
     * The Maximum.
     */
    static int maximum = 8;
    /**
     * The Col index.
     */
    static int colIndex = 0;


    static {
        colIndex = rnNum();
    }

    /**
     * Paint.
     */
    public static void paint() {
        int x = 0;
        int y = 0;

        for (int index = 0; index < MAX_GRAPHICS; index++) {
            BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bimage.createGraphics();
            graphics.setColor(getColByIndex());
            graphics.drawRect(x, y, width, height);
            // fill all the image with white

            Shape shape = new CubicCurve2D.Double((double) rnNum(), (double) rnNum(),
                    (double) height / rnNum(), (double) height / rnNum(), rnNum(),
            (double) height / rnNum(), (double) height / rnNum(), rnNum());
            ;
            WobbleStroke stroke = new WobbleStroke(50.0f, 70f);
            Shape newShape = stroke.createStrokedShape(shape);
            graphics.setColor(getColByIndex());
            graphics.draw(newShape);
            graphics.fill(newShape);
            graphics.draw(shape);
            graphics.fill(shape);
            graphics.draw3DRect(width / rnNum(), height /  rnNum(), width / 2, height / 2, true);


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
