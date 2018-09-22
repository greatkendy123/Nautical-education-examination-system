package navigation.view;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import navigation.Main;
import navigation.model.Question;
import navigation.utils.Application;
import navigation.utils.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NoteDialog extends Stage {
    private Scene scene;
    private Parent root;
    private final static double WIDTH = 300.0;
    private final static double HEIGHT =400.0;
    private Question question = null;
    private JFXButton save = null;
    private JFXButton cancel = null;
    private TextArea textArea = null;
    private boolean isExists = false;
    private static final Logger logger = LogManager.getLogger(NoteDialog.class.getName());

    public NoteDialog(Question question) {

        this.question = question;

        initView();
        event();
        initNote();
        this.setTitle("添加笔记");
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UNDECORATED);
        this.show();
    }
    private void initView(){
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/note_dialog_view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene = new Scene(root,WIDTH,HEIGHT);
        cancel = (JFXButton) root.lookup("#cancel");
        save   = (JFXButton) root.lookup("#save");
        textArea = (TextArea) root.lookup("#textArea");
        this.setScene(scene);
    }
    private  void event(){
        cancel.setOnAction(e->this.close());
        save.setOnAction(e->{
            if(!textArea.getText().trim().equals("")){
                try {
                    DBManager.insertNote(question.getOrder(),textArea.getText(),isExists);
                    this.close();
                } catch (SQLException e1) {
                    logger.error(e1.getMessage());
                    Notifications.create().text("添加笔记错误！").position(Pos.TOP_CENTER).showError();
                }
            }else {
                Notifications.create().text("笔记内容不能为空").position(Pos.TOP_CENTER).showError();
            }
        });

    }

    /**
     * 初始化笔记,查找数据库中师傅存在该笔记，如果存在取出来
     * 不存在就不做处理
     *
     */
    private void initNote(){
        Service<String> service = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        Connection con = null;
                        PreparedStatement psql = null;
                        ResultSet res= null;
                        String result = null;
                        try {
                            con = DBManager.getConnection();
                            psql = con.prepareStatement("SELECT note_content FROM user_note WHERE subject_name=? AND  question_id=? ");
                            psql.setString(1, Application.currentSubject.getSubjectName());
                            psql.setInt(2,question.getOrder());
                            res = psql.executeQuery();
                            if (res.next()){
                                result = res.getString(1);
                            }
                        }finally {
                            if (con!=null) con.close();
                            if (psql!=null) psql.close();
                            if (res!=null) res.close();
                        }
                        return result;
                    }
                };
            }
        };
        service.setOnFailed(e->{
            Notifications.create().position(Pos.TOP_CENTER).text("读取笔记失败!").showError();
            logger.error(service.getException().getMessage());
        });
        service.setOnSucceeded(e->{
            if (service.getValue()!=null) {
                textArea.setText(service.getValue());
                isExists = true;
            }
        });
        service.start();

    }
}
