package com.famta.controller;

import com.famta.model.GiaoVien;
import com.famta.service.JdbcTeacherService;
import com.famta.service.TeacherService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.stream.Collectors;

public class TeacherManagementController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<GiaoVien> teacherTable;

    @FXML
    private TableColumn<GiaoVien, String> colId;

    @FXML
    private TableColumn<GiaoVien, String> colFullName;

    @FXML
    private TableColumn<GiaoVien, String> colEmail;

    @FXML
    private TableColumn<GiaoVien, String> colPhone;

    private final TeacherService teacherService = new JdbcTeacherService();
    private final ObservableList<GiaoVien> teachers = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
    colId.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getMaGiaoVien())));
    colFullName.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getHoTenDayDu())));
    colEmail.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getDiaChiEmail())));
    colPhone.setCellValueFactory(cell -> new javafx.beans.property.ReadOnlyStringWrapper(safe(cell.getValue().getSdt())));

        teacherTable.setItems(teachers);

        searchField.textProperty().addListener((obs, old, neu) -> filter(neu));

        // load initially on FX thread
        Platform.runLater(this::loadTeachers);
    }

    private void filter(String text) {
        if (text == null || text.isBlank()) {
            teacherTable.setItems(teachers);
            return;
        }
        String q = text.trim().toLowerCase();
        List<GiaoVien> filtered = teachers.stream()
                .filter(g -> (g.getMaGiaoVien() != null && g.getMaGiaoVien().toLowerCase().contains(q))
                        || (g.getHoTenDayDu() != null && g.getHoTenDayDu().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        teacherTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleRefresh() {
        loadTeachers();
    }

    private void loadTeachers() {
        List<GiaoVien> list = teacherService.findAll();
        System.out.println("TeacherManagementController loaded " + list.size() + " teachers from database.");
        teachers.setAll(list);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
