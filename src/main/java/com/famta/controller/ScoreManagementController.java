package com.famta.controller;

import com.famta.service.JdbcScoreService;
import com.famta.service.ScoreService;
import com.famta.service.dto.ScoreClassOption;
import com.famta.service.dto.ScoreEntry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

/**
 * Controller responsible for managing the score entry screen.
 */
public class ScoreManagementController {

    private static final String ALL_SUBJECTS_OPTION = "Tất cả môn học";
    private static final double MIN_SCORE = 0.0;
    private static final double MAX_SCORE = 10.0;
    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.##", symbols);
    }

    @FXML
    private ComboBox<ScoreClassOption> classFilter;

    @FXML
    private ComboBox<String> subjectFilter;

    @FXML
    private ComboBox<ScoreFormulaPreset> formulaSelector;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<ScoreRow> scoreTable;

    @FXML
    private TableColumn<ScoreRow, String> studentIdColumn;

    @FXML
    private TableColumn<ScoreRow, String> studentNameColumn;

    @FXML
    private TableColumn<ScoreRow, Double> regularScoreColumn;

    @FXML
    private TableColumn<ScoreRow, Double> midtermScoreColumn;

    @FXML
    private TableColumn<ScoreRow, Double> finalScoreColumn;

    @FXML
    private Button downloadTemplateButton;

    @FXML
    private Button saveDraftButton;

    @FXML
    private Button saveButton;

    @FXML
    private Label summaryLabel;

    @FXML
    private Label statusLabel;

    private final ScoreService scoreService = new JdbcScoreService();
    private final ObservableList<ScoreRow> masterData = FXCollections.observableArrayList();
    private final ObservableList<ScoreClassOption> allClassOptions = FXCollections.observableArrayList();
    private final ObservableList<ScoreClassOption> visibleClassOptions = FXCollections.observableArrayList();
    private FilteredList<ScoreRow> filteredData;
    private final BooleanProperty hasDirtyChanges = new SimpleBooleanProperty(false);

    @FXML
    private void initialize() {
        configureTable();
        configureFilters();
        configureButtons();
        loadClassOptions();
    }

    private void configureTable() {
        scoreTable.setEditable(true);
        scoreTable.setPlaceholder(new Label("Chọn lớp để hiển thị danh sách học viên"));

        studentIdColumn.setCellValueFactory(cell -> cell.getValue().studentIdProperty());
        studentNameColumn.setCellValueFactory(cell -> cell.getValue().studentNameProperty());

        makeEditableScoreColumn(regularScoreColumn, ScoreRow::regularScoreProperty, ScoreRow::setRegularScore);
        makeEditableScoreColumn(midtermScoreColumn, ScoreRow::midtermScoreProperty, ScoreRow::setMidtermScore);
        makeEditableScoreColumn(finalScoreColumn, ScoreRow::finalScoreProperty, ScoreRow::setFinalScore);

        filteredData = new FilteredList<>(masterData, row -> true);
        filteredData.addListener((ListChangeListener<ScoreRow>) change -> updateSummary());
        SortedList<ScoreRow> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(scoreTable.comparatorProperty());
        scoreTable.setItems(sorted);
        updateSummary();
    }

    private void configureFilters() {
        scoreTable.getSelectionModel().setCellSelectionEnabled(true);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applySearchFilter(newValue));

        formulaSelector.setItems(FXCollections.observableArrayList(ScoreFormulaPreset.values()));
        formulaSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(ScoreFormulaPreset preset) {
                return preset == null ? "" : preset.getLabel();
            }

            @Override
            public ScoreFormulaPreset fromString(String label) {
                for (ScoreFormulaPreset preset : ScoreFormulaPreset.values()) {
                    if (preset.getLabel().equals(label)) {
                        return preset;
                    }
                }
                return ScoreFormulaPreset.STANDARD;
            }
        });
        formulaSelector.getSelectionModel().select(ScoreFormulaPreset.STANDARD);

        classFilter.setItems(visibleClassOptions);
        classFilter.setCellFactory(listView -> new ClassListCell());
        classFilter.setButtonCell(new ClassListCell());
        classFilter.valueProperty().addListener((obs, oldValue, newValue) -> onClassSelected(newValue));

        subjectFilter.valueProperty().addListener((obs, oldValue, newValue) -> filterClassesBySubject(newValue));
    }

    private void configureButtons() {
        downloadTemplateButton.disableProperty().bind(classFilter.valueProperty().isNull());
        saveDraftButton.disableProperty().bind(Bindings.isEmpty(masterData));
        saveButton.disableProperty().bind(hasDirtyChanges.not().or(Bindings.isEmpty(masterData)));
    }

    private void makeEditableScoreColumn(
        TableColumn<ScoreRow, Double> column,
        Function<ScoreRow, ObjectProperty<Double>> propertyAccessor,
        ScoreValueUpdater updater
    ) {
        column.setCellValueFactory(cell -> propertyAccessor.apply(cell.getValue()));
        column.setCellFactory(col -> new EditingScoreCell());
        column.setOnEditCommit(event -> {
            ScoreRow row = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null && Double.isNaN(newValue)) {
                setStatus("Giá trị điểm không hợp lệ. Vui lòng nhập số trong khoảng 0 - 10.", true);
                scoreTable.refresh();
                return;
            }
            Double sanitized = sanitizeEditedScore(newValue);
            updater.apply(row, sanitized);
            updateDirtyState();
        });
    }

    private Double sanitizeEditedScore(Double raw) {
        if (raw == null) {
            return null;
        }
        if (Double.isNaN(raw) || Double.isInfinite(raw)) {
            return null;
        }
        double clamped = Math.max(MIN_SCORE, Math.min(MAX_SCORE, raw));
        if (Math.abs(clamped - raw) > 1e-6) {
            setStatus("Điểm được điều chỉnh để nằm trong khoảng 0 - 10.", true);
        }
        return roundTwoDecimals(clamped);
    }

    private void loadClassOptions() {
        try {
            List<ScoreClassOption> options = scoreService.findClassOptions();
            allClassOptions.setAll(options);
            visibleClassOptions.setAll(options);
            populateSubjectFilter(options);
            if (!visibleClassOptions.isEmpty()) {
                classFilter.getSelectionModel().selectFirst();
            } else {
                summaryLabel.setText("Chưa có lớp học nào");
                setStatus("Không tìm thấy lớp học để nhập điểm.", true);
            }
        } catch (Exception ex) {
            allClassOptions.clear();
            visibleClassOptions.clear();
            setStatus("Không thể tải danh sách lớp: " + ex.getMessage(), true);
        }
    }

    private void populateSubjectFilter(List<ScoreClassOption> options) {
        Set<String> subjects = options.stream()
            .map(ScoreClassOption::subjectName)
            .filter(name -> name != null && !name.isBlank())
            .collect(Collectors.toCollection(() -> new TreeSet<>(String::compareToIgnoreCase)));
        ObservableList<String> subjectItems = FXCollections.observableArrayList();
        subjectItems.add(ALL_SUBJECTS_OPTION);
        subjectItems.addAll(subjects);
        subjectFilter.setItems(subjectItems);
        subjectFilter.getSelectionModel().selectFirst();
    }

    private void filterClassesBySubject(String subjectName) {
        if (subjectName == null || subjectName.equals(ALL_SUBJECTS_OPTION)) {
            visibleClassOptions.setAll(allClassOptions);
        } else {
            visibleClassOptions.setAll(allClassOptions.stream()
                .filter(option -> subjectName.equals(option.subjectName()))
                .collect(Collectors.toCollection(ArrayList::new)));
        }
        if (!visibleClassOptions.isEmpty()) {
            classFilter.getSelectionModel().selectFirst();
        } else {
            classFilter.getSelectionModel().clearSelection();
            masterData.clear();
            updateDirtyState();
            updateSummary();
            setStatus("Không tìm thấy lớp nào cho môn " + subjectName, true);
        }
    }

    private void onClassSelected(ScoreClassOption option) {
        masterData.clear();
        updateDirtyState();
        if (option == null) {
            updateSummary();
            return;
        }
        try {
            List<ScoreEntry> entries = scoreService.findScoresByClass(option.classId());
            for (ScoreEntry entry : entries) {
                masterData.add(new ScoreRow(entry.studentId(), entry.fullName(), entry.regularScore(), entry.midtermScore(), entry.finalScore()));
            }
            updateDirtyState();
            applySearchFilter(searchField.getText());
            setStatus("Đang xem lớp " + option.className(), false);
        } catch (Exception ex) {
            masterData.clear();
            setStatus("Không thể tải điểm: " + ex.getMessage(), true);
        }
    }

    private void applySearchFilter(String keyword) {
        if (filteredData == null) {
            return;
        }
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.getDefault());
        filteredData.setPredicate(row -> normalized.isEmpty() || row.matches(normalized));
        updateSummary();
    }

    private void updateDirtyState() {
        boolean dirty = masterData.stream().anyMatch(ScoreRow::isDirty);
        hasDirtyChanges.set(dirty);
    }

    private void updateSummary() {
        int total = filteredData == null ? 0 : filteredData.size();
        summaryLabel.setText("Hiển thị " + total + " học viên");
    }

    @FXML
    private void handleDownloadTemplate() {
        exportCsv(false);
    }

    @FXML
    private void handleSaveDraft() {
        exportCsv(true);
    }

    @FXML
    private void handleSaveChanges() {
        ScoreClassOption option = classFilter.getValue();
        if (option == null) {
            setStatus("Vui lòng chọn lớp trước khi lưu.", true);
            return;
        }
        if (masterData.isEmpty()) {
            setStatus("Không có dữ liệu để lưu.", true);
            return;
        }
        List<ScoreRow> dirtyRows = masterData.stream()
            .filter(ScoreRow::isDirty)
            .collect(Collectors.toList());
        if (dirtyRows.isEmpty()) {
            setStatus("Không có thay đổi nào cần lưu.", false);
            return;
        }
        try {
            for (ScoreRow row : dirtyRows) {
                scoreService.updateScore(option.classId(), row.getStudentId(), row.getRegularScore(), row.getMidtermScore(), row.getFinalScore());
                row.markPersisted();
            }
            updateDirtyState();
            setStatus("Đã lưu " + dirtyRows.size() + " bản ghi cho lớp " + option.className(), false);
        } catch (Exception ex) {
            setStatus("Lưu điểm thất bại: " + ex.getMessage(), true);
        }
    }

    private void exportCsv(boolean includeScores) {
        ScoreClassOption option = classFilter.getValue();
        if (option == null) {
            setStatus("Vui lòng chọn lớp để xuất dữ liệu.", true);
            return;
        }
        if (includeScores && masterData.isEmpty()) {
            setStatus("Không có dữ liệu để lưu nháp.", true);
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (Comma separated)", "*.csv"));
        chooser.setInitialFileName((includeScores ? "scores-draft-" : "scores-template-") + option.classId() + ".csv");
        Window owner = Optional.ofNullable(scoreTable.getScene()).map(Scene::getWindow).orElse(null);
        File target = chooser.showSaveDialog(owner);
        if (target == null) {
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(target.toPath(), StandardCharsets.UTF_8)) {
            writer.write("MaHocSinh,HoVaTen,DiemThuongXuyen,DiemGiuaKy,DiemCuoiKy");
            writer.newLine();
            for (ScoreRow row : masterData) {
                if (includeScores) {
                    writer.write(toCsv(row.getStudentId(), row.getStudentName(), row.getRegularScore(), row.getMidtermScore(), row.getFinalScore()));
                } else {
                    writer.write(toCsv(row.getStudentId(), row.getStudentName(), null, null, null));
                }
                writer.newLine();
            }
            setStatus("Đã xuất file " + target.getName(), false);
        } catch (IOException ex) {
            setStatus("Không thể ghi file CSV: " + ex.getMessage(), true);
        }
    }

    private String toCsv(String studentId, String studentName, Double tx, Double gk, Double ck) {
        return String.join(",",
            escape(studentId),
            escape(studentName),
            escape(formatScore(tx)),
            escape(formatScore(gk)),
            escape(formatScore(ck))
        );
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private String formatScore(Double value) {
        return value == null ? "" : DECIMAL_FORMAT.format(value);
    }

    private void setStatus(String message, boolean error) {
        if (statusLabel == null) {
            return;
        }
        statusLabel.getStyleClass().removeAll("error-message", "success-message");
        if (!statusLabel.getStyleClass().contains("info-text")) {
            statusLabel.getStyleClass().add("info-text");
        }
        if (error) {
            if (!statusLabel.getStyleClass().contains("error-message")) {
                statusLabel.getStyleClass().add("error-message");
            }
        } else {
            if (!statusLabel.getStyleClass().contains("success-message")) {
                statusLabel.getStyleClass().add("success-message");
            }
        }
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private static double clampScore(double value) {
        return roundTwoDecimals(Math.max(MIN_SCORE, Math.min(MAX_SCORE, value)));
    }

    private static double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    @FunctionalInterface
    private interface ScoreValueUpdater {
        void apply(ScoreRow row, Double value);
    }

    private static final class EditingScoreCell extends TextFieldTableCell<ScoreRow, Double> {
        EditingScoreCell() {
            super(new ScoreStringConverter());
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            }
        }
    }

    private static final class ScoreStringConverter extends StringConverter<Double> {
        @Override
        public String toString(Double value) {
            return value == null ? "" : DECIMAL_FORMAT.format(value);
        }

        @Override
        public Double fromString(String text) {
            if (text == null || text.isBlank()) {
                return null;
            }
            try {
                String normalized = text.trim().replace(',', '.');
                return Double.parseDouble(normalized);
            } catch (NumberFormatException ex) {
                return Double.NaN;
            }
        }
    }

    private static final class ClassListCell extends ListCell<ScoreClassOption> {
        @Override
        protected void updateItem(ScoreClassOption item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.toString());
        }
    }

    private static final class ScoreRow {
        private final StringProperty studentId = new SimpleStringProperty(this, "studentId");
        private final StringProperty studentName = new SimpleStringProperty(this, "studentName");
        private final ObjectProperty<Double> regularScore = new SimpleObjectProperty<>(this, "regularScore");
        private final ObjectProperty<Double> midtermScore = new SimpleObjectProperty<>(this, "midtermScore");
        private final ObjectProperty<Double> finalScore = new SimpleObjectProperty<>(this, "finalScore");
        private final BooleanProperty dirty = new SimpleBooleanProperty(this, "dirty", false);
        private final String normalizedId;
        private final String normalizedName;
        private Double originalRegular;
        private Double originalMidterm;
        private Double originalFinal;

        ScoreRow(String studentId, String studentName, Double regular, Double midterm, Double finalScore) {
            this.studentId.set(Objects.requireNonNull(studentId, "studentId"));
            this.studentName.set(studentName == null ? "" : studentName.trim());
            this.normalizedId = this.studentId.get().toLowerCase(Locale.getDefault());
            this.normalizedName = this.studentName.get().toLowerCase(Locale.getDefault());
            this.regularScore.set(regular == null ? null : clampScore(regular));
            this.midtermScore.set(midterm == null ? null : clampScore(midterm));
            this.finalScore.set(finalScore == null ? null : clampScore(finalScore));
            markPersisted();
        }

        StringProperty studentIdProperty() {
            return studentId;
        }

        StringProperty studentNameProperty() {
            return studentName;
        }

        ObjectProperty<Double> regularScoreProperty() {
            return regularScore;
        }

        ObjectProperty<Double> midtermScoreProperty() {
            return midtermScore;
        }

        ObjectProperty<Double> finalScoreProperty() {
            return finalScore;
        }

        void setRegularScore(Double value) {
            regularScore.set(value == null ? null : clampScore(value));
            updateDirty();
        }

        void setMidtermScore(Double value) {
            midtermScore.set(value == null ? null : clampScore(value));
            updateDirty();
        }

        void setFinalScore(Double value) {
            finalScore.set(value == null ? null : clampScore(value));
            updateDirty();
        }

        Double getRegularScore() {
            return regularScore.get();
        }

        Double getMidtermScore() {
            return midtermScore.get();
        }

        Double getFinalScore() {
            return finalScore.get();
        }

        String getStudentId() {
            return studentId.get();
        }

        String getStudentName() {
            return studentName.get();
        }

        boolean matches(String keyword) {
            if (keyword == null || keyword.isBlank()) {
                return true;
            }
            return normalizedId.contains(keyword) || normalizedName.contains(keyword);
        }

        boolean isDirty() {
            return dirty.get();
        }

        void markPersisted() {
            originalRegular = regularScore.get();
            originalMidterm = midtermScore.get();
            originalFinal = finalScore.get();
            dirty.set(false);
        }

        void updateDirty() {
            dirty.set(!Objects.equals(regularScore.get(), originalRegular)
                || !Objects.equals(midtermScore.get(), originalMidterm)
                || !Objects.equals(finalScore.get(), originalFinal));
        }
    }

    private enum ScoreFormulaPreset {
        STANDARD("Chuẩn 20-30-50", 0.2, 0.3, 0.5),
        EVEN("Trung bình cộng", 1d / 3d, 1d / 3d, 1d / 3d),
        FINAL_HEAVY("Ưu tiên cuối kỳ", 0.1, 0.2, 0.7);

        private final String label;
        private final double regularWeight;
        private final double midtermWeight;
        private final double finalWeight;

        ScoreFormulaPreset(String label, double regularWeight, double midtermWeight, double finalWeight) {
            this.label = label;
            this.regularWeight = regularWeight;
            this.midtermWeight = midtermWeight;
            this.finalWeight = finalWeight;
        }

        String getLabel() {
            return label;
        }

        double calculate(Double regular, Double midterm, Double finalExam) {
            double regularScore = regular == null ? 0.0 : regular;
            double midtermScore = midterm == null ? 0.0 : midterm;
            double finalScore = finalExam == null ? 0.0 : finalExam;
            double result = regularScore * regularWeight + midtermScore * midtermWeight + finalScore * finalWeight;
            return clampScore(result);
        }
    }
}
