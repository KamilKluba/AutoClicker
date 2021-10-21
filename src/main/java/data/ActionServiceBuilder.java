package data;


import javafx.scene.input.KeyCode;

import java.io.File;

public class ActionServiceBuilder {
    private int actionsNumber;
    private int actionPeriod;
    private String action;
    private String trigger;
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
    private int colorDiff;

    public ActionServiceBuilder setActionsNumber(int actionsNumber) {
        this.actionsNumber = actionsNumber;
        return this;
    }

    public ActionServiceBuilder setActionPeriod(int actionPeriod) {
        this.actionPeriod = actionPeriod;
        return this;
    }

    public ActionServiceBuilder setAction(String action) {
        this.action = action;
        return this;
    }

    public ActionServiceBuilder setTrigger(String trigger) {
        this.trigger = trigger;
        return this;
    }

    public ActionServiceBuilder setFileAction(File fileAction) {
        this.fileAction = fileAction;
        return this;
    }

    public ActionServiceBuilder setFileTrigger(File fileTrigger) {
        this.fileTrigger = fileTrigger;
        return this;
    }

    public ActionServiceBuilder setKeyCode(KeyCode keyCode) {
        this.keyCode = keyCode;
        return this;
    }

    public ActionServiceBuilder setMouseButton(int mouseButton) {
        this.mouseButton = mouseButton;
        return this;
    }

    public ActionServiceBuilder setPointPixelColor(Point pointPixelColor) {
        this.pointPixelColor = pointPixelColor;
        return this;
    }

    public ActionServiceBuilder setPointRectangle1(Point pointRectangle1) {
        this.pointRectangle1 = pointRectangle1;
        return this;
    }

    public ActionServiceBuilder setPointRectangle2(Point pointRectangle2) {
        this.pointRectangle2 = pointRectangle2;
        return this;
    }

    public ActionServiceBuilder setPointMouseClick(Point pointMouseClick) {
        this.pointMouseClick = pointMouseClick;
        return this;
    }

    public ActionServiceBuilder setPreviousMousePosition(Point previousMousePosition) {
        this.previousMousePosition = previousMousePosition;
        return this;
    }

    public ActionServiceBuilder setDesiredPixelColor(SerializableColor desiredPixelColor) {
        this.desiredPixelColor = desiredPixelColor;
        return this;
    }

    public ActionServiceBuilder setColorDiff(int colorDiff) {
        this.colorDiff = colorDiff;
        return this;
    }

    public ActionService build() {
        return new ActionService(actionsNumber, actionPeriod, action, trigger, fileAction, fileTrigger, keyCode, mouseButton, pointPixelColor, pointRectangle1, pointRectangle2, pointMouseClick, previousMousePosition, desiredPixelColor, colorDiff);
    }
}
