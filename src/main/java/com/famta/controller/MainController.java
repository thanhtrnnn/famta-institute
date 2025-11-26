package com.famta.controller;

import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import com.famta.session.UserSession;
import com.famta.util.ThemeManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.animation.AnimationTimer;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary controller responsible for navigation between feature screens.
 */
public class MainController {

    private static final String SCREEN_ROOT = "/fxml/screens/";
    private static final Map<String, String> NAVIGATION_TITLES = Map.ofEntries(
        Map.entry("dashboard", "Tổng quan"),
        Map.entry("timetable", "Thời khóa biểu"),
        Map.entry("students", "Học sinh"),
        Map.entry("guardians", "Phụ huynh"),
        Map.entry("teachers", "Giáo viên"),
        Map.entry("courses", "Khoá học"),
        Map.entry("scores", "Điểm số"),
        Map.entry("reports", "Báo cáo"),
        Map.entry("accounts", "Tài khoản"),
        Map.entry("master-data", "Danh mục")
    );

    @FXML
    private Label roleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private StackPane contentHost;

    @FXML
    private ToggleGroup navigationGroup;

    @FXML
    private ToggleButton dashboardToggle;
    @FXML private ToggleButton timetableToggle;
    @FXML private ToggleButton studentsToggle;
    @FXML private ToggleButton guardiansToggle;
    @FXML private ToggleButton teachersToggle;
    @FXML private ToggleButton coursesToggle;
    @FXML private ToggleButton scoresToggle;
    @FXML private ToggleButton reportsToggle;
    @FXML private ToggleButton masterDataToggle;
    
    @FXML
    private Button accountButton;

    @FXML
    private Pane sidebarSnowPane;
    @FXML
    private AnchorPane decorationPane;

    private final Map<String, Node> viewCache = new HashMap<>();
    private TaiKhoan authenticatedAccount;
    private Runnable signOutCallback;
    private AnimationTimer snowTimer;
    private final List<Circle> snowflakes = new ArrayList<>();
    private final Random random = new Random();

    @FXML
    private void handleSwitchTheme() {
        if (contentHost.getScene() != null) {
            ThemeManager.toggleTheme(contentHost.getScene());
            updateChristmasDecorations();
        }
    }

    @FXML
    private void initialize() {
        if (roleLabel != null) {
            roleLabel.setText("--");
        }
        statusLabel.setText("Vui lòng đăng nhập để bắt đầu");
        navigationGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null && oldToggle != null) {
                // Prevent deselection by re-selecting the old toggle
                oldToggle.setSelected(true);
            } else {
                onNavigationChanged(newToggle);
            }
        });
        
        // Initialize snow effect
        Platform.runLater(this::updateChristmasDecorations);
    }

    private void updateChristmasDecorations() {
        boolean isChristmas = ThemeManager.isChristmasThemeActive();
        
        // Toggle sidebar snow
        if (isChristmas) {
            startSnowEffect();
        } else {
            stopSnowEffect();
        }
        
        // Toggle main decorations (Santa/Snow Pile)
        updateDecorationVisibility();
    }

    private void startSnowEffect() {
        if (snowTimer != null) return;
        
        sidebarSnowPane.getChildren().clear();
        snowflakes.clear();
        
        // Create initial snowflakes
        for (int i = 0; i < 50; i++) {
            createSnowflake();
        }
        
        snowTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (Circle flake : snowflakes) {
                    flake.setCenterY(flake.getCenterY() + (double)flake.getUserData());
                    if (flake.getCenterY() > sidebarSnowPane.getHeight()) {
                        flake.setCenterY(-10);
                        flake.setCenterX(random.nextDouble() * sidebarSnowPane.getWidth());
                    }
                }
            }
        };
        snowTimer.start();
    }

    private void stopSnowEffect() {
        if (snowTimer != null) {
            snowTimer.stop();
            snowTimer = null;
        }
        sidebarSnowPane.getChildren().clear();
        snowflakes.clear();
    }

    private void createSnowflake() {
        Circle flake = new Circle(random.nextDouble() * 2 + 1, javafx.scene.paint.Color.WHITE);
        flake.setOpacity(random.nextDouble() * 0.6 + 0.2);
        flake.setCenterX(random.nextDouble() * 200); // Approximate width of sidebar
        flake.setCenterY(random.nextDouble() * 600);
        flake.setUserData(random.nextDouble() * 2 + 0.5); // Speed
        
        snowflakes.add(flake);
        sidebarSnowPane.getChildren().add(flake);
    }

    private void onNavigationChanged(Toggle selected) {
        if (selected == null) {
            return;
        }
        Object data = selected.getUserData();
        if (data == null) {
            return;
        }
        showView(String.valueOf(data));
        updateDecorationVisibility();
    }

    private void updateDecorationVisibility() {
        if (decorationPane == null) return;
        
        boolean isChristmas = ThemeManager.isChristmasThemeActive();
        if (!isChristmas) {
            decorationPane.setVisible(false);
            return;
        }
        
        // Check current view
        Toggle selected = navigationGroup.getSelectedToggle();
        if (selected != null && selected.getUserData() != null) {
            String viewKey = String.valueOf(selected.getUserData());
            // Hide on Reports, Scores, Master Data
            if ("reports".equals(viewKey) || "scores".equals(viewKey) || "master-data".equals(viewKey)) {
                decorationPane.setVisible(false);
            } else {
                decorationPane.setVisible(true);
            }
        } else {
            decorationPane.setVisible(true);
        }
    }

    public void initializeSession(TaiKhoan account, Runnable signOutCallback) {
        this.authenticatedAccount = Objects.requireNonNull(account, "account");
        this.signOutCallback = signOutCallback;
        this.viewCache.clear();
        if (roleLabel != null) {
            roleLabel.setText(account.getQuyen().toDisplayLabel() + " • " + account.getTenDangNhap());
        }
        statusLabel.setText("Xin chào, " + account.getTenDangNhap() + "! Chọn một chức năng để tiếp tục.");
        
        applyRoleBasedAccess(account.getQuyen());
        
        if (dashboardToggle != null) {
            dashboardToggle.setSelected(true);
            onNavigationChanged(dashboardToggle);
        }
    }

    private void applyRoleBasedAccess(QuyenTruyCap role) {
        // Reset all to visible
        setVisible(true, studentsToggle, guardiansToggle, teachersToggle, coursesToggle, scoresToggle, reportsToggle, masterDataToggle, timetableToggle);
        
        // Account button is ADMIN only
        if (accountButton != null) {
            boolean isAdmin = role == QuyenTruyCap.ADMIN;
            accountButton.setVisible(isAdmin);
            accountButton.setManaged(isAdmin);
        }

        switch (role) {
            case ADMIN:
                // Admin sees everything
                break;
            case GIAO_VIEN:
                // Teacher sees: Dashboard, Timetable, Students, Courses, Scores, Reports
                // Hides: Guardians, Teachers, Master Data
                setVisible(false, guardiansToggle, teachersToggle, masterDataToggle);
                break;
            case HOC_VIEN:
                // Student sees: Dashboard, Timetable, Courses, Scores
                // Hides: Students, Guardians, Teachers, Reports, Master Data
                setVisible(false, studentsToggle, guardiansToggle, teachersToggle, reportsToggle, masterDataToggle);
                break;
            case PHU_HUYNH:
                // Guardian sees: Dashboard, Timetable, Scores
                // Hides: Students, Guardians, Teachers, Courses, Reports, Master Data
                setVisible(false, studentsToggle, guardiansToggle, teachersToggle, coursesToggle, reportsToggle, masterDataToggle);
                break;
        }
    }

    private void setVisible(boolean visible, ToggleButton... buttons) {
        for (ToggleButton btn : buttons) {
            if (btn != null) {
                btn.setVisible(visible);
                btn.setManaged(visible);
            }
        }
    }



    private void showView(String key) {
        Node view = viewCache.computeIfAbsent(key, this::loadView);
        if (view != null) {
            contentHost.getChildren().setAll(view);
            statusLabel.setText("Đã tải giao diện: " + NAVIGATION_TITLES.getOrDefault(key, key));
        } else {
            statusLabel.setText("Không thể tải giao diện " + NAVIGATION_TITLES.getOrDefault(key, key));
        }
    }

    private Node loadView(String key) {
        String resourcePath = SCREEN_ROOT + key + ".fxml";
        try {
            return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcePath)));
        } catch (IOException | NullPointerException ex) {
            System.err.println("Không thể tải FXML " + resourcePath + ": " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    private void selectNavigation(String key) {
        if (navigationGroup == null) {
            return;
        }
        navigationGroup.getToggles().stream()
            .filter(toggle -> key.equals(String.valueOf(toggle.getUserData())))
            .findFirst()
            .ifPresent(toggle -> toggle.setSelected(true));
    }

    @FXML
    private void handleStudentManagement() {
        selectNavigation("students");
    }

    @FXML
    private void handleTeacherManagement() {
        selectNavigation("teachers");
    }

    @FXML
    private void handleClassManagement() {
        selectNavigation("classes");
    }

    @FXML
    private void handleCourseManagement() {
        selectNavigation("courses");
    }

    @FXML
    private void handleScoreManagement() {
        selectNavigation("scores");
    }

    @FXML
    private void handleReports() {
        selectNavigation("reports");
    }

    @FXML
    private void handleAccountManagement() {
        if (!UserSession.isAdmin()) {
            statusLabel.setText("Bạn không có quyền truy cập màn hình tài khoản.");
            return;
        }
        showView("accounts");
    }

    @FXML
    private void handleSignOut() {
        String user = Optional.ofNullable(authenticatedAccount)
            .map(TaiKhoan::getTenDangNhap)
            .orElse("tài khoản hiện tại");
        statusLabel.setText("Đã đăng xuất khỏi " + user);
        authenticatedAccount = null;
        UserSession.clear();
        viewCache.clear();
        Optional.ofNullable(signOutCallback)
            .ifPresent(callback -> Platform.runLater(callback));
    }
}