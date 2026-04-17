package com.group4.tarecruitment.util;

import javafx.scene.Parent;
import javafx.scene.Scene;

public final class ThemeManager {
    private static final String THEME_CSS = "/styles/app-theme.css";

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
        if (scene == null) {
            return;
        }
        if (ThemeManager.class.getResource(THEME_CSS) != null) {
            String css = ThemeManager.class.getResource(THEME_CSS).toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
        } else {
            System.err.println("Theme CSS not found: " + THEME_CSS);
        }
    }
}
