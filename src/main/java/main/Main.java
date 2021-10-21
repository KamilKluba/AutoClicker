package main;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.sun.jna.Native;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindowController controller = loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("Auto clicker");
        primaryStage.setScene(new Scene(root, 416, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args){
        launch(args);
//        Main main = new Main();
    }
}
