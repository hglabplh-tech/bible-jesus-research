package org.harry.jesus.fxutils.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;


/**
 * The type Apple man.
 */
public class AppleMan {

    /**
     * The Color list.
     */
    static List<Color> colorList = Arrays.asList(Color.WHITE, Color.YELLOW,
            Color.RED, Color.BLUE, Color.CYAN, Color.MAGENTA);
    /**
     * The Width.
     */
    static int width = 700;
    /**
     * The Height.
     */
    static int height = 500;

    /**
     * Paint.
     */
    public static void paint() {
        int x = 0;
        int y = 0;

        BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bimage.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.drawRect(x,y,width,height);
        // fill all the image with white
        graphics.setColor(Color.GREEN);
        graphics.fillRect(0, 0, width, height);

        // create a circle with black
        graphics.setColor(Color.YELLOW);
        graphics.fillOval(0, height / 3, width / 2, height / 3);

        // create a circle with black
        graphics.setColor(Color.CYAN);
        graphics.fillOval(width / 3, 0, width / 3, height / 2);
        graphics.setColor(Color.MAGENTA);
        graphics.fillOval(width / 3, height / 4, width / 3, height / 4);
        graphics.setColor(Color.BLUE);
        graphics.fillOval(width / 2, height / 3, width / 3, height / 2);

        zeichneMandelbrotmenge(graphics, 0, 500);
        // Disposes of this graphics context and releases any system resources that it is using.
        graphics.dispose();
        Image img = SwingFXUtils.toFXImage(bimage, null);
        ImageMaker.saveToFile(img, null);
    }

    /**
     * Iter zahl int.
     *
     * @param cx    the cx
     * @param cy    the cy
     * @param maxIt the max it
     * @return the int
     */
    public static int iterZahl(final double cx, final double cy, int maxIt) {
// bestimmt Anzahl der Iterationen
        int zaehler = 0;
        double zx = 0.0, zy = 0.0, tmp;
        do {
            tmp = zx*zx - zy*zy + cx;
            zy = 2*zx*zy + cy;
            zx = tmp;
            zaehler = zaehler + 1;
        } while (zx*zx + zy*zy <= 4.0 && zaehler < maxIt);
        return zaehler;
    }

    /**
     * Zeichne mandelbrotmenge.
     *
     * @param g      the g
     * @param liRand the li rand
     * @param obRand the ob rand
     */
    public static void zeichneMandelbrotmenge(Graphics2D g, int liRand, int obRand) {
        int colIndex = 0;
        double xa = -2.02, xe = 0.7, ya = -1.2, ye = 1.2; // Ratio 17:15
        double dx = (xe-xa)/(width-1), dy = (ye-ya)/(width-1);
        int yHalbe = height/2;
        double cx, cy;
        int maxIt = 500;
        cx = xa;
        g.setColor(colorList.get(colIndex));
        colIndex = setColIndex(colIndex);
        long zeit = System.currentTimeMillis();
        for (int sp = 0; sp < width; sp++) {
            cy = ye; // von oben nach unten
            for (int ze = 0; ze < height; ze++) {
                if (iterZahl(cx, cy, maxIt) == maxIt)  {
                    Color actCol = colorList.get(colIndex);
                    g.setColor(actCol);
                    colIndex = setColIndex(colIndex);
                    g.drawLine(sp + liRand, ze + obRand, sp + liRand, ze + obRand);
                }
                cy = cy - dy;
            } // for ze
            cy = 0; // cy = 0 separat behandeln !
            if (iterZahl(cx, cy, maxIt) == maxIt) {
                Color actCol = colorList.get(colIndex);
                g.setColor(actCol);
                colIndex = setColIndex(colIndex);
                g.drawLine(sp + liRand, yHalbe + obRand, sp + liRand, yHalbe + obRand);
            }
            cx = cx + dx;
        } // for sp
        System.out.println("benötigte Zeit = " + (System.currentTimeMillis() - zeit) + " ms");
    }

    private static int setColIndex(int colIndex) {
        colIndex++;
        if (colIndex ==  colorList.size()) {
            colIndex = 0;
        }
        return colIndex;
    }

}
