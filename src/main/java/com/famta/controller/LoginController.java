package com.famta.controller;

import com.famta.model.TaiKhoan;
import com.famta.service.AuthService;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

/**
 * Controller for the login form.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    private AuthService authService;
    private Consumer<TaiKhoan> loginSuccessHandler;

    @FXML
    private void initialize() {
        if (messageLabel != null) {
            messageLabel.setText("");
            messageLabel.setManaged(false);
            messageLabel.setVisible(false);
        }
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
        }
    }

    public void setAuthService(AuthService authService) {
        this.authService = Objects.requireNonNull(authService, "authService");
    }

    public void setLoginSuccessHandler(Consumer<TaiKhoan> loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        if (authService == null) {
            throw new IllegalStateException("AuthService chưa được cấu hình cho LoginController");
        }
        hideMessage();
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        setBusy(true);
        Task<Optional<TaiKhoan>> loginTask = new Task<>() {
            @Override
            protected Optional<TaiKhoan> call() {
                return authService.authenticate(username, password);
            }
        };

        loginTask.setOnSucceeded(evt -> {
            setBusy(false);
            Optional<TaiKhoan> result = loginTask.getValue();
            if (result.isPresent()) {
                passwordField.clear();
                if (loginSuccessHandler != null) {
                    loginSuccessHandler.accept(result.get());
                }
            } else {
                showError("Sai tên đăng nhập hoặc mật khẩu.");
            }
        });

        loginTask.setOnFailed(evt -> {
            setBusy(false);
            Throwable throwable = loginTask.getException();
            throwable.printStackTrace();
            showError("Không thể đăng nhập. Vui lòng thử lại.");
        });

        Thread worker = new Thread(loginTask, "login-worker");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }

    private void hideMessage() {
        if (messageLabel != null) {
            messageLabel.setText("");
            messageLabel.setVisible(false);
            messageLabel.setManaged(false);
        }
    }

    private void showError(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setVisible(true);
            messageLabel.setManaged(true);
        }
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
        usernameField.setDisable(busy);
        passwordField.setDisable(busy);
        if (progressIndicator != null) {
            progressIndicator.setVisible(busy);
            progressIndicator.setManaged(busy);
        }
    }
}
