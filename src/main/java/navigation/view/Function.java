package navigation.view;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;

public class Function extends Stage {
    private Scene scene;
    private Parent root;
    private final static double WIDTH  = 900.0;
    private final static double HEIGHT = 600.0;
    public Function(){
        initView();
        this.setScene(scene);
        this.setResizable(false);
        this.setTitle("航海类教育考试系统 v3.0");
        this.show();
    }

    private void initView() {
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/function_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root, WIDTH, HEIGHT);
        this.getIcons().add(new Image("images/icon.png"));
    }

}
