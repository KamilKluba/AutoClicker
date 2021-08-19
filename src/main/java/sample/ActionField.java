package sample;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;


public class ActionField {
    private TextField textFieldX;
    private TextField textFieldY;
    private Label labelKey;
    private TextField textFieldActionsNumber;
    private TextField textFieldActionsPeriod;
    private Button buttonKey;
    private Button buttonMouse;
    private KeyCode keyCode;
    private MouseButton mouseButton;
    private CheckBox checkboxActionStart;
    private Robot robot;

    private Thread lifeCycle;
    private boolean alive;

    public ActionField(){

    }

    public ActionField(TextField textFieldX, TextField textFieldY, Label labelKey, TextField textFieldActionsNumber,
                       TextField textFieldActionsPeriod, Button buttonKey, Button buttonMouse, CheckBox checkboxActionStart) {
        this.textFieldX = textFieldX;
        this.textFieldY = textFieldY;
        this.labelKey = labelKey;
        this.textFieldActionsNumber = textFieldActionsNumber;
        this.textFieldActionsPeriod = textFieldActionsPeriod;
        this.buttonKey = buttonKey;
        this.buttonMouse = buttonMouse;
        this.checkboxActionStart = checkboxActionStart;
        this.robot = new Robot();

        this.textFieldActionsNumber.setText("0");
        this.textFieldActionsPeriod.setText("1000");
//        this.buttonKey.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
//            keyCode = keyEvent.getCode();
//            labelKey.setText(keyCode.getName());
//            labelKey.requestFocus();
//            textFieldX.setText(null);
//            textFieldY.setText(null);
//        });
//        this.buttonMouse.addEventFilter(MouseEvent.MOUSE_CLICKED, keyEvent -> {
//            Stage stage = new Stage();
//            stage.initStyle(StageStyle.UNDECORATED);
//            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
//            stage.setScene(new Scene(new Pane(), dimension.width, dimension.height));
//            stage.setOpacity(0.2);
//            stage.addEventFilter(MouseEvent.MOUSE_CLICKED, keyEvent2 -> {
//                textFieldX.setText(keyEvent2.getScreenX() + "");
//                textFieldY.setText(keyEvent2.getScreenY() + "");
//                mouseButton = keyEvent2.getButton();
//                labelKey.setText(keyEvent2.getButton().name());
//                stage.close();
//            });
//            stage.show();
//        });
    }

    public void configure(){
        this.alive = true;

        if(checkboxActionStart.isSelected()){
            lifeCycle = new Thread(() -> {
                for (int j = 0; j < getActionsNumber() && alive; j++) {
                    Platform.runLater(() -> {
                        robot.keyPress(keyCode);
                        System.out.println("KLIK " + keyCode.getName());
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("PUSZCZAM " + keyCode.getName());
                        robot.keyRelease(keyCode);
                    });
                    try {
                        for(int k = 0; k < getActionPeriod(); k = k + 100){
                            Thread.sleep(100);
                            if(!alive) break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void start(){
        lifeCycle.start();
        System.out.println("jazda");
    }

    public void kill(){
        this.alive = false;
    }

    public String getTextX() {
        return textFieldX.getText();
    }

    public String getTextY() {
        return textFieldY.getText();
    }

    public String getTextKey() {
        return labelKey.getText();
    }

    public int getActionsNumber() {
        int parsedNumber = Integer.parseInt(textFieldActionsNumber.getText());
        return parsedNumber == 0 ? Integer.MAX_VALUE : parsedNumber;
    }

    public int getActionPeriod() {
        return Integer.parseInt(textFieldActionsPeriod.getText());
    }

    public Button getButtonKey() {
        return buttonKey;
    }

    public Button getButtonMouse() {
        return buttonMouse;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public CheckBox getCheckboxActionStart() {
        return checkboxActionStart;
    }
}
