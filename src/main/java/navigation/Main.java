package navigation;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class Main extends Application{
    private static final double    width  = 500.0;
    private static final double    height = 300.0;
    private  Parent    root;
    private  Scene     scene;
    private static final Logger   logger = LogManager.getLogger(Main.class.getName());


    public void start(Stage primaryStage) throws Exception {
        initView();
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("航海类教育考试系统V3.0");
        primaryStage.setResizable(false);
        primaryStage.isAlwaysOnTop();
        primaryStage.show();
    }
    void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/splash_view.fxml"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        scene =new Scene(root,width,height);

    }
    public static void main(String[] args){
        launch();
    }
}
