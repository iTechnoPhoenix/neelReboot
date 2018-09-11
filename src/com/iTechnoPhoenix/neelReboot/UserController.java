package com.iTechnoPhoenix.neelReboot;

import com.iTechnoPhoenix.database.UserOperation;
import com.iTechnoPhoenix.model.User;
import com.iTechnoPhoenix.neelSupport.PhoenixConfiguration;
import com.iTechnoPhoenix.neelSupport.PhoenixSupport;
import com.iTechnoPhoenix.neelSupport.Support;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class UserController implements Initializable {

    @FXML
    private JFXTextField txt_name;

    @FXML
    private JFXComboBox<String> cb_role;

    @FXML
    private JFXTextField txt_username;

    @FXML
    private JFXPasswordField txt_password;

    @FXML
    private JFXTreeTableView<User> tbl_users;

    @FXML
    private StackPane window;

    private JFXTreeTableColumn<User, String> tcName;
    private JFXTreeTableColumn<User, String> tcUserName;
    private JFXTreeTableColumn<User, String> tcRole;
    private JFXTreeTableColumn<User, Integer> tcAction;

    private UserOperation userdb;
    private ObservableList<User> userList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cb_role.setItems(PhoenixConfiguration.getRoles());
        userdb = new UserOperation();
        initTable();
        refresh();
    }

    @FXML
    private void btn_cancel(ActionEvent event) {
        cancel();
    }

    @FXML
    private void btn_save(ActionEvent event) {
        save();
    }

    @FXML
    private void btn_cancel_key(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            cancel();
        }
    }

    @FXML
    private void btn_save_key(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            save();
        }
    }

    private void initTable() {
        tcAction = new JFXTreeTableColumn<>();
        tcAction.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getValue().getId()).asObject());
        tcAction.setCellFactory(param -> new ActionCell(tbl_users));
        tcName = new JFXTreeTableColumn<>("नाव");
        tcName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));
        tcUserName = new JFXTreeTableColumn<>("युझरनेम");
        tcUserName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getUsername()));
        tcRole = new JFXTreeTableColumn<>("भूमिका");
        tcRole.setCellValueFactory(param -> new SimpleStringProperty(PhoenixConfiguration.getRoles().get(param.getValue().getValue().getRole())));
        tbl_users.getColumns().addAll(tcAction, tcName, tcUserName, tcRole);
    }

    private void refresh() {
        userList = userdb.getAllUser();
        TreeItem<User> treeItem = new RecursiveTreeItem<>(userList, RecursiveTreeObject::getChildren);
        tbl_users.setRoot(treeItem);
        tbl_users.setShowRoot(false);
    }

    private void save() {
        if (PhoenixSupport.isValidate(txt_name, txt_username, txt_password) && PhoenixSupport.isValidate(cb_role)) {
            if (PhoenixSupport.role == 0) {
                User u = new User();
                u.setName(txt_name.getText());
                u.setUsername(txt_username.getText());
                u.setPassword(txt_password.getText());
                u.setRole(cb_role.getSelectionModel().getSelectedIndex());
                if (!userList.contains(u)) {
                    userdb.addUser(u, window);
                    refresh();
                    cancel();
                } else {
                    txt_username.requestFocus();
                    txt_username.clear();
                    PhoenixSupport.Error("युझरनेम आधीच वापरण्यात आहे.", window);
                }
            } else {
                PhoenixSupport.Error("परवानगी नाही", window);
            }
        } else {
            PhoenixSupport.Error("सर्व माहिती भरा", window);
        }
    }

    private void cancel() {
        txt_name.clear();
        txt_password.clear();
        txt_username.clear();
        cb_role.getSelectionModel().clearSelection();
    }

    public class ActionCell extends JFXTreeTableCell<User, Integer> {

        final JFXButton edit = new JFXButton("Edit");
        final JFXButton delete = new JFXButton("delete");
        final HBox actiongroup = new HBox();
        final StackPane paddedButton = new StackPane();

        public ActionCell(final JFXTreeTableView<User> table) {
            actiongroup.getChildren().addAll(edit, delete);
            edit.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Edit Row_48px.png"));
            edit.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            delete.setGraphic(new ImageView("/com/iTechnoPhoenix/resource/Trash Can_48px.png"));
            delete.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            edit.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    table.getSelectionModel().select(getTreeTableRow().getIndex());
                    User u = table.getSelectionModel().getSelectedItem().getValue();
                    VBox vb = new VBox();
                    JFXTextField txtName = new JFXTextField();
                    txtName.setPromptText("नाव");
                    txtName.setLabelFloat(true);
                    txtName.setMaxWidth(200);
                    txtName.setText(String.valueOf(u.getName()));
//                    JFXTextField txtUsername = new JFXTextField();
//                    txtUsername.setPromptText("युझरनेम");
//                    txtUsername.setLabelFloat(true);
//                    txtUsername.setMaxWidth(200);
//                    txtUsername.setText(String.valueOf(u.getUsername()));
                    JFXComboBox<String> cbRole = new JFXComboBox<>();
                    cbRole.setItems(PhoenixConfiguration.getRoles());
                    cbRole.setPromptText("भूमिका");
                    cbRole.setLabelFloat(true);
                    cbRole.setMaxWidth(200);
                    cbRole.getSelectionModel().select(PhoenixConfiguration.getRoles().get(u.getRole()));
                    vb.setSpacing(16);
                    vb.setAlignment(Pos.CENTER);
                    vb.getChildren().addAll(txtName, cbRole);
                    JFXButton btnSave = new JFXButton("जतन");
                    btnSave.getStyleClass().add("btn-search");
                    JFXButton btnClose = new JFXButton("रद्ध");
                    btnClose.getStyleClass().add("btn-cancel");
                    JFXDialog dialog = Support.getDialog(window, new Label("युनिट बदल करणे"), vb, btnSave, btnClose);
                    btnSave.setOnAction(e -> {
                        if (PhoenixSupport.isValidate(txtName) && PhoenixSupport.isValidate(cbRole)) {
                            if (PhoenixSupport.role == 0) {
                                u.setName(txtName.getText());
                                u.setRole(cbRole.getSelectionModel().getSelectedIndex());
                                userdb.updateUser(u, window);
                                refresh();
                                dialog.close();
                            } else {
                                PhoenixSupport.Error("परवानगी नाही", window);
                            }
                        } else {
                            PhoenixSupport.Error("सर्व माहिती भरा", window);
                        }
                    });
                    btnSave.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                            if (PhoenixSupport.isValidate(txtName) && PhoenixSupport.isValidate(cbRole)) {
                                if (PhoenixSupport.role == 0) {
                                    u.setName(txtName.getText());
                                    u.setRole(cbRole.getSelectionModel().getSelectedIndex());
                                    userdb.updateUser(u, window);
                                    refresh();
                                    dialog.close();
                                } else {
                                    PhoenixSupport.Error("परवानगी नाही", window);
                                }
                            } else {
                                PhoenixSupport.Error("सर्व माहिती भरा", window);
                            }
                        }
                    });
                    btnClose.setOnAction(e -> dialog.close());
                    btnClose.setOnKeyPressed(e -> {
                        if (e.getCode() == KeyCode.ENTER) {
                            dialog.close();
                        }
                    });
                    dialog.show();

                    dialog.setOnDialogOpened(e -> btnSave.requestFocus());
                }
            });
            delete.setOnMouseClicked(event -> {
                table.getSelectionModel().select(getTreeTableRow().getIndex());
                User user = table.getSelectionModel().getSelectedItem().getValue();
                userdb.deleteUser(user.getId(), window);
                refresh();
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
