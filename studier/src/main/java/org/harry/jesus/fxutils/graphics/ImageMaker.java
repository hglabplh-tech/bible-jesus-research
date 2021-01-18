package org.harry.jesus.fxutils.graphics;

import org.jetbrains.annotations.NotNull;
import org.pmw.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

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
}
