package data;

import controllers.ConfigurationWindowController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
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
    private MouseButton mouseButton;
    private final Point pointPixelColor = new Point();
    private final Point pointRectangle1 = new Point();
    private final Point pointRectangle2 = new Point();
    private final SerializableColor desiredPixelColor = new SerializableColor();
    private final SerializableColor currentPixelColor = new SerializableColor();
    private double minColorR;
    private double maxColorR;
    private double minColorG;
    private double maxColorG;
    private double minColorB;
    private double maxColorB;
    private int colorDiff;
    private File directoryImages;
    private File selectedImage;
    private final Point pointMouseClick = new Point();
    private final Point previousMousePosition = new Point();
    private transient Robot robot;
    private transient Thread lifeCycle;
    private boolean alive;
    private BufferedImage imageToSearch;
    private String mode;
    private boolean mouseMode;
    private boolean keyboardMode;
    private boolean colorMode;
    private boolean imageMode;
    private boolean reverseMode;

    private final List<String> actions = Arrays.asList("Klik myszą", "Klik klawiaturą");
    private final List<String> triggers = Arrays.asList("Czas", "Kolor piksela", "Kolor piksela różny od", "Obecność obrazu w polu", "Brak obecności obrazu w polu");

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
        if(mode != null)comboBoxAction.getSelectionModel().select(mode.split("\\|")[0]);

        comboBoxTrigger = new ComboBox<>();
        comboBoxTrigger.setLayoutX(119.0);
        comboBoxTrigger.setLayoutY(10.0);
        comboBoxTrigger.setPrefWidth(95.0);
        comboBoxTrigger.setPromptText("Trigger");
        AnchorPane.setLeftAnchor(comboBoxTrigger, 110.0);
        AnchorPane.setTopAnchor(comboBoxTrigger, 10.0);
        ObservableList<String> comboBoxTriggerItems = comboBoxTrigger.getItems();
        comboBoxTriggerItems.addAll(triggers);
        if(mode != null)comboBoxTrigger.getSelectionModel().select(mode.split("\\|")[1]);

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

        robot = new Robot();
    }

    private void configureAction(){
        action = comboBoxAction.getSelectionModel().getSelectedItem();
        trigger = comboBoxTrigger.getSelectionModel().getSelectedItem();
        if(action == null || trigger == null){
            textAreaDescription.setText("Może najpierw wybierz akcje i trigger tępaku jebany :/");
            return;
        }

        mode = action + "|" + trigger;
        mouseMode = false;
        keyboardMode = false;
        colorMode = false;
        imageMode = false;
        reverseMode = false;

        if(actionsNumber == 0) actionsNumber = Integer.MAX_VALUE;

        switch (mode) {
            case "Klik myszą|Czas" -> {
                mouseMode = true;
            }
            case "Klik myszą|Kolor piksela" -> {
                mouseMode = true;
                colorMode = true;
            }
            case "Klik myszą|Kolor piksela różny od" -> {
                mouseMode = true;
                colorMode = true;
                reverseMode = true;
            }
            case "Klik myszą|Obecność obrazu w polu" -> {
                mouseMode = true;
                imageMode = true;
            }
            case "Klik myszą|Brak obecności obrazu w polu" -> {
                mouseMode = true;
                imageMode = true;
                reverseMode = true;
            }
            case "Klik klawiaturą|Czas" -> {
                keyboardMode = true;
            }
            case "Klik klawiaturą|Kolor piksela" -> {
                keyboardMode = true;
                colorMode = true;
            }
            case "Klik klawiaturą|Kolor piksela różny od" -> {
                keyboardMode = true;
                colorMode = true;
                reverseMode = true;
            }
            case "Klik klawiaturą|Obecność obrazu w polu" -> {
                keyboardMode = true;
                imageMode = true;
            }
            case "Klik klawiaturą|Brak obecności obrazu w polu" -> {
                keyboardMode = true;
                imageMode = true;
                reverseMode = true;
            }
        }
        loadConfigurationPanel(mode);
    }

    public void configureThread(){
        switch (mode) {
            case "Klik myszą|Czas"                              -> configureMouseTime();
            case "Klik myszą|Kolor piksela"                     -> configureMousePixelEqualColor();
            case "Klik myszą|Kolor piksela różny od"            -> configureMousePixelUnequalColor();
            case "Klik myszą|Obecność obrazu w polu"            -> configureMouseImagePresence();
            case "Klik myszą|Brak obecności obrazu w polu"      -> configureMouseImageAbsence();
            case "Klik klawiaturą|Czas"                         -> configureKeyboardTime();
            case "Klik klawiaturą|Kolor piksela"                -> configureKeyboardPixelEqualColor();
            case "Klik klawiaturą|Kolor piksela różny od"       -> configureKeyboardPixelUnequalColor();
            case "Klik klawiaturą|Obecność obrazu w polu"       -> configureKeyboardImagePresence();
            case "Klik klawiaturą|Brak obecności obrazu w polu" -> configureKeyboardImageAbsence();
        }
    }

    private void configureMouseTime() {
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                Platform.runLater(() -> {
                    previousMousePosition.setPoint(robot.getMousePosition());
                    robot.mouseMove(pointMouseClick.getPoint());
                    robot.mouseClick(mouseButton);
                    robot.mouseMove(previousMousePosition.getPoint());
                });
                waitForNextAction();
            }
        });
    }

    private void configureMousePixelEqualColor() {
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                currentPixelColor.setColor(robot.getPixelColor(pointPixelColor.getPoint()));
                if(colorsWithDiffEqual()) {
                    Platform.runLater(() -> {
                        previousMousePosition.setPoint(robot.getMousePosition());
                        robot.mouseMove(pointMouseClick.getPoint());
                        robot.mouseClick(mouseButton);
                        robot.mouseMove(previousMousePosition.getPoint());
                    });
                }
                waitForNextAction();
            }
        });
    }

    private void configureMousePixelUnequalColor() {
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                currentPixelColor.setColor(robot.getPixelColor(pointPixelColor.getPoint()));
                if(!colorsWithDiffEqual()) {
                    Platform.runLater(() -> {
                        previousMousePosition.setPoint(robot.getMousePosition());
                        robot.mouseMove(pointMouseClick.getPoint());
                        robot.mouseClick(mouseButton);
                        robot.mouseMove(previousMousePosition.getPoint());
                    });
                }
                waitForNextAction();
            }
        });
    }

    private void configureMouseImagePresence() {
        lifeCycle = new Thread(() -> {
            try {
                imageToSearch = ImageIO.read(selectedImage);
                for (int h = 0; h < actionsNumber && alive; h++) {
                    if(findImage()){
                        Platform.runLater(() -> {
                            previousMousePosition.setPoint(robot.getMousePosition());
                            robot.mouseMove(pointMouseClick.getPoint());
                            robot.mouseClick(mouseButton);
                            robot.mouseMove(previousMousePosition.getPoint());

                        });
                    }
                    waitForNextAction();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureMouseImageAbsence() {
        lifeCycle = new Thread(() -> {
            try {
                imageToSearch = ImageIO.read(selectedImage);
                for (int h = 0; h < actionsNumber && alive; h++) {
                    Platform.runLater(() -> {
                        if(!findImage()){
                            previousMousePosition.setPoint(robot.getMousePosition());
                            robot.mouseMove(pointMouseClick.getPoint());
                            robot.mouseClick(mouseButton);
                            robot.mouseMove(previousMousePosition.getPoint());
                        } else{
                        }
                    });

                    waitForNextAction();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureKeyboardTime() {
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                Platform.runLater(() -> {
                    robot.keyPress(keyCode);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    robot.keyRelease(keyCode);
                });
                waitForNextAction();
            }
        });
    }

    private void configureKeyboardPixelEqualColor() {
        lifeCycle = new Thread(() -> {
            calculateColors();
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                Platform.runLater(() -> {
                    currentPixelColor.setColor(robot.getPixelColor(pointPixelColor.getPoint()));
                    if(colorsWithDiffEqual()) {
                        robot.keyPress(keyCode);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.keyRelease(keyCode);
                    }
                });
                waitForNextAction();
            }
        });
    }

    private void configureKeyboardPixelUnequalColor() {
        lifeCycle = new Thread(() -> {
            calculateColors();
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                Platform.runLater(() -> {
                    currentPixelColor.setColor(robot.getPixelColor(pointPixelColor.getPoint()));
                    if(!colorsWithDiffEqual()) {
                        robot.keyPress(keyCode);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.keyRelease(keyCode);
                    }
                });
                waitForNextAction();
            }
        });
    }

    private void configureKeyboardImagePresence() {
        lifeCycle = new Thread(() -> {
            try {
                imageToSearch = ImageIO.read(selectedImage);
                for (int h = 0; h < actionsNumber && alive; h++) {
                    if(findImage()){
                        Platform.runLater(() -> {
                            robot.keyPress(keyCode);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            robot.keyRelease(keyCode);
                        });
                    }
                    waitForNextAction();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void configureKeyboardImageAbsence() {
        lifeCycle = new Thread(() -> {
            try {
                imageToSearch = ImageIO.read(selectedImage);
                for (int h = 0; h < actionsNumber && alive; h++) {
                    Platform.runLater(() -> {
                        if(!findImage()){
                            robot.keyPress(keyCode);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            robot.keyRelease(keyCode);
                        }
                    });
                    waitForNextAction();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void runMouseTime() {
    }

    private void runMousePixelColor() {
    }

    private void runMouseImagePresence() {
    }

    private void runMouseImageAbsence() {
    }

    private void runKeyboardTime() {
    }

    private void runKeyboardPixelColor() {
    }

    private void runKeyboardImagePresence() {
    }

    private void runKeyboardImageAbsence() {
    }

    private boolean findImage(){
        int firstPixelValue = imageToSearch.getRGB(0, 0);
        int imageToSearchWidth = imageToSearch.getWidth();
        int imageToSearchHeight = imageToSearch.getHeight();
        double areaWidth = pointRectangle2.getX() - pointRectangle1.getX();
        double areaHeight = pointRectangle2.getY() - pointRectangle1.getY();
        int numberOfPixelsToFind = imageToSearch.getWidth() * imageToSearch.getHeight();
        WritableImage screenshot = robot.getScreenCapture(null, pointRectangle1.getX(), pointRectangle1.getY(), areaWidth, areaHeight);
        BufferedImage areaToSearchIn = SwingFXUtils.fromFXImage(screenshot, null);

        for(int i = 0; i < areaToSearchIn.getWidth(); i++){
            for(int j = 0; j < areaToSearchIn.getHeight(); j++){
                int imageRgb = areaToSearchIn.getRGB(i, j);
                if(imageRgb == firstPixelValue) {
                    boolean keepSearching = true;
                    int numberOfFoundPixels = 0;
                    for (int k = 0; k < imageToSearchWidth && keepSearching; k++) {
                        for (int l = 0; l < imageToSearchHeight && keepSearching; l++) {
                            if (i + k < areaWidth && j + l < areaHeight && imageToSearch.getRGB(k, l) == areaToSearchIn.getRGB(i + k, j + l)) {
                                numberOfFoundPixels++;
                            } else {
                                keepSearching = false;
                            }
                        }
                    }
                    if(numberOfFoundPixels == numberOfPixelsToFind){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void loadConfigurationPanel(String mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfigurationWindow.fxml"));
            Parent root = loader.load();
            ConfigurationWindowController controller = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 300, 250));
            controller.setStage(stage);
            controller.setActionField(this);
            controller.selectMode(mode);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            prepareDescription();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void start() {
        this.alive = true;

        if (checkboxActive.isSelected()) {
            System.out.println(textAreaDescription.getText());
            lifeCycle.start();
        }
    }

    private void prepareDescription(){
        String description = "Co: ";

        if(mouseMode){
            description += mouseButton.name() + " BUTTON myszki. Gdzie: w miejscu (" + pointMouseClick.getX() + "," + pointMouseClick.getY() + ")\n";
        } else if(keyboardMode){
            description += keyCode.name() + " na klawiaturze.\n";
        }
        if(colorMode){
            description += "Kiedy: gdy piksel (" + pointPixelColor.getX() + ", " +pointPixelColor.getY() + ") będzie miał kolor";
            if(reverseMode) description += " inny niż";
            description += " " + desiredPixelColor.getColorDescription() + " +/- " + colorDiff + ".\n";
        } else if(imageMode){
            description += "Kiedy: gdy w obszarze prostokąta między (" + pointRectangle1.getX() + ", " + pointRectangle1.getY() +
                    ") a (" + pointRectangle2.getX() + ", " + pointRectangle2.getY() + ")";
            if(reverseMode) description += " nie";
            description += " znajdzie się obraz zapisany pod ścieżką " + selectedImage.getPath() + ".\n";
        }
        description += "Akcja zostanie wykonana co " + actionPeriod + " +/- " + periodRandomizer + "ms, " + actionsNumber + " razy.";

        this.description = description;
        textAreaDescription.setText(description);
    }

    private boolean colorsWithDiffEqual(){
        if(!(currentPixelColor.getRed() <= maxColorR && currentPixelColor.getRed() >= minColorR)) return false;
        if(!(currentPixelColor.getGreen() <= maxColorG && currentPixelColor.getGreen() >= minColorG)) return false;
        return currentPixelColor.getBlue() <= maxColorB && currentPixelColor.getBlue() >= minColorB;
    }

    private void calculateColors(){
        minColorR = desiredPixelColor.getRed() - colorDiff * 0.01;
        maxColorR = desiredPixelColor.getRed() + colorDiff * 0.01;
        minColorG = desiredPixelColor.getGreen() - colorDiff * 0.01;
        maxColorG = desiredPixelColor.getGreen() + colorDiff * 0.01;
        minColorB = desiredPixelColor.getBlue() - colorDiff * 0.01;
        maxColorB = desiredPixelColor.getBlue() + colorDiff * 0.01;
    }

    private void waitForNextAction(){
        try {
            for(int k = 0; k < getActionPeriod(); k = k + 10){
                Thread.sleep(10);
                if(!alive) break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kill(){
        this.alive = false;
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

    public MouseButton getMouseButton() {
        return mouseButton;
    }

    public void setMouseButton(MouseButton mouseButton) {
        this.mouseButton = mouseButton;
    }

    public Point2D getPointPixelColor() {
        return pointPixelColor.getPoint();
    }

    public void setPointPixelColor(Point2D pointPixelColor) {
        this.pointPixelColor.setPoint(pointPixelColor);
    }

    public Color getDesiredPixelColor() {
        return desiredPixelColor.getColor();
    }

    public void setDesiredPixelColor(Color desiredPixelColor) {
        this.desiredPixelColor.setColor(desiredPixelColor);
    }

    public Point2D getPointRectangle1() {
        return pointRectangle1.getPoint();
    }

    public void setPointRectangle1(Point2D pointRectangle1) {
        this.pointRectangle1.setPoint(pointRectangle1);
    }

    public Point2D getPointRectangle2() {
        return pointRectangle2.getPoint();
    }

    public void setPointRectangle2(Point2D pointRectangle2) {
        this.pointRectangle2.setPoint(pointRectangle2);
    }

    public File getDirectoryImages() {
        return directoryImages;
    }

    public void setDirectoryImages(File directoryImages) {
        this.directoryImages = directoryImages;
    }

    public File getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(File selectedImage) {
        this.selectedImage = selectedImage;
    }

    public int getColorDiff() {
        return colorDiff;
    }

    public void setColorDiff(int colorDiff) {
        this.colorDiff = colorDiff;
    }

    public Point2D getPointMouseClick() {
        return pointMouseClick.getPoint();
    }

    public void setPointMouseClick(Point2D pointMouseClick) {
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
