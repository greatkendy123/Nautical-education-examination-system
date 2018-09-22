package navigation.controller;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import navigation.model.Question;
import navigation.utils.DBManager;
import navigation.view.NoteDialog;
import navigation.view.ShowSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MistakeController implements Initializable {
    @FXML
    private Label order;
    @FXML
    private Text question;
    @FXML
    private ImageView questionImage;
    @FXML
    private RadioButton A;
    @FXML
    private RadioButton B;
    @FXML
    private RadioButton C;
    @FXML
    private RadioButton D;
    @FXML
    private JFXButton past;
    @FXML
    private JFXButton next;
    @FXML
    private JFXButton remove;
    @FXML
    private JFXButton select;
    @FXML
    private JFXButton addNote;
    @FXML
    private JFXButton feedBack;
    @FXML
    private JFXButton analysis;
    @FXML
    private TextArea questionResult;
    @FXML
    private ScrollPane scroll;
    @FXML
    private ToggleGroup selectGroup;

    private List<Question> questions;

    private int index = 0;

    private ShowSelect showSelect = null;

    private Logger logger = LogManager.getLogger(MistakeController.class.getName());


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        event();
        loadData();
    }


    private void initView() {
        question.wrappingWidthProperty().bind(scroll.widthProperty().multiply(0.9));
        questionResult.prefWidthProperty().bind(scroll.widthProperty().multiply(0.9));

    }

    private void event() {
        next.setOnAction(e -> {
            if (index < questions.size() - 1 && questions.size() > 0) {
                index++;
                setData();
            }
        });
        past.setOnAction(e -> {
            if (index > 0) {
                index--;
                setData();
            }
        });
        selectGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                questions.get(index).setUserResult(newValue.getUserData().toString());
                showSelect.setMark(index);
                if (!newValue.getUserData().toString().equals(questions.get(index).getCorrectResult())) {
                    setResult();
                    questionResult.setVisible(true);
                }
            }
        }));
        analysis.setOnAction(e -> questionResult.setVisible(true));
        addNote.setOnAction(e -> new NoteDialog(questions.get(index)));
        select.setOnAction(e -> {
            if (questions.size() > 0) {
                showSelect.show();
            }
        });
        feedBack.setOnAction(e -> Notifications.create().text("无网络,无法反馈").position(Pos.TOP_CENTER).showError());
        remove.setOnAction(e -> {
            int result = 0;
            try {
                result = new DBManager().removeMistake(questions.get(index).getOrder());
            } catch (SQLException e1) {
                e1.printStackTrace();
                logger.error(e1.getMessage());
            }
            if (result > 0) {
                Notifications.create().position(Pos.TOP_CENTER).text("移除成功,下次将不会再展示该题!").showInformation();
            } else {
                Notifications.create().position(Pos.TOP_CENTER).text("移除失败!").showError();
            }
        });
    }


    private void loadData() {
        Service<List<Question>> service = new Service<List<Question>>() {
            @Override
            protected Task<List<Question>> createTask() {
                return new Task<List<Question>>() {
                    @Override
                    protected List<Question> call() throws Exception {
                        return DBManager.getMistakeData();
                    }
                };
            }
        };
        service.setOnFailed(e -> {
            Notifications.create().position(Pos.TOP_CENTER).text("加载数据出错!").showError();
            logger.error(service.getException().getMessage());
        });
        service.setOnSucceeded(e -> {
            questions = service.getValue();
            setData();
            showSelect = new ShowSelect(questions.size(), this);

        });

        service.start();
    }

    private void setData() {
        if (questions.size() > 0) {
            PractiseController.setToggle(selectGroup, questions, index);
            if (questions.get(index).getUserResult() != null &&
                    !questions.get(index).getUserResult().equals(questions.get(index).getCorrectResult())) {
                questionResult.setVisible(true);
            } else {
                questionResult.setVisible(false);
            }
            question.setText(questions.get(index).getQuestion());
            A.setText(questions.get(index).getA());
            B.setText(questions.get(index).getB());
            C.setText(questions.get(index).getC());
            D.setText(questions.get(index).getD());
            if (!questions.get(index).getPicturePath().trim().equals("")) {
                Image image = null;
                try {
                    image = new Image(new FileInputStream(questions.get(index).getPicturePath().trim()), 350, 250, false, true);

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                questionImage.setImage(image);
                questionImage.setVisible(true);
            } else {
                questionImage.setImage(null);
                questionImage.setVisible(false);
            }
            setResult();
        }else {
            Notifications.create().text("错题数目为空!").position(Pos.TOP_CENTER).showInformation();
        }
    }

    public void setResult() {

        questionResult.setText("正确答案:" + questions.get(index).getCorrectResult() + "\r\n" +
                "你的答案:" + (questions.get(index).getUserResult()
                == null ? "" : questions.get(index).getUserResult()) + "\r\n" +
                "答案解析:" + questions.get(index).getResultAnalysis());
        order.setText("第" + (index + 1) + "题");

    }

    /**
     * 回调函数
     *
     * @param index
     */
    public void callBack(int index) {
        this.index = index;
        setData();
    }
}
