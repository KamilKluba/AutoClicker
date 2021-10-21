package data;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ActionService extends Service<Void> {
    private boolean alive = true;
    private int actionsNumber;
    private int actionPeriod;
    private Robot robot;
    private String action;
    private String trigger;
    private Random random = new Random();

    private BufferedImage imageToSearch;
    private File fileAction;
    private File fileTrigger;
    private KeyCode keyCode;
    private int mouseButton;
    private Point pointPixelColor;
    private Point pointRectangle1;
    private Point pointRectangle2;
    private Point pointMouseClick;
    private Point previousMousePosition;
    private SerializableColor desiredPixelColor;
    private SerializableColor currentPixelColor = new SerializableColor();
    private int colorDiff;
    private double minColorR;
    private double maxColorR;
    private double minColorG;
    private double maxColorG;
    private double minColorB;
    private double maxColorB;
    private boolean isPlaying = false;
    private Media media;
    private MediaPlayer mediaPlayer;

    public ActionService(int actionsNumber, int actionPeriod, String action, String trigger, File fileAction, File fileTrigger, KeyCode keyCode, int mouseButton, Point pointPixelColor, Point pointRectangle1, Point pointRectangle2, Point pointMouseClick, Point previousMousePosition, SerializableColor desiredPixelColor, int colorDiff) {
        this.actionsNumber = actionsNumber;
        this.actionPeriod = actionPeriod;
        this.action = action;
        this.trigger = trigger;
        this.fileAction = fileAction;
        this.fileTrigger = fileTrigger;
        this.keyCode = keyCode;
        this.mouseButton = mouseButton;
        this.pointPixelColor = pointPixelColor;
        this.pointRectangle1 = pointRectangle1;
        this.pointRectangle2 = pointRectangle2;
        this.pointMouseClick = pointMouseClick;
        this.previousMousePosition = previousMousePosition;
        this.desiredPixelColor = desiredPixelColor;
        this.colorDiff = colorDiff;

        calculateColors();
        try {
            if(fileTrigger != null) {
                this.imageToSearch = ImageIO.read(fileTrigger);
            }
            this.robot = new Robot();
        } catch(IOException | AWTException e){
            e.printStackTrace();
        }
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                alive = true;
                prepareAction();
                for (int j = 0; j < actionsNumber && alive; j++) {
                    System.out.println("Starting a loop");
                    if(isConditionFulfilled()){
                        System.out.println("Condition fulfilled");
                        performAction();
                        System.out.println("Action performed");
                    } else {
                        System.out.println("Condition not fulfilled");
                        stopAction();
                        System.out.println("Action stopped");
                    }
                    System.out.println("Waiting for a next action");
                    waitForNextAction();
                    System.out.println("Waiting finished\n\n");
                }

                return null;
            }
        };
    }

    public void kill(){
        alive = false;
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    private boolean isConditionFulfilled(){
        switch(trigger){
            case Modes.TIME -> {
                return true;
            }
            case Modes.PIXEL_COLOR -> {
                currentPixelColor.setColor(robot.getPixelColor(pointPixelColor.getX(), pointPixelColor.getY()));
                return colorsWithDiffEqual();
            }
            case Modes.PIXEL_COLOR_DIFF -> {
                currentPixelColor.setColor(robot.getPixelColor(pointPixelColor.getX(), pointPixelColor.getY()));
                return !colorsWithDiffEqual();
            }
            case Modes.IMAGE_PRESENCE -> {
                return findImage();
            }
            case Modes.IMAGE_ABSENCE -> {
                return !findImage();
            }
        }
        return false;
    }

    private void prepareAction(){
        switch (action){
            case Modes.SOUND -> {
                isPlaying = false;
                media = new Media(fileAction.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(() -> {
                    isPlaying = false;
                    mediaPlayer.stop();
                });
            }
        }
    }

    private void performAction(){
        switch (action){
            case Modes.MOUSE -> {
                previousMousePosition.setPoint(MouseInfo.getPointerInfo().getLocation());
                robot.mouseMove(pointMouseClick.getX(), pointMouseClick.getY());
                robot.mousePress(mouseButton);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.mouseRelease(mouseButton);
                robot.mouseMove(previousMousePosition.getX(), previousMousePosition.getY());
            }
            case Modes.KEYBOARD -> {
                robot.keyPress(keyCode.getCode());
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                robot.keyRelease(keyCode.getCode());
            }
            case Modes.SOUND -> {
                if(!isPlaying) {
                    mediaPlayer.play();
                    isPlaying = true;
                }
            }
            case Modes.SHUTDOWN -> {

            }
        }
    }

    private void stopAction(){
        switch (action){
            case Modes.MOUSE -> {

            }
            case Modes.KEYBOARD -> {

            }
            case Modes.SOUND -> {
                if(isPlaying) {
                    mediaPlayer.stop();
                    isPlaying = false;
                }
            }
            case Modes.SHUTDOWN -> {
            }
        }
    }

    private void waitForNextAction(){
        try {
            for(int k = 0; k < actionPeriod; k = k + 10){
                Thread.sleep(10);
                if(!alive) break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean findImage(){
        int firstPixelValue = imageToSearch.getRGB(0, 0);
        int imageToSearchWidth = imageToSearch.getWidth();
        int imageToSearchHeight = imageToSearch.getHeight();
        double areaWidth = pointRectangle2.getX() - pointRectangle1.getX();
        double areaHeight = pointRectangle2.getY() - pointRectangle1.getY();
        int numberOfPixelsToFind = imageToSearch.getWidth() * imageToSearch.getHeight();
        BufferedImage areaToSearchIn = robot.createScreenCapture(new Rectangle((int)pointRectangle1.getX(), (int)pointRectangle1.getY(), (int)areaWidth, (int)areaHeight));

        for(int i = 0; i < areaToSearchIn.getWidth(); i++){
            for(int j = 0; j < areaToSearchIn.getHeight(); j++){
                int imageRgb = areaToSearchIn.getRGB(i, j);
                if(imageRgb == firstPixelValue) {
                    boolean keepSearching = true;
                    int numberOfFoundPixels = 0;
                    for (int k = 0; k < imageToSearchWidth && keepSearching; k++) {
                        for (int l = 0; l < imageToSearchHeight && keepSearching; l++) {
                            if (i + k < areaWidth && j + l < areaHeight) {
                                int imageToSearchRGB = imageToSearch.getRGB(k, l);
                                int imageToSearchAlpha = imageToSearchRGB >> 24;
                                if(imageToSearchAlpha == -1){
                                    if (imageToSearchRGB == areaToSearchIn.getRGB(i + k, j + l)) {
                                        numberOfFoundPixels++;
                                    } else {
                                        keepSearching = false;
                                    }
                                } else {
                                    numberOfFoundPixels++;
                                }
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

    private void calculateColors(){
        minColorR = desiredPixelColor.getRed() - colorDiff * 0.01;
        maxColorR = desiredPixelColor.getRed() + colorDiff * 0.01;
        minColorG = desiredPixelColor.getGreen() - colorDiff * 0.01;
        maxColorG = desiredPixelColor.getGreen() + colorDiff * 0.01;
        minColorB = desiredPixelColor.getBlue() - colorDiff * 0.01;
        maxColorB = desiredPixelColor.getBlue() + colorDiff * 0.01;
    }

    private boolean colorsWithDiffEqual(){
        if(!(currentPixelColor.getRed() <= maxColorR && currentPixelColor.getRed() >= minColorR)) return false;
        if(!(currentPixelColor.getGreen() <= maxColorG && currentPixelColor.getGreen() >= minColorG)) return false;
        return currentPixelColor.getBlue() <= maxColorB && currentPixelColor.getBlue() >= minColorB;
    }
}
