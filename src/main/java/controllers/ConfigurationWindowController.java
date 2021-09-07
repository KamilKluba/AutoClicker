package controllers;

import data.ActionField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;


public class ConfigurationWindowController {
    @FXML TextField textFieldActionsNumber;
    @FXML TextField textFieldActionPeriod;
    @FXML TextField textFieldPeriodRandomizer;
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
    @FXML ComboBox<File> comboBoxSearchedImage;

    @FXML AnchorPane anchorPaneKeyboard;
    @FXML TextField textFieldKey;

    @FXML AnchorPane anchorPaneMouse;
    @FXML TextField textFieldMouseClickX;
    @FXML TextField textFieldMouseClickY;
    @FXML Button buttonMouseButton;

    @FXML Button buttonOk;
    @FXML Button buttonCancel;

    private ActionField actionField;
    private Stage stage;
    private KeyCode keyCode;
    private MouseButton mouseButton;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private File directoryImages;
    private File selectedImage;
    private boolean colorMode;
    private boolean imageMode;
    private boolean keyboardMode;
    private boolean mouseMode;

    public void initialize(){
        buttonMouseButton.addEventFilter(MouseEvent.MOUSE_CLICKED, keyEvent -> {
            mouseButton = keyEvent.getButton();
            buttonMouseButton.setText(keyEvent.getButton().name());
        });

        textFieldKey.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            keyCode = keyEvent.getCode();
            textFieldKey.setText(keyEvent.getCode().getName());
        });
    }

    public void actionOk(){
        if(keyCode == null && mouseButton == null) {
            return;
        }
        try {
            int actionsNumber = Integer.parseInt(textFieldActionsNumber.getText());
            int actionsPeriod = Integer.parseInt(textFieldActionPeriod.getText());
            int periodRandomizer = Integer.parseInt(textFieldPeriodRandomizer.getText());
            actionField.setActionsNumber(actionsNumber);
            actionField.setActionPeriod(actionsPeriod);
            actionField.setPeriodRandomizer(periodRandomizer);
            if(colorMode){
                double colorPixelX = Double.parseDouble(textFieldColorPixelX.getText());
                double colorPixelY = Double.parseDouble(textFieldColorPixelY.getText());
                int colorDiff = Integer.parseInt(textFieldColorDiff.getText());
                actionField.setPointPixelColor(new Point2D(colorPixelX, colorPixelY));
                actionField.setColorDiff(colorDiff);
            }
            if(imageMode){
                double rectangleX1 = Double.parseDouble(textFieldX1.getText());
                double rectangleY1 = Double.parseDouble(textFieldY1.getText());
                double rectangleX2 = Double.parseDouble(textFieldX2.getText());
                double rectangleY2 = Double.parseDouble(textFieldY2.getText());
                actionField.setPointRectangle1(new Point2D(rectangleX1, rectangleY1));
                actionField.setPointRectangle2(new Point2D(rectangleX2, rectangleY2));
            }
            if(mouseMode){
                double mouseClickX = Double.parseDouble(textFieldMouseClickX.getText());
                double mouseClickY = Double.parseDouble(textFieldMouseClickY.getText());
                actionField.setPointMouseClick(new Point2D(mouseClickX, mouseClickY));
            }
        } catch(NumberFormatException e){
            e.printStackTrace();
            return;
        }

        actionField.setKeyCode(keyCode);
        actionField.setMouseButton(mouseButton);
        actionField.setDesiredPixelColor(colorPickerPixel.getValue());
        actionField.setDirectoryImages(directoryImages);
        actionField.setSelectedImage(selectedImage);
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
        AtomicReference<Double> pressX = new AtomicReference<>((double) 0);
        AtomicReference<Double> pressY = new AtomicReference<>((double) 0);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.setOpacity(0.3);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        Pane pane = new Pane();
        try {
            double x1 = Double.parseDouble(textFieldX1.getText());
            double y1 = Double.parseDouble(textFieldY1.getText()) + 13;
            double x2 = Double.parseDouble(textFieldX2.getText());
            double y2 = Double.parseDouble(textFieldY2.getText()) + 13;

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
            pressX.set(keyEvent.getScreenX());
            pressY.set(keyEvent.getSceneY());
            textFieldX1.setText(String.valueOf(keyEvent.getScreenX()));
            textFieldY1.setText(String.valueOf(keyEvent.getScreenY()));
        });
        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, keyEvent -> {
            pane.getChildren().clear();
            pane.getChildren().add(new Line(pressX.get(), pressY.get(), keyEvent.getScreenX(), pressY.get()));
            pane.getChildren().add(new Line(pressX.get(), pressY.get(), pressX.get(), keyEvent.getSceneY()));
            pane.getChildren().add(new Line(keyEvent.getScreenX(), pressY.get(), keyEvent.getScreenX(), keyEvent.getSceneY()));
            pane.getChildren().add(new Line(pressX.get(), keyEvent.getSceneY(), keyEvent.getScreenX(), keyEvent.getSceneY()));
            textFieldX2.setText(String.valueOf(keyEvent.getScreenX()));
            textFieldY2.setText(String.valueOf(keyEvent.getScreenY()));
        });
        stage.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            try {
                double x1 = Double.parseDouble(textFieldX1.getText());
                double y1 = Double.parseDouble(textFieldY1.getText());
                double x2 = Double.parseDouble(textFieldX2.getText());
                double y2 = Double.parseDouble(textFieldY2.getText());
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

    public void actionSelectDirectory(){
        directoryImages = directoryChooser.showDialog(stage);
        directoryChooser.setInitialDirectory(directoryImages);
        comboBoxSearchedImage.getSelectionModel().clearSelection();
    }

    public void actionRefreshImages(){
        fillImagesCombo();
    }

    public void actionSelectImage(){
        selectedImage = comboBoxSearchedImage.getSelectionModel().getSelectedItem();
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
            textFieldMouseClickX.setText(String.valueOf(keyEvent.getScreenX()));
            textFieldMouseClickY.setText(String.valueOf(keyEvent.getScreenY()));
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
        this.colorPickerPixel.setValue(actionField.getDesiredPixelColor());
        this.textFieldColorDiff.setText(String.valueOf(actionField.getColorDiff()));
        this.directoryImages = actionField.getDirectoryImages();
        if(actionField.getPointPixelColor() != null) {
            this.textFieldColorPixelX.setText(String.valueOf(actionField.getPointPixelColor().getX()));
            this.textFieldColorPixelY.setText(String.valueOf(actionField.getPointPixelColor().getY()));
        }
        if(actionField.getPointRectangle1() != null) {
            this.textFieldX1.setText(String.valueOf(actionField.getPointRectangle1().getX()));
            this.textFieldY1.setText(String.valueOf(actionField.getPointRectangle1().getY()));
        }
        if(actionField.getPointRectangle2() != null) {
            this.textFieldX2.setText(String.valueOf(actionField.getPointRectangle2().getX()));
            this.textFieldY2.setText(String.valueOf(actionField.getPointRectangle2().getY()));
        }
        if(actionField.getPointMouseClick() != null) {
            this.textFieldMouseClickX.setText(String.valueOf(actionField.getPointMouseClick().getX()));
            this.textFieldMouseClickY.setText(String.valueOf(actionField.getPointMouseClick().getY()));
        }
        if(actionField.getKeyCode() != null) {
            this.textFieldKey.setText(actionField.getKeyCode().getName());
            this.keyCode = actionField.getKeyCode();
        }
        if(actionField.getMouseButton() != null){
            this.textFieldKey.setText(actionField.getMouseButton().name());
            this.mouseButton = actionField.getMouseButton();
        }
        fillImagesCombo();
        this.selectedImage = actionField.getSelectedImage();
        comboBoxSearchedImage.getSelectionModel().select(this.selectedImage);
    }

    public void selectMode(String mode){
        anchorPanePixelColor.setVisible(false);
        anchorPaneImageSelector.setVisible(false);
        anchorPaneMouse.setVisible(false);
        anchorPaneKeyboard.setVisible(false);

        colorMode = false;
        imageMode = false;
        keyboardMode = false;
        mouseMode = false;

        switch (mode) {
            case "Klik myszą|Czas" -> {
                mouseMode = true;
                anchorPaneMouse.setVisible(true);
            }
            case "Klik myszą|Kolor piksela", "Klik myszą|Kolor piksela różny od" -> {
                mouseMode = true;
                colorMode = true;
                anchorPanePixelColor.setVisible(true);
                anchorPaneMouse.setVisible(true);
            }
            case "Klik myszą|Obecność obrazu w polu", "Klik myszą|Brak obecności obrazu w polu" -> {
                mouseMode = true;
                imageMode = true;
                anchorPaneImageSelector.setVisible(true);
                anchorPaneMouse.setVisible(true);
            }
            case "Klik klawiaturą|Czas" -> {
                keyboardMode = true;
                anchorPaneKeyboard.setVisible(true);
            }
            case "Klik klawiaturą|Kolor piksela", "Klik klawiaturą|Kolor piksela różny od" -> {
                keyboardMode = true;
                colorMode = true;
                anchorPanePixelColor.setVisible(true);
                anchorPaneKeyboard.setVisible(true);
            }
            case "Klik klawiaturą|Obecność obrazu w polu", "Klik klawiaturą|Brak obecności obrazu w polu" -> {
                keyboardMode = true;
                imageMode = true;
                anchorPaneImageSelector.setVisible(true);
                anchorPaneKeyboard.setVisible(true);
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
                Color color = robot.getPixelColor(keyEvent.getScreenX(), keyEvent.getScreenY());
                colorPickerPixel.setValue(color);
            }
            if(mode.equals("pixel") || doBoth) {
                textFieldColorPixelX.setText(String.valueOf(keyEvent.getScreenX()));
                textFieldColorPixelY.setText(String.valueOf(keyEvent.getScreenY()));
            }
        });
        stage.show();
    }

    private void fillImagesCombo(){
        if(directoryImages == null) return;
        directoryChooser.setInitialDirectory(directoryImages);
        FileFilter fileFilter = pathname -> pathname.getName().endsWith("jpg") || pathname.getName().endsWith("png");
        File[] files = directoryImages.listFiles(fileFilter);
        ObservableList<File> comboBoxSearchedImageItems = comboBoxSearchedImage.getItems();
        comboBoxSearchedImageItems.clear();
        assert files != null;
        comboBoxSearchedImageItems.addAll(Arrays.asList(files));
    }
}
