package org.harry.jesus.fxutils.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.pmw.tinylog.Logger;

import java.io.File;

public class PlayBible {

    private static final String MP3_SUFFIX = ".mp3";

    private final String mediaPath;

    private final MediaView mView;

    private MediaPlayer mediaPlayer;

    public PlayBible(String mediaPath, MediaView mView) {
        this.mView = mView;
        this.mediaPath = mediaPath;
    }

    public void stopChapter() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
    }


    public MediaPlayer  playChapter(BibleTextUtils.BookLink link) {
        File chapterMP3 = getMP3File(link);
        try {
            if (chapterMP3.exists() && mView != null) {
                Media hit = new Media(chapterMP3.toURI().toString());
                mediaPlayer = new MediaPlayer(hit);
                return mediaPlayer;
            }
        } catch (Exception ex) {
            Logger.trace(ex);
            Logger.trace("Unable to play chapter: " + chapterMP3.getAbsolutePath() + "\nreasonCode:" + ex.getMessage());
        }
        return mediaPlayer;
    }

    public File getMP3File(BibleTextUtils.BookLink link) {
        BibleTextUtils.BookLabel label  = new BibleTextUtils.BookLabel(link.getBookLabel());
        String namePart = String.format("%s %d",label.getLongName(), link.getChapter());
        String fullName = namePart + MP3_SUFFIX;
        return new File(mediaPath, fullName);
    }
}
