package navigation.controller;

import navigation.model.Subject;
import navigation.view.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.WindowEvent;
import navigation.utils.Application;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectLessonController implements Initializable {
    @FXML
    private TableColumn order;
    @FXML
    private TableColumn subjectName;
    @FXML
    private  TableColumn subjectStatus;
    @FXML
    private TableView table;
    private ObservableList<Subject> obs = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initData();
        event();

    }
    private void initView(){

        order.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        subjectName.prefWidthProperty().bind(table.widthProperty().multiply(0.59));
        subjectStatus.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        subjectName.setCellValueFactory( new PropertyValueFactory<>("subjectName"));
        subjectStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        order.setCellValueFactory(new PropertyValueFactory<>("order"));
        table.setItems(obs);
        table.setSortPolicy(e-> false);

    }

    /***
     * 初始化数据
     */
    private void initData(){
        for (Subject subject: Application.subjects){
            obs.add(subject);
        }
    }

    /**
     * 双击TableView中的数据，跳转界面
     */
    private void event(){
        table.setOnMouseClicked(e->{
            if (e.getClickCount()==2&& table.getSelectionModel().getSelectedIndex()!=-1){
               Application.currentSubject = obs.get(table.getSelectionModel().getSelectedIndex());
               table.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
               new Function();
            }
        });
    }
}
