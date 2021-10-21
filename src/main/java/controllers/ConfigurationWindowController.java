package controllers;

import data.ActionField;
import data.Modes;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;


public class ConfigurationWindowController {
    @FXML TextField textFieldActionsNumber;
    @FXML TextField textFieldActionPeriod;
    @FXML TextField textFieldPeriodRandomizer;
    @FXML Button buttonFileTrigger;
    @FXML AnchorPane anchorPanePixelColor;
    @FXML TextField textFieldColorPixelX;
    @FXML TextField textFieldColorPixelY;
    @FXML CheckBox checkboxPixelColor;
    @FXML ColorPicker colorPickerPixel;
    @FXML TextField textFieldColorDiff;

    @FXML AnchorPane anchorPaneImageSelector;
    @FXML TextField textFieldX1;
    @FXML TextField textFieldY1;
    @FXML TextField textFieldX2;
    @FXML TextField textFieldY2;

    @FXML AnchorPane anchorPaneKeyboard;
    @FXML TextField textFieldKey;

    @FXML AnchorPane anchorPaneMouse;
    @FXML TextField textFieldMouseClickX;
    @FXML TextField textFieldMouseClickY;
    @FXML Button buttonMouseButton;

    @FXML AnchorPane anchorPaneSound;
    @FXML Button buttonFileAction;

    @FXML Button buttonOk;
    @FXML Button buttonCancel;

    private String action;
    private String trigger;
    private ActionField actionField;
    private Stage stage;
    private KeyCode keyCode;
    private int mouseButton;
    private final FileChooser fileTriggerChooser = new FileChooser();
    private final FileChooser fileActionChooser = new FileChooser();
    private File fileTrigger;
    private File fileAction;

    public void initialize(){
        buttonMouseButton.addEventFilter(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            mouseButton = (int)Math.pow(2, keyEvent.getButton().ordinal() + 9);
            buttonMouseButton.setText(keyEvent.getButton().name());
        });

        textFieldKey.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            keyCode = keyEvent.getCode();
            textFieldKey.setText(keyEvent.getCode().getName());
        });
        fileTriggerChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All image files", "*.jpg", "*.jpeg", "*.jpe", "*.jfif", "*.png", "*.bmp", "*.dib"));
        fileTriggerChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg", "*.jpe", "*.jfif"));
        fileTriggerChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileTriggerChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bit map", "*.bmp", "*.dib"));

        fileActionChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All music files", "*.mp3", "*.wav"));
        fileActionChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        fileActionChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV", "*.wav"));
    }

    public void actionOk(){
        if(action.equals(Modes.KEYBOARD) && keyCode == null) {
            return;
        }
        try {
            int actionsNumber = Integer.parseInt(textFieldActionsNumber.getText());
            int actionsPeriod = Integer.parseInt(textFieldActionPeriod.getText());
            int periodRandomizer = Integer.parseInt(textFieldPeriodRandomizer.getText());
            actionField.setActionsNumber(actionsNumber);
            actionField.setActionPeriod(actionsPeriod);
            actionField.setPeriodRandomizer(periodRandomizer);
            if(trigger.equals(Modes.PIXEL_COLOR) || trigger.equals(Modes.PIXEL_COLOR_DIFF)){
                int colorPixelX = Integer.parseInt(textFieldColorPixelX.getText());
                int colorPixelY = Integer.parseInt(textFieldColorPixelY.getText());
                int colorDiff = Integer.parseInt(textFieldColorDiff.getText());
                actionField.setPointPixelColor(new java.awt.Point(colorPixelX, colorPixelY));
                actionField.setColorDiff(colorDiff);
            }
            if(trigger.equals(Modes.IMAGE_PRESENCE) || trigger.equals(Modes.IMAGE_ABSENCE)){
                int rectangleX1 = Integer.parseInt(textFieldX1.getText());
                int rectangleY1 = Integer.parseInt(textFieldY1.getText());
                int rectangleX2 = Integer.parseInt(textFieldX2.getText());
                int rectangleY2 = Integer.parseInt(textFieldY2.getText());
                actionField.setPointRectangle1(new java.awt.Point(rectangleX1, rectangleY1));
                actionField.setPointRectangle2(new java.awt.Point(rectangleX2, rectangleY2));
            }
            if(action.equals(Modes.MOUSE)){
                int mouseClickX = Integer.parseInt(textFieldMouseClickX.getText());
                int mouseClickY = Integer.parseInt(textFieldMouseClickY.getText());
                actionField.setPointMouseClick(new java.awt.Point(mouseClickX, mouseClickY));
            }
        } catch(NumberFormatException e){
            e.printStackTrace();
            return;
        }

        actionField.setKeyCode(keyCode);
        actionField.setMouseButton(mouseButton);
        actionField.setDesiredPixelColor(new java.awt.Color((float)colorPickerPixel.getValue().getRed(), (float)colorPickerPixel.getValue().getGreen(),
                (float)colorPickerPixel.getValue().getBlue(), (float)colorPickerPixel.getValue().getOpacity()));
        actionField.setFileAction(fileAction);
        actionField.setFileTrigger(fileTrigger);
        stage.close();
    }

    public void actionCancel(){
        stage.close();
    }

    public void actionChoosePixel(){
        getPixelDetails("pixel");
    }

    public void actionChoosePixelColor(){
        getPixelDetails("color");
    }

    public void actionSelectImageArea(){
        AtomicReference<Integer> pressX = new AtomicReference<>(0);
        AtomicReference<Integer> pressY = new AtomicReference<>(0);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setOpacity(0.3);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Pane pane = new Pane();
        try {
            int x1 = Integer.parseInt(textFieldX1.getText());
            int y1 = Integer.parseInt(textFieldY1.getText()) + 13;
            int x2 = Integer.parseInt(textFieldX2.getText());
            int y2 = Integer.parseInt(textFieldY2.getText()) + 13;

            pane.getChildren().clear();
            pane.getChildren().add(new Line(x1, y1, x1, y2));
            pane.getChildren().add(new Line(x1, y1, x2, y1));
            pane.getChildren().add(new Line(x1, y2, x2, y2));
            pane.getChildren().add(new Line(x2, y1, x2, y2));
        } catch(NumberFormatException ignored) {}
        Scene scene = new Scene(pane, dimension.width, dimension.height);
        scene.setCursor(Cursor.CROSSHAIR);
        stage.setScene(scene);
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, keyEvent -> {
            pressX.set((int)keyEvent.getScreenX());
            pressY.set((int)keyEvent.getSceneY());
            textFieldX1.setText(String.valueOf((int)keyEvent.getScreenX()));
            textFieldY1.setText(String.valueOf((int)keyEvent.getScreenY()));
        });
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, keyEvent -> {
            pane.getChildren().clear();
            pane.getChildren().add(new Line(pressX.get(), pressY.get(), (int)keyEvent.getScreenX(), pressY.get()));
            pane.getChildren().add(new Line(pressX.get(), pressY.get(), pressX.get(), (int)keyEvent.getSceneY()));
            pane.getChildren().add(new Line((int)keyEvent.getScreenX(), pressY.get(), (int)keyEvent.getScreenX(), (int)keyEvent.getSceneY()));
            pane.getChildren().add(new Line(pressX.get(), (int)keyEvent.getSceneY(), (int)keyEvent.getScreenX(), (int)keyEvent.getSceneY()));
            textFieldX2.setText(String.valueOf((int)keyEvent.getScreenX()));
            textFieldY2.setText(String.valueOf((int)keyEvent.getScreenY()));
        });
        stage.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            try {
                int x1 = Integer.parseInt(textFieldX1.getText());
                int y1 = Integer.parseInt(textFieldY1.getText());
                int x2 = Integer.parseInt(textFieldX2.getText());
                int y2 = Integer.parseInt(textFieldY2.getText());
                if(x1 > x2){
                    textFieldX1.setText(String.valueOf(x2));
                    textFieldX2.setText(String.valueOf(x1));
                }
                if(y1 > y2){
                    textFieldY1.setText(String.valueOf(y2));
                    textFieldY2.setText(String.valueOf(y1));
                }
            } catch(NumberFormatException ignored) {}
            KeyCode keyCode = keyEvent.getCode();
            if(keyCode.isWhitespaceKey() || keyCode.equals(KeyCode.ESCAPE)){
                stage.close();
            }
        });
        stage.show();
    }

    public void actionSelectFileTrigger(){
        File chosenFile;
        if((chosenFile = fileTriggerChooser.showOpenDialog(stage)) != null) {
            fileTrigger = chosenFile;
            fileTriggerChooser.setInitialDirectory(fileTrigger.toPath().getParent().toFile());
            buttonFileTrigger.setText(fileTrigger.getName());
        }
    }

    public void actionSelectFileAction(){
        File chosenFile;
        if((chosenFile = fileActionChooser.showOpenDialog(stage)) != null){
            fileAction = chosenFile;
            fileActionChooser.setInitialDirectory(fileAction.toPath().getParent().toFile());
            buttonFileAction.setText(fileAction.getName());
        }
    }

    public void actionSelectMouseClickPixel() {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setOpacity(0.3);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Pane pane = new Pane();
        Scene scene = new Scene(pane, dimension.width, dimension.height);
        scene.setCursor(Cursor.CROSSHAIR);
        stage.setScene(scene);
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            stage.close();
            textFieldMouseClickX.setText(String.valueOf((int)keyEvent.getScreenX()));
            textFieldMouseClickY.setText(String.valueOf((int)keyEvent.getScreenY()));
        });
        stage.show();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setActionField(ActionField actionField){
        this.actionField = actionField;
        this.textFieldActionsNumber.setText(String.valueOf(actionField.getActionsNumber()));
        this.textFieldActionPeriod.setText(String.valueOf(actionField.getActionPeriod()));
        this.textFieldPeriodRandomizer.setText(String.valueOf(actionField.getPeriodRandomizer()));
        this.colorPickerPixel.setValue(new Color((double)actionField.getDesiredPixelColor().getRed() / 255, (double)actionField.getDesiredPixelColor().getBlue() / 255,
                                                (double)actionField.getDesiredPixelColor().getBlue() / 255, (double)actionField.getDesiredPixelColor().getAlpha() / 255));
        this.textFieldColorDiff.setText(String.valueOf(actionField.getColorDiff()));
        this.mouseButton = actionField.getMouseButton();
        this.buttonMouseButton.setText(String.valueOf(mouseButton));
        if(actionField.getPointPixelColor() != null) {
            this.textFieldColorPixelX.setText(String.valueOf(actionField.getPointPixelColor().x));
            this.textFieldColorPixelY.setText(String.valueOf(actionField.getPointPixelColor().y));
        }
        if(actionField.getPointRectangle1() != null) {
            this.textFieldX1.setText(String.valueOf(actionField.getPointRectangle1().x));
            this.textFieldY1.setText(String.valueOf(actionField.getPointRectangle1().y));
        }
        if(actionField.getPointRectangle2() != null) {
            this.textFieldX2.setText(String.valueOf(actionField.getPointRectangle2().x));
            this.textFieldY2.setText(String.valueOf(actionField.getPointRectangle2().y));
        }
        if(actionField.getPointMouseClick() != null) {
            this.textFieldMouseClickX.setText(String.valueOf(actionField.getPointMouseClick().x));
            this.textFieldMouseClickY.setText(String.valueOf(actionField.getPointMouseClick().y));
        }
        if(actionField.getKeyCode() != null) {
            this.textFieldKey.setText(actionField.getKeyCode().getName());
            this.keyCode = actionField.getKeyCode();
        }
        if(actionField.getFileTrigger() != null){
            this.fileTrigger = actionField.getFileTrigger();
            this.buttonFileTrigger.setText(fileTrigger.getName());
            fileTriggerChooser.setInitialDirectory(fileTrigger.toPath().getParent().toFile());
        }
        if(actionField.getFileAction() != null){
            this.fileAction = actionField.getFileAction();
            this.buttonFileAction.setText(fileAction.getName());
            fileActionChooser.setInitialDirectory(fileAction.toPath().getParent().toFile());
        }
    }

    public void selectMode(String action, String trigger){
        this.action = action;
        this.trigger = trigger;
        anchorPanePixelColor.setVisible(false);
        anchorPaneImageSelector.setVisible(false);
        anchorPaneMouse.setVisible(false);
        anchorPaneKeyboard.setVisible(false);
        anchorPaneSound.setVisible(false);

        switch(action){
            case Modes.MOUSE -> {
                anchorPaneMouse.setVisible(true);
            }
            case Modes.KEYBOARD -> {
                anchorPaneKeyboard.setVisible(true);
            }
            case Modes.SOUND -> {
                anchorPaneSound.setVisible(true);
            }
            case Modes.SHUTDOWN -> {

            }
        }

        switch(trigger){
            case Modes.TIME -> {

            }
            case Modes.PIXEL_COLOR, Modes.PIXEL_COLOR_DIFF -> {
                anchorPanePixelColor.setVisible(true);
            }
            case Modes.IMAGE_PRESENCE, Modes.IMAGE_ABSENCE -> {
                anchorPaneImageSelector.setVisible(true);
            }
        }
    }

    private void getPixelDetails(String mode){
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setOpacity(0.004);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Pane pane = new Pane();
        Scene scene = new Scene(pane, dimension.width, dimension.height);
        scene.setCursor(Cursor.CROSSHAIR);
        stage.setScene(scene);
        stage.addEventFilter(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            stage.close();
            boolean doBoth = checkboxPixelColor.isSelected();
            if(mode.equals("color") || doBoth) {
                Robot robot = new Robot();
                Color color = robot.getPixelColor((int)keyEvent.getScreenX(), (int)keyEvent.getScreenY());
                colorPickerPixel.setValue(color);
            }
            if(mode.equals("pixel") || doBoth) {
                textFieldColorPixelX.setText(String.valueOf((int)keyEvent.getScreenX()));
                textFieldColorPixelY.setText(String.valueOf((int)keyEvent.getScreenY()));
            }
        });
        stage.show();
    }
}
