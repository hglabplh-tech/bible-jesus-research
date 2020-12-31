package org.harry.jesus.fxutils.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.util.*;

public class PlayMediaTest {

    @Test
    public void playCheck() {
        String book1Cor13 = "C:\\Users\\haral\\biblebooks\\to_hear\\MP3\\1. Korinther 13.mp3";
        Media hit = new Media(new File(book1Cor13).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }


}
