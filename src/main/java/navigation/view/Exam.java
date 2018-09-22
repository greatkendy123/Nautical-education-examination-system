package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Exam extends Stage {
    private Scene scene;
    private Parent root;
    public Exam() {
        initView();
        this.initStyle(StageStyle.UNDECORATED);
        this.setTitle("模拟考场");
        this.setFullScreen(true);
        this.setResizable(false);
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/exam_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root);
        this.setScene(scene);
    }
}
