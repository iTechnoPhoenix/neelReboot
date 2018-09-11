package com.iTechnoPhoenix.neelReboot;

import com.iTechnoPhoenix.database.CustomerOperation;
import com.iTechnoPhoenix.database.MeterOperation;
import com.iTechnoPhoenix.model.Customer;
import com.iTechnoPhoenix.model.Meter;
import com.iTechnoPhoenix.neelSupport.PhoenixSupport;
import com.iTechnoPhoenix.neelSupport.Support;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomerController implements Initializable {

    @FXML
    private JFXTextField txt_name;

    @FXML
    private JFXTextField txt_mobile;

    @FXML
    private JFXTextField txt_email;

    @FXML
    private JFXTextArea txt_address;

    @FXML
    private JFXTextField txt_meter;

    @FXML
    private JFXDatePicker txt_date;

    @FXML
    private JFXTextField txt_current_reading;

    @FXML
    private JFXTextField txt_balance;

    @FXML
    private JFXTextField txt_deposit;

    @FXML
    private JFXTreeTableView<Meter> txt_metertable;

    private JFXTreeTableColumn<Meter, String> tc_meter, tc_connnection;
    private JFXTreeTableColumn<Meter, Long> tc_currentReading;
    private JFXTreeTableColumn<Meter, Double> tc_outstanding, tc_deposit;
    private JFXTreeTableColumn<Meter, Integer> tc_action;

    private ObservableList<Meter> meterlist;
    @FXML
    private StackPane window;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        meterlist = FXCollections.observableArrayList();
        txt_address.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.TAB) {
                txt_meter.requestFocus();
            }

        });
        createMeterTabel();

    }

    public void createMeterTabel() {
        tc_meter = new JFXTreeTableColumn<>("मीटर क्रमांक");
        tc_connnection = new JFXTreeTableColumn<>("दिनांक");
        tc_currentReading = new JFXTreeTableColumn<>("चालू रीडीग");
        tc_outstanding = new JFXTreeTableColumn<>("थकबाकी");
        tc_deposit = new JFXTreeTableColumn<>("जमा");
        tc_action = new JFXTreeTableColumn();
        tc_meter.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getMetor_num()));
        tc_connnection.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getCon_date()));
        tc_currentReading.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getValue().getCurr_reading()).asObject());
        tc_outstanding.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getOutstanding()).asObject());
        tc_deposit.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getDeposit()).asObject());
        tc_action.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue().getId()).asObject());
        tc_action.setCellFactory(param -> new ActionCell(txt_metertable));
        final TreeItem<Meter> root = new RecursiveTreeItem<>(meterlist, RecursiveTreeObject::getChildren);
        txt_metertable.getColumns().addAll(tc_action, tc_meter, tc_connnection, tc_currentReading, tc_outstanding, tc_deposit);
        txt_metertable.setRoot(root);
        txt_metertable.setShowRoot(false);
    }

    @FXML
    private void btn_key_meter_add(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Add_meter();

        }

    }

    public class ActionCell extends JFXTreeTableCell<Meter, Integer> {

        final JFXButton edit = new JFXButton("Edit");
        final JFXButton delete = new JFXButton("delete");
        final HBox actiongroup = new HBox();
        final StackPane paddedButton = new StackPane();

        public ActionCell(final JFXTreeTableView<Meter> table) {
            actiongroup.getChildren().addAll(edit, delete);
            edit.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Edit Row_48px.png"));
            edit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            delete.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Trash Can_48px.png"));
            delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            edit.setOnMouseClicked((e) -> {
                EditDialog();
            });
            delete.setOnMouseClicked((e) -> {
                table.getSelectionModel().select(getTreeTableRow().getIndex());
                Meter m = table.getSelectionModel().getSelectedItem().getValue();
                meterlist.remove(m);
            });

        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(actiongroup);
            } else {
                setGraphic(null);
            }
        }
    }

    public void EditDialog() {
        StackPane dialogWindow = new StackPane();
        dialogWindow.setPrefSize(640, 320);

        VBox outerbox = new VBox();
        outerbox.setAlignment(Pos.CENTER_LEFT);
        Label header = new Label("मीटर माहित");
        header.setStyle("-fx-background-color: #1F9CFF;-fx-text-fill:white;");
        VBox.setVgrow(header, Priority.ALWAYS);
        header.setMaxWidth(1.7976931348623157E308);
        VBox.setMargin(header, new Insets(8));
        header.setPadding(new Insets(8, 0, 8, 8));

        JFXTextField p_meter = new JFXTextField();
        p_meter.setPromptText("मीटर क्रमांक");
        VBox.setMargin(p_meter, new Insets(8));
        p_meter.setPadding(new Insets(8, 16, 8, 16));

        JFXDatePicker p_connection = new JFXDatePicker();
        p_connection.setPromptText("दिनांक");
        VBox.setVgrow(p_connection, Priority.ALWAYS);
        VBox.setMargin(p_connection, new Insets(8));
        p_connection.setPadding(new Insets(8, 16, 8, 16));

        JFXTextField p_currentreading = new JFXTextField();
        p_currentreading.setPromptText("चालू रीडीग");
        VBox.setMargin(p_currentreading, new Insets(8));
        p_currentreading.setPadding(new Insets(8, 16, 8, 16));

        JFXTextField p_outstanding = new JFXTextField();
        p_outstanding.setPromptText("थकबाकी");
        VBox.setMargin(p_outstanding, new Insets(8));
        p_outstanding.setPadding(new Insets(8, 16, 8, 16));

        JFXTextField p_deposit = new JFXTextField();
        p_deposit.setPromptText("जमा");
        VBox.setMargin(p_deposit, new Insets(8));
        p_deposit.setPadding(new Insets(8, 16, 8, 16));

        JFXButton btn_banksave = new JFXButton("जतन करा");
        btn_banksave.setStyle("-fx-background-color: #4BB89B; -fx-text-fill:white;");
        VBox.setMargin(btn_banksave, new Insets(8, 16, 8, 16));
        btn_banksave.setPadding(new Insets(8));
        JFXButton btn_bankclose = new JFXButton("रद्द करा");
        btn_bankclose.setStyle("-fx-background-color:#3E4A4F; -fx-text-fill:white;");
        VBox.setMargin(btn_bankclose, new Insets(8, 16, 8, 16));
        btn_bankclose.setPadding(new Insets(8));

        HBox buttonrow = new HBox();
        buttonrow.setSpacing(8);
        VBox.setMargin(buttonrow, new Insets(8, 16, 8, 16));
        buttonrow.setAlignment(Pos.CENTER_RIGHT);
        buttonrow.getChildren().addAll(btn_banksave, btn_bankclose);
        outerbox.getChildren().addAll(header, p_meter, p_connection, p_currentreading, p_outstanding, p_deposit, buttonrow);
        dialogWindow.getChildren().add(outerbox);

        Support.getDialog(window, dialogWindow, JFXDialog.DialogTransition.CENTER).show();

    }

    @FXML
    void btn_add_meter(ActionEvent event) {
        Add_meter();
    }

    private void Add_meter() {
        if (PhoenixSupport.isValidate(txt_meter, txt_current_reading)) {
            Meter m = new Meter();
            m.setMetor_num(txt_meter.getText());
            m.setCon_date(txt_date.getValue().toString());
            m.setOutstanding(PhoenixSupport.getDouble(txt_balance.getText()));
            m.setCurr_reading(PhoenixSupport.getLong(txt_current_reading.getText()));
            m.setDeposit(PhoenixSupport.getDouble(txt_deposit.getText()));
            meterlist.add(m);
            clearMeter();
        } else {
            PhoenixSupport.Error("मीटर क्रमांक आणि चालू रीडीग भरणे अनिवार्य आहे.", window);
        }
    }

    private void clearMeter() {
        txt_meter.clear();
        txt_balance.clear();
        txt_current_reading.clear();
        txt_date.getEditor().clear();
        txt_deposit.clear();
    }

    private void clearAll() {
        txt_name.clear();
        txt_email.clear();
        txt_mobile.clear();
        txt_address.clear();
        meterlist.clear();
        txt_metertable.refresh();
        clearMeter();
        meterlist.clear();
    }

    @FXML
    void btn_cancel(ActionEvent event) {
        clearAll();
    }

    @FXML
    void btn_save(ActionEvent event) {
        if (PhoenixSupport.isValidate(txt_name)) {
            if (!meterlist.isEmpty()) {
                Customer customer = new Customer();
                customer.setName(txt_name.getText());
                customer.setEmail(txt_email.getText());
                customer.setPhone(txt_mobile.getText());
                customer.setAddress(txt_address.getText());
                CustomerOperation co = new CustomerOperation();
                int cust_id = co.addCustomer(customer, window);
                customer.setCust_num(cust_id);
                MeterOperation mo = new MeterOperation();
                meterlist.forEach((e) -> {
                    e.setCustomeObject(customer);
                    mo.addMeter(e, window);
                });
                PhoenixSupport.Info("ग्राहकाची माहित जतन झाली आहे.", "ग्राहक माहित", window);

            } else {
                PhoenixSupport.Error("मीटरची माहीत भरणे अनिवार्य आहे.", window);
            }
        } else if (!meterlist.isEmpty()) {
            PhoenixSupport.Error("ग्राहकाचे नाव भरणे अनिवार्य आहे.", window);
        } else {
            PhoenixSupport.Error("ग्राहकाचे नाव आणि मीटरची माहीत भरणे अनिवार्य आहे.", window);
        }

    }

}
