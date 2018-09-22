package navigation.view;

import com.jfoenix.controls.JFXButton;
import navigation.model.Caption;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import navigation.utils.Application;

import java.awt.*;
import java.io.IOException;


public class SimulationExamSelect extends Stage {
    private Scene scene = null;
    private Parent root = null;
    private final static double WIDTH  = 900.0;
    private final static double HEIGHT = 600.0;
    private Text secondTitle;
    private Text threeTitle;
    private JFXButton start;
    private boolean isBack = false;
    public SimulationExamSelect() {
        isBack = true;
        initView();
        event();
        this.setTitle("进入考场");
        this.setScene(scene);
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/simulation_exam_select_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        secondTitle = (Text) root.lookup("#secondTitle");
        threeTitle = (Text) root.lookup("#threeTitle");
        start     = (JFXButton) root.lookup("#start");
        Caption caption = Application.getCaption("exam");
        secondTitle.setText(caption.getSecondTile());
        threeTitle.setText(caption.getThreeTitle());

        scene = new Scene(root,WIDTH,HEIGHT);
    }
    private void event(){
        start.setOnAction(e->{
            isBack = false;
            this.close();
            new Exam();
        });
        this.setOnCloseRequest(e->{
            if (isBack) new Function();
        });
    }
}
