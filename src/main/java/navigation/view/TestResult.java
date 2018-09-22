package navigation.view;

import navigation.controller.TestResultController;
import navigation.model.Question;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class TestResult extends Stage {
    private Scene scene;
    private Parent root;
    private static final double WIDTH = 900.0;
    private static final double HEIGHT = 600.0;

    private TestResultController controller;
    public TestResult(List<Question> list,String type) {
        controller = new TestResultController().init(list,this,type);
        initView();
        this.setTitle("航海类教育考试系统V3.0");
        this.initStyle(StageStyle.UNIFIED);
        this.show();

    }

    private void initView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/test_result_view.fxml"));
            fxmlLoader.setController(controller);
            root = fxmlLoader.load();


        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root, WIDTH, HEIGHT);
        this.setScene(scene);

    }
}
