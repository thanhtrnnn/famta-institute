package com.famta.controller;

import com.famta.model.QuyenTruyCap;
import com.famta.model.TaiKhoan;
import com.famta.session.UserSession;
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
import javafx.scene.layout.StackPane;

/**
 * Primary controller responsible for navigation between feature screens.
 */
public class MainController {

    private static final String SCREEN_ROOT = "/fxml/screens/";
    private static final Map<String, String> NAVIGATION_TITLES = Map.ofEntries(
        Map.entry("dashboard", "Tổng quan"),
        Map.entry("students", "Học viên"),
        Map.entry("guardians", "Phụ huynh"),
        Map.entry("teachers", "Giáo viên"),
        Map.entry("classes", "Lớp học"),
        Map.entry("courses", "Khóa học"),
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
    @FXML private ToggleButton studentsToggle;
    @FXML private ToggleButton guardiansToggle;
    @FXML private ToggleButton teachersToggle;
    @FXML private ToggleButton classesToggle;
    @FXML private ToggleButton coursesToggle;
    @FXML private ToggleButton scoresToggle;
    @FXML private ToggleButton reportsToggle;
    @FXML private ToggleButton masterDataToggle;
    
    @FXML
    private Button accountButton;

    private final Map<String, Node> viewCache = new HashMap<>();
    private TaiKhoan authenticatedAccount;
    private Runnable signOutCallback;

    @FXML
    private void initialize() {
        if (roleLabel != null) {
            roleLabel.setText("--");
        }
        statusLabel.setText("Vui lòng đăng nhập để bắt đầu");
        navigationGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> onNavigationChanged(newToggle));
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
        setVisible(true, studentsToggle, guardiansToggle, teachersToggle, classesToggle, coursesToggle, scoresToggle, reportsToggle, masterDataToggle);
        
        // Account button is ADMIN only
        if (accountButton != null) {
            boolean isAdmin = role == QuyenTruyCap.ADMIN;
            accountButton.setVisible(isAdmin);
            accountButton.setManaged(isAdmin);
        }

        switch (role) {
            case ADMIN:
                // All visible
                break;
            case GIAO_VIEN:
                setVisible(false, teachersToggle, reportsToggle, masterDataToggle);
                break;
            case HOC_VIEN:
                setVisible(false, studentsToggle, guardiansToggle, teachersToggle, reportsToggle, masterDataToggle);
                break;
            case PHU_HUYNH:
                setVisible(false, studentsToggle, guardiansToggle, teachersToggle, classesToggle, coursesToggle, reportsToggle, masterDataToggle);
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

    private void onNavigationChanged(Toggle selected) {
        if (selected == null) {
            return;
        }
        Object data = selected.getUserData();
        if (data == null) {
            return;
        }
        showView(String.valueOf(data));
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