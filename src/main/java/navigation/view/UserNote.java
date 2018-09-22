package navigation.view;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import navigation.utils.DBManager;
import org.controlsfx.control.Notifications;

public class UserNote extends Stage {
    private BorderPane container = new BorderPane();
    private Scene scene = new Scene(container, 900, 600);
    private TextArea textArea = new TextArea();


    public UserNote() {
        initView();
        this.setScene(scene);
        this.setTitle("用户笔记");
        this.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("images/icon.png")));
        this.show();
        loadData();
    }

    private void initView() {
        container.setCenter(textArea);
        textArea.setFont(Font.font(20.0));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        this.setOnCloseRequest(e -> new Function());
    }

    private void loadData() {
        Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        String note = DBManager.getUserNote();
                        return note;
                    }
                };
            }
        };
        service.setOnFailed(e -> {

        });
        service.setOnSucceeded(e -> {
                    if (service.getValue() != null && !service.getValue().trim().equals("")) {
                        textArea.setText(service.getValue());
                    } else {
                        Notifications.create().position(Pos.BOTTOM_RIGHT).text("笔记为空,快去添加笔记吧！");
                    }
                }
        );
        service.start();
    }
}
