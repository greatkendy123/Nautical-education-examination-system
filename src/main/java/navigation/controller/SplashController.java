package navigation.controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import navigation.view.LessonSelect;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import navigation.utils.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class SplashController implements Initializable {
    @FXML
    private ImageView icon;
    private static final Logger logger = LogManager.getLogger(SplashController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info(System.getProperty("java.classpath"));
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        new DBManager().initData();
                        return null;
                    }
                };
            }
        };
        service.setOnSucceeded(e -> {
            new LessonSelect();
            icon.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        service.setOnFailed(e -> {
                    logger.error(service.getException().getMessage());
                    DBManager.closeDataBases();
                    Optional<ButtonType> result = new Alert(Alert.AlertType.ERROR, "初始化数据失败").showAndWait();
                    if (result.get() == ButtonType.OK) {
                        Platform.exit();
                    }
                }
        );
        service.start();
    }

}
