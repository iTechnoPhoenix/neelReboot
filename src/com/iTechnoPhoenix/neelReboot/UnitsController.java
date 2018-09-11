package com.iTechnoPhoenix.neelReboot;

import com.iTechnoPhoenix.database.UnitsOperation;
import com.iTechnoPhoenix.model.Unit;
import com.iTechnoPhoenix.neelSupport.PhoenixSupport;
import com.iTechnoPhoenix.neelSupport.Support;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;

public class UnitsController implements Initializable {

    @FXML
    private StackPane window;

    @FXML
    private JFXTextField txt_minunit;

    @FXML
    private JFXTextField txt_maxunit;

    @FXML
    private JFXTextField txt_price;

    @FXML
    private JFXTreeTableView<Unit> tbl_units;

    private JFXTreeTableColumn<Unit, Integer> tcAction;
    private JFXTreeTableColumn<Unit, Integer> tcMax;
    private JFXTreeTableColumn<Unit, Integer> tcMin;
    private JFXTreeTableColumn<Unit, Double> tcUnitprice;

    private UnitsOperation unitsdb;
    private ObservableList<Unit> unitList;

    @FXML
    private void btn_search(ActionEvent event) {
        save();
    }

    private void btn_search_key(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            save();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PhoenixSupport.onlyNumber(txt_maxunit);
        PhoenixSupport.onlyNumber(txt_minunit);
        PhoenixSupport.onlyNumber(txt_price);
        unitsdb = new UnitsOperation();
        initTable();
        refreshTable();
    }

    public void initTable() {
        tcAction = new JFXTreeTableColumn<>();
        tcAction.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue().getId()).asObject());
        tcAction.setCellFactory(param -> new ActionCell(tbl_units));
        tcMin = new JFXTreeTableColumn<>("किमान युनिट");
        tcMin.setMinWidth(25);
        tcMin.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue().getMin()).asObject());
        tcMax = new JFXTreeTableColumn<>("कमाल युनिट");
        tcMax.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue().getMax()).asObject());
        tcUnitprice = new JFXTreeTableColumn<>("रक्कम");
        tcUnitprice.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().getUnitprice()).asObject());
        tbl_units.getColumns().addAll(tcAction, tcMin, tcMax, tcUnitprice);
    }

    public void refreshTable() {
        unitList = unitsdb.getAllUnits();
        TreeItem<Unit> treeItem = new RecursiveTreeItem<>(unitList, RecursiveTreeObject::getChildren);
        tbl_units.setRoot(treeItem);
        tbl_units.setShowRoot(false);
    }

    public void save() {
        if (PhoenixSupport.isValidate(txt_maxunit, txt_minunit, txt_price)) {
            if (PhoenixSupport.getInteger(txt_maxunit.getText()) != 0 && PhoenixSupport.getInteger(txt_maxunit.getText()) != 0 && PhoenixSupport.getInteger(txt_price.getText()) != 0) {
                if (PhoenixSupport.getInteger(txt_maxunit.getText()) > PhoenixSupport.getInteger(txt_minunit.getText())) {
                    Unit u = new Unit();
                    u.setMax(PhoenixSupport.getInteger(txt_maxunit.getText()));
                    u.setMin(PhoenixSupport.getInteger(txt_minunit.getText()));
                    u.setUnitprice(PhoenixSupport.getDouble(txt_price.getText()));
                    if (!unitList.contains(u)) {
                        unitsdb.addUnits(u, window);
                        refreshTable();
                    } else {
                        PhoenixSupport.Error("भरलेली माहिती पहिलीच जतन केली आहे.", window);
                    }
                    clear();
                } else {

                }
            } else {
                PhoenixSupport.Error("कमाल / किमान युनिट 0 आहे.", window);
                PhoenixSupport.Error("कमाल युनिट हि किमान युनिट पेक्षा छोटी आहे.", window);
            }

        } else {
            PhoenixSupport.Error("सर्व माहिती भरा", window);
        }
    }

    public void clear() {
        txt_maxunit.clear();
        txt_minunit.clear();
        txt_price.clear();
    }

    public class ActionCell extends JFXTreeTableCell<Unit, Integer> {

        final JFXButton edit = new JFXButton("Edit");
        final JFXButton delete = new JFXButton("delete");
        final HBox actiongroup = new HBox();
        final StackPane paddedButton = new StackPane();

        public ActionCell(final JFXTreeTableView<Unit> table) {
            actiongroup.getChildren().addAll(edit, delete);
            edit.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Edit Row_48px.png"));
            edit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            delete.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Trash Can_48px.png"));
            delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            edit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    table.getSelectionModel().select(getTreeTableRow().getIndex());
                    Unit u = table.getSelectionModel().getSelectedItem().getValue();
                    VBox vb = new VBox();
                    JFXTextField txtMin = new JFXTextField();
                    PhoenixSupport.onlyNumber(txtMin);
                    txtMin.setPromptText("किमान युनिट");
                    txtMin.setLabelFloat(true);
                    txtMin.setMaxWidth(200);
                    txtMin.setText(String.valueOf(u.getMin()));
                    JFXTextField txtMax = new JFXTextField();
                    PhoenixSupport.onlyNumber(txtMax);
                    txtMax.setPromptText("कमाल युनिट");
                    txtMax.setLabelFloat(true);
                    txtMax.setMaxWidth(200);
                    txtMax.setText(String.valueOf(u.getMax()));
                    JFXTextField txtUnit = new JFXTextField();
                    PhoenixSupport.onlyNumber(txtUnit);
                    txtUnit.setPromptText("रक्कम");
                    txtUnit.setLabelFloat(true);
                    txtUnit.setMaxWidth(200);
                    txtUnit.setText(String.valueOf(u.getUnitprice()));
                    vb.setSpacing(16);
                    vb.setAlignment(Pos.CENTER);
                    vb.getChildren().addAll(txtMin, txtMax, txtUnit);
                    JFXButton btnSave = new JFXButton("जतन");
                    btnSave.getStyleClass().add("btn-search");
                    JFXButton btnClose = new JFXButton("रद्ध");
                    btnClose.getStyleClass().add("btn-cancel");
                    JFXDialog dialog = Support.getDialog(window, new Label("युनिट बदल करणे"), vb, btnSave, btnClose);
                    btnSave.setOnAction(e -> {
                        if (PhoenixSupport.getInteger(txtMax.getText()) > PhoenixSupport.getInteger(txtMin.getText())) {
                            u.setMin(PhoenixSupport.getInteger(txtMin.getText()));
                            u.setMax(PhoenixSupport.getInteger(txtMax.getText()));
                            u.setUnitprice(PhoenixSupport.getDouble(txtUnit.getText()));
                            unitsdb.updateUnits(u, window);
                            refreshTable();
                            dialog.close();
                        } else {
                            txtMax.setFocusColor(Paint.valueOf("red"));
                            txtMax.requestFocus();
                        }
                    });
                    btnSave.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                            if (PhoenixSupport.getInteger(txtMax.getText()) > PhoenixSupport.getInteger(txtMin.getText())) {
                                u.setMin(PhoenixSupport.getInteger(txtMin.getText()));
                                u.setMax(PhoenixSupport.getInteger(txtMax.getText()));
                                u.setUnitprice(PhoenixSupport.getDouble(txtUnit.getText()));
                                unitsdb.updateUnits(u, window);
                                refreshTable();
                                dialog.close();
                            } else {
                                txtMax.setFocusColor(Paint.valueOf("red"));
                                txtMax.requestFocus();
                            }
                        }
                    }
                    );
                    btnClose.setOnAction(e
                            -> dialog.close());
                    btnClose.setOnKeyPressed(e
                            -> {
                        if (e.getCode() == KeyCode.ENTER) {
                            dialog.close();
                        }
                    }
                    );
                    dialog.show();

                    dialog.setOnDialogOpened(e
                            -> btnSave.requestFocus());
                }
            });
            delete.setOnMouseClicked(
                    new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event
                ) {
                    table.getSelectionModel().select(getTreeTableRow().getIndex());
                    Unit unit = table.getSelectionModel().getSelectedItem().getValue();
                    unitsdb.deleteUnits(unit.getId(), window);
                    refreshTable();
                }
            }
            );
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
