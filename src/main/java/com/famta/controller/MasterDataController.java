package com.famta.controller;

import com.famta.model.*;
import com.famta.service.JdbcCatalogService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MasterDataController {

    // --- NAM HOC ---
    @FXML private TableView<NamHoc> tableNamHoc;
    @FXML private TableColumn<NamHoc, String> colMaNamHoc;
    @FXML private TableColumn<NamHoc, String> colTenNamHoc;
    @FXML private TableColumn<NamHoc, LocalDate> colNgayBatDau;
    @FXML private TableColumn<NamHoc, LocalDate> colNgayKetThuc;
    @FXML private TextField maNamHocField;
    @FXML private TextField tenNamHocField;
    @FXML private DatePicker ngayBatDauPicker;
    @FXML private DatePicker ngayKetThucPicker;

    // --- HOC KY ---
    @FXML private TableView<HocKy> tableHocKy;
    @FXML private TableColumn<HocKy, String> colMaHocKy;
    @FXML private TableColumn<HocKy, Integer> colThuTuKy;
    @FXML private TableColumn<HocKy, LocalDate> colNgayBatDauHK;
    @FXML private TableColumn<HocKy, LocalDate> colNgayKetThucHK;
    @FXML private TextField maHocKyField;
    @FXML private TextField thuTuKyField;
    @FXML private DatePicker ngayBatDauHKPicker;
    @FXML private DatePicker ngayKetThucHKPicker;

    // --- KHOA ---
    @FXML private TableView<Khoa> tableKhoa;
    @FXML private TableColumn<Khoa, String> colMaKhoa;
    @FXML private TableColumn<Khoa, String> colTenKhoa;
    @FXML private TextField maKhoaField;
    @FXML private TextField tenKhoaField;

    // --- MON HOC ---
    @FXML private TableView<MonHoc> tableMonHoc;
    @FXML private TableColumn<MonHoc, String> colMaMonHoc;
    @FXML private TableColumn<MonHoc, String> colTenMonHoc;
    @FXML private TableColumn<MonHoc, String> colKhoaMonHoc;
    @FXML private TextField maMonHocField;
    @FXML private TextField tenMonHocField;
    @FXML private ComboBox<Khoa> khoaBox;

    // --- PHONG HOC ---
    @FXML private TableView<PhongHoc> tablePhongHoc;
    @FXML private TableColumn<PhongHoc, String> colMaPhongHoc;
    @FXML private TableColumn<PhongHoc, String> colTenPhongHoc;
    @FXML private TableColumn<PhongHoc, String> colLoaiPhongHoc;
    @FXML private TextField maPhongHocField;
    @FXML private TextField tenPhongHocField;
    @FXML private ComboBox<LoaiPhongHoc> loaiPhongBox;

    private final JdbcCatalogService catalogService = new JdbcCatalogService();

    @FXML
    public void initialize() {
        setupNamHoc();
        setupHocKy();
        setupKhoa();
        setupMonHoc();
        setupPhongHoc();
    }

    // ================= NAM HOC =================
    private void setupNamHoc() {
        colMaNamHoc.setCellValueFactory(new PropertyValueFactory<>("maNamHoc"));
        colTenNamHoc.setCellValueFactory(new PropertyValueFactory<>("tenNamHoc"));
        colNgayBatDau.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayKetThuc.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));

        tableNamHoc.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                maNamHocField.setText(newVal.getMaNamHoc());
                maNamHocField.setDisable(true);
                tenNamHocField.setText(newVal.getTenNamHoc());
                ngayBatDauPicker.setValue(newVal.getNgayBatDau());
                ngayKetThucPicker.setValue(newVal.getNgayKetThuc());
                loadHocKy(newVal);
            } else {
                tableHocKy.setItems(FXCollections.observableArrayList());
            }
        });
        loadNamHoc();
    }

    @FXML
    public void loadNamHoc() {
        try {
            tableNamHoc.setItems(FXCollections.observableArrayList(catalogService.getAllNamHoc()));
        } catch (SQLException e) {
            showError("Lỗi tải năm học", e.getMessage());
        }
    }

    @FXML
    public void handleClearNamHoc() {
        maNamHocField.clear();
        maNamHocField.setDisable(false);
        tenNamHocField.clear();
        ngayBatDauPicker.setValue(null);
        ngayKetThucPicker.setValue(null);
        tableNamHoc.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSaveNamHoc() {
        try {
            NamHoc nh = new NamHoc(
                maNamHocField.getText(),
                tenNamHocField.getText(),
                ngayBatDauPicker.getValue(),
                ngayKetThucPicker.getValue()
            );
            if (maNamHocField.isDisabled()) {
                catalogService.updateNamHoc(nh);
            } else {
                catalogService.createNamHoc(nh);
            }
            loadNamHoc();
            handleClearNamHoc();
        } catch (Exception e) {
            showError("Lỗi lưu năm học", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteNamHoc() {
        NamHoc selected = tableNamHoc.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete("năm học " + selected.getTenNamHoc())) {
            try {
                catalogService.deleteNamHoc(selected.getMaNamHoc());
                loadNamHoc();
                handleClearNamHoc();
            } catch (SQLException e) {
                showError("Lỗi xóa năm học", e.getMessage());
            }
        }
    }

    // ================= HOC KY =================
    private void setupHocKy() {
        colMaHocKy.setCellValueFactory(new PropertyValueFactory<>("maHocKy"));
        colThuTuKy.setCellValueFactory(new PropertyValueFactory<>("thuTuKy"));
        colNgayBatDauHK.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayKetThucHK.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));

        tableHocKy.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                maHocKyField.setText(newVal.getMaHocKy());
                maHocKyField.setDisable(true);
                thuTuKyField.setText(String.valueOf(newVal.getThuTuKy()));
                ngayBatDauHKPicker.setValue(newVal.getNgayBatDau());
                ngayKetThucHKPicker.setValue(newVal.getNgayKetThuc());
            }
        });
    }

    private void loadHocKy(NamHoc namHoc) {
        if (namHoc == null) return;
        try {
            tableHocKy.setItems(FXCollections.observableArrayList(catalogService.getHocKyByNamHoc(namHoc.getMaNamHoc())));
        } catch (SQLException e) {
            showError("Lỗi tải học kỳ", e.getMessage());
        }
    }

    @FXML
    public void handleClearHocKy() {
        maHocKyField.clear();
        maHocKyField.setDisable(false);
        thuTuKyField.clear();
        ngayBatDauHKPicker.setValue(null);
        ngayKetThucHKPicker.setValue(null);
        tableHocKy.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSaveHocKy() {
        NamHoc currentNamHoc = tableNamHoc.getSelectionModel().getSelectedItem();
        if (currentNamHoc == null) {
            showError("Lỗi", "Vui lòng chọn năm học trước");
            return;
        }
        try {
            HocKy hk = new HocKy(
                maHocKyField.getText(),
                Integer.parseInt(thuTuKyField.getText()),
                ngayBatDauHKPicker.getValue(),
                ngayKetThucHKPicker.getValue(),
                currentNamHoc
            );
            if (maHocKyField.isDisabled()) {
                catalogService.updateHocKy(hk);
            } else {
                catalogService.createHocKy(hk);
            }
            loadHocKy(currentNamHoc);
            handleClearHocKy();
        } catch (NumberFormatException e) {
            showError("Lỗi nhập liệu", "Thứ tự kỳ phải là số nguyên");
        } catch (Exception e) {
            showError("Lỗi lưu học kỳ", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteHocKy() {
        HocKy selected = tableHocKy.getSelectionModel().getSelectedItem();
        NamHoc currentNamHoc = tableNamHoc.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete("học kỳ " + selected.getThuTuKy())) {
            try {
                catalogService.deleteHocKy(selected.getMaHocKy());
                loadHocKy(currentNamHoc);
                handleClearHocKy();
            } catch (SQLException e) {
                showError("Lỗi xóa học kỳ", e.getMessage());
            }
        }
    }

    // ================= KHOA =================
    private void setupKhoa() {
        colMaKhoa.setCellValueFactory(new PropertyValueFactory<>("maKhoa"));
        colTenKhoa.setCellValueFactory(new PropertyValueFactory<>("tenKhoa"));

        tableKhoa.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                maKhoaField.setText(newVal.getMaKhoa());
                maKhoaField.setDisable(true);
                tenKhoaField.setText(newVal.getTenKhoa());
            }
        });
        loadKhoa();
    }

    @FXML
    public void loadKhoa() {
        try {
            List<Khoa> list = catalogService.getAllKhoa();
            tableKhoa.setItems(FXCollections.observableArrayList(list));
            khoaBox.setItems(FXCollections.observableArrayList(list)); // Update ComboBox for MonHoc
        } catch (SQLException e) {
            showError("Lỗi tải khoa", e.getMessage());
        }
    }

    @FXML
    public void handleClearKhoa() {
        maKhoaField.clear();
        maKhoaField.setDisable(false);
        tenKhoaField.clear();
        tableKhoa.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSaveKhoa() {
        try {
            Khoa k = new Khoa(maKhoaField.getText(), tenKhoaField.getText());
            if (maKhoaField.isDisabled()) {
                catalogService.updateKhoa(k);
            } else {
                catalogService.createKhoa(k);
            }
            loadKhoa();
            handleClearKhoa();
        } catch (Exception e) {
            showError("Lỗi lưu khoa", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteKhoa() {
        Khoa selected = tableKhoa.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete("khoa " + selected.getTenKhoa())) {
            try {
                catalogService.deleteKhoa(selected.getMaKhoa());
                loadKhoa();
                handleClearKhoa();
            } catch (SQLException e) {
                showError("Lỗi xóa khoa", e.getMessage());
            }
        }
    }

    // ================= MON HOC =================
    private void setupMonHoc() {
        colMaMonHoc.setCellValueFactory(new PropertyValueFactory<>("maMonHoc"));
        colTenMonHoc.setCellValueFactory(new PropertyValueFactory<>("tenMonHoc"));
        colKhoaMonHoc.setCellValueFactory(cell -> {
            if (cell.getValue().getKhoa() != null) {
                return new SimpleStringProperty(cell.getValue().getKhoa().getTenKhoa());
            }
            return new SimpleStringProperty("");
        });

        khoaBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Khoa object) {
                return object == null ? "" : object.getTenKhoa();
            }
            @Override
            public Khoa fromString(String string) {
                return null;
            }
        });

        tableMonHoc.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                maMonHocField.setText(newVal.getMaMonHoc());
                maMonHocField.setDisable(true);
                tenMonHocField.setText(newVal.getTenMonHoc());
                // Select correct Khoa in ComboBox
                if (newVal.getKhoa() != null) {
                    for (Khoa k : khoaBox.getItems()) {
                        if (k.getMaKhoa().equals(newVal.getKhoa().getMaKhoa())) {
                            khoaBox.getSelectionModel().select(k);
                            break;
                        }
                    }
                } else {
                    khoaBox.getSelectionModel().clearSelection();
                }
            }
        });
        loadMonHoc();
    }

    @FXML
    public void loadMonHoc() {
        try {
            tableMonHoc.setItems(FXCollections.observableArrayList(catalogService.getAllMonHoc()));
        } catch (SQLException e) {
            showError("Lỗi tải môn học", e.getMessage());
        }
    }

    @FXML
    public void handleClearMonHoc() {
        maMonHocField.clear();
        maMonHocField.setDisable(false);
        tenMonHocField.clear();
        khoaBox.getSelectionModel().clearSelection();
        tableMonHoc.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSaveMonHoc() {
        try {
            MonHoc mh = new MonHoc(
                maMonHocField.getText(),
                tenMonHocField.getText(),
                khoaBox.getValue()
            );
            if (maMonHocField.isDisabled()) {
                catalogService.updateMonHoc(mh);
            } else {
                catalogService.createMonHoc(mh);
            }
            loadMonHoc();
            handleClearMonHoc();
        } catch (Exception e) {
            showError("Lỗi lưu môn học", e.getMessage());
        }
    }

    @FXML
    public void handleDeleteMonHoc() {
        MonHoc selected = tableMonHoc.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete("môn học " + selected.getTenMonHoc())) {
            try {
                catalogService.deleteMonHoc(selected.getMaMonHoc());
                loadMonHoc();
                handleClearMonHoc();
            } catch (SQLException e) {
                showError("Lỗi xóa môn học", e.getMessage());
            }
        }
    }

    // ================= PHONG HOC =================
    private void setupPhongHoc() {
        colMaPhongHoc.setCellValueFactory(new PropertyValueFactory<>("maPhongHoc"));
        colTenPhongHoc.setCellValueFactory(new PropertyValueFactory<>("tenPhongHoc"));
        colLoaiPhongHoc.setCellValueFactory(new PropertyValueFactory<>("tenLoaiPhongHoc"));

        loaiPhongBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(LoaiPhongHoc object) {
                return object == null ? "" : object.getTenLoaiPhongHoc();
            }
            @Override
            public LoaiPhongHoc fromString(String string) {
                return null;
            }
        });

        tablePhongHoc.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                maPhongHocField.setText(newVal.getMaPhongHoc());
                maPhongHocField.setDisable(true);
                tenPhongHocField.setText(newVal.getTenPhongHoc());
                // Select correct LoaiPhongHoc
                if (newVal.getMaLoaiPhongHoc() != null) {
                    for (LoaiPhongHoc l : loaiPhongBox.getItems()) {
                        if (l.getMaLoaiPhongHoc().equals(newVal.getMaLoaiPhongHoc())) {
                            loaiPhongBox.getSelectionModel().select(l);
                            break;
                        }
                    }
                } else {
                    loaiPhongBox.getSelectionModel().clearSelection();
                }
            }
        });
        loadPhongHoc();
    }

    @FXML
    public void loadPhongHoc() {
        try {
            tablePhongHoc.setItems(FXCollections.observableArrayList(catalogService.getAllPhongHoc()));
            loaiPhongBox.setItems(FXCollections.observableArrayList(catalogService.getAllLoaiPhongHoc()));
        } catch (SQLException e) {
            showError("Lỗi tải phòng học", e.getMessage());
        }
    }

    @FXML
    public void handleClearPhongHoc() {
        maPhongHocField.clear();
        maPhongHocField.setDisable(false);
        tenPhongHocField.clear();
        loaiPhongBox.getSelectionModel().clearSelection();
        tablePhongHoc.getSelectionModel().clearSelection();
    }

    @FXML
    public void handleSavePhongHoc() {
        try {
            LoaiPhongHoc lph = loaiPhongBox.getValue();
            PhongHoc ph = new PhongHoc(
                maPhongHocField.getText(),
                tenPhongHocField.getText(),
                lph != null ? lph.getMaLoaiPhongHoc() : null,
                lph != null ? lph.getTenLoaiPhongHoc() : null
            );
            if (maPhongHocField.isDisabled()) {
                catalogService.updatePhongHoc(ph);
            } else {
                catalogService.createPhongHoc(ph);
            }
            loadPhongHoc();
            handleClearPhongHoc();
        } catch (Exception e) {
            showError("Lỗi lưu phòng học", e.getMessage());
        }
    }

    @FXML
    public void handleDeletePhongHoc() {
        PhongHoc selected = tablePhongHoc.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete("phòng học " + selected.getTenPhongHoc())) {
            try {
                catalogService.deletePhongHoc(selected.getMaPhongHoc());
                loadPhongHoc();
                handleClearPhongHoc();
            } catch (SQLException e) {
                showError("Lỗi xóa phòng học", e.getMessage());
            }
        }
    }

    // ================= UTILS =================
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean confirmDelete(String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " + name + "?");
        alert.setContentText("Hành động này không thể hoàn tác.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
