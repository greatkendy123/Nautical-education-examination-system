package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class About extends Stage {
    private Scene scene = null;
    private Parent root = null;

    public About() {
        initView();
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("关于");
        this.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("images/function/about_icon.png")));
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/about_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root,300,300);
        this.setScene(scene);

    }
}
