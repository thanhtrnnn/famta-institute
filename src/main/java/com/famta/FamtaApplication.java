package com.famta;

import com.famta.controller.LoginController;
import com.famta.controller.MainController;
import com.famta.database.DatabaseManager;
import com.famta.model.TaiKhoan;
import com.famta.service.AuthService;
import com.famta.session.UserSession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * FAMTA Institute Management System
 * Main Application Class
 */
public class FamtaApplication extends Application {

    private Stage primaryStage;
    private AuthService authService;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        try {
            // Using PNG for better compatibility with JavaFX
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/famta-transparent.png")));
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
            e.printStackTrace();
        }
        stage.setTitle("FAMTA Institute Management System");
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        authService = new AuthService(databaseManager);
        showLoginScene();
    }

    private void showLoginScene() {
        try {
            UserSession.clear();
            FXMLLoader loader = new FXMLLoader(FamtaApplication.class.getResource("/fxml/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 520, 420);
            applyStyles(scene);
            LoginController controller = loader.getController();
            controller.setAuthService(authService);
            controller.setLoginSuccessHandler(this::showMainScene);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(480);
            primaryStage.setMinHeight(360);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể tải màn hình đăng nhập", ex);
        }
    }

    private void showMainScene(TaiKhoan account) {
        try {
            UserSession.setCurrentAccount(account);
            FXMLLoader loader = new FXMLLoader(FamtaApplication.class.getResource("/fxml/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            applyStyles(scene);
            MainController controller = loader.getController();
            controller.initializeSession(account, this::showLoginScene);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
        } catch (IOException ex) {
            throw new IllegalStateException("Không thể tải màn hình chính", ex);
        }
    }

    private void applyStyles(Scene scene) {
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        com.famta.util.ThemeManager.applyTheme(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}