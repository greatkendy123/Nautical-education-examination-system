package navigation.controller;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import navigation.Main;
import navigation.model.Question;
import navigation.utils.Application;
import navigation.utils.DBManager;
import navigation.view.TestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class ExamController implements Initializable {
    @FXML
    private Pagination pages = null;

    private BorderPane panel1 = new BorderPane();
    private BorderPane panel2 = new BorderPane();

    private VBox topBox = new VBox();
    private HBox orderBox = new HBox();
    private Text order = new Text("第1题");
    private Text type = new Text("试题类型:选择题");
    private VBox typeBox = new VBox();

    private VBox center = new VBox();
    private HBox questionBox = new HBox();
    private Text question = new Text();
    private HBox pictureBox = new HBox();
    private ImageView imageView = new ImageView();

    private ToggleGroup toggleGroup = new ToggleGroup();
    private RadioButton A = new RadioButton("A");
    private RadioButton B = new RadioButton("B");
    private RadioButton C = new RadioButton("C");
    private RadioButton D = new RadioButton("D");


    private JFXButton past = new JFXButton("上一题",
            new ImageView(new Image(java.lang.ClassLoader.getSystemResourceAsStream("images/function/past.png"), 25.0, 25.0, false, true)));
    private JFXButton next = new JFXButton("下一题",
            new ImageView(new Image(java.lang.ClassLoader.getSystemResourceAsStream("images/function/next.png"), 25.0, 25.0, false, true)));
    private JFXButton mark = new JFXButton("标记",
            new ImageView(new Image(java.lang.ClassLoader.getSystemResourceAsStream("images/function/mark.png"), 25.0, 25.0, false, true)));
    private JFXButton cancelMark = new JFXButton("取消标记",
            new ImageView(new Image(java.lang.ClassLoader.getSystemResourceAsStream("images/function/cancel_mark.png"), 25.0, 25.0, false, true)));
    private JFXButton select = new JFXButton("选择题目",
            new ImageView(new Image(java.lang.ClassLoader.getSystemResourceAsStream("images/function/watch.png"), 25.0, 25.0, false, true)));
    private JFXButton handOn = new JFXButton("交卷",
            new ImageView(new Image(java.lang.ClassLoader.getSystemResourceAsStream("images/function/handon.png"), 25.0, 25.0, false, true)));
    private HBox bottomBox = new HBox(5.0);

    private VBox selectBox = new VBox();

    private JFXButton time = new JFXButton();
    private HBox timeBox = new HBox();

    private ScrollPane scrollPane1 = new ScrollPane();
    private ScrollPane scrollPane2 = new ScrollPane();
    private List<Question> list = new ArrayList();

    private int index = 0;
    private FlowPane flowPane = new FlowPane();
    private Group group = new Group(flowPane);
    private List<JFXButton> buttonGroup = new ArrayList<>();
    private Timer timer = new Timer();

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        event();
        setId();
        loadData();
    }

    private void initView() {

        scrollPane1.setFitToHeight(true);
        scrollPane1.setFitToHeight(true);
        scrollPane2.setFitToWidth(true);
        scrollPane2.setFitToHeight(true);

        topBox.setAlignment(Pos.CENTER_LEFT);
        typeBox.setAlignment(Pos.CENTER);


        time.setFont(Font.loadFont(ClassLoader.getSystemResourceAsStream("fonts/digifaw.ttf"), 30));
        time.setTextFill(Color.rgb(255, 0, 0));
        timeBox.setAlignment(Pos.CENTER_RIGHT);
        timeBox.getChildren().add(time);
        typeBox.setSpacing(5.0);
        typeBox.getChildren().addAll(type, timeBox);
        orderBox.getChildren().add(order);
        topBox.getChildren().addAll(typeBox, orderBox);

        pictureBox.setAlignment(Pos.CENTER);
        pictureBox.getChildren().add(imageView);
        question.wrappingWidthProperty().bind(panel1.widthProperty().multiply(0.8));

        questionBox.setAlignment(Pos.CENTER);
        questionBox.getChildren().add(question);

        A.prefWidthProperty().bind(scrollPane1.widthProperty().multiply(0.7));
        B.prefWidthProperty().bind(scrollPane1.widthProperty().multiply(0.7));
        C.prefWidthProperty().bind(scrollPane1.widthProperty().multiply(0.7));
        D.prefWidthProperty().bind(scrollPane1.widthProperty().multiply(0.7));
        A.setWrapText(true);
        B.setWrapText(true);
        C.setWrapText(true);
        D.setWrapText(true);
        A.setUserData("A");
        B.setUserData("B");
        C.setUserData("C");
        D.setUserData("D");

        toggleGroup.getToggles().addAll(A, B, C, D);
        selectBox.setSpacing(5.0);
        selectBox.getChildren().addAll(A, B, C, D);
        selectBox.setPadding(new Insets(10, 10, 10, 10));

        bottomBox.setPadding(new Insets(10.0));
        past.setDisable(true);
        bottomBox.getChildren().addAll(past, next, mark,
                cancelMark,select, handOn);

        center.setSpacing(10.0);
        center.getChildren().addAll(questionBox, pictureBox, selectBox);
        panel1.setTop(topBox);
        panel1.setCenter(center);
        panel1.setBottom(bottomBox);

        flowPane.setHgap(3.0);
        flowPane.setVgap(3.0);
        panel2.setCenter(group);

        for (int i = 0; i < 100; i++) {
            JFXButton button;
            if (i < 9) {
                button = new JFXButton("0" + (i + 1));

            } else {
                button = new JFXButton((i + 1) + "");

            }
            button.setUserData(i);
            button.setOnAction(e -> {
                if (Integer.parseInt(button.getUserData().toString()) != index) {
                    index = Integer.parseInt(button.getUserData().toString());
                    setData();
                }
                if (pages.getCurrentPageIndex() != 0) pages.setCurrentPageIndex(0);
            });
            button.getStyleClass().add("buttonGroup");
            buttonGroup.add(button);
            flowPane.getChildren().add(button);
        }

        panel1.minWidthProperty().bind(scrollPane1.widthProperty().multiply(0.998));
        scrollPane1.setContent(panel1);
        scrollPane2.setContent(panel2);


        pages.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        pages.setPageFactory(index -> initPage(index));


    }

    private void setId() {

        past.getStyleClass().add("function");
        next.getStyleClass().add("function");
        mark.getStyleClass().add("function");
        cancelMark.getStyleClass().add("function");
        select.getStyleClass().add("function");
        handOn.getStyleClass().add("function");
        type.setId("type");
        typeBox.setId("typeBox");
        bottomBox.setId("bottomBox");
        orderBox.setId("orderBox");
        A.getStyleClass().addAll("select");
        B.getStyleClass().addAll("select");
        C.getStyleClass().addAll("select");
        D.getStyleClass().addAll("select");
        question.setId("question");


    }

    private void event() {
        handOn.setOnAction(e -> {
            new TestResult(list,"exam");
            timer.cancel();
            handOn.getScene().getWindow().fireEvent(new Event(WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        next.setOnAction(e -> {
            ++index;
            if (index > 98) {
                next.setDisable(true);
            }
            if (past.isDisable()) past.setDisable(false);
            setData();

        });
        past.setOnAction(e -> {
            --index;
            if (index < 1) {
                past.setDisable(true);
            }
            if (next.isDisable()) next.setDisable(false);
            setData();
        });
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) list.get(index).setUserResult(newValue.getUserData().toString());
        }));
        mark.setOnAction(e -> {
            if (!list.get(index).isMark()) {
                list.get(index).setMark(true);
                mark.setDisable(true);
                if (cancelMark.isDisable()) cancelMark.setDisable(false);
                setMark(true);
            }

        });
        cancelMark.setOnAction(e -> {
            if (list.get(index).isMark()) {
                list.get(index).setMark(false);
                mark.setDisable(false);
                cancelMark.setDisable(true);
                setMark(false);
            }

        });
        select.setOnAction(e ->
                pages.setCurrentPageIndex(1)
        );


    }

    private void setMark(boolean isMark) {
        if (isMark) {
            buttonGroup.get(index).setStyle("-fx-border-color:red");
        } else {
            buttonGroup.get(index).setStyle("-fx-border-color: gray");
        }
    }


    private ScrollPane initPage(int index) {
        ScrollPane pane;
        if (index == 0) {
            pane = scrollPane1;
        } else {
            pane = scrollPane2;
        }
        return pane;
    }

    private void loadData() {
        Service<List<Question>> service = new Service<List<Question>>() {
            @Override
            protected Task<List<Question>> createTask() {
                return new Task<List<Question>>() {
                    @Override
                    protected List<Question> call() throws Exception {
                        return DBManager.getExamData();

                    }
                };
            }
        };
        service.setOnFailed(e -> {
            Notifications.create().text("加载失败！").position(Pos.TOP_CENTER).show();
            logger.error(service.getException().getMessage());
        });
        service.setOnSucceeded(e -> {
            list = service.getValue();
            setData();
            Application.recordTime(time,60,timer,handOn);
        });
        service.start();

    }

    private void setData() {


        PractiseController.setToggle(toggleGroup, list, index);
        if (list.get(index).isMark()) {
            mark.setDisable(true);
            if (cancelMark.isDisable()) cancelMark.setDisable(false);
        } else {
            cancelMark.setDisable(true);
            if (mark.isDisable()) mark.setDisable(false);
        }
        question.setText(list.get(index).getQuestion());
        A.setText(list.get(index).getA());
        B.setText(list.get(index).getB());
        C.setText(list.get(index).getC());
        D.setText(list.get(index).getD());
        if (!list.get(index).getPicturePath().trim().equals("")) {
            Image image = null;
            try {
                image = new Image(new FileInputStream(list.get(index).getPicturePath().trim()), 350, 250, false, true);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            imageView.setImage(image);
        } else {
            imageView.setImage(null);
        }
        order.setText("第" + (index + 1) + "题");
    }
}
