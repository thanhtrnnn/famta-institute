package com.famta.controller;

import com.famta.service.JdbcCourseService;
import com.famta.service.dto.CourseSummary;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * Controller powering the course catalog view.
 */
public class CourseManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> departmentFilter;

    @FXML
    private ComboBox<String> levelFilter;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private FlowPane courseContainer;

    @FXML
    private Label summaryLabel;

    private final JdbcCourseService courseService = new JdbcCourseService();
    private final ObservableList<CourseSummary> masterData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        configureFilters();
        loadCourses();
        hookListeners();
    }

    @FXML
    private void handleCreateCourse() {
        summaryLabel.setText("Chức năng tạo khóa học sẽ sớm được bổ sung.");
    }

    @FXML
    private void handleImportCatalog() {
        summaryLabel.setText("Chức năng nhập danh mục đang trong quá trình phát triển.");
    }

    private void configureFilters() {
        departmentFilter.setItems(FXCollections.observableArrayList("Tất cả"));
        departmentFilter.getSelectionModel().selectFirst();

        levelFilter.setItems(FXCollections.observableArrayList("Tất cả"));
        levelFilter.getSelectionModel().selectFirst();

        statusFilter.setItems(FXCollections.observableArrayList("Tất cả", "Đang giảng dạy", "Đang mở đăng ký"));
        statusFilter.getSelectionModel().selectFirst();
    }

    private void hookListeners() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> renderFiltered());
        departmentFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderFiltered());
        levelFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderFiltered());
        statusFilter.valueProperty().addListener((obs, oldValue, newValue) -> renderFiltered());
    }

    private void loadCourses() {
        try {
            List<CourseSummary> courses = courseService.fetchCourses();
            masterData.setAll(courses);
            rebuildFilterOptions();
            renderFiltered();
        } catch (Exception ex) {
            masterData.clear();
            summaryLabel.setText("Không thể tải danh sách khóa học: " + ex.getMessage());
            courseContainer.getChildren().setAll(createPlaceholder("Không thể lấy dữ liệu"));
        }
    }

    private void rebuildFilterOptions() {
        updateCombo(departmentFilter, masterData.stream()
            .map(CourseSummary::department)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));

        updateCombo(levelFilter, masterData.stream()
            .map(CourseSummary::level)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
    }

    private void updateCombo(ComboBox<String> comboBox, Set<String> rawValues) {
        String previous = comboBox.getSelectionModel().getSelectedItem();
        List<String> sorted = rawValues.stream()
            .map(String::trim)
            .filter(value -> !value.isBlank())
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Tất cả");
        items.addAll(sorted);
        comboBox.setItems(items);
        if (previous != null && items.contains(previous)) {
            comboBox.getSelectionModel().select(previous);
        } else {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    private void renderFiltered() {
        Predicate<CourseSummary> predicate = buildPredicate();
        List<CourseSummary> filtered = masterData.stream().filter(predicate).collect(Collectors.toList());
        summaryLabel.setText("Đang hiển thị " + filtered.size() + " khóa học");
        courseContainer.getChildren().clear();
        if (filtered.isEmpty()) {
            courseContainer.getChildren().add(createPlaceholder("Không tìm thấy khóa học phù hợp"));
            return;
        }
        filtered.forEach(summary -> courseContainer.getChildren().add(createCard(summary)));
    }

    private Predicate<CourseSummary> buildPredicate() {
        String keyword = normalize(searchField.getText());
        String department = departmentFilter.getSelectionModel().getSelectedItem();
        String level = levelFilter.getSelectionModel().getSelectedItem();
        String status = statusFilter.getSelectionModel().getSelectedItem();
        return summary -> matches(summary.department(), department)
            && matches(summary.level(), level)
            && matches(summary.status(), status)
            && matchesKeyword(summary, keyword);
    }

    private boolean matches(String value, String filterValue) {
        if (filterValue == null || "Tất cả".equalsIgnoreCase(filterValue)) {
            return true;
        }
        if (value == null) {
            return false;
        }
        return filterValue.equalsIgnoreCase(value.trim());
    }

    private boolean matchesKeyword(CourseSummary summary, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }
        return contains(summary.className(), keyword)
            || contains(summary.subject(), keyword)
            || contains(summary.department(), keyword)
            || contains(summary.teacher(), keyword);
    }

    private VBox createCard(CourseSummary summary) {
        VBox card = new VBox(6);
        card.getStyleClass().add("course-card");

        Label title = new Label(summary.className());
        title.getStyleClass().add("card-title");

        Label meta = new Label(summary.durationLabel());
        meta.getStyleClass().add("card-meta");
        meta.setTooltip(new Tooltip(summary.timeRange()));

        Label description = new Label(summary.detailSummary());
        description.setWrapText(true);

        Label status = new Label(summary.status() + " • " + summary.enrolled() + " học viên");
        status.getStyleClass().add("card-meta");

        Button detailsButton = new Button("Xem chi tiết");
        detailsButton.setOnAction(event -> handleViewDetails(summary));

        card.getChildren().addAll(title, meta, description, status, detailsButton);
        return card;
    }

    private Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("info-text");
        return placeholder;
    }

    private void handleViewDetails(CourseSummary summary) {
        summaryLabel.setText("Đang xem khóa học " + summary.className() + " (" + summary.code() + ")");
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
