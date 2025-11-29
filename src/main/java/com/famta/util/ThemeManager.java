package com.famta.util;

import javafx.scene.Scene;
import java.util.prefs.Preferences;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

public class ThemeManager {
    private static final String CHRISTMAS_CSS = "/css/christmas.css";
    private static final String CHRISTMAS_AUDIO = "/audio/christmas_music.mp3";
    private static final String PREF_THEME = "app_theme_christmas";
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private static MediaPlayer christmasPlayer;

    static {
        try {
            Media christmasMedia = new Media(ThemeManager.class.getResource(CHRISTMAS_AUDIO).toExternalForm());
            christmasPlayer = new MediaPlayer(christmasMedia);
            christmasPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop infinitely
            christmasPlayer.setVolume(0.5); // Set initial volume
        } catch (Exception e) {
            System.err.println("Could not load Christmas audio: " + e.getMessage());
            christmasPlayer = null;
        }
    }

    public static void applyTheme(Scene scene) {
        boolean isChristmas = prefs.getBoolean(PREF_THEME, false);
        if (isChristmas) {
            enableChristmasTheme(scene);
        } else {
            disableChristmasTheme();
        }
    }

    public static void toggleTheme(Scene scene) {
        String cssUrl = ThemeManager.class.getResource(CHRISTMAS_CSS).toExternalForm();
        if (scene.getStylesheets().contains(cssUrl)) {
            scene.getStylesheets().remove(cssUrl);
            prefs.putBoolean(PREF_THEME, false);
            disableChristmasTheme();
        } else {
            scene.getStylesheets().add(cssUrl);
            prefs.putBoolean(PREF_THEME, true);
            enableChristmasTheme(scene);
        }
    }
    
    private static void enableChristmasTheme(Scene scene) {
        String cssUrl = ThemeManager.class.getResource(CHRISTMAS_CSS).toExternalForm();
        if (cssUrl != null && !scene.getStylesheets().contains(cssUrl)) {
            scene.getStylesheets().add(cssUrl);
        }
        if (christmasPlayer != null) {
            christmasPlayer.setVolume(0.5);
            if (christmasPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                christmasPlayer.play();
            }
        }
    }

    private static void disableChristmasTheme() {
        if (christmasPlayer != null && christmasPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            fadeOut();
        }
    }

    private static void fadeOut() {
        if (christmasPlayer == null) return;
        Timeline fadeOut = new Timeline(
            new KeyFrame(Duration.seconds(2), new javafx.animation.KeyValue(christmasPlayer.volumeProperty(), 0))
        );
        fadeOut.play();
    }
    
    public static boolean isChristmasThemeActive() {
        return prefs.getBoolean(PREF_THEME, false);
    }
}
