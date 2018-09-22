package navigation.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import navigation.model.GradeModel;
import navigation.utils.DBManager;
import navigation.utils.NetWork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class GradeController implements Initializable {
    @FXML
    private TableColumn order;
    @FXML
    private TableColumn date;
    @FXML
    private TableColumn score;
    @FXML
    private TableView table;
    @FXML
    private MenuItem showTest;
    @FXML
    private MenuItem deleteTest;

    private MaskerPane pane = new MaskerPane();

    private ObservableList<GradeModel> obs = FXCollections.observableArrayList();

    private final static Logger LOGGER = LogManager.getLogger(GradeController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initData();
        event();
    }

    private void event() {
        deleteTest.setOnAction(e -> {

            int index = table.getSelectionModel().getSelectedIndex();
            Service service = new Service() {
                @Override
                protected Task createTask() {
                    return new Task() {
                        @Override
                        protected Object call() throws Exception {
                            int result = DBManager.deleteGrade(obs.get(index).getPath());
                            if (result != 0) {
                                File file = new File(obs.get(index).getPath());
                                if (file.exists()) file.delete();
                            }
                            return null;
                        }
                    };
                }
            };
            service.setOnFailed(e1 -> {
                Notifications.create().text("删除失败!").position(Pos.TOP_CENTER).showError();
                LOGGER.error(service.getMessage());
            });
            service.setOnSucceeded(e1 ->
                    obs.remove(index)
            );
            service.start();

        });
        showTest.setOnAction(e -> {
            int index = table.getSelectionModel().getSelectedIndex();
            try {
                NetWork.openLink("file://" + obs.get(index).getPath());
            } catch (Exception e1) {
                LOGGER.error(e1.getMessage());
                Notifications.create().text("调起系统浏览器失败,请确保系统有安装浏览器！").position(Pos.TOP_CENTER).showError();
            }
        });
    }

    private void initView() {
        table.setPlaceholder(pane);
        order.prefWidthProperty().bind(table.widthProperty().multiply(0.33));
        date.prefWidthProperty().bind(table.widthProperty().multiply(0.33));
        score.prefWidthProperty().bind(table.widthProperty().multiply(0.33));

        order.setCellValueFactory(new PropertyValueFactory("order"));
        date.setCellValueFactory(new PropertyValueFactory("date"));
        score.setCellValueFactory(new PropertyValueFactory("score"));

        table.setItems(obs);


    }

    private void initData() {
        Service<List<GradeModel>> service = new Service<List<GradeModel>>() {
            @Override
            protected Task<List<GradeModel>> createTask() {

                return new Task<List<GradeModel>>() {
                    @Override
                    protected List<GradeModel> call() throws Exception {
                        List<GradeModel> list = null;
                        list = DBManager.readGrade();
                        return list;
                    }
                };
            }
        };
        service.setOnFailed(e -> {
            Notifications.create().text("获取成绩失败!").position(Pos.TOP_CENTER).showError();
            LOGGER.error(service.getException().getMessage());

        });
        service.setOnSucceeded(e -> {
                    List<GradeModel> list = service.getValue();
                    if (list.size() > 0) {
                        for (GradeModel grade : list) {
                            obs.add(grade);
                        }

                    } else {
                        pane.setText("暂无成绩!");
                    }
                }
        );

        service.start();

    }

    /**
     * 移除制定位置上的成绩
     *
     * @param index
     */
    public void remove(int index) {
        obs.remove(index - 1);
        if (obs.size() == 0) {
            pane.setText("暂无成绩");
        }
    }

}
