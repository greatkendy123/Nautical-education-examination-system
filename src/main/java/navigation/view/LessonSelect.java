package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 课程选择界面
 *
 */
public class LessonSelect extends Stage {
    private Parent root = null;
    private Scene scene = null;
    private static final double WIDTH = 500;
    private static  final double HEIGHT =350;


    public LessonSelect() {
        initView();
        this.setScene(scene);
        this.setResizable(false);
        this.setTitle("单元选择");
        this.centerOnScreen();
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/lesson_select_view.fxml"));
        }catch (IOException e){
            e.printStackTrace();
        }
        scene =new Scene(root,WIDTH,HEIGHT);
    }
}
