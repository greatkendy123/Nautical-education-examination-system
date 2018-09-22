package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.IOException;

public class Search extends Stage {
    private final static double WIDTH  = 900.0;
    private final static double HEIGHT = 600.0;
    private Scene scene = null;
    private Parent root = null;
    public Search() {
        initView();
        event();
        this.setTitle("智能搜索");
        this.setScene(scene);
        this.show();

    }
    private void initView(){
        try {
            root= FXMLLoader.load(ClassLoader.getSystemResource("fxml/search_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene =new Scene(root,WIDTH,HEIGHT);
    }
    private void event(){
        this.setOnCloseRequest(e->
            new Function()
        );
    }

}
