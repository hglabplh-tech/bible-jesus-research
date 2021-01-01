package org.harry.jesus.fxutils.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.harry.jesus.jesajautils.BibleTextUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Optional;

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
        Optional<File> chapterMP3 = getMP3File(link);
        try {
            if (chapterMP3.isPresent() && mView != null) {
                Media hit = new Media(chapterMP3.get().toURI().toString());
                mediaPlayer = new MediaPlayer(hit);
                return mediaPlayer;
            }
        } catch (Exception ex) {
            Logger.trace(ex);
            Logger.trace("Unable to play chapter: "
                    + chapterMP3.get().getAbsolutePath()
                    + "\nreasonCode:" + ex.getMessage());
        }
        return mediaPlayer;
    }

    public Optional<File> getMP3File(BibleTextUtils.BookLink link) {
        File mediaDirFile= new File(mediaPath);
        if (mediaDirFile.exists()) {
            File [] files = mediaDirFile.listFiles(new MediaFilter(link));
            if (files.length == 1) {
                return Optional.of(files[0]);
            }
        }
        return Optional.empty();
    }

    public static class MediaFilter implements FilenameFilter {

        private final BibleTextUtils.BookLink link;

        public MediaFilter(BibleTextUtils.BookLink link)  {
            this.link = link;
        }
        @Override
        public boolean accept(File dir, String name) {
            BibleTextUtils.BookLabel label  = new BibleTextUtils.BookLabel(link.getBookLabel());
            String namePart = String.format("%s %d",label.getLongName(), link.getChapter());
            String bookChapterNo = name.substring(0, 5);
            Integer bookNo = 0;
            Integer chapterNo = 0;
            if (bookChapterNo.matches("[0-9]+")) {
                bookNo = Integer.parseInt(bookChapterNo.substring(0, 2));
                chapterNo = Integer.parseInt(bookChapterNo.substring(2, 5));
            }
            if (name.contains(namePart)) {
                return true;
            } else if (label.getBookNumber().equals(bookNo)
                    &&  link.getChapter().equals(chapterNo)) {
                return true;
            }
            return false;
        }

    }
}
