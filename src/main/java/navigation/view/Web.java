package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Web extends Stage {
    private Scene scene;
    private Parent root;
    private final static Logger LOGGER = LogManager.getLogger(Web.class.getName());
    public static String url;
    public Web(String title,String url) {
        this.url = url;
        initView();
        this.show();
        this.setWidth(900.0);
        this.setHeight(600.0);
        this.setTitle(title);
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/web_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        scene = new Scene(root);
        this.setScene(scene);

    }
}
