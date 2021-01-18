package org.harry.jesus.fxutils.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.harry.jesus.jesajautils.Tuple;
import org.harry.jesus.synchjeremia.SynchThread;
import org.jetbrains.annotations.NotNull;
import org.pmw.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

public class ImageMaker {
    public static Optional<BufferedImage> createImage (String verseText,
                                                       Color color, Float size,
                                                       final BufferedImage image)  {
        try {
            Graphics graphics = image.getGraphics();
            graphics.setFont(graphics.getFont().deriveFont(size));
            graphics.setColor(color);
            StringBuffer cookedText = getCookedText(verseText, image, graphics);
            setTextToImage(graphics, cookedText, image);
            graphics.dispose();
            return Optional.of(image);
        } catch (Exception ex) {
            Logger.trace(ex);
            return Optional.empty();
        }
    }

    private static void setTextToImage(Graphics graphics, StringBuffer cookedText,
                                       BufferedImage image) {
        int y = 50;
        FontMetrics fMetrics = graphics.getFontMetrics();
        Integer imgWidth = image.getWidth();
        String cookedString = cookedText.toString();
        for (String out : cookedString.split("\n")) {
            Integer textPixelsWidth = fMetrics.stringWidth(out);
            Integer xPos = ((imgWidth - textPixelsWidth) / 2);
            graphics.drawString(out, xPos, y);
            y = y + 70;
        }
    }

    @NotNull
    private static StringBuffer getCookedText(String verseText, BufferedImage image, Graphics graphics) {
        FontMetrics fMetrics = graphics.getFontMetrics();
        Integer imgWidth = image.getWidth();
        Integer fTextWidth = fMetrics.stringWidth(verseText);
        //Integer everageWitdth = fTextWidth / verseText.length();
        Integer everageWitdth = fMetrics.stringWidth("EB");
        Integer charsPerLine = imgWidth / everageWitdth;
        int cut = 0;
        StringBuffer cookedText = new StringBuffer();

        for (String word: verseText.split(" ")) {
            cut = cut + word.length() + 1;
            if (cut >= charsPerLine -4) {
                cut = 0;
                cookedText.append('\n');
                cookedText.append(word + " ");
            } else {
                cookedText.append(word + " ");
            }
        }
        return cookedText;
    }

    public static Color convertColor(javafx.scene.paint.Color fx) {
        return  new Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
    }

    public static void saveToFile(Image image) {
        File outputFile = new File(SynchThread.appDir, UUID.randomUUID().toString() + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Tuple<Integer, Integer> getZoomValues(Image source, Integer relation) {
        double ratio = source.getWidth() / source.getHeight();
        int width = 0;
        int height = 0;
        if (relation / ratio < relation) {
            width = relation;
            height = (int) (relation / ratio);
        } else if (relation * ratio < relation) {
            height = relation;
            width = (int) (relation * ratio);
        } else {
            height = relation;
            width = relation;
        }
        return new Tuple<>(width,height);
    }

    public static byte [] getBytesFromImage(Image image) {
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", s);
            byte[] result = s.toByteArray();
            s.close(); //especially if you are using a different output stream.
            return result;
        } catch (IOException ex) {
            return null;
        }
    }
}
