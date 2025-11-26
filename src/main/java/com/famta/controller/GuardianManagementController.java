package com.famta.controller;

import com.famta.model.NguoiGiamHo;
import com.famta.service.JdbcGuardianService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GuardianManagementController {

    @FXML private TextField searchField;
    @FXML private TableView<NguoiGiamHo> table;
    @FXML private TableColumn<NguoiGiamHo, String> colMa;
    @FXML private TableColumn<NguoiGiamHo, String> colHoTen;
    @FXML private TableColumn<NguoiGiamHo, String> colEmail;
    @FXML private TableColumn<NguoiGiamHo, String> colSdt;
    @FXML private TableColumn<NguoiGiamHo, String> colHocSinh;
    @FXML private TableColumn<NguoiGiamHo, String> colQuanHe;

    @FXML private Label resultsSummary;
    @FXML private Label formTitle;
    @FXML private Label formStatus;
    @FXML private TextField maNghField;
    @FXML private TextField hoField;
    @FXML private TextField tenLotField;
    @FXML private TextField tenField;
    @FXML private TextField emailField;
    @FXML private TextField sdtField;

    private final JdbcGuardianService service = new JdbcGuardianService();

    @FXML
    public void initialize() {
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNguoiGiamHo"));
        colHoTen.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getHoTenDayDu()));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("diaChiEmail"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        
        colHocSinh.setCellValueFactory(cell -> {
            var list = cell.getValue().getQuanHeVoiHocSinh();
            if (list == null || list.isEmpty()) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(
                list.stream().map(q -> q.getHocSinh().getHoTenDayDu()).collect(java.util.stream.Collectors.joining(", "))
            );
        });
        
        colQuanHe.setCellValueFactory(cell -> {
            var list = cell.getValue().getQuanHeVoiHocSinh();
            if (list == null || list.isEmpty()) return new javafx.beans.property.SimpleStringProperty("");
            return new javafx.beans.property.SimpleStringProperty(
                list.stream().map(q -> q.getLoaiNguoiGiamHo().name()).collect(java.util.stream.Collectors.joining(", "))
            );
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                populateForm(newVal);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearch();
        });

        clearForm();
        loadData();
    }

    private void clearForm() {
        maNghField.clear();
        hoField.clear();
        tenLotField.clear();
        tenField.clear();
        emailField.clear();
        sdtField.clear();
        formTitle.setText("Thêm người giám hộ mới");
        formStatus.setText("");
        table.getSelectionModel().clearSelection();
    }

    private void populateForm(NguoiGiamHo ngh) {
        maNghField.setText(ngh.getMaNguoiGiamHo());
        hoField.setText(ngh.getHo());
        tenLotField.setText(ngh.getTenLot());
        tenField.setText(ngh.getTen());
        emailField.setText(ngh.getDiaChiEmail());
        sdtField.setText(ngh.getSdt());
        formTitle.setText("Cập nhật thông tin");
        formStatus.setText("");
    }

    @FXML
    public void handleSendNotification() {
        NguoiGiamHo selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Gửi thông báo");
            dialog.setHeaderText("Gửi thông báo tới " + selected.getHoTenDayDu());
            dialog.setContentText("Nội dung:");
            dialog.showAndWait().ifPresent(content -> {
                selected.guiThongBao(content);
                showInformation("Đã gửi thông báo thành công!");
            });
        } else {
            showWarning("Chưa chọn người giám hộ để gửi thông báo");
        }
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void loadData() {
        try {
            List<NguoiGiamHo> list = service.getAllGuardians();
            table.setItems(FXCollections.observableArrayList(list));
            resultsSummary.setText("Hiển thị " + list.size() + " người giám hộ");
        } catch (SQLException e) {
            showError("Lỗi tải dữ liệu", e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadData();
            return;
        }
        try {
            List<NguoiGiamHo> list = service.searchGuardians(keyword.trim());
            table.setItems(FXCollections.observableArrayList(list));
            resultsSummary.setText("Tìm thấy " + list.size() + " kết quả");
        } catch (SQLException e) {
            showError("Lỗi tìm kiếm", e.getMessage());
        }
    }

    @FXML
    public void handleAdd() {
        clearForm();
        // In a real app, generate ID here or let DB handle it
        maNghField.setText("NGH" + System.currentTimeMillis() % 100000); 
        hoField.requestFocus();
    }

    @FXML
    public void handleEdit() {
        NguoiGiamHo selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            populateForm(selected);
        } else {
            showWarning("Chưa chọn người giám hộ để sửa");
        }
    }

    @FXML
    public void handleCancelEdit() {
        clearForm();
    }

    @FXML
    public void handleSave() {
        String ma = maNghField.getText();
        String ho = hoField.getText();
        String tenLot = tenLotField.getText();
        String ten = tenField.getText();
        String email = emailField.getText();
        String sdt = sdtField.getText();

        if (ma == null || ma.isBlank()) {
            formStatus.setText("Thiếu mã người giám hộ");
            return;
        }
        if (ho == null || ho.isBlank() || ten == null || ten.isBlank()) {
            formStatus.setText("Vui lòng nhập họ và tên");
            return;
        }

        try {
            NguoiGiamHo ngh = new NguoiGiamHo(ma, ho, tenLot, ten, email, sdt);
            // Check if exists in table items to decide update or insert
            // Ideally check DB, but for now check table selection or ID match
            boolean isUpdate = false;
            for (NguoiGiamHo item : table.getItems()) {
                if (item.getMaNguoiGiamHo().equals(ma)) {
                    isUpdate = true;
                    break;
                }
            }

            if (isUpdate) {
                service.updateGuardian(ngh);
                showInformation("Đã cập nhật người giám hộ");
            } else {
                service.createGuardian(ngh);
                showInformation("Đã thêm người giám hộ mới");
            }
            loadData();
            clearForm();
        } catch (SQLException e) {
            formStatus.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        NguoiGiamHo selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Bạn có chắc muốn xóa người giám hộ " + selected.getTen() + "?")) {
                try {
                    service.deleteGuardian(selected.getMaNguoiGiamHo());
                    loadData();
                    clearForm();
                } catch (SQLException e) {
                    showError("Lỗi xóa dữ liệu", e.getMessage());
                }
            }
        } else {
            showWarning("Chưa chọn người giám hộ để xóa");
        }
    }

    private void showDialog(NguoiGiamHo ngh) {
        Dialog<NguoiGiamHo> dialog = new Dialog<>();
        dialog.setTitle(ngh == null ? "Thêm Người Giám Hộ" : "Sửa Người Giám Hộ");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMa = new TextField();
        TextField txtHo = new TextField();
        TextField txtTenLot = new TextField();
        TextField txtTen = new TextField();
        TextField txtEmail = new TextField();
        TextField txtSdt = new TextField();

        if (ngh != null) {
            txtMa.setText(ngh.getMaNguoiGiamHo());
            txtMa.setDisable(true);
            txtHo.setText(ngh.getHo());
            txtTenLot.setText(ngh.getTenLot());
            txtTen.setText(ngh.getTen());
            txtEmail.setText(ngh.getDiaChiEmail());
            txtSdt.setText(ngh.getSdt());
        }

        grid.add(new Label("Mã NGH:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Họ:"), 0, 1);
        grid.add(txtHo, 1, 1);
        grid.add(new Label("Tên lót:"), 0, 2);
        grid.add(txtTenLot, 1, 2);
        grid.add(new Label("Tên:"), 0, 3);
        grid.add(txtTen, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(txtEmail, 1, 4);
        grid.add(new Label("SĐT:"), 0, 5);
        grid.add(txtSdt, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (ngh != null) {
                    ngh.setHo(txtHo.getText());
                    ngh.setTenLot(txtTenLot.getText());
                    ngh.setTen(txtTen.getText());
                    ngh.setDiaChiEmail(txtEmail.getText());
                    ngh.setSdt(txtSdt.getText());
                    return ngh;
                } else {
                    return new NguoiGiamHo(
                        txtMa.getText(),
                        txtHo.getText(),
                        txtTenLot.getText(),
                        txtTen.getText(),
                        txtEmail.getText(),
                        txtSdt.getText()
                    );
                }
            }
            return null;
        });

        Optional<NguoiGiamHo> result = dialog.showAndWait();
        result.ifPresent(n -> {
            try {
                if (ngh == null) {
                    service.createGuardian(n);
                } else {
                    service.updateGuardian(n);
                }
                loadData();
            } catch (SQLException e) {
                showError("Lỗi lưu dữ liệu", e.getMessage());
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
