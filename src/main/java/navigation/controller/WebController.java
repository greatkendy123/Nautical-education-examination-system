package navigation.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import navigation.view.Web;
import org.controlsfx.control.MaskerPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WebController implements Initializable {
    @FXML
    private WebView webView = null;
    @FXML
    private MaskerPane mask;

    private WebEngine engine = null;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        event();
    }
    private void initView(){
        engine = webView.getEngine();
        engine.load(Web.url);
        mask.progressProperty().bind(engine.getLoadWorker().progressProperty());
    }
    private void event(){
        engine.getLoadWorker().workDoneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null&&newValue.doubleValue()==100.0){
               mask.setVisible(false);
            }else {
                mask.setVisible(true);
            }
        });
    }
}
