package navigation.controller;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import navigation.Main;
import navigation.model.Question;
import navigation.model.TestResultModel;
import navigation.utils.DBManager;
import navigation.utils.FileManager;
import navigation.utils.HtmlManager;
import navigation.view.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class TestResultController implements Initializable {
    @FXML
    private TableView table;
    @FXML
    private TextArea question;
    @FXML
    private TableColumn result;
    @FXML
    private TableColumn order;
    @FXML
    private TableColumn scoreValue;
    @FXML
    private TableColumn score;
    @FXML
    private TableColumn correctResult;
    @FXML
    private TableColumn userResult;
    @FXML
    private BorderPane container;
    @FXML
    private Button scoreButton;
    @FXML
    private Button save;
    private String type;
    private FileChooser fileChooser = new FileChooser();

    private int testScore = 0;
    private ObservableList<TestResultModel> obs = FXCollections.observableArrayList();
    private List<Question> list;
    private Stage stage = null;

    private static final Logger logger = LogManager.getLogger(TestResultController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        event();
        dataAnalysis();
    }

    private void initView() {
        fileChooser.setTitle("选择保存路径");
        fileChooser.setInitialFileName(new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".html");
        table.prefWidthProperty().bind(container.widthProperty().multiply(0.4));
        question.prefWidthProperty().bind(container.widthProperty().multiply(0.6));
        //禁止对数据进行排序
        table.setSortPolicy(e -> false);
        result.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        order.prefWidthProperty().bind(table.widthProperty().multiply(0.15));
        scoreValue.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        score.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        correctResult.prefWidthProperty().bind(table.widthProperty().multiply(0.16));
        userResult.prefWidthProperty().bind(table.widthProperty().multiply(0.16));

        question.setFont(Font.font(20.0));
        result.setCellValueFactory(new PropertyValueFactory<>("result"));
        order.setCellValueFactory(new PropertyValueFactory<>("order"));
        scoreValue.setCellValueFactory(new PropertyValueFactory<>("scoreValue"));
        score.setCellValueFactory(new PropertyValueFactory<>("score"));
        correctResult.setCellValueFactory(new PropertyValueFactory<>("correctResult"));
        userResult.setCellValueFactory(new PropertyValueFactory<>("userResult"));

        table.setItems(obs);


    }

    public TestResultController init(List<Question> list, Stage stage,String type) {
        this.stage = stage;
        this.list = list;
        this.type = type;
        return this;
    }

    private void event() {
        stage.setOnCloseRequest(e -> {
                    if (list.size() > 0) list.clear();
                    new Function();
                }
        );
        table.setOnMouseClicked(e -> {
            if (table.getSelectionModel().getSelectedIndex() != -1) {
                showHtml(table.getSelectionModel().getSelectedIndex());
            }
        });
        save.setOnAction(e -> {

            Notifications.create().text("保存必须为html格式,请勿修改文件后缀名！").position(Pos.TOP_CENTER).showWarning();
            File file = fileChooser.showSaveDialog(stage.getOwner());
            outTest(file);

        });

    }

    private void dataAnalysis() {
        Service<Integer> service = new Service<Integer>() {
            @Override
            protected Task<Integer> createTask() {
                return new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {

                        for (int i = 0; i < list.size(); i++) {
                            Question question = list.get(i);
                            TestResultModel model = new TestResultModel();
                            if (question.getUserResult() != null && question.getUserResult().equals(question.getCorrectResult())) {
                                model.setResult(" √");
                                model.setScore("1");
                                ++testScore;
                            } else {
                                if (question.getUserResult() != null) DBManager.insertMistake(question.getOrder());
                                model.setResult("×");
                                model.setScore("0");
                            }
                            model.setOrder((i + 1) + "");
                            model.setCorrectResult(question.getCorrectResult());
                            model.setUserResult(question.getUserResult());
                            model.setScoreValue(1 + "");
                            obs.add(model);
                        }
                        /**
                         * 如果时考试的话保存试卷
                         */
                        if (type.equals("exam")){
                            String fileName = UUID.randomUUID().toString();
                            FileManager.saveExam(list,fileName,testScore);
                            DBManager.saveGrade(fileName,testScore);
                        }

                        return testScore;
                    }
                };
            }
        };
        service.setOnSucceeded(e -> {
            scoreButton.setText("得分:" + service.getValue());
            showHtml(0);
        });
        service.setOnFailed(e -> {
            Notifications notify = Notifications.create();
            notify.position(Pos.TOP_CENTER);
            notify.text("评分过程发生未知错误!");
            notify.showError();
            logger.error(service.getException().getMessage());

        });
        service.start();
    }

    private void showHtml(int index) {
        question.setText(list.get(index).getQuestion() + "\r\n\n" +
                list.get(index).getA() + "\r\n\n"
                + list.get(index).getB() + "\r\n\n" +
                list.get(index).getC() + "\r\n\n" +
                list.get(index).getD() + "\r\n\n" +
                "正确答案:" + list.get(index).getCorrectResult() + "\r\n\n" +
                "你的答案:" + (list.get(index).getUserResult() == null ? ""
                : list.get(index).getUserResult()) + "\r\n\n" +
                "答案解析:" + list.get(index).getResultAnalysis());
    }

    /**
     * 输出本次考试试卷
     */
    private void outTest(File file) {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        StringBuffer sb = new StringBuffer("<html><head>" +
                                "<title>航海类教育考试系统V3.0</title>" +
                                "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />" +
                                "<style>" +
                                "div{" +
                                "margin:10.0;" +
                                "}" +
                                "li{font-size:20.0;" +
                                "margin-bottom:3.0;}" +
                                "</style>" +
                                "</head> <body>");
                        for (Question question : list) {
                            sb.append(HtmlManager.productOutputHtml(question));
                        }

                        sb.append("<div></div></body></html>");
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(sb.toString().getBytes());
                        outputStream.flush();
                        if (outputStream != null) outputStream.close();
                        return null;
                    }
                };
            }
        };
        service.setOnFailed(e -> logger.error(service.getException().getMessage()));
        service.setOnSucceeded(e -> Notifications.create().position(Pos.BOTTOM_RIGHT).text("文件保存成功！").showWarning());
        service.start();

    }
}
