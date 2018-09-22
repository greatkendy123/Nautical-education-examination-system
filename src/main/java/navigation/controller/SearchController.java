package navigation.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.control.MaskerPane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController implements Initializable {
    @FXML
    private WebView webView = null;
    @FXML
    private TableColumn order;
    @FXML
    private TableColumn question;
    @FXML
    private TableColumn type;
    @FXML
    private TableView  tableView;
    @FXML
    private MaskerPane  loadProgress;

    private ProgressIndicator progress;


    private WebEngine engine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }
    private void init(){
        engine = webView.getEngine();
        tableView.setPlaceholder(progress);
        order.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        question.prefWidthProperty().bind(tableView.widthProperty().multiply(0.6));
        type.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2));
        engine.load("file://"+new File("./data/html/lead.html").getAbsolutePath());
        engine.getLoadWorker().progressProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            if (newValue.doubleValue()==1.0){
                loadProgress.setVisible(false);
            }else {
                loadProgress.setVisible(true);
                loadProgress.setText("加载中...");
            }
        }));
        engine.getLoadWorker().exceptionProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue!=null){
                loadProgress.setText("加载失败!");
            }
        }));


    }

}
