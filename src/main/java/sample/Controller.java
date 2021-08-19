package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;

public class Controller {
    @FXML private Button buttonStart;
    @FXML private Button buttonStop;

    @FXML private TextField textFieldX1;
    @FXML private TextField textFieldY1;
    @FXML private Label labelKey1;
    @FXML private TextField textFieldActionsNumber1;
    @FXML private TextField textFieldActionsPeriod1;
    @FXML private Button buttonKey1;
    @FXML private Button buttonMouse1;
    @FXML private CheckBox checkboxActionStart1;

    @FXML private TextField textFieldX2;
    @FXML private TextField textFieldY2;
    @FXML private Label labelKey2;
    @FXML private TextField textFieldActionsNumber2;
    @FXML private TextField textFieldActionsPeriod2;
    @FXML private Button buttonKey2;
    @FXML private Button buttonMouse2;
    @FXML private CheckBox checkboxActionStart2;

    @FXML private TextField textFieldX3;
    @FXML private TextField textFieldY3;
    @FXML private Label labelKey3;
    @FXML private TextField textFieldActionsNumber3;
    @FXML private TextField textFieldActionsPeriod3;
    @FXML private Button buttonKey3;
    @FXML private Button buttonMouse3;
    @FXML private CheckBox checkboxActionStart3;

    @FXML private TextField textFieldX4;
    @FXML private TextField textFieldY4;
    @FXML private Label labelKey4;
    @FXML private TextField textFieldActionsNumber4;
    @FXML private TextField textFieldActionsPeriod4;
    @FXML private Button buttonKey4;
    @FXML private Button buttonMouse4;
    @FXML private CheckBox checkboxActionStart4;

    @FXML private TextField textFieldX5;
    @FXML private TextField textFieldY5;
    @FXML private Label labelKey5;
    @FXML private TextField textFieldActionsNumber5;
    @FXML private TextField textFieldActionsPeriod5;
    @FXML private Button buttonKey5;
    @FXML private Button buttonMouse5;
    @FXML private CheckBox checkboxActionStart5;

    private ArrayList<ActionField> actionFields;

    private boolean alive = true;

    public void initialize(){
        ActionField actionField1 = new ActionField(textFieldX1, textFieldY1, labelKey1, textFieldActionsNumber1, textFieldActionsPeriod1, buttonKey1, buttonMouse1, checkboxActionStart1);
        ActionField actionField2 = new ActionField(textFieldX2, textFieldY2, labelKey2, textFieldActionsNumber2, textFieldActionsPeriod2, buttonKey2, buttonMouse2, checkboxActionStart2);
        ActionField actionField3 = new ActionField(textFieldX3, textFieldY3, labelKey3, textFieldActionsNumber3, textFieldActionsPeriod3, buttonKey3, buttonMouse3, checkboxActionStart3);
        ActionField actionField4 = new ActionField(textFieldX4, textFieldY4, labelKey4, textFieldActionsNumber4, textFieldActionsPeriod4, buttonKey4, buttonMouse4, checkboxActionStart4);
        ActionField actionField5 = new ActionField(textFieldX5, textFieldY5, labelKey5, textFieldActionsNumber5, textFieldActionsPeriod5, buttonKey5, buttonMouse5, checkboxActionStart5);

        actionFields = new ArrayList<>(Arrays.asList(actionField1, actionField2, actionField3, actionField4, actionField5));
    }

    public void start(){
        alive = true;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(ActionField actionField : actionFields){
            actionField.configure();
        }
        for(ActionField actionField : actionFields){
            actionField.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        for(ActionField actionField : actionFields){
            actionField.kill();
        }
    }
}
