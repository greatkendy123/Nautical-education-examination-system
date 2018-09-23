package navigation.controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import navigation.model.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    @FXML
    private TableColumn order;
    @FXML
    private TableColumn question;
    @FXML
    private TableView tableView;
    @FXML
    private RadioButton A;
    @FXML
    private RadioButton B;
    @FXML
    private RadioButton C;
    @FXML
    private RadioButton D;
    @FXML
    private Text questions;
    @FXML
    private ImageView picture;
    @FXML
    private TextField keyword;
    @FXML
    private JFXButton searchAction;
    @FXML
    private TabPane tabPane;
    @FXML
    private BorderPane container;

    private ObservableList<Question> obs = FXCollections.observableArrayList();
    private static final Logger LOGGER = LogManager.getLogger(SearchController.class.getName());


    private ProgressIndicator progress;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        init();
        event();
    }

    private void init() {

        tableView.setPlaceholder(progress);
        order.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        question.prefWidthProperty().bind(tableView.widthProperty().multiply(0.8));
        tableView.setSortPolicy(e->false);

        order.setCellValueFactory(new PropertyValueFactory("order"));
        question.setCellValueFactory(new PropertyValueFactory("question"));

        tableView.setItems(obs);
        tabPane.prefWidthProperty().bind(container.widthProperty().multiply(0.7));
        tableView.prefWidthProperty().bind(container.widthProperty().multiply(0.3));



    }

    private void event() {
        searchAction.setOnAction(e -> {
            if (!keyword.getText().trim().equals("")) {
                search(keyword.getText());
            } else {
                Notifications.create().position(Pos.TOP_CENTER).text("搜索关键字不能为空！").showWarning();
            }
        });


    }

    private void search(String keyword) {
        Service<List<Question>> service = new Service<List<Question>>() {
            @Override
            protected Task<List<Question>> createTask() {
                return new Task<List<Question>>() {
                    @Override
                    protected List<Question> call() throws Exception {

                        return null;
                    }
                };
            }
        };
        service.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Notifications.create().text("搜索出错了！").position(Pos.TOP_CENTER).showError();
                LOGGER.error(newValue.getMessage());
            }
        });
        service.setOnSucceeded(e -> {
            List<Question> results = service.getValue();
            if (results!=null&&results.size() > 0) {
                for (Question model : results) {
                    obs.add(model);
                }
            } else {

                Notifications.create().text("未找到相关内容!").position(Pos.TOP_CENTER).showInformation();

            }
        });
        service.start();
    }

}
