package data;

import controllers.ConfigurationWindowController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class ActionField implements Serializable {
    private transient AnchorPane anchorPane;
    private transient TextArea textAreaDescription;
    private transient CheckBox checkboxActive;
    private transient ComboBox<String> comboBoxAction;
    private transient ComboBox<String> comboBoxTrigger;
    private transient Button buttonConfigure;
    private transient Line line;

    private int actionsNumber = 0;
    private int actionPeriod = 1000;
    private int periodRandomizer = 0;
    private String action;
    private String trigger;
    private String description;
    private KeyCode keyCode;
    private int mouseButton;
    private final Point pointPixelColor = new Point();
    private final Point pointRectangle1 = new Point();
    private final Point pointRectangle2 = new Point();
    private final SerializableColor desiredPixelColor = new SerializableColor();
    private int colorDiff;
    private File fileTrigger;
    private File fileAction;
    private final Point pointMouseClick = new Point();
    private final Point previousMousePosition = new Point();
    private transient ActionService actionService;

    private final List<String> actions = Modes.actions;
    private final List<String> triggers = Modes.triggers;

    public ActionField(){
        prepareControl();
    }

    public void prepareControl(){
        anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(160.0);
        anchorPane.setPrefWidth(400.0);

        textAreaDescription = new TextArea();
        textAreaDescription.setEditable(false);
        textAreaDescription.setLayoutX(100.0);
        textAreaDescription.setLayoutY(19.0);
        textAreaDescription.setPrefHeight(100.0);
        textAreaDescription.setPrefWidth(380.0);
        textAreaDescription.setWrapText(true);
        AnchorPane.setLeftAnchor(textAreaDescription, 10.0);
        AnchorPane.setTopAnchor(textAreaDescription, 45.0);
        textAreaDescription.setText(description);

        checkboxActive = new CheckBox();
        checkboxActive.setLayoutX(316.0);
        checkboxActive.setLayoutY(14.0);
        checkboxActive.setMnemonicParsing(false);
        checkboxActive.setText("Uruchom");
        AnchorPane.setLeftAnchor(checkboxActive, 315.0);
        AnchorPane.setTopAnchor(checkboxActive, 15.0);

        comboBoxAction = new ComboBox<>();
        comboBoxAction.setLayoutX(25.0);
        comboBoxAction.setLayoutY(14.0);
        comboBoxAction.setPrefWidth(95.0);
        comboBoxAction.setPromptText("Akcja");
        AnchorPane.setLeftAnchor(comboBoxAction, 10.0);
        AnchorPane.setTopAnchor(comboBoxAction, 10.0);
        ObservableList<String> comboBoxActionItems = comboBoxAction.getItems();
        comboBoxActionItems.addAll(actions);
        comboBoxAction.getSelectionModel().select(action);

        comboBoxTrigger = new ComboBox<>();
        comboBoxTrigger.setLayoutX(119.0);
        comboBoxTrigger.setLayoutY(10.0);
        comboBoxTrigger.setPrefWidth(95.0);
        comboBoxTrigger.setPromptText("Trigger");
        AnchorPane.setLeftAnchor(comboBoxTrigger, 110.0);
        AnchorPane.setTopAnchor(comboBoxTrigger, 10.0);
        ObservableList<String> comboBoxTriggerItems = comboBoxTrigger.getItems();
        comboBoxTriggerItems.addAll(triggers);
        comboBoxTrigger.getSelectionModel().select(trigger);

        buttonConfigure = new Button();
        buttonConfigure.setLayoutX(210.0);
        buttonConfigure.setLayoutY(10.0);
        buttonConfigure.setMnemonicParsing(false);
        buttonConfigure.setPrefWidth(95.0);
        buttonConfigure.setText("Konfiguruj");
        AnchorPane.setLeftAnchor(buttonConfigure, 210.0);
        AnchorPane.setTopAnchor(buttonConfigure, 10.0);
        buttonConfigure.setOnAction(actionEvent -> configureAction());

        line = new Line();
        line.setEndX(400.0);
        line.setLayoutX(145.0);
        line.setLayoutY(140.0);
        AnchorPane.setLeftAnchor(line, 0.0);
        AnchorPane.setTopAnchor(line, 160.0);

        ObservableList<Node> children = anchorPane.getChildren();
        children.add(textAreaDescription);
        children.add(checkboxActive);
        children.add(comboBoxAction);
        children.add(comboBoxTrigger);
        children.add(buttonConfigure);
        children.add(line);
    }

    private void configureAction(){
        action = comboBoxAction.getSelectionModel().getSelectedItem();
        trigger = comboBoxTrigger.getSelectionModel().getSelectedItem();
        if(action == null || trigger == null){
            textAreaDescription.setText("Może najpierw wybierz akcje i trigger tępaku jebany :/");
            return;
        }

        if(actionsNumber == 0) actionsNumber = Integer.MAX_VALUE;

        loadConfigurationPanel();
    }

    private void loadConfigurationPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfigurationWindow.fxml"));
            Parent root = loader.load();
            ConfigurationWindowController controller = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 300, 250));
            controller.setStage(stage);
            controller.setActionField(this);
            controller.selectMode(action, trigger);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            prepareDescription();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void recreateService(){
        ActionServiceBuilder builder = new ActionServiceBuilder();
        actionService = builder.setAction(action)
                .setTrigger(trigger)
                .setActionsNumber(actionsNumber)
                .setActionPeriod(actionPeriod)
                .setFileAction(fileAction)
                .setFileTrigger(fileTrigger)
                .setKeyCode(keyCode)
                .setMouseButton(mouseButton)
                .setPointPixelColor(pointPixelColor)
                .setPointRectangle1(pointRectangle1)
                .setPointRectangle2(pointRectangle2)
                .setPointMouseClick(pointMouseClick)
                .setPreviousMousePosition(previousMousePosition)
                .setDesiredPixelColor(desiredPixelColor)
                .setColorDiff(colorDiff)
                .build();
    }

    public void start() {
        if (checkboxActive.isSelected()) {
            actionService.start();
        }
    }

    private void prepareDescription(){
        String description = "Co: ";

        if(action.equals(Modes.MOUSE)){
            description += mouseButton + " BUTTON myszki. Gdzie: w miejscu (" + pointMouseClick.getX() + "," + pointMouseClick.getY() + ")\n";
        } else if(action.equals(Modes.KEYBOARD)){
            description += keyCode.name() + " na klawiaturze.\n";
        } else if(action.equals(Modes.SOUND)){
            description += "dźwięk zapisany pod ścieżką ";// + sele
        }
        if(trigger.equals(Modes.PIXEL_COLOR) || trigger.equals(Modes.PIXEL_COLOR_DIFF)){
            description += "Kiedy: gdy piksel (" + pointPixelColor.getX() + ", " +pointPixelColor.getY() + ") będzie miał kolor";
            if(trigger.equals(Modes.PIXEL_COLOR_DIFF)) description += " inny niż";
            description += " " + desiredPixelColor.getColorDescription() + " +/- " + colorDiff + ".\n";
        } else if(trigger.equals(Modes.IMAGE_PRESENCE) || trigger.equals(Modes.IMAGE_ABSENCE)){
            description += "Kiedy: gdy w obszarze prostokąta między (" + pointRectangle1.getX() + ", " + pointRectangle1.getY() +
                    ") a (" + pointRectangle2.getX() + ", " + pointRectangle2.getY() + ")";
            if(trigger.equals(Modes.IMAGE_ABSENCE)) description += " nie";
            description += " znajdzie się obraz zapisany pod ścieżką " + fileTrigger.getPath() + ".\n";
        }
        description += "Akcja zostanie wykonana co " + actionPeriod + " +/- " + periodRandomizer + "ms, " + actionsNumber + " razy.";

        this.description = description;
        textAreaDescription.setText(description);
    }

    public void kill(){
        actionService.kill();
    }

    public AnchorPane getControl() {
        return anchorPane;
    }

    public int getActionsNumber() {
        return actionsNumber;
    }

    public void setActionsNumber(int actionsNumber) {
        this.actionsNumber = actionsNumber;
    }

    public int getActionPeriod() {
        return actionPeriod;
    }

    public void setActionPeriod(int actionPeriod) {
        this.actionPeriod = actionPeriod;
    }

    public int getPeriodRandomizer() {
        return periodRandomizer;
    }

    public void setPeriodRandomizer(int periodRandomizer) {
        this.periodRandomizer = periodRandomizer;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(KeyCode keyCode) {
        this.keyCode = keyCode;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public void setMouseButton(int mouseButton) {
        this.mouseButton = mouseButton;
    }

    public java.awt.Point getPointPixelColor() {
        return pointPixelColor.getPoint();
    }

    public void setPointPixelColor(java.awt.Point pointPixelColor) {
        this.pointPixelColor.setPoint(pointPixelColor);
    }

    public Color getDesiredPixelColor() {
        return desiredPixelColor.getColor();
    }

    public void setDesiredPixelColor(Color desiredPixelColor) {
        this.desiredPixelColor.setColor(desiredPixelColor);
    }

    public java.awt.Point getPointRectangle1() {
        return pointRectangle1.getPoint();
    }

    public void setPointRectangle1(java.awt.Point pointRectangle1) {
        this.pointRectangle1.setPoint(pointRectangle1);
    }

    public java.awt.Point getPointRectangle2() {
        return pointRectangle2.getPoint();
    }

    public void setPointRectangle2(java.awt.Point pointRectangle2) {
        this.pointRectangle2.setPoint(pointRectangle2);
    }

    public File getFileAction() {
        return fileAction;
    }

    public void setFileAction(File fileAction) {
        this.fileAction = fileAction;
    }

    public File getFileTrigger() {
        return fileTrigger;
    }

    public void setFileTrigger(File fileTrigger) {
        this.fileTrigger = fileTrigger;
    }

    public int getColorDiff() {
        return colorDiff;
    }

    public void setColorDiff(int colorDiff) {
        this.colorDiff = colorDiff;
    }

    public java.awt.Point getPointMouseClick() {
        return pointMouseClick.getPoint();
    }

    public void setPointMouseClick(java.awt.Point pointMouseClick) {
        this.pointMouseClick.setPoint(pointMouseClick);
    }

    public CheckBox getCheckboxActive() {
        return checkboxActive;
    }

    public ComboBox<String> getComboBoxAction() {
        return comboBoxAction;
    }

    public ComboBox<String> getComboBoxTrigger() {
        return comboBoxTrigger;
    }

    public Button getButtonConfigure() {
        return buttonConfigure;
    }
}
