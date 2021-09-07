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
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ActionField implements Serializable {
    private AnchorPane anchorPane;
    private TextArea textAreaDescription;
    private CheckBox checkboxActive;
    private ComboBox<String> comboBoxAction;
    private ComboBox<String> comboBoxTrigger;
    private Button buttonConfigure;
    private Line line;

    private int actionsNumber = 0;
    private int actionPeriod = 1000;
    private int periodRandomizer = 0;
    private KeyCode keyCode;
    private MouseButton mouseButton;
    private Point2D pointPixelColor;
    private Point2D pointRectangle1;
    private Point2D pointRectangle2;
    private Color desiredPixelColor;
    private Color currentPixelColor;
    private double minColorR;
    private double maxColorR;
    private double minColorG;
    private double maxColorG;
    private double minColorB;
    private double maxColorB;
    private int colorDiff;
    private File directoryImages;
    private File selectedImage;
    private Point2D pointMouseClick;

    private Point2D previousMousePosition;
    private final Robot robot = new Robot();
    private Thread lifeCycle;
    private boolean alive;
    private BufferedImage imageToSearch;
    private boolean mouseMode;
    private boolean keyboardMode;
    private boolean colorMode;
    private boolean imageMode;
    private boolean reverseMode;

    private final List<String> actions = Arrays.asList("Klik myszą", "Klik klawiaturą");
    private final List<String> triggers = Arrays.asList("Czas", "Kolor piksela", "Kolor piksela różny od", "Obecność obrazu w polu", "Brak obecności obrazu w polu");

    public ActionField(){
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

        comboBoxTrigger = new ComboBox<>();
        comboBoxTrigger.setLayoutX(119.0);
        comboBoxTrigger.setLayoutY(10.0);
        comboBoxTrigger.setPrefWidth(95.0);
        comboBoxTrigger.setPromptText("Trigger");
        AnchorPane.setLeftAnchor(comboBoxTrigger, 110.0);
        AnchorPane.setTopAnchor(comboBoxTrigger, 10.0);
        ObservableList<String> comboBoxTriggerItems = comboBoxTrigger.getItems();
        comboBoxTriggerItems.addAll(triggers);

        buttonConfigure = new Button();
        buttonConfigure.setLayoutX(210.0);
        buttonConfigure.setLayoutY(10.0);
        buttonConfigure.setMnemonicParsing(false);
        buttonConfigure.setPrefWidth(95.0);
        buttonConfigure.setText("Konfiguruj");
        AnchorPane.setLeftAnchor(buttonConfigure, 210.0);
        AnchorPane.setTopAnchor(buttonConfigure, 10.0);
        buttonConfigure.setOnAction(actionEvent -> configureAction(comboBoxAction.getSelectionModel().getSelectedItem(), comboBoxTrigger.getSelectionModel().getSelectedItem()));

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

    private void configureAction(String action, String trigger){
        if(action == null || trigger == null){
            textAreaDescription.setText("Może najpierw wybierz akcje i trigger tępaku jebany :/");
            return;
        }

        String mode = action + "|" + trigger;
        mouseMode = false;
        keyboardMode = false;
        colorMode = false;
        imageMode = false;
        reverseMode = false;

        if(actionsNumber == 0) actionsNumber = Integer.MAX_VALUE;

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
        loadConfigurationPanel(mode);
    }

    private void configureMouseTime() {
        mouseMode = true;
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                Platform.runLater(() -> {
                    previousMousePosition = robot.getMousePosition();
                    robot.mouseMove(pointMouseClick);
                    robot.mouseClick(mouseButton);
                    robot.mouseMove(previousMousePosition);
                });
                waitForNextAction();
            }
        });
    }

    private void configureMousePixelEqualColor() {
        mouseMode = true;
        colorMode = true;
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                currentPixelColor = robot.getPixelColor(pointPixelColor);
                if(colorsWithDiffEqual()) {
                    Platform.runLater(() -> {
                        previousMousePosition = robot.getMousePosition();
                        robot.mouseMove(pointMouseClick);
                        robot.mouseClick(mouseButton);
                        robot.mouseMove(previousMousePosition);
                    });
                }
                waitForNextAction();
            }
        });
    }

    private void configureMousePixelUnequalColor() {
        mouseMode = true;
        colorMode = true;
        reverseMode = true;
        lifeCycle = new Thread(() -> {
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                currentPixelColor = robot.getPixelColor(pointPixelColor);
                if(!colorsWithDiffEqual()) {
                    Platform.runLater(() -> {
                        previousMousePosition = robot.getMousePosition();
                        robot.mouseMove(pointMouseClick);
                        robot.mouseClick(mouseButton);
                        robot.mouseMove(previousMousePosition);
                    });
                }
                waitForNextAction();
            }
        });
    }

    private void configureMouseImagePresence() {
        mouseMode = true;
        imageMode = true;
        lifeCycle = new Thread(() -> {
            try {
                imageToSearch = ImageIO.read(selectedImage);
                for (int h = 0; h < actionsNumber && alive; h++) {
                    if(findImage()){
                        Platform.runLater(() -> {
                            previousMousePosition = robot.getMousePosition();
                            robot.mouseMove(pointMouseClick);
                            robot.mouseClick(mouseButton);
                            robot.mouseMove(previousMousePosition);

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
        mouseMode = true;
        imageMode = true;
        reverseMode = true;
        lifeCycle = new Thread(() -> {
            try {
                imageToSearch = ImageIO.read(selectedImage);
                for (int h = 0; h < actionsNumber && alive; h++) {
                    Platform.runLater(() -> {
                        if(!findImage()){
                            previousMousePosition = robot.getMousePosition();
                            robot.mouseMove(pointMouseClick);
                            robot.mouseClick(mouseButton);
                            robot.mouseMove(previousMousePosition);
                            System.out.println("mouse image absence click");
                        } else{
                            System.out.println("mouse image absence no click");
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
        keyboardMode = true;
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
        System.out.println("keyboard pixel color equal conf");
        keyboardMode = true;
        colorMode = true;
        lifeCycle = new Thread(() -> {
            System.out.println("keyboard pixel color equal starts");
            calculateColors();
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                Platform.runLater(() -> {
                    currentPixelColor = robot.getPixelColor(pointPixelColor);
                    if(colorsWithDiffEqual()) {
                        robot.keyPress(keyCode);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.keyRelease(keyCode);
                        System.out.println("keyboard pixel color equal click");
                    } else {
                        System.out.println("keyboard color equal no click");
                    }
                });
                waitForNextAction();
            }
        });
    }

    private void configureKeyboardPixelUnequalColor() {
        System.out.println("keyboard pixel color unequal conf");
        keyboardMode = true;
        colorMode = true;
        reverseMode = true;
        lifeCycle = new Thread(() -> {
            System.out.println("keyboard pixel color unequal starts " + getActionsNumber() + " " + alive);
            calculateColors();
            for (int j = 0; j < getActionsNumber() && alive; j++) {
                System.out.println("inside keyboard pixel color unequal loop " + j);
                Platform.runLater(() -> {
                    currentPixelColor = robot.getPixelColor(pointPixelColor);
                    if(!colorsWithDiffEqual()) {
                        robot.keyPress(keyCode);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        robot.keyRelease(keyCode);
                        System.out.println("keyboard pixel color unequal click");
                    } else {
                        System.out.println("keyboard color unequal no click");
                    }
                });
                waitForNextAction();
            }
        });
    }

    private void configureKeyboardImagePresence() {
        keyboardMode = true;
        imageMode = true;
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
        keyboardMode = true;
        imageMode = true;
        reverseMode = true;
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
//        description += " będzie klikany co " + textFieldActionPeriod.getText() + " +/-" + textFieldPeriodRandomizer.getText() + "ms," +
//                (textFieldActionsNumber.getText().equals("0") ? " nieskonczenie wiele" : textFieldActionsNumber.getText()) +
//                " razy.\n";
//
        if(colorMode){
            description += "Kiedy: gdy piksel (" + pointPixelColor.getX() + ", " +pointPixelColor.getY() + ") będzie miał kolor";
            if(reverseMode) description += " inny niż";
            description += desiredPixelColor + " +/- " + colorDiff;
        } else if(imageMode){
            description += "Kiedy: gdy w obszarze prostokąta między (" + pointRectangle1.getX() + ", " + pointRectangle1.getY() +
                    ") a (" + pointRectangle2.getX() + ", " + pointRectangle2.getY() + ")";
            if(reverseMode) description += " nie";
            description += " znajdzie się obraz zapisany pod ścieżką " + selectedImage.getPath() + ".\n";
        }
        description += "Akcja zostanie wykonana co " + actionPeriod + " +/- " + periodRandomizer + "ms, " + actionsNumber + " razy.";

        textAreaDescription.setText(description);
    }

    private boolean colorsWithDiffEqual(){
        if(!(currentPixelColor.getRed() < maxColorR && currentPixelColor.getRed() > minColorR)) return false;
        if(!(currentPixelColor.getGreen() < maxColorG && currentPixelColor.getGreen() > minColorG)) return false;
        return currentPixelColor.getBlue() < maxColorB && currentPixelColor.getBlue() > minColorB;
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
        return pointPixelColor;
    }

    public void setPointPixelColor(Point2D pointPixelColor) {
        this.pointPixelColor = pointPixelColor;
    }

    public Color getDesiredPixelColor() {
        return desiredPixelColor;
    }

    public void setDesiredPixelColor(Color desiredPixelColor) {
        this.desiredPixelColor = desiredPixelColor;
    }

    public Point2D getPointRectangle1() {
        return pointRectangle1;
    }

    public void setPointRectangle1(Point2D pointRectangle1) {
        this.pointRectangle1 = pointRectangle1;
    }

    public Point2D getPointRectangle2() {
        return pointRectangle2;
    }

    public void setPointRectangle2(Point2D pointRectangle2) {
        this.pointRectangle2 = pointRectangle2;
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
        return pointMouseClick;
    }

    public void setPointMouseClick(Point2D pointMouseClick) {
        this.pointMouseClick = pointMouseClick;
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
