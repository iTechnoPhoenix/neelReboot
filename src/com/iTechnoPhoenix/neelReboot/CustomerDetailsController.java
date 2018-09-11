/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author NARENDRA JADHAV
 */
public class CustomerDetailsController implements Initializable {

    @FXML
    private TextField txt_search;

    @FXML
    private JFXTreeTableView<Meter> txt_customer;
    private JFXTreeTableColumn<Meter, String> tc_meter, tc_connnection, tc_name, tc_mobile, tc_email, tc_address;
    private JFXTreeTableColumn<Meter, Long> tc_currentReading;
    private JFXTreeTableColumn<Meter, Double> tc_outstanding, tc_deposit;
    private JFXTreeTableColumn<Meter, Integer> tc_action;
    @FXML
    private StackPane window;

    private ObservableList<Meter> meterlist, allmeter;
    private ObservableSet<String> suggestionlist;
    public String prashant = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        suggestionlist = FXCollections.observableSet();
        allmeter = FXCollections.observableArrayList();
        tc_name = new JFXTreeTableColumn<>("नाव");
        tc_email = new JFXTreeTableColumn<>("ईमेल");
        tc_mobile = new JFXTreeTableColumn<>("मोबाई नं");
        tc_address = new JFXTreeTableColumn<>("पत्ता");
        tc_meter = new JFXTreeTableColumn<>("मीटर क्रमांक");
        tc_connnection = new JFXTreeTableColumn<>("दिनांक");
        tc_currentReading = new JFXTreeTableColumn<>("चालू रीडीग");
        tc_outstanding = new JFXTreeTableColumn<>("थकबाकी");
        tc_deposit = new JFXTreeTableColumn<>("जमा");
        tc_action = new JFXTreeTableColumn();

        tc_name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getCustomeObject().getName()));
        tc_email.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getCustomeObject().getEmail()));
        tc_mobile.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getCustomeObject().getPhone()));
        tc_address.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getCustomeObject().getAddress()));
        tc_meter.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getMetor_num()));
        tc_connnection.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getCon_date()));
        tc_currentReading.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getValue().getCurr_reading()).asObject());
        tc_outstanding.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getOutstanding()).asObject());
        tc_deposit.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getDeposit()).asObject());
        tc_action.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue().getId()).asObject());
        tc_action.setCellFactory(param -> new ActionCell(txt_customer));
        txt_customer.getColumns().addAll(tc_action, tc_name, tc_address, tc_meter, tc_connnection, tc_currentReading, tc_outstanding, tc_deposit, tc_mobile, tc_email);
        txt_search.textProperty().addListener((observable, oldValue, newValue) -> {
            txt_customer.setPredicate(t -> t.getValue().getCustomeObject().getName().startsWith(newValue) || t.getValue().getMetor_num().startsWith(newValue));
        });
        getData();

    }

    public void getData() {
        CustomerOperation co = new CustomerOperation();
        meterlist = co.getCustomerByName();
        allmeter.addAll(meterlist);
        if (!meterlist.isEmpty()) {
            meterlist.forEach((e) -> {
                suggestionlist.add(e.getCustomeObject().getName());
                suggestionlist.add(e.getMetor_num());
            });
        }
        TextFields.bindAutoCompletion(txt_search, suggestionlist);
        final TreeItem<Meter> root = new RecursiveTreeItem<>(meterlist, RecursiveTreeObject::getChildren);
        txt_customer.setRoot(root);
        txt_customer.setShowRoot(false);
    }

    @FXML
    private void btn_new_customer(ActionEvent event) {
        try {
            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_RIGHT);
            JFXButton b = new JFXButton("X");
            header.getChildren().add(b);
            StackPane root = FXMLLoader.load(getClass().getResource("/com/iTechnoPhoenix/neelReboot/Customer.fxml"));
            JFXDialog d = Support.getDialog(window, header, root);
            d.show();
            d.setOnDialogClosed(e -> getData());
            b.setOnMouseClicked((e) -> {
                d.close();
                getData();
            });

        } catch (IOException ex) {
            Logger.getLogger(CustomerDetailsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public class ActionCell extends JFXTreeTableCell<Meter, Integer> {

        final JFXButton edit = new JFXButton("Edit");
        final JFXButton delete = new JFXButton("Delete");
        final HBox actiongroup = new HBox();
        final StackPane paddedButton = new StackPane();

        public ActionCell(final JFXTreeTableView<Meter> table) {
            actiongroup.setAlignment(Pos.CENTER);
            actiongroup.getChildren().addAll(edit, delete);
            edit.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Edit Row_48px.png"));
            edit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            delete.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Trash Can_48px.png"));
            delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            edit.setOnMouseClicked(event -> {
                JFXDialog dialog;
                table.getSelectionModel().select(getTreeTableRow().getIndex());
                Meter meter = table.getSelectionModel().getSelectedItem().getValue();
                VBox vb = new VBox();
                JFXTextField txtName = new JFXTextField();
                txtName.setPromptText("नाव");
                txtName.setLabelFloat(true);
                txtName.setMaxWidth(200);
                txtName.setText(meter.getCustomeObject().getName());
                VBox.setMargin(txtName, new Insets(8));
                JFXTextField txtMobile = new JFXTextField();
                txtMobile.setPromptText("मोबिले नं");
                txtMobile.setLabelFloat(true);
                txtMobile.setMaxWidth(200);
                txtMobile.setText(meter.getCustomeObject().getPhone());
                VBox.setMargin(txtMobile, new Insets(8));
                JFXTextField txtEmail = new JFXTextField();
                txtEmail.setPromptText("एमैल");
                txtEmail.setLabelFloat(true);
                txtEmail.setMaxWidth(200);
                txtEmail.setText(meter.getCustomeObject().getEmail());
                VBox.setMargin(txtEmail, new Insets(8));
                JFXTextArea txtAddress = new JFXTextArea();
                txtAddress.setPromptText("पत्ता");
                txtAddress.setLabelFloat(true);
                txtAddress.setPrefRowCount(2);
                txtAddress.setMaxWidth(200);
                txtAddress.setText(meter.getCustomeObject().getAddress());
                VBox.setMargin(txtAddress, new Insets(8));
                JFXTextField txtMeterNum = new JFXTextField();
                txtMeterNum.setPromptText("मीटर नं");
                txtMeterNum.setLabelFloat(true);
                txtMeterNum.setMaxWidth(200);
                txtMeterNum.setText(meter.getMetor_num());
                VBox.setMargin(txtMeterNum, new Insets(8));
                JFXTextField txtCurReading = new JFXTextField();
                txtCurReading.setPromptText("चालु रिडिंग");
                txtCurReading.setLabelFloat(true);
                txtCurReading.setMaxWidth(200);
                txtCurReading.setText(String.valueOf(meter.getCurr_reading()));
                VBox.setMargin(txtCurReading, new Insets(8));
                JFXTextField txtOutstanding = new JFXTextField();
                txtOutstanding.setPromptText("थकबाकी");
                txtOutstanding.setLabelFloat(true);
                txtOutstanding.setMaxWidth(200);
                txtOutstanding.setText(String.valueOf(meter.getOutstanding()));
                VBox.setMargin(txtOutstanding, new Insets(8));
                JFXTextField txtDeposit = new JFXTextField();
                txtDeposit.setPromptText("जमा");
                txtDeposit.setLabelFloat(true);
                txtDeposit.setMaxWidth(200);
                txtDeposit.setText(String.valueOf(meter.getDeposit()));
                VBox.setMargin(txtDeposit, new Insets(8));
                JFXDatePicker dpMeterCon = new JFXDatePicker();
                dpMeterCon.setPromptText("मीटर लावलेली दि ");
                dpMeterCon.setMaxWidth(200);
                VBox.setMargin(dpMeterCon, new Insets(8));
                String s[] = meter.getCon_date().split("-");
                String y[] = s[2].split(" ");
                dpMeterCon.setValue(LocalDate.parse(y[0] + "/" + s[1] + "/" + s[0], DateTimeFormatter.ofPattern("d/M/yyyy")));
                dpMeterCon.setDayCellFactory(
                        (DatePicker param) -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        setDisable(empty || item.compareTo(LocalDate.now()) > 0);
                    }

                }
                );

                JFXButton btnUpdate = new JFXButton("जतन करा");
                btnUpdate.getStyleClass().add("btn-search");
                JFXButton btnClose = new JFXButton("रद्ध");
                btnClose.getStyleClass().add("btn-cancel");
                vb.setSpacing(16);
                vb.setAlignment(Pos.CENTER);
                vb.getChildren().addAll(txtName, txtMobile, txtEmail, txtAddress, txtMeterNum, dpMeterCon, txtOutstanding, txtCurReading, txtDeposit);
                StackPane sp = new StackPane(vb);
                dialog = Support.getDialog(window, new Label("ग्राहक आणि मीटर मध्ये बदलने"), sp, btnUpdate, btnClose);
                dialog.show();

                btnUpdate.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        if (PhoenixSupport.isValidate(txtName, txtMeterNum)) {
                            CustomerOperation customerdb = new CustomerOperation();
                            Customer c = new Customer();
                            c.setName(txtName.getText());
                            c.setAddress(txtAddress.getText());
                            c.setEmail(txtEmail.getText());
                            c.setPhone(txtMobile.getText());
                            Meter m = new Meter();
                            m.setMetor_num(txtMeterNum.getText());
                            m.setCurr_reading(PhoenixSupport.getInteger(txtCurReading.getText()));
                            m.setCon_date(dpMeterCon.getValue().toString());
                            m.setOutstanding(PhoenixSupport.getDouble(txtOutstanding.getText()));
                            m.setDeposit(PhoenixSupport.getDouble(txtDeposit.getText()));
                            c.setCust_num(meter.getCustomeObject().getCust_num());
                            m.setId(meter.getId());
                            m.setCustomeObject(c);
                            customerdb.updateCustomer(c, window);

                            MeterOperation mo = new MeterOperation();
                            mo.updateMeter(m, window);

                            getData();

                            dialog.close();
                        } else {
                            PhoenixSupport.Error("ग्राहक आणि मीटर माहिती भरा", sp);
                        }
                    }
                });

                btnClose.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        dialog.close();
                    }
                });
            });
            delete.setOnMouseClicked(event -> {
                table.getSelectionModel().select(getTreeTableRow().getIndex());
                Meter meter = table.getSelectionModel().getSelectedItem().getValue();
                MeterOperation mo = new MeterOperation();
                mo.deleteMeter(meter.getId(), window);
                meterlist.remove(meter);
                table.refresh();
            });
        }

        @Override

        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(actiongroup);
            } else {
                setGraphic(null);
            }
        }
    }

}
