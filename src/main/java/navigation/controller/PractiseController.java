package navigation.controller;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import navigation.Main;
import navigation.model.Question;
import navigation.model.UnitSelectModel;
import navigation.view.NoteDialog;
import navigation.view.TestResult;
import navigation.view.UnitSelect;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import javafx.scene.layout.VBox;

import javafx.scene.text.Text;

import javafx.stage.Stage;
import navigation.utils.Application;
import navigation.utils.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;

public class PractiseController implements Initializable {
    @FXML
    private ToggleGroup selectGroup;
    @FXML
    private VBox questionContainer;
    @FXML
    private VBox cardContainer;
    @FXML
    private BorderPane container;
    @FXML
    private JFXButton handOn;
    @FXML
    private RadioButton A;
    @FXML
    private RadioButton B;
    @FXML
    private RadioButton C;
    @FXML
    private RadioButton D;
    @FXML
    private Text question;
    @FXML
    private Text order;
    @FXML
    private ImageView questionPicture;
    @FXML
    private JFXButton time;
    @FXML
    private FlowPane flowPane;
    @FXML
    private JFXButton addNote;
    @FXML
    private HBox select;

    private Stage stage;


    private Timer timer = new Timer();

    private Image image = null;

    private UnitSelectModel model;

    private List<Question> questions;


    private List<Button> list = new ArrayList<>();


    private int index = 0;

    private static final Logger logger = LogManager.getLogger(PractiseController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        event();
        loadData();
    }

    private void initView() {

        questionContainer.prefWidthProperty().bind(container.widthProperty().multiply(0.6));
        question.wrappingWidthProperty().bind(questionContainer.widthProperty().multiply(0.7));
        flowPane.setHgap(2.0);
        flowPane.setVgap(2.0);
        select.prefWidthProperty().bind(questionContainer.widthProperty().multiply(0.8));


    }

    private void event() {
        /**
         * 监控单选按钮变化
         */
        selectGroup.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {

            if (newValue != null) {
                questions.get(index).setUserResult(newValue.getUserData().toString());
                list.get(index).setStyle("-fx-border-width: 2px");
                list.get(index).setStyle("-fx-border-color: green");
                    if (index < 99&&questions.get(index+1).getUserResult()==null) {
                        ++index;
                        setData();
                    }

            }
        });
        /**
         *
         * 监控窗口关闭
         *
         */
        stage.setOnCloseRequest(e -> {
            dispose();
            if (questions.size() > 0) questions.clear();
            new UnitSelect("questions");
        });
        /**
         *
         * 提交试卷
         *
         */
        handOn.setOnAction(e -> {
            new TestResult(questions,"practise");
            dispose();
        });
        /**
         *
         * 添加笔记
         *
         */
        addNote.setOnAction(e ->
                new NoteDialog(questions.get(index))
        );

    }

    /**
     * 初始化数据
     *
     * @param model
     * @return
     */
    public PractiseController init(UnitSelectModel model, Stage stage) {
        this.model = model;
        this.stage = stage;
        return this;
    }

    /**
     * 销毁数据释放内存
     */
    public void dispose() {
        timer.cancel();
        stage.close();
    }

    /**
     * 异步加载数据,防止ui界面卡住
     */
    private void loadData() {
        Service<List<Question>> service = new Service<List<Question>>() {
            @Override
            protected Task<List<Question>> createTask() {
                return new Task<List<Question>>() {
                    @Override
                    protected List<Question> call() throws Exception {
                        List<Question> list = DBManager.getQuestios(Application.currentSubject.getDataBases(), model.getOrder());
                        return list;
                    }
                };
            }
        };
        service.setOnFailed(e ->
           logger.error(service.getException().getMessage())
        );
        service.setOnSucceeded(e -> {
            questions = service.getValue();
            setData();
            addButtonGroup(questions.size());
            Application.recordTime(time, 60, timer, handOn);
        });

        service.start();

    }

    void setData() {
        setToggle(selectGroup, questions, index);
        question.setText(questions.get(index).getQuestion());
        A.setText(questions.get(index).getA());
        B.setText(questions.get(index).getB());
        C.setText(questions.get(index).getC());
        D.setText(questions.get(index).getD());
        if (!questions.get(index).getPicturePath().trim().equals("")) {
            try {
                image = new Image(new FileInputStream(questions.get(index).getPicturePath().trim()), 350, 250, false, true);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            questionPicture.setImage(image);
        } else {
            questionPicture.setImage(null);
        }
        order.setText("第" + (index + 1) + "题");
    }

    void addButtonGroup(int size) {
        for (int i = 0; i < size; i++) {
            JFXButton button = new JFXButton();
            if (i < 9)
                button.setText("0" + (i + 1));
            else
                button.setText((i + 1) + "");
            button.setUserData(i);
            button.setOnAction(e -> {
                        if (Integer.parseInt(button.getUserData().toString()) != index) {
                            index = Integer.parseInt(button.getUserData().toString());
                            setData();
                        }
                    }
            );
            button.setStyle("-fx-border-color: red");
            list.add(button);
            flowPane.getChildren().add(button);
        }
    }

    public static void setToggle(ToggleGroup group, List<Question> list, int index) {
        if (list.get(index).getUserResult() != null) {
            group.getToggles().forEach(toggle -> {
                if (toggle.getUserData().equals(list.get(index).getUserResult())) {
                    toggle.setSelected(true);
                    return;
                }
            });
        } else {
            if (group.getSelectedToggle() != null) group.getSelectedToggle().setSelected(false);
        }
    }

}
