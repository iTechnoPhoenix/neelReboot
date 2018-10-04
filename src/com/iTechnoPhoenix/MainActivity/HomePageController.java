/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iTechnoPhoenix.MainActivity;

import com.iTechnoPhoenix.database.BillOperation;
import com.iTechnoPhoenix.neelSupport.PhoenixConfiguration;
import com.iTechnoPhoenix.neelSupport.PhoenixSupport;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author NARENDRA JADHAV
 */
public class HomePageController implements Initializable {

    @FXML
    private Label lbl_date;
    @FXML
    private Label lbl_month;
    @FXML
    private Label lbl_unpaid;
    @FXML
    private Label lbl_paid_bill;
    @FXML
    private Label lbl_totol_unpaid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lbl_date.setText(LocalDate.now().toString());
        String s[] = lbl_date.getText().split("-");
        lbl_month.setText(PhoenixConfiguration.getMonth().get((int) (Integer.parseInt(s[1]) / 2) - 1));
        lbl_month.setStyle("-fx-font-family:Shivaji01; -fx-font-size:22px;");
        BillOperation op = new BillOperation();
        int[] i = op.getPendingBillscount(lbl_month.getText().toString(), "2018");
        lbl_unpaid.setText("0" + String.valueOf(i[0]));

        lbl_paid_bill.setText("0" + String.valueOf(op.getPaiedBillscount(lbl_month.getText().toString(), "2018")));

        lbl_totol_unpaid.setText("0" + String.valueOf(i[1]));
    }

}
