package jimageprocessor;

import com.cyzapps.mathrecog.CharLearningMgr;
import com.cyzapps.mathrecog.ExprRecognizer;
import com.cyzapps.mathrecog.MisrecogWordMgr;
import com.cyzapps.uptloadermgr.UPTJavaLoaderMgr;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SNH extends Application{

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));   // Settings for elements on the screen
        primaryStage.setTitle("算你狠"); // TODO add name to the program window
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
