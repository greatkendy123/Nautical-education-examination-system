package navigation.controller;

import javafx.stage.WindowEvent;
import navigation.Main;
import navigation.model.UnitSelectModel;
import navigation.view.Mistake;
import navigation.view.Practise;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import navigation.utils.Application;
import navigation.utils.DBManager;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UnitSelectController implements Initializable {
    @FXML
    private TableColumn order;
    @FXML
    private TableColumn title;
    @FXML
    private TableColumn time;
    @FXML
    private TableColumn score;
    @FXML
    private TableColumn times;
    @FXML
    private TableView dataTable;
    private ObservableList<UnitSelectModel> obs = FXCollections.observableArrayList();
    private Group group = new Group();
    private ProgressIndicator progress = new ProgressIndicator();
    public static boolean isBackFunction = true;
    private static final Logger logger = LogManager.getLogger(UnitSelectController.class.getName());


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        event();
        loadData();

    }

    void initView() {

        order.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));
        title.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.59));
        time.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));
        score.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));
        times.prefWidthProperty().bind(dataTable.widthProperty().multiply(0.1));
        order.setCellValueFactory(new PropertyValueFactory<>("order"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        score.setCellValueFactory(new PropertyValueFactory<>("score"));
        times.setCellValueFactory(new PropertyValueFactory<>("times"));
        dataTable.setItems(obs);
        progress.setPrefWidth(100.0);
        progress.setPrefHeight(100.0);
        group.getChildren().add(progress);
        dataTable.setPlaceholder(group);
        dataTable.setSortPolicy(e -> false);


    }

    void event() {
        dataTable.setOnMouseClicked(e -> {
            if (dataTable.getSelectionModel().getFocusedIndex() != -1 && e.getClickCount() > 1) {
                UnitSelectModel model = obs.get(dataTable.getSelectionModel().getFocusedIndex());
                isBackFunction = false;
                new Practise(model);
                dataTable.getScene().getWindow().fireEvent(new WindowEvent(dataTable.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    private void loadData() {
        Service<List<UnitSelectModel>> service = new Service<List<UnitSelectModel>>() {
            @Override
            protected Task<List<UnitSelectModel>> createTask() {
                return new Task<List<UnitSelectModel>>() {
                    @Override
                    protected List<UnitSelectModel> call() throws Exception {
                        List<UnitSelectModel> list = null;
                        list = DBManager.getUnitList(Application.currentSubject.getDataBases(), Application.currentSubject.getSubjectName());

                        return list;
                    }
                };
            }
        };
        service.setOnFailed(e -> {
            Notifications notification = Notifications.create();
            notification.position(Pos.TOP_CENTER);
            notification.text("加载出错！");
            notification.showError();
            logger.error(service.getException().getMessage());
        });

        service.setOnSucceeded(e -> {
            List<UnitSelectModel> list = service.getValue();
            for (UnitSelectModel unit : list) {
                obs.add(unit);
            }
        });

        service.start();

    }
}
