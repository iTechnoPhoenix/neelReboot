package com.iTechnoPhoenix.Receipt;

import com.iTechnoPhoenix.database.BankOpearation;
import com.iTechnoPhoenix.database.BillOperation;
import com.iTechnoPhoenix.database.FailureOperation;
import com.iTechnoPhoenix.database.ReceiptOperation;
import com.iTechnoPhoenix.model.Banks;
import com.iTechnoPhoenix.model.Bill;
import com.iTechnoPhoenix.model.MeterBill;
import com.iTechnoPhoenix.model.Receipt;
import com.iTechnoPhoenix.neelSupport.BillSupport;
import com.iTechnoPhoenix.neelSupport.PhoenixSupport;
import com.iTechnoPhoenix.neelSupport.Support;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import englishtomarathinumberconvertor.MarathiNumber;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ReceiptTransactionController implements Initializable {

    @FXML
    private JFXTextField txt_bill_number;

    @FXML
    private Label txt_customer;

    @FXML
    private Label txt_duration;

    @FXML
    private Label txt_lastdate;

    @FXML
    private JFXTreeTableView<Bill> tbl_meter;

    @FXML
    private Label lbl_preious_paid_amt;

    @FXML
    private Label lbl_total;

    @FXML
    private JFXTextField txt_delay_payment;

    @FXML
    private JFXTextField txt_paid_amount;

    @FXML
    private Label txt_remaining_amt;

    @FXML
    private StackPane window;

    private JFXTreeTableColumn<Bill, String> tcMeterNumber;
    private JFXTreeTableColumn<Bill, Long> tcPreviousReading;
    private JFXTreeTableColumn<Bill, Long> tcCurrentReading;
    private JFXTreeTableColumn<Bill, Long> tcUseUnit;
    private JFXTreeTableColumn<Bill, Double> tcOutstanding;
    private JFXTreeTableColumn<Bill, Double> tcInterest;
    private JFXTreeTableColumn<Bill, Double> tcServiceCharge;
    private JFXTreeTableColumn<Bill, Double> tcBillAmount;
    private JFXTreeTableColumn<Bill, Double> tcTotal;

    private JFXDialog dialog;
    private JFXTextField txt_cheque;
    private JFXComboBox<Banks> cb_banklist;
    private BillOperation billdb;
    private ReceiptOperation recieptdb;
    private FailureOperation faildb;
    private ObservableList<Bill> billList;
    private ObservableList<Banks> bankList;
    private Bill bill;
    private Banks bank = new Banks();
    private double grandtotal;
    private double remaining;

    @FXML
    private void btn_cancel(ActionEvent event) {
        cancel();
    }

    @FXML
    private void btn_cash_paid(ActionEvent event) {
        save(1);
    }

    @FXML
    private void btn_cheque_paid(ActionEvent event) {
        chequeDialog();
    }

    @FXML
    private void btn_cancel_key(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            cancel();
        }
    }

    @FXML
    private void btn_cash_paid_key(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            save(1);
        }
    }

    @FXML
    private void btn_cheque_paid_key(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            chequeDialog();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        billdb = new BillOperation();
        recieptdb = new ReceiptOperation();
        faildb = new FailureOperation();
        initTable();
        txt_bill_number.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                grandtotal = 0;
                remaining = 0;
                if (!txt_bill_number.getText().isEmpty()) {
                    String s = txt_bill_number.getText();

                    billList = billdb.getBillRef(PhoenixSupport.getInteger(s));
                    Receipt receipt = recieptdb.checkBill(PhoenixSupport.getInteger(txt_bill_number.getText()));

                    if (!billList.isEmpty()) {
                        if (billList.get(0).getSt() != 2) {
                            txt_duration.setText(billList.get(0).getPeriod());
                            txt_duration.getStyleClass().add("label-marathi");
                            String str1[] = billList.get(0).getPdate().split(" ");
                            txt_lastdate.setText(str1[0]);
                            txt_customer.setText(billList.get(0).getCust().getName());
                            refreshTable();

                            for (Bill b : billList) {
                                grandtotal = grandtotal + b.getTotal();
                                remaining = remaining + b.getPaidamt();
                            }

                            if (receipt != null) {
                                if (billList.get(0).getSt() == 3) {
                                    PhoenixSupport.Info("ह्या बिलाची रक्कम आधीच मिळाली आहे", "पावती व्यवहार", window);
                                    cancel();
                                } else if (billList.get(0).getSt() == 5) {
                                    if (receipt.getCheq_no() != null) {
                                        if (!faildb.checkFailed(receipt.getCheq_no())) {
                                            lbl_preious_paid_amt.setText(String.valueOf(receipt.getAmount()));
                                        } else {
                                            lbl_preious_paid_amt.setText(String.valueOf(grandtotal - remaining));
                                        }
                                    } else {
                                        lbl_preious_paid_amt.setText(String.valueOf(receipt.getAmount()));
                                    }
                                } else {
                                    lbl_preious_paid_amt.setText("००");
                                }
                            } else {
                                lbl_preious_paid_amt.setText("००");
                            }
                            txt_delay_payment.setText("००");
                            grandCalculation();
                        } else {
                            PhoenixSupport.Error("पुढल्या महिन्याच बिल आधीच बनवले आहे.", window);
                            txt_bill_number.clear();
                            txt_bill_number.requestFocus();
                        }
                    } else {
                        PhoenixSupport.Error("बिल क्रमांक चुकीचा आहे. कृपया तपासून पहा.", window);
                        txt_bill_number.clear();
                        txt_bill_number.requestFocus();
                    }
                }
            }
        });
        txt_delay_payment.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    grandCalculation();
                }
            }
        });
        txt_paid_amount.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    txt_remaining_amt.setText(String.valueOf(PhoenixSupport.getDouble(lbl_total.getText()) - PhoenixSupport.getDouble(txt_paid_amount.getText())));
                }
            }
        });
    }

    private void refreshTable() {
        TreeItem<Bill> treeItem = new RecursiveTreeItem<>(billList, RecursiveTreeObject::getChildren);
        tbl_meter.setRoot(treeItem);
        tbl_meter.setShowRoot(false);
    }

    public void chequeDialog() {

        BankOpearation bankdb = new BankOpearation();

        bankList = FXCollections.observableArrayList();
        bankList = bankdb.getBanksByCode("");

        StackPane dialogWindow = new StackPane();
        dialogWindow.setPrefSize(640, 320);

        VBox outerbox = new VBox();
        outerbox.setAlignment(Pos.TOP_CENTER);

        // header
        Label header = new Label("बँकेची माहित");
        header.setStyle("-fx-background-color: #1F9CFF;-fx-text-fill:white;");
        VBox.setVgrow(header, Priority.ALWAYS);
        header.setMaxWidth(1.7976931348623157E308);
        VBox.setMargin(header, new Insets(8));
        header.setPadding(new Insets(8, 0, 8, 8));
        // new button
        JFXButton newBank = new JFXButton("नवीन बँक");
        newBank.setTextFill(Color.WHITE);
        newBank.setStyle("-fx-background-color:#4BB89B");
        VBox.setMargin(newBank, new Insets(8, 16, 0, 8));

        // innerStock
        StackPane innerStock = new StackPane();
        VBox.setMargin(innerStock, new Insets(8));
        innerStock.setStyle("-fx-border-color: #CECFD0; -fx-border-width: 1;");
        VBox newbox = new VBox();
        newbox.setAlignment(Pos.CENTER_RIGHT);
        newbox.setVisible(false);
        StackPane.setMargin(newbox, new Insets(8));
        newbox.setPadding(new Insets(8, 16, 8, 16));

        JFXTextField txt_bank = new JFXTextField();
        txt_bank.setPromptText("बँक नाव");
        VBox.setMargin(txt_bank, new Insets(8));
        txt_bank.setPadding(new Insets(8, 16, 8, 16));
        JFXTextField txt_code = new JFXTextField();
        txt_code.setPromptText("बँक IFSC Code");
        VBox.setMargin(txt_code, new Insets(8));
        txt_code.setPadding(new Insets(8, 16, 8, 16));
        JFXTextField txt_branch = new JFXTextField();
        txt_branch.setPromptText("बँक शाखा");
        VBox.setMargin(txt_branch, new Insets(8));
        txt_branch.setPadding(new Insets(8, 16, 8, 16));

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

        newbox.getChildren().addAll(txt_bank, txt_code, txt_branch, buttonrow);

        VBox billbank = new VBox();

        billbank.setAlignment(Pos.CENTER_RIGHT);

        cb_banklist = new JFXComboBox<>();
        cb_banklist.setMaxWidth(1.7976931348623157E308);
        cb_banklist.setPromptText("बँक नाव");
        cb_banklist.setItems(bankList);
        VBox.setMargin(cb_banklist, new Insets(8));

        txt_cheque = new JFXTextField();
        txt_cheque.setPromptText("चेक क्रमांक");
        VBox.setMargin(txt_cheque, new Insets(8));

        JFXButton btn_billsave = new JFXButton("जतन करा");
        btn_billsave.setStyle("-fx-background-color: #4BB89B;-fx-text-fill:white;");
        JFXButton btn_billcancl = new JFXButton("रद्ध करा");
        btn_billcancl.setStyle("-fx-background-color:#3E4A4F; -fx-text-fill:white;");
        VBox.setMargin(btn_billsave, new Insets(8, 8, 8, 8));

        StackPane.setMargin(billbank, new Insets(8));
        billbank.setPadding(new Insets(8, 16, 8, 16));
        HBox hb = new HBox();
        hb.setSpacing(16);
        hb.setAlignment(Pos.CENTER_RIGHT);
        hb.getChildren().addAll(btn_billsave, btn_billcancl);
        billbank.getChildren().addAll(cb_banklist, txt_cheque, hb);

        innerStock.getChildren().addAll(newbox, billbank);

        StackPane.setMargin(dialogWindow, new Insets(8));

        outerbox.getChildren().addAll(header, newBank, innerStock);
        dialogWindow.getChildren().add(outerbox);

        JFXDialog dialog = Support.getDialog(window, dialogWindow, JFXDialog.DialogTransition.TOP);
        dialog.setOverlayClose(false);

        btn_billcancl.setOnMouseClicked(e -> {
            dialog.close();
        });

        btn_billcancl.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {

                dialog.close();
            }
        });

        dialog.show();

        btn_bankclose.setOnMouseClicked((e) -> {
            txt_bank.clear();
            txt_branch.clear();
            txt_code.clear();
            newbox.setVisible(false);
            billbank.setVisible(true);
        });

        btn_bankclose.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                txt_bank.clear();
                txt_branch.clear();
                txt_code.clear();
                newbox.setVisible(false);
                billbank.setVisible(true);
            }
        });

        btn_banksave.setOnMouseClicked((e) -> {
            if (PhoenixSupport.isValidate(txt_code, txt_bank, txt_branch)) {
                bank.setBankname(txt_bank.getText());
                bank.setBranch(txt_branch.getText());
                bank.setCode(txt_code.getText());
                bankdb.saveBank(bank, window);
                bankList.add(bank);
                newbox.setVisible(false);
                billbank.setVisible(true);
            } else {
                PhoenixSupport.Error("सर्व माहिती भरा", window);
            }
        });

        btn_banksave.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (PhoenixSupport.isValidate(txt_code, txt_bank, txt_branch)) {
                    bank.setBankname(txt_bank.getText());
                    bank.setBranch(txt_branch.getText());
                    bank.setCode(txt_code.getText());
                    bankdb.saveBank(bank, window);
                    bankList.add(bank);
                    newbox.setVisible(false);
                    billbank.setVisible(true);
                } else {
                    PhoenixSupport.Error("सर्व माहिती भरा", window);
                }
            }
        });

        newBank.setOnMouseClicked((e) -> {
            txt_bank.clear();
            txt_branch.clear();
            txt_code.clear();
            newbox.setVisible(true);
            billbank.setVisible(false);
        });

        newBank.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                txt_bank.clear();
                txt_branch.clear();
                txt_code.clear();
                newbox.setVisible(true);
                billbank.setVisible(false);
            }
        });

        btn_billsave.setOnMouseClicked(e -> {
            save(2);
            dialog.close();
        });

        btn_billsave.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                save(2);
                dialog.close();
            }
        });
    }

    public void initTable() {
        tcMeterNumber = new JFXTreeTableColumn<>("मीटर क्रमांक");
        tcMeterNumber.setPrefWidth(95);
        tcMeterNumber.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().getValue().getMeter().getMetor_num()));
        tcPreviousReading = new JFXTreeTableColumn<>("मागील रिडिंग");
        tcPreviousReading.setPrefWidth(95);
        tcPreviousReading.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getValue().getMeter().getCurr_reading()).asObject());
        tcCurrentReading = new JFXTreeTableColumn<>("चालु रिडिंग");
        tcCurrentReading.setPrefWidth(95);
        tcCurrentReading.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getValue().getCurunit()).asObject());
        tcUseUnit = new JFXTreeTableColumn<>("वापर युनिट");
        tcUseUnit.setPrefWidth(95);
        tcUseUnit.setCellValueFactory(param -> new SimpleLongProperty(param.getValue().getValue().getUseunit()).asObject());
        tcOutstanding = new JFXTreeTableColumn<>("थकबाकी");
        tcOutstanding.setPrefWidth(95);
        tcOutstanding.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getMeter().getOutstanding()).asObject());
        tcInterest = new JFXTreeTableColumn<>("१८% व्याज");
        tcInterest.setPrefWidth(95);
        tcInterest.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getInterested()).asObject());
        tcServiceCharge = new JFXTreeTableColumn<>("सर चार्ज");
        tcServiceCharge.setPrefWidth(95);
        tcServiceCharge.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getScharges()).asObject());
        tcBillAmount = new JFXTreeTableColumn<>("बिलाची रक्क्म");
        tcBillAmount.setPrefWidth(95);
        tcBillAmount.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getCuramount()).asObject());
        tcTotal = new JFXTreeTableColumn<>("एकूण रक्क्म");
        tcTotal.setPrefWidth(95);
        tcTotal.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getTotal()).asObject());
        tbl_meter.getColumns().addAll(tcMeterNumber, tcPreviousReading, tcCurrentReading, tcUseUnit, tcOutstanding, tcInterest, tcServiceCharge, tcBillAmount, tcTotal);
    }

    private void grandCalculation() {
        lbl_total.setText(String.valueOf(grandtotal - PhoenixSupport.getDouble(lbl_preious_paid_amt.getText()) + PhoenixSupport.getDouble(txt_delay_payment.getText())));
        txt_remaining_amt.setText(String.valueOf(PhoenixSupport.getDouble(lbl_total.getText()) - PhoenixSupport.getDouble(txt_paid_amount.getText())));
    }

    private void cancel() {
        txt_bill_number.clear();
        txt_customer.setText("");
        txt_delay_payment.clear();
        txt_duration.setText("");
        txt_lastdate.setText("");
        lbl_total.setText("००");
        lbl_preious_paid_amt.setText("००");
        txt_paid_amount.clear();
        txt_remaining_amt.setText("००");
        billList.clear();
        grandtotal = 0;
        refreshTable();
    }

    public void save(int i) {
        if (PhoenixSupport.isValidate(txt_bill_number)) {
            Receipt receipt = new Receipt();
            receipt.setBillno(PhoenixSupport.getInteger(txt_bill_number.getText()));
            receipt.setDelay_amount(PhoenixSupport.getDouble(txt_delay_payment.getText()));
            receipt.setUid(PhoenixSupport.uid);
            receipt.setPdate(LocalDate.now().toString());
            if (i == 1) {
                receipt.setPaymode(i);
            } else if (i == 2) {
                receipt.setBankid(cb_banklist.getSelectionModel().getSelectedItem().getBid());
                receipt.setCheq_no(txt_cheque.getText());
                receipt.setPaymode(i);
            }
            receipt.setUid(PhoenixSupport.uid);
            receipt.setAmount(PhoenixSupport.getDouble(txt_paid_amount.getText()));

            MarathiNumber mn = new MarathiNumber();
            recieptdb = new ReceiptOperation();
            int result = 0;
            result = recieptdb.saveReceipt(receipt);

            if (result != 0) {
                bill = new Bill();
                bill.setTotal(PhoenixSupport.getDouble(txt_paid_amount.getText()));
                bill.setCustomername(txt_customer.getText());
                bill.setCustomernumber(billList.get(0).getCust().getCust_num());
                bill.setBillref(PhoenixSupport.getInteger(txt_bill_number.getText()));
                bill.setAddress(billList.get(0).getCust().getAddress());
                if (i == 2) {
                    bill.setBankname(cb_banklist.getSelectionModel().getSelectedItem().getBankname());
                    bill.setCheque(txt_cheque.getText());
                }
                bill.setNumberInWord(mn.getMarathiNumber(PhoenixSupport.getDouble(txt_paid_amount.getText())));
                bill.setRid(result);
                bill.setPdate(LocalDate.now().toString());
                ArrayList<Bill> list = new ArrayList<>();
                list.add(bill);
                getSuccessDialog(list);
            } else {
                PhoenixSupport.Info("पावती जतन नाही जाहली", "पावती", window);
            }
        } else {
            PhoenixSupport.Error("बिल क्रमांक टाका.", window);
            txt_bill_number.requestFocus();
        }
    }

    private void getSuccessDialog(ArrayList list) {
        BillSupport billSupport = new BillSupport();
        JFXButton btnDPrint = new JFXButton("प्रिंट करा");
        btnDPrint.getStyleClass().add("btn-search");
        btnDPrint.setOnAction(e -> {
            ArrayList<MeterBill> meterBillList = billSupport.assignBillValue(billList);
            PhoenixSupport ps = new PhoenixSupport();
            ps.printReceipt(list);
            cancel();
            dialog.close();
        });
        btnDPrint.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                ArrayList<MeterBill> meterBillList = billSupport.assignBillValue(billList);
                PhoenixSupport ps = new PhoenixSupport();
                ps.printReceipt(list);
                cancel();
                dialog.close();
            }
        });
        JFXButton btnDCancel = new JFXButton("रद्द करा");
        btnDCancel.getStyleClass().add("btn-cancel");
        btnDCancel.setOnAction(e -> {
            cancel();
            dialog.close();
        });
        btnDCancel.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                cancel();
                dialog.close();
            }
        });
        dialog = Support.getDialog(window, new Label("पावती व्यवहार"), new Label("पावती जतन झाली आहे."), btnDPrint, btnDCancel);
        dialog.show();
        dialog.setOnDialogOpened(e -> {
            btnDCancel.requestFocus();
        });
    }
}
