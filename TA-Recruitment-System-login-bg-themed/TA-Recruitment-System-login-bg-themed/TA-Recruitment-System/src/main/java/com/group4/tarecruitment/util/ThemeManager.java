package com.group4.tarecruitment.util;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.net.URL;

public final class ThemeManager {
    private static final String THEME_PATH = "/styles/pdf-theme.css";

    private ThemeManager() {
    }

    public static Scene createScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        apply(scene);
        return scene;
    }

    public static Scene createScene(Parent root) {
        Scene scene = new Scene(root);
        apply(scene);
        return scene;
    }

    public static void apply(Scene scene) {
        URL url = ThemeManager.class.getResource(THEME_PATH);
        if (url == null) {
            System.err.println("Theme stylesheet not found: " + THEME_PATH);
            return;
        }

        String css = url.toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }
}
