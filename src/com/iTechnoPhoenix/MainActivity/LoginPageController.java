package com.iTechnoPhoenix.MainActivity;

import com.iTechnoPhoenix.database.UserOperation;
import com.iTechnoPhoenix.neelSupport.PhoenixSupport;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author NARENDRA JADHAV
 */
public class LoginPageController implements Initializable {

    @FXML
    private Label lbl_softwareName;
    @FXML
    private JFXTextField txt_username;
    @FXML
    private JFXPasswordField txt_password;
    @FXML
    private StackPane window;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbl_softwareName.setText("सुकापूर ग्रामपंचायत");
    }

    @FXML
    private void btn_key_login(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (PhoenixSupport.isValidate(txt_username, txt_password)) {
                UserOperation up = new UserOperation();
                if (up.login(txt_username.getText(), txt_password.getText()) != null) {
                    showWindow(event);
                } else {
                    PhoenixSupport.Error("Invalid User", window);
                }
            } else {
                PhoenixSupport.Error("Enter Username and Password ", window);
            }

        }
    }

    @FXML
    private void btn_login(ActionEvent event) {

        if (PhoenixSupport.isValidate(txt_username, txt_password)) {
            UserOperation up = new UserOperation();
            if (up.login(txt_username.getText(), txt_password.getText()) != null) {
                showWindow(event);
            } else {

                PhoenixSupport.Error("Invalid User", window);
            }
        } else {
            PhoenixSupport.Error("Enter Username and Password ", window);
        }
    }

    @FXML
    private void btn_key_cancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            clear();
        }
    }

    @FXML
    private void btn_cancel(ActionEvent event) {
        clear();
    }

    private void clear() {
        System.exit(0);
    }

    public void showWindow(ActionEvent event) {
        try {

            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
            FXMLLoader fl = new FXMLLoader(getClass().getResource("MainActivity.fxml"));
            Scene ss = new Scene(fl.load());
            Stage stage = new Stage();
            stage.setScene(ss);
            stage.setTitle("Neel (सुकापूर ग्रामपंचायत). Developed by iTechnoPhoenix");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showWindow(KeyEvent event) {
        try {

            ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
            FXMLLoader fl = new FXMLLoader(getClass().getResource("MainActivity.fxml"));
            Scene ss = new Scene(fl.load());
            Stage stage = new Stage();
            stage.setTitle("Neel (सुकापूर ग्रामपंचायत). Developed by iTechnoPhoenix");
            stage.setScene(ss);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginPageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
