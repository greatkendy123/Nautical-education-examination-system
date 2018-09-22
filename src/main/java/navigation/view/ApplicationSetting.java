package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationSetting extends Stage {
    private Scene scene = null;
    private Parent root = null;
    public ApplicationSetting() {
        initView();
        this.setTitle("设置");
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/application_setting_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root,300,400);
        this.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("images/function/setting.png")));
        this.setScene(scene);

    }
}
