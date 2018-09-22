package navigation.view;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import navigation.controller.MistakeController;

import java.util.ArrayList;
import java.util.List;

public class ShowSelect extends Stage {
    private ScrollPane panel = new ScrollPane();
    private Scene scene = new Scene(panel, 300, 400);
    private FlowPane flow = new FlowPane();
    private int total = 0;
    private MistakeController controller;
    private List<JFXButton> buttons = new ArrayList<>();

    public ShowSelect(int total, MistakeController controller) {
        this.controller = controller;
        this.total = total;
        initView();
        event();
        this.setTitle("选择题目");
        this.setScene(scene);
        this.setResizable(false);
        this.initModality(Modality.APPLICATION_MODAL);
        this.show();
    }

    private void initView() {
        flow.setPrefWrapLength(250);
        flow.setAlignment(Pos.CENTER);
        flow.setVgap(1.0);
        flow.setHgap(1.0);
        panel.setContent(flow);
        panel.setFitToHeight(true);
        panel.setFitToWidth(true);
        for (int i = 0; i < total; i++) {
            JFXButton button = null;
            if (i < 9) {
                button = new JFXButton("0" + (i+1));
            } else {
                button = new JFXButton((i+1) + "");
            }
            button.setUserData(i);
            button.setStyle("-fx-background-color: yellow");
            buttons.add(button);
            button.setOnAction(e -> {
                JFXButton source = (JFXButton) e.getSource();
                int index = (int) source.getUserData();
                controller.callBack(index);
                this.hide();
            });
            flow.getChildren().add(button);
        }
    }

    private void event() {
        this.setOnCloseRequest(e -> this.hide());
    }
    public void setMark(int index){
        buttons.get(index).setStyle("-fx-background-color: green");
    }
}
