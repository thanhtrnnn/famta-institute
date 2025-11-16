package com.famta.controller;

import com.famta.model.*;
import com.famta.service.JdbcCatalogService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MasterDataController {

    @FXML private TableView<NamHoc> tableNamHoc;
    @FXML private TableColumn<NamHoc, String> colMaNamHoc;
    @FXML private TableColumn<NamHoc, String> colTenNamHoc;
    @FXML private TableColumn<NamHoc, LocalDate> colNgayBatDau;
    @FXML private TableColumn<NamHoc, LocalDate> colNgayKetThuc;

    @FXML private TableView<Khoa> tableKhoa;
    @FXML private TableColumn<Khoa, String> colMaKhoa;
    @FXML private TableColumn<Khoa, String> colTenKhoa;

    @FXML private TableView<MonHoc> tableMonHoc;
    @FXML private TableColumn<MonHoc, String> colMaMonHoc;
    @FXML private TableColumn<MonHoc, String> colTenMonHoc;
    @FXML private TableColumn<MonHoc, String> colKhoaMonHoc;

    @FXML private TableView<PhongHoc> tablePhongHoc;
    @FXML private TableColumn<PhongHoc, String> colMaPhongHoc;
    @FXML private TableColumn<PhongHoc, String> colTenPhongHoc;
    @FXML private TableColumn<PhongHoc, String> colLoaiPhongHoc;

    private final JdbcCatalogService catalogService = new JdbcCatalogService();

    @FXML
    public void initialize() {
        setupNamHocTable();
        setupKhoaTable();
        setupMonHocTable();
        setupPhongHocTable();

        loadNamHoc();
        loadKhoa();
        loadMonHoc();
        loadPhongHoc();
    }

    private void setupNamHocTable() {
        colMaNamHoc.setCellValueFactory(new PropertyValueFactory<>("maNamHoc"));
        colTenNamHoc.setCellValueFactory(new PropertyValueFactory<>("tenNamHoc"));
        colNgayBatDau.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayKetThuc.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));
    }

    private void setupKhoaTable() {
        colMaKhoa.setCellValueFactory(new PropertyValueFactory<>("maKhoa"));
        colTenKhoa.setCellValueFactory(new PropertyValueFactory<>("tenKhoa"));
    }

    private void setupMonHocTable() {
        colMaMonHoc.setCellValueFactory(new PropertyValueFactory<>("maMonHoc"));
        colTenMonHoc.setCellValueFactory(new PropertyValueFactory<>("tenMonHoc"));
        colKhoaMonHoc.setCellValueFactory(cellData -> {
            if (cellData.getValue().getKhoa() != null) {
                return new SimpleStringProperty(cellData.getValue().getKhoa().getTenKhoa());
            }
            return new SimpleStringProperty("");
        });
    }

    private void setupPhongHocTable() {
        colMaPhongHoc.setCellValueFactory(new PropertyValueFactory<>("maPhongHoc"));
        colTenPhongHoc.setCellValueFactory(new PropertyValueFactory<>("tenPhongHoc"));
        colLoaiPhongHoc.setCellValueFactory(new PropertyValueFactory<>("tenLoaiPhongHoc"));
    }

    @FXML
    public void loadNamHoc() {
        try {
            List<NamHoc> list = catalogService.getAllNamHoc();
            tableNamHoc.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Lỗi tải danh sách năm học", e.getMessage());
        }
    }

    @FXML
    public void loadKhoa() {
        try {
            List<Khoa> list = catalogService.getAllKhoa();
            tableKhoa.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Lỗi tải danh sách khoa", e.getMessage());
        }
    }

    @FXML
    public void loadMonHoc() {
        try {
            List<MonHoc> list = catalogService.getAllMonHoc();
            tableMonHoc.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Lỗi tải danh sách môn học", e.getMessage());
        }
    }

    @FXML
    public void loadPhongHoc() {
        try {
            List<PhongHoc> list = catalogService.getAllPhongHoc();
            tablePhongHoc.setItems(FXCollections.observableArrayList(list));
        } catch (SQLException e) {
            showError("Lỗi tải danh sách phòng học", e.getMessage());
        }
    }

    // --- NAM HOC ACTIONS ---
    @FXML
    public void handleAddNamHoc() {
        showNamHocDialog(null);
    }

    @FXML
    public void handleEditNamHoc() {
        NamHoc selected = tableNamHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showNamHocDialog(selected);
        } else {
            showWarning("Chưa chọn năm học để sửa");
        }
    }

    @FXML
    public void handleDeleteNamHoc() {
        NamHoc selected = tableNamHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Bạn có chắc muốn xóa năm học " + selected.getTenNamHoc() + "?")) {
                try {
                    catalogService.deleteNamHoc(selected.getMaNamHoc());
                    loadNamHoc();
                } catch (SQLException e) {
                    showError("Lỗi xóa năm học", e.getMessage());
                }
            }
        } else {
            showWarning("Chưa chọn năm học để xóa");
        }
    }

    private void showNamHocDialog(NamHoc namHoc) {
        Dialog<NamHoc> dialog = new Dialog<>();
        dialog.setTitle(namHoc == null ? "Thêm Năm Học" : "Sửa Năm Học");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMa = new TextField();
        TextField txtTen = new TextField();
        DatePicker dpStart = new DatePicker();
        DatePicker dpEnd = new DatePicker();

        if (namHoc != null) {
            txtMa.setText(namHoc.getMaNamHoc());
            txtMa.setDisable(true);
            txtTen.setText(namHoc.getTenNamHoc());
            dpStart.setValue(namHoc.getNgayBatDau());
            dpEnd.setValue(namHoc.getNgayKetThuc());
        }

        grid.add(new Label("Mã Năm Học:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên Năm Học:"), 0, 1);
        grid.add(txtTen, 1, 1);
        grid.add(new Label("Ngày Bắt Đầu:"), 0, 2);
        grid.add(dpStart, 1, 2);
        grid.add(new Label("Ngày Kết Thúc:"), 0, 3);
        grid.add(dpEnd, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (namHoc != null) {
                    namHoc.setTenNamHoc(txtTen.getText());
                    namHoc.setNgayBatDau(dpStart.getValue());
                    namHoc.setNgayKetThuc(dpEnd.getValue());
                    return namHoc;
                } else {
                    return new NamHoc(txtMa.getText(), txtTen.getText(), dpStart.getValue(), dpEnd.getValue());
                }
            }
            return null;
        });

        Optional<NamHoc> result = dialog.showAndWait();
        result.ifPresent(nh -> {
            try {
                if (namHoc == null) {
                    catalogService.createNamHoc(nh);
                } else {
                    catalogService.updateNamHoc(nh);
                }
                loadNamHoc();
            } catch (SQLException e) {
                showError("Lỗi lưu năm học", e.getMessage());
            }
        });
    }

    // --- KHOA ACTIONS ---
    @FXML
    public void handleAddKhoa() {
        showKhoaDialog(null);
    }

    @FXML
    public void handleEditKhoa() {
        Khoa selected = tableKhoa.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showKhoaDialog(selected);
        } else {
            showWarning("Chưa chọn khoa để sửa");
        }
    }

    @FXML
    public void handleDeleteKhoa() {
        Khoa selected = tableKhoa.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Bạn có chắc muốn xóa khoa " + selected.getTenKhoa() + "?")) {
                try {
                    catalogService.deleteKhoa(selected.getMaKhoa());
                    loadKhoa();
                } catch (SQLException e) {
                    showError("Lỗi xóa khoa", e.getMessage());
                }
            }
        } else {
            showWarning("Chưa chọn khoa để xóa");
        }
    }

    private void showKhoaDialog(Khoa khoa) {
        Dialog<Khoa> dialog = new Dialog<>();
        dialog.setTitle(khoa == null ? "Thêm Khoa" : "Sửa Khoa");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMa = new TextField();
        TextField txtTen = new TextField();

        if (khoa != null) {
            txtMa.setText(khoa.getMaKhoa());
            txtMa.setDisable(true);
            txtTen.setText(khoa.getTenKhoa());
        }

        grid.add(new Label("Mã Khoa:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên Khoa:"), 0, 1);
        grid.add(txtTen, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (khoa != null) {
                    khoa.setTenKhoa(txtTen.getText());
                    return khoa;
                } else {
                    return new Khoa(txtMa.getText(), txtTen.getText());
                }
            }
            return null;
        });

        Optional<Khoa> result = dialog.showAndWait();
        result.ifPresent(k -> {
            try {
                if (khoa == null) {
                    catalogService.createKhoa(k);
                } else {
                    catalogService.updateKhoa(k);
                }
                loadKhoa();
            } catch (SQLException e) {
                showError("Lỗi lưu khoa", e.getMessage());
            }
        });
    }

    // --- MON HOC ACTIONS ---
    @FXML
    public void handleAddMonHoc() {
        showMonHocDialog(null);
    }

    @FXML
    public void handleEditMonHoc() {
        MonHoc selected = tableMonHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showMonHocDialog(selected);
        } else {
            showWarning("Chưa chọn môn học để sửa");
        }
    }

    @FXML
    public void handleDeleteMonHoc() {
        MonHoc selected = tableMonHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Bạn có chắc muốn xóa môn học " + selected.getTenMonHoc() + "?")) {
                try {
                    catalogService.deleteMonHoc(selected.getMaMonHoc());
                    loadMonHoc();
                } catch (SQLException e) {
                    showError("Lỗi xóa môn học", e.getMessage());
                }
            }
        } else {
            showWarning("Chưa chọn môn học để xóa");
        }
    }

    private void showMonHocDialog(MonHoc monHoc) {
        Dialog<MonHoc> dialog = new Dialog<>();
        dialog.setTitle(monHoc == null ? "Thêm Môn Học" : "Sửa Môn Học");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMa = new TextField();
        TextField txtTen = new TextField();
        ComboBox<Khoa> cbKhoa = new ComboBox<>();

        try {
            cbKhoa.setItems(FXCollections.observableArrayList(catalogService.getAllKhoa()));
            cbKhoa.setCellFactory(param -> new ListCell<Khoa>() {
                @Override
                protected void updateItem(Khoa item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTenKhoa());
                    }
                }
            });
            cbKhoa.setButtonCell(new ListCell<Khoa>() {
                @Override
                protected void updateItem(Khoa item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTenKhoa());
                    }
                }
            });
        } catch (SQLException e) {
            showError("Lỗi tải danh sách khoa", e.getMessage());
        }

        if (monHoc != null) {
            txtMa.setText(monHoc.getMaMonHoc());
            txtMa.setDisable(true);
            txtTen.setText(monHoc.getTenMonHoc());
            if (monHoc.getKhoa() != null) {
                for (Khoa k : cbKhoa.getItems()) {
                    if (k.getMaKhoa().equals(monHoc.getKhoa().getMaKhoa())) {
                        cbKhoa.setValue(k);
                        break;
                    }
                }
            }
        }

        grid.add(new Label("Mã Môn Học:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên Môn Học:"), 0, 1);
        grid.add(txtTen, 1, 1);
        grid.add(new Label("Khoa:"), 0, 2);
        grid.add(cbKhoa, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (monHoc != null) {
                    monHoc.setTenMonHoc(txtTen.getText());
                    monHoc.setKhoa(cbKhoa.getValue());
                    return monHoc;
                } else {
                    return new MonHoc(txtMa.getText(), txtTen.getText(), cbKhoa.getValue());
                }
            }
            return null;
        });

        Optional<MonHoc> result = dialog.showAndWait();
        result.ifPresent(mh -> {
            try {
                if (monHoc == null) {
                    catalogService.createMonHoc(mh);
                } else {
                    catalogService.updateMonHoc(mh);
                }
                loadMonHoc();
            } catch (SQLException e) {
                showError("Lỗi lưu môn học", e.getMessage());
            }
        });
    }

    // --- PHONG HOC ACTIONS ---
    @FXML
    public void handleAddPhongHoc() {
        showPhongHocDialog(null);
    }

    @FXML
    public void handleEditPhongHoc() {
        PhongHoc selected = tablePhongHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showPhongHocDialog(selected);
        } else {
            showWarning("Chưa chọn phòng học để sửa");
        }
    }

    @FXML
    public void handleDeletePhongHoc() {
        PhongHoc selected = tablePhongHoc.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (showConfirmation("Bạn có chắc muốn xóa phòng học " + selected.getTenPhongHoc() + "?")) {
                try {
                    catalogService.deletePhongHoc(selected.getMaPhongHoc());
                    loadPhongHoc();
                } catch (SQLException e) {
                    showError("Lỗi xóa phòng học", e.getMessage());
                }
            }
        } else {
            showWarning("Chưa chọn phòng học để xóa");
        }
    }

    private void showPhongHocDialog(PhongHoc phongHoc) {
        Dialog<PhongHoc> dialog = new Dialog<>();
        dialog.setTitle(phongHoc == null ? "Thêm Phòng Học" : "Sửa Phòng Học");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtMa = new TextField();
        TextField txtTen = new TextField();
        ComboBox<LoaiPhongHoc> cbLoai = new ComboBox<>();

        try {
            cbLoai.setItems(FXCollections.observableArrayList(catalogService.getAllLoaiPhongHoc()));
            cbLoai.setCellFactory(param -> new ListCell<LoaiPhongHoc>() {
                @Override
                protected void updateItem(LoaiPhongHoc item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTenLoaiPhongHoc());
                    }
                }
            });
            cbLoai.setButtonCell(new ListCell<LoaiPhongHoc>() {
                @Override
                protected void updateItem(LoaiPhongHoc item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTenLoaiPhongHoc());
                    }
                }
            });
        } catch (SQLException e) {
            showError("Lỗi tải danh sách loại phòng học", e.getMessage());
        }

        if (phongHoc != null) {
            txtMa.setText(phongHoc.getMaPhongHoc());
            txtMa.setDisable(true);
            txtTen.setText(phongHoc.getTenPhongHoc());
            if (phongHoc.getMaLoaiPhongHoc() != null) {
                for (LoaiPhongHoc l : cbLoai.getItems()) {
                    if (l.getMaLoaiPhongHoc().equals(phongHoc.getMaLoaiPhongHoc())) {
                        cbLoai.setValue(l);
                        break;
                    }
                }
            }
        }

        grid.add(new Label("Mã Phòng Học:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên Phòng Học:"), 0, 1);
        grid.add(txtTen, 1, 1);
        grid.add(new Label("Loại Phòng:"), 0, 2);
        grid.add(cbLoai, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (phongHoc != null) {
                    phongHoc.setTenPhongHoc(txtTen.getText());
                    if (cbLoai.getValue() != null) {
                        phongHoc.setMaLoaiPhongHoc(cbLoai.getValue().getMaLoaiPhongHoc());
                        phongHoc.setTenLoaiPhongHoc(cbLoai.getValue().getTenLoaiPhongHoc());
                    }
                    return phongHoc;
                } else {
                    String maLoai = cbLoai.getValue() != null ? cbLoai.getValue().getMaLoaiPhongHoc() : null;
                    String tenLoai = cbLoai.getValue() != null ? cbLoai.getValue().getTenLoaiPhongHoc() : null;
                    return new PhongHoc(txtMa.getText(), txtTen.getText(), maLoai, tenLoai);
                }
            }
            return null;
        });

        Optional<PhongHoc> result = dialog.showAndWait();
        result.ifPresent(ph -> {
            try {
                if (phongHoc == null) {
                    catalogService.createPhongHoc(ph);
                } else {
                    catalogService.updatePhongHoc(ph);
                }
                loadPhongHoc();
            } catch (SQLException e) {
                showError("Lỗi lưu phòng học", e.getMessage());
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
