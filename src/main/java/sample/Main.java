package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Twoja stararara");
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args){
        launch(args);
//        Main main = new Main();
//
//        main.saveImage();

    }

    public void saveImage(){
        Platform.runLater(() -> {

            try {
                BufferedImage imageToSearch = ImageIO.read(getClass().getResource("/Czarowana.png"));
                int firstPixelValue = imageToSearch.getRGB(0, 0);
                int numberOfPixelsToFind = imageToSearch.getWidth() * imageToSearch.getHeight();

                boolean imageFound = false;
                boolean goOn = true;
                Robot robot = new Robot();
                WritableImage screenshot = robot.getScreenCapture(null, 100, 100, 300, 50);
                BufferedImage imageToSearchIn = SwingFXUtils.fromFXImage(screenshot, null);
                File file = new File("D:\\Programy\\IntelliJ IDEA 2021.1\\Workspace\\AutoClicker\\out\\artifacts\\AutoClicker_jar\\foto.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < imageToSearchIn.getWidth() && goOn; i++){ ;
                    for(int j = 0; j < imageToSearchIn.getHeight() && goOn; j++){
                        int imageRgb = imageToSearchIn.getRGB(i, j);
                        if(imageRgb == firstPixelValue) {
                            boolean keepSearching = true;
                            int numberOfFoundPixels = 0;
                            for (int k = 0; k < imageToSearch.getWidth() && keepSearching; k++) {
                                for (int l = 0; l < imageToSearch.getHeight() && keepSearching; l++) {
                                    if (imageToSearch.getRGB(k, l) == imageToSearchIn.getRGB(i + k, j + l)) {
                                        numberOfFoundPixels++;
                                    } else {
                                        keepSearching = false;
                                    }
                                }
                            }
                            if(numberOfFoundPixels == numberOfPixelsToFind){
                                imageFound = true;
                                goOn = false;
                            }
                        }
                    }
                }

                System.out.println(imageFound + " " + numberOfPixelsToFind + " ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
