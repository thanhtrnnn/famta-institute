package com.famta.controller;

import com.famta.model.GiaoVien;
import com.famta.model.QuyenTruyCap;
import com.famta.service.AccountAdminService;
import com.famta.service.AccountOverviewService;
import com.famta.service.JdbcAccountAdminService;
import com.famta.service.JdbcAccountOverviewService;
import com.famta.service.JdbcTeacherService;
import com.famta.service.TeacherService;
import com.famta.service.dto.AdminClassOverview;
import com.famta.service.dto.GuardianContactView;
import com.famta.service.dto.StudentClassEnrollment;
import com.famta.session.UserSession;
import com.famta.util.AccountUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Controller that surfaces the account-specific projections for each role
 * defined in {@code QuyenTruyCap}.
 */
public class AccountManagementController {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    private final AccountOverviewService accountService = new JdbcAccountOverviewService();
    private final AccountAdminService accountAdminService = new JdbcAccountAdminService();
    private final TeacherService teacherService = new JdbcTeacherService();

    private final ObservableList<AdminClassOverview> adminRows = FXCollections.observableArrayList();
    private final ObservableList<GiaoVien> teacherRows = FXCollections.observableArrayList();
    private final ObservableList<StudentClassEnrollment> studentRows = FXCollections.observableArrayList();
    private final ObservableList<GuardianContactView> guardianRows = FXCollections.observableArrayList();
    private final ObservableList<AccountRow> accountRows = FXCollections.observableArrayList();

    private FilteredList<AdminClassOverview> adminFiltered;
    private FilteredList<GiaoVien> teacherFiltered;
    private FilteredList<StudentClassEnrollment> studentFiltered;
    private FilteredList<GuardianContactView> guardianFiltered;
    private FilteredList<AccountRow> accountFiltered;

    private AccountRow selectedAccount;

    @FXML
    private Tab accountAdminTab;

    @FXML
    private TableView<AccountRow> accountTable;

    @FXML
    private TableColumn<AccountRow, String> accountUsernameColumn;

    @FXML
    private TableColumn<AccountRow, String> accountRoleColumn;

    @FXML
    private TextField accountSearchField;

    @FXML
    private TextField accountUsernameField;

    @FXML
    private ComboBox<QuyenTruyCap> accountRoleComboBox;

    @FXML
    private PasswordField accountPasswordField;

    @FXML
    private PasswordField accountConfirmPasswordField;

    @FXML
    private Label accountFormTitle;

    @FXML
    private Label accountFormStatus;

    @FXML
    private TextField fullNameField;

    @FXML
    private void handleGenerateUsername() {
        String fullName = fullNameField.getText();
        QuyenTruyCap role = accountRoleComboBox.getValue();
        if (fullName == null || fullName.isBlank()) {
            setAccountFormStatus("Vui lòng nhập họ tên", "warning-message");
            return;
        }
        if (role == null) {
            setAccountFormStatus("Vui lòng chọn quyền", "warning-message");
            return;
        }
        
        String base = AccountUtils.generateBaseUsername(fullName, role);
        String candidate = base;
        int counter = 2;
        
        while (isUsernameTaken(candidate)) {
            candidate = base + counter;
            counter++;
        }
        
        accountUsernameField.setText(candidate);
        setAccountFormStatus("Đã tạo: " + candidate, "success-message");
    }
    
    private boolean isUsernameTaken(String username) {
        return accountRows.stream().anyMatch(r -> r.username().equalsIgnoreCase(username));
    }

    @FXML
    private Button accountDeleteButton;

    @FXML
    private Label adminCountLabel;

    @FXML
    private Label teacherCountLabel;

    @FXML
    private Label studentCountLabel;

    @FXML
    private Label guardianCountLabel;

    @FXML
    private Label lastUpdatedLabel;

    @FXML
    private Label statusMessage;

    @FXML
    private TableView<AdminClassOverview> adminTable;

    @FXML
    private TableColumn<AdminClassOverview, String> adminClassIdColumn;

    @FXML
    private TableColumn<AdminClassOverview, String> adminClassNameColumn;

    @FXML
    private TableColumn<AdminClassOverview, String> adminSubjectColumn;

    @FXML
    private TableColumn<AdminClassOverview, String> adminTeacherColumn;

    @FXML
    private TableColumn<AdminClassOverview, String> adminRoomColumn;

    @FXML
    private TextField adminSearchField;

    @FXML
    private TableView<GiaoVien> teacherTable;

    @FXML
    private TableColumn<GiaoVien, String> teacherIdColumn;

    @FXML
    private TableColumn<GiaoVien, String> teacherNameColumn;

    @FXML
    private TableColumn<GiaoVien, String> teacherEmailColumn;

    @FXML
    private TableColumn<GiaoVien, String> teacherPhoneColumn;

    @FXML
    private TextField teacherSearchField;

    @FXML
    private TableView<StudentClassEnrollment> studentTable;

    @FXML
    private TableColumn<StudentClassEnrollment, String> studentIdColumn;

    @FXML
    private TableColumn<StudentClassEnrollment, String> studentNameColumn;

    @FXML
    private TableColumn<StudentClassEnrollment, String> studentClassIdColumn;

    @FXML
    private TableColumn<StudentClassEnrollment, String> studentClassNameColumn;

    @FXML
    private TextField studentSearchField;

    @FXML
    private TableView<GuardianContactView> guardianTable;

    @FXML
    private TableColumn<GuardianContactView, String> guardianStudentIdColumn;

    @FXML
    private TableColumn<GuardianContactView, String> guardianStudentNameColumn;

    @FXML
    private TableColumn<GuardianContactView, String> guardianNameColumn;

    @FXML
    private TableColumn<GuardianContactView, String> guardianRelationColumn;

    @FXML
    private TableColumn<GuardianContactView, String> guardianEmailColumn;

    @FXML
    private TableColumn<GuardianContactView, String> guardianPhoneColumn;

    @FXML
    private TextField guardianSearchField;

    @FXML
    private void initialize() {
        configureAccountSection();
        configureAdminTable();
        configureTeacherTable();
        configureStudentTable();
        configureGuardianTable();
        refreshAllData();
    }

    @FXML
    private void handleRefreshAccounts() {
        refreshAllData();
    }

    @FXML
    private void handleNewAccount() {
        if (!ensureAdminPermissions()) {
            setAccountFormStatus("Chỉ admin mới có thể thao tác tài khoản", "error-message");
            return;
        }
        enterCreateMode();
    }

    @FXML
    private void handleSaveAccount() {
        if (!ensureAdminPermissions()) {
            setAccountFormStatus("Chỉ admin mới có thể thao tác tài khoản", "error-message");
            return;
        }
        String username = trimToEmpty(accountUsernameField.getText());
        QuyenTruyCap selectedRole = accountRoleComboBox.getValue();
        String password = accountPasswordField.getText();
        String confirm = accountConfirmPasswordField.getText();
        boolean passwordProvided = password != null && !password.isBlank();

        if (selectedAccount == null) {
            if (username.isEmpty()) {
                setAccountFormStatus("Vui lòng nhập tên đăng nhập", "error-message");
                return;
            }
            if (!passwordProvided || confirm == null || confirm.isBlank()) {
                setAccountFormStatus("Vui lòng nhập mật khẩu và xác nhận", "error-message");
                return;
            }
            if (!password.equals(confirm)) {
                setAccountFormStatus("Mật khẩu xác nhận không khớp", "error-message");
                return;
            }
            try {
                accountAdminService.createAccount(username, password, selectedRole);
                setAccountFormStatus("Đã tạo tài khoản " + username, "success-message");
                refreshAccountData();
                enterCreateMode();
            } catch (Exception ex) {
                setAccountFormStatus(ex.getMessage(), "error-message");
            }
        } else {
            boolean roleChanged = selectedRole != null && !selectedRole.equals(selectedAccount.role());
            if (!roleChanged && !passwordProvided) {
                setAccountFormStatus("Không có thay đổi để lưu", "warning-message");
                return;
            }
            if (passwordProvided && (confirm == null || !password.equals(confirm))) {
                setAccountFormStatus("Mật khẩu xác nhận không khớp", "error-message");
                return;
            }
            try {
                if (roleChanged) {
                    accountAdminService.updateRole(selectedAccount.username(), selectedRole);
                }
                if (passwordProvided) {
                    accountAdminService.resetPassword(selectedAccount.username(), password);
                }
                setAccountFormStatus("Đã cập nhật tài khoản " + selectedAccount.username(), "success-message");
                refreshAccountData();
                selectAccount(selectedAccount.username());
                accountPasswordField.clear();
                accountConfirmPasswordField.clear();
            } catch (Exception ex) {
                setAccountFormStatus(ex.getMessage(), "error-message");
            }
        }
    }

    @FXML
    private void handleDeleteAccount() {
        if (!ensureAdminPermissions()) {
            setAccountFormStatus("Chỉ admin mới có thể thao tác tài khoản", "error-message");
            return;
        }
        if (selectedAccount == null) {
            setAccountFormStatus("Chọn tài khoản để xóa", "warning-message");
            return;
        }
        boolean isSelf = UserSession.getCurrentAccount()
            .map(account -> account.getTenDangNhap().equalsIgnoreCase(selectedAccount.username()))
            .orElse(false);
        if (isSelf) {
            setAccountFormStatus("Không thể tự xóa tài khoản đang đăng nhập", "error-message");
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Xóa tài khoản");
        alert.setHeaderText("Bạn chắc chắn muốn xóa " + selectedAccount.username() + "?");
        alert.setContentText("Thao tác này không thể hoàn tác.");
        Optional<javafx.scene.control.ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == javafx.scene.control.ButtonType.OK) {
            try {
                accountAdminService.deleteAccount(selectedAccount.username());
                setAccountFormStatus("Đã xóa " + selectedAccount.username(), "success-message");
                refreshAccountData();
                enterCreateMode();
            } catch (Exception ex) {
                setAccountFormStatus(ex.getMessage(), "error-message");
            }
        }
    }

    private void configureAdminTable() {
        adminClassIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().maLopHoc())));
        adminClassNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().tenLopHoc())));
        adminSubjectColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().monHocDisplay())));
        adminTeacherColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().giaoVienDisplay())));
        adminRoomColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().phongHocDisplay())));
        adminFiltered = new FilteredList<>(adminRows, row -> true);
        SortedList<AdminClassOverview> sorted = new SortedList<>(adminFiltered);
        sorted.comparatorProperty().bind(adminTable.comparatorProperty());
        adminTable.setItems(sorted);
        adminTable.setPlaceholder(createPlaceholder("Không có lớp học nào"));
        adminSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyAdminFilter(newValue));
    }

    private void configureTeacherTable() {
        teacherIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getMaGiaoVien())));
        teacherNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getHoTenDayDu())));
        teacherEmailColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getDiaChiEmail())));
        teacherPhoneColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getSdt())));
        teacherFiltered = new FilteredList<>(teacherRows, row -> true);
        SortedList<GiaoVien> sorted = new SortedList<>(teacherFiltered);
        sorted.comparatorProperty().bind(teacherTable.comparatorProperty());
        teacherTable.setItems(sorted);
        teacherTable.setPlaceholder(createPlaceholder("Không có giáo viên nào"));
        teacherSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyTeacherFilter(newValue));
    }

    private void configureStudentTable() {
        studentIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().maHocSinh())));
        studentNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().tenHocSinh())));
        studentClassIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().maLopHoc())));
        studentClassNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().lopDisplay())));
        studentFiltered = new FilteredList<>(studentRows, row -> true);
        SortedList<StudentClassEnrollment> sorted = new SortedList<>(studentFiltered);
        sorted.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sorted);
        studentTable.setPlaceholder(createPlaceholder("Không tìm thấy học sinh nào"));
        studentSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyStudentFilter(newValue));
    }

    private void configureGuardianTable() {
        guardianStudentIdColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().maHocSinh())));
        guardianStudentNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().tenHocSinh())));
        guardianNameColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().tenNguoiGiamHo())));
        guardianRelationColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().quanHeDisplay())));
        guardianEmailColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().email())));
        guardianPhoneColumn.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().soDienThoai())));
        guardianFiltered = new FilteredList<>(guardianRows, row -> true);
        SortedList<GuardianContactView> sorted = new SortedList<>(guardianFiltered);
        sorted.comparatorProperty().bind(guardianTable.comparatorProperty());
        guardianTable.setItems(sorted);
        guardianTable.setPlaceholder(createPlaceholder("Chưa có thông tin phụ huynh"));
        guardianSearchField.textProperty().addListener((obs, oldValue, newValue) -> applyGuardianFilter(newValue));
    }

    private void configureAccountSection() {
        if (accountRoleComboBox != null) {
            accountRoleComboBox.setItems(FXCollections.observableArrayList(QuyenTruyCap.values()));
            accountRoleComboBox.getSelectionModel().select(QuyenTruyCap.HOC_VIEN);
        }
        if (accountTable == null) {
            return;
        }
        accountUsernameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().username()));
        accountRoleColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().role().toDisplayLabel()));
        accountFiltered = new FilteredList<>(accountRows, row -> true);
        SortedList<AccountRow> sorted = new SortedList<>(accountFiltered);
        sorted.comparatorProperty().bind(accountTable.comparatorProperty());
        accountTable.setItems(sorted);
        accountTable.setPlaceholder(createPlaceholder("Chưa có tài khoản đăng nhập"));
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> onAccountSelected(newRow));
        if (accountSearchField != null) {
            accountSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyAccountFilter(newVal));
        }
        if (accountDeleteButton != null) {
            accountDeleteButton.setDisable(true);
        }
        if (UserSession.isAdmin()) {
            refreshAccountData();
            enterCreateMode();
        } else if (accountAdminTab != null) {
            accountAdminTab.setDisable(true);
        }
    }

    private void refreshAllData() {
        try {
            List<AdminClassOverview> adminData = accountService.fetchAdminClassOverview();
            adminRows.setAll(adminData);
            adminCountLabel.setText(String.valueOf(adminData.size()));

            List<GiaoVien> teacherData = teacherService.findAll();
            teacherRows.setAll(teacherData);
            teacherCountLabel.setText(String.valueOf(teacherData.size()));

            List<StudentClassEnrollment> enrollmentData = accountService.fetchStudentClassEnrollments();
            studentRows.setAll(enrollmentData);
            studentCountLabel.setText(String.valueOf(countDistinct(enrollmentData.stream().map(StudentClassEnrollment::maHocSinh).toList())));

            List<GuardianContactView> guardianData = accountService.fetchGuardianContacts();
            guardianRows.setAll(guardianData);
            guardianCountLabel.setText(String.valueOf(countDistinct(guardianData.stream().map(GuardianContactView::maNguoiGiamHo).toList())));

            lastUpdatedLabel.setText(TIMESTAMP_FORMATTER.format(LocalDateTime.now()));
            setStatusMessage("Đã cập nhật dữ liệu tài khoản", "success-message");
            applyAdminFilter(adminSearchField.getText());
            applyTeacherFilter(teacherSearchField.getText());
            applyStudentFilter(studentSearchField.getText());
            applyGuardianFilter(guardianSearchField.getText());
            if (UserSession.isAdmin()) {
                refreshAccountData();
            }
        } catch (Exception ex) {
            setStatusMessage("Không thể tải dữ liệu: " + ex.getMessage(), "error-message");
        }
    }

    private void refreshAccountData() {
        if (!UserSession.isAdmin()) {
            return;
        }
        try {
            List<AccountRow> rows = accountAdminService.findAll().stream()
                .map(account -> new AccountRow(account.getTenDangNhap(), account.getQuyen()))
                .collect(Collectors.toList());
            accountRows.setAll(rows);
            applyAccountFilter(accountSearchField == null ? null : accountSearchField.getText());
        } catch (Exception ex) {
            setAccountFormStatus(ex.getMessage(), "error-message");
        }
    }

    private int countDistinct(List<String> ids) {
        Set<String> unique = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String id : ids) {
            if (id != null && !id.isBlank()) {
                unique.add(id.trim());
            }
        }
        return unique.size();
    }

    @FXML
    private void handleCopyGuardianEmails() {
        if (!ensureAdminPermissions()) {
            setStatusMessage("Chỉ admin mới có thể sao chép email phụ huynh", "warning-message");
            return;
        }
        List<String> emails = guardianRows.stream()
            .map(GuardianContactView::email)
            .filter(value -> value != null && !value.isBlank())
            .distinct()
            .toList();
        if (emails.isEmpty()) {
            setStatusMessage("Không có email phụ huynh để sao chép", "warning-message");
            return;
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(String.join("; ", emails));
        clipboard.setContent(content);
        setStatusMessage("Đã sao chép " + emails.size() + " email phụ huynh", "success-message");
    }

    private void setStatusMessage(String message, String styleClass) {
        statusMessage.setText(message);
        statusMessage.getStyleClass().removeAll("success-message", "error-message", "warning-message");
        if (styleClass != null && !styleClass.isBlank()) {
            statusMessage.getStyleClass().add(styleClass);
        }
    }

    private void setAccountFormStatus(String message, String styleClass) {
        if (accountFormStatus == null) {
            return;
        }
        boolean hasMessage = message != null && !message.isBlank();
        accountFormStatus.setText(hasMessage ? message : "");
        accountFormStatus.setVisible(hasMessage);
        accountFormStatus.setManaged(hasMessage);
        accountFormStatus.getStyleClass().removeAll("success-message", "error-message", "warning-message");
        if (styleClass != null && !styleClass.isBlank()) {
            accountFormStatus.getStyleClass().add(styleClass);
        }
    }

    private Label createPlaceholder(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("info-text");
        return label;
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void applyAdminFilter(String query) {
        String keyword = normalize(query);
        adminFiltered.setPredicate(row -> keyword.isEmpty()
            || contains(row.maLopHoc(), keyword)
            || contains(row.tenLopHoc(), keyword)
            || contains(row.monHocDisplay(), keyword)
            || contains(row.giaoVienDisplay(), keyword)
            || contains(row.phongHocDisplay(), keyword));
    }

    private void applyTeacherFilter(String query) {
        String keyword = normalize(query);
        teacherFiltered.setPredicate(row -> keyword.isEmpty()
            || contains(row.getMaGiaoVien(), keyword)
            || contains(row.getHoTenDayDu(), keyword)
            || contains(row.getDiaChiEmail(), keyword)
            || contains(row.getSdt(), keyword));
    }

    private void applyStudentFilter(String query) {
        String keyword = normalize(query);
        studentFiltered.setPredicate(row -> keyword.isEmpty()
            || contains(row.maHocSinh(), keyword)
            || contains(row.tenHocSinh(), keyword)
            || contains(row.maLopHoc(), keyword)
            || contains(row.lopDisplay(), keyword));
    }

    private void applyGuardianFilter(String query) {
        String keyword = normalize(query);
        guardianFiltered.setPredicate(row -> keyword.isEmpty()
            || contains(row.maHocSinh(), keyword)
            || contains(row.tenHocSinh(), keyword)
            || contains(row.maNguoiGiamHo(), keyword)
            || contains(row.tenNguoiGiamHo(), keyword)
            || contains(row.quanHeDisplay(), keyword)
            || contains(row.email(), keyword)
            || contains(row.soDienThoai(), keyword));
    }

    private void applyAccountFilter(String query) {
        if (accountFiltered == null) {
            return;
        }
        String keyword = normalize(query);
        accountFiltered.setPredicate(row -> keyword.isEmpty()
            || contains(row.username(), keyword)
            || contains(row.role().name(), keyword)
            || contains(row.role().toDisplayLabel(), keyword));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.getDefault());
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean contains(String source, String keyword) {
        if (source == null) {
            return false;
        }
        return source.toLowerCase(Locale.getDefault()).contains(keyword);
    }

    private boolean ensureAdminPermissions() {
        return UserSession.isAdmin();
    }

    private void enterCreateMode() {
        selectedAccount = null;
        if (accountTable != null) {
            accountTable.getSelectionModel().clearSelection();
        }
        if (accountFormTitle != null) {
            accountFormTitle.setText("Tạo tài khoản mới");
        }
        if (accountUsernameField != null) {
            accountUsernameField.setDisable(false);
            accountUsernameField.clear();
        }
        if (accountRoleComboBox != null) {
            accountRoleComboBox.getSelectionModel().select(QuyenTruyCap.HOC_VIEN);
        }
        if (accountPasswordField != null) {
            accountPasswordField.clear();
        }
        if (accountConfirmPasswordField != null) {
            accountConfirmPasswordField.clear();
        }
        if (accountDeleteButton != null) {
            accountDeleteButton.setDisable(true);
        }
        setAccountFormStatus("", null);
    }

    private void onAccountSelected(AccountRow row) {
        selectedAccount = row;
        if (row == null) {
            enterCreateMode();
            return;
        }
        if (accountFormTitle != null) {
            accountFormTitle.setText("Chỉnh sửa: " + row.username());
        }
        if (accountUsernameField != null) {
            accountUsernameField.setText(row.username());
            accountUsernameField.setDisable(true);
        }
        if (accountRoleComboBox != null) {
            accountRoleComboBox.getSelectionModel().select(row.role());
        }
        if (accountPasswordField != null) {
            accountPasswordField.clear();
        }
        if (accountConfirmPasswordField != null) {
            accountConfirmPasswordField.clear();
        }
        if (accountDeleteButton != null) {
            accountDeleteButton.setDisable(false);
        }
        setAccountFormStatus("", null);
    }

    private void selectAccount(String username) {
        if (accountTable == null || username == null) {
            return;
        }
        accountRows.stream()
            .filter(row -> row.username().equalsIgnoreCase(username))
            .findFirst()
            .ifPresent(row -> accountTable.getSelectionModel().select(row));
    }

    private record AccountRow(String username, QuyenTruyCap role) { }
}
