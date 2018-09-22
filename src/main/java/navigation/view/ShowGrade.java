package navigation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ShowGrade extends Stage {
    private Parent root= null;
    private Scene scene = null;
    private final static Logger LOGGER = LogManager.getLogger(ShowGrade.class.getName());
    public ShowGrade() {
        initView();
        this.setTitle("考试记录");
        this.initModality(Modality.APPLICATION_MODAL);
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/grade_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        scene = new Scene(root,300,400);
        this.setScene(scene);


    }
}
