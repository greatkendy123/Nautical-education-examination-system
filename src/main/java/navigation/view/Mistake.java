package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;

public class Mistake extends Stage {
    private static final double WIDTH = 900.0;
    private static final double HEIGHT = 600.0;
    private Scene scene = null;
    private Parent root = null;

    public Mistake() {
         initView();
         event();
        this.setTitle("错题重做");
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/mistake_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root);
        this.setScene(scene);
    }
    private void event(){
        this.setOnCloseRequest(e-> new Function());
    }
}
