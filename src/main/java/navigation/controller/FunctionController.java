package navigation.controller;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import navigation.utils.DBManager;
import navigation.view.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.sqlite.core.DB;

import javax.management.Notification;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class FunctionController implements Initializable {
    @FXML
    private JFXButton questionsBank;
    @FXML
    private JFXButton mistake;
    @FXML
    private JFXButton note;
    @FXML
    private JFXButton exam;
    @FXML
    private JFXButton search;
    @FXML
    private JFXButton about;
    @FXML
    private Canvas canvas;
    @FXML
    private JFXButton setting;

    private GraphicsContext gc;

    private static final Logger logger = LogManager.getLogger(FunctionController.class.getName());


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        event();
        initData();
    }

    private void drawScore(double score) {
        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(8.0);
        gc.strokeOval(50, 50, 150, 150);
        gc.setLineWidth(2.0);
        gc.setFont(Font.font(40.0));
        if (score < 60.0) {
            gc.setStroke(Color.RED);
        }
        gc.strokeText(score + "", 100, 140);
    }

    private void event() {
        questionsBank.setOnAction(e -> toUnitSelect("questions"));
        mistake.setOnAction(e -> {
            new Mistake();
            note.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        note.setOnAction(e -> {
            new UserNote();
            note.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        exam.setOnAction(e -> {
            new SimulationExamSelect();
            exam.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        search.setOnAction(e -> {
            new Search();
            search.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        about.setOnAction(e ->
                new About()
        );
        setting.setOnAction(e ->
                new ApplicationSetting()
        );

        canvas.setOnMouseClicked(e -> new ShowGrade());
    }

    void toUnitSelect(String type) {
        new UnitSelect(type);
        questionsBank.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void initData() {
        Service<Double> service = new Service<Double>() {
            @Override
            protected Task<Double> createTask() {
                return new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {
                        double average = DBManager.getAverageScore();
                        return average;

                    }
                };
            }
        };
        service.setOnFailed(e -> {
            Notifications.create().position(Pos.TOP_CENTER).text("初始化数据失败").showError();
            logger.error(service.getException().getMessage());
        });
        service.setOnSucceeded(e -> {
            DecimalFormat format = new DecimalFormat("0.0");
            drawScore(new Double(format.format(service.getValue())));

        });
        service.start();
    }

}
