package navigation.view;

import navigation.controller.UnitSelectController;
import navigation.model.Caption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import navigation.utils.Application;

import java.io.IOException;

public class UnitSelect extends Stage {
    private Scene scene = null;
    private Parent root = null;
    private final static double WIDTH = 900.0;
    private final static double HEIGHT = 600.0;
    public static String type;
    private Text secondTitle;
    private Text threeTitle;

    public UnitSelect(String type) {
        this.type = type;
        UnitSelectController.isBackFunction = true;
        initView();
        this.setScene(scene);
        this.show();
        this.setTitle("真考题库");
    }

    private void initView() {

        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/unit_select.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        secondTitle = (Text) root.lookup("#secondTitle");
        threeTitle = (Text) root.lookup("#threeTitle");

        Caption caption = Application.getCaption(type);
        secondTitle.setText(caption.getSecondTile());
        threeTitle.setText(caption.getThreeTitle());
        scene = new Scene(root, WIDTH, HEIGHT);
        this.setOnCloseRequest(e -> {
            if (UnitSelectController.isBackFunction) {
                new Function();
            }
        });
    }
}
