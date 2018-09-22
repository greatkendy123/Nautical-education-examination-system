package navigation.view;


import javafx.geometry.Pos;
import navigation.controller.PractiseController;
import navigation.model.UnitSelectModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;


import javax.management.Notification;
import java.io.IOException;

public class Practise extends Stage {
    private Scene scene;
    private Parent root;
    private  double width = 900.0;
    private  double height = 600.0;
    private UnitSelectModel unit;
    private FXMLLoader fxmlLoader= null;
    private PractiseController controller = null;


    public Practise(UnitSelectModel unit) {
        this.unit = unit;
        controller = new PractiseController().init(unit,this);
        initView();
        this.setTitle(unit.getTitle());
        this.setScene(scene);
        this.show();
    }

    private void initView() {
        try {
            fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/practise_view.fxml"));
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root, width, height);
    }


}
