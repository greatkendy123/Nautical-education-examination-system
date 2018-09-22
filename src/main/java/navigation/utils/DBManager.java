package navigation.utils;

import com.alibaba.druid.pool.DruidDataSource;

import navigation.model.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 数据库管理综合类
 */
public class DBManager {
    private static DruidDataSource dataSource = null;
    private static final Properties config = new Properties();
    private static final Logger logger = LogManager.getLogger(DBManager.class.getName());

    /**
     * 静态块加载JDBC驱动
     */
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            config.load(new FileInputStream("./config/database.properties"));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        initDataSource();

    }

    /**
     * 初始化druid数据库连接池
     */
    private static void initDataSource() {
        dataSource = new DruidDataSource();
        dataSource.setUrl(config.getProperty("url"));
        dataSource.setMaxActive(Integer.parseInt(config.getProperty("maxActive")));
        dataSource.setMinIdle(Integer.parseInt(config.getProperty("minIdle")));
        dataSource.setMaxWait(Integer.parseInt(config.getProperty("maxWait")));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(config.getProperty("testWhile")));

    }

    /**
     * 从Durid数据库连接池中获取Connection对象
     */
    public static Connection getConnection() {
        if (dataSource == null) {
            initDataSource();
        }
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * 关闭连接池
     */
    public static void closeDataBases() {
        if (dataSource != null) dataSource.close();
    }

    /**
     * 初始化数据库表
     */
    public static void initData() throws SQLException {
        Connection con;
        Statement sql;
        ResultSet res;
        con = getConnection();
        sql = con.createStatement();
        /**
         * 创建科目信息表
         *
         * */
        sql.execute("CREATE TABLE IF NOT EXISTS subject(id INTEGER PRIMARY KEY  AUTOINCREMENT," +
                "subject_name VARCHAR(30) NOT NULL,data_table_name VARCHAR(30) NOT NULL, subject_status VARCHAR NOT NULL," +
                "question_number VARCHAR NOT NULL)");
        /**
         * 创建Caption数据表
         * */
        sql.execute("CREATE TABLE IF NOT EXISTS caption(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "second_title VARCHAR NOT NULL,three_title VARCHAR NOT NULL,sub_type VARCHAR NOT NULL)");
        /**
         * 创建用户笔记表 user_note
         *
         */
        sql.execute("CREATE TABLE IF NOT EXISTS user_note(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "subject_name TEXT NOT NULL,databases TEXT NOT NULL,question_id TEXT NOT NULL,note_content TEXT NOT NULL)");
        /**
         * 获取科目信息表
         * */
        res = sql.executeQuery("SELECT * FROM subject");
        int order = 1;
        while (res.next()) {
            Subject subject = new Subject(res.getString("subject_name"),
                    res.getString("data_table_name"), order++, res.getString("subject_status"));
            Application.subjects.add(subject);
        }
        /***
         * 获取Caption中的数据
         */

        res = sql.executeQuery("SELECT second_title,three_title,sub_type FROM caption");
        while (res.next()) {
            Map<String, Caption> map = new HashMap<>();
            map.put(res.getString("sub_type"), new Caption(res.getString("second_title"), res.getString("three_title")));
            Application.captios.add(map);
        }
        /**
         * 创建错题题表
         *
         */
        sql.execute("CREATE TABLE IF NOT EXISTS mistake(id INTEGER PRIMARY KEY AUTOINCREMENT,subject_name TEXT NOT NULL," +
                "question_id INTEGER NOT NULL,times TEXT NOT NULL)");
        /***
         * 创建成绩表
         */
        sql.execute("CREATE TABLE IF NOT EXISTS grade(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "score INTEGER(10) NOT NULL,subject_name VARCHAR(50) NOT NULL," +
                "test_date VARCHAR(30) NOT NULL," +
                "file_path VARCHAR(100) NOT NULL)");
        /**
         * 创建科目表
         */
        for (Subject subject : Application.subjects) {
            sql.execute("CREATE TABLE IF NOT EXISTS " + subject.getDataBases() +
                    "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "question TEXT NOT NULL," +
                    "a TEXT NOT NULL," +
                    "b TEXT NOT NULL," +
                    "c TEXT," +
                    "d tTEXT," +
                    "correct_result TEXT NOT NULL," +
                    "picture_path TEXT," +
                    "chapter TEXT NOT NULL," +
                    "result_analysis TEXT" +
                    ")");
        }
        /**
         * 关闭连接操作(将连接返回给数据库系统连接池)
         */
        if (con != null) con.close();
        if (sql != null) sql.close();
        if (res != null) res.close();
    }

    /**
     * 根据传过来的数据库表名称查取该表所具有的单元数
     */
    public static List<UnitSelectModel> getUnitList(String tableName, String subjectName) throws SQLException {
        List<Integer> list = new ArrayList<>();
        Connection con = getConnection();
        PreparedStatement psql = con.prepareStatement("SELECT DISTINCT chapter FROM " + tableName);
        ResultSet res = psql.executeQuery();
        while (res.next()) {
            list.add(res.getInt(1));
        }
        Collections.sort(list);
        List<UnitSelectModel> units = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            psql = con.prepareStatement("SELECT COUNT(chapter) FROM " + tableName + " WHERE chapter=" + list.get(i));
            res = psql.executeQuery();
            if (res.next()) units.add(new UnitSelectModel(i + 1, subjectName + "真考题库", "100", res.getInt(1), 0));
        }

        if (con != null) con.close();
        if (psql != null) psql.close();
        if (res != null) res.close();

        return units;
    }

    /**
     * 根据传过来的参数获取对应的题库
     */
    public static List<Question> getQuestios(String tableName, int chapter) throws SQLException {
        List<Question> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        try {
            con = getConnection();
            psql = con.prepareStatement("SELECT * FROM " + tableName + " WHERE chapter=" + chapter);
            res = psql.executeQuery();
            loadQuestion(list, res);
        } finally {

            if (con != null) con.close();
            if (psql != null) con.close();
            if (res != null) res.close();
        }
        return list;
    }

    /**
     * 插入笔记
     */
    public static int insertNote(int id, String content, boolean isExists) throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        int result = 0;
        try {
            con = getConnection();
            if (!isExists) {

                psql = con.prepareStatement("INSERT INTO user_note(subject_name,databases,question_id,note_content) VALUES(?,?,?,?)");
                psql.setString(1, Application.currentSubject.getSubjectName());
                psql.setString(2, Application.currentSubject.getDataBases());
                psql.setInt(3, id);
                psql.setString(4, content);
            } else {
                psql = con.prepareStatement("UPDATE user_note SET note_content = ? WHERE subject_name = ? AND question_id=?");
                psql.setString(1, content);
                psql.setString(2, Application.currentSubject.getSubjectName());
                psql.setInt(3, id);
            }
            result = psql.executeUpdate();

        } finally {
            if (con != null) con.close();
            if (psql != null) psql.close();

        }
        return result;
    }

    /**
     * 抽取数据库中的用户笔记
     */
    public static String getUserNote() throws SQLException {
        StringBuffer sb = new StringBuffer();
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        try {
            con = getConnection();
            psql = con.prepareStatement("SELECT " + Application.currentSubject.getDataBases() + ".*,user_note.note_content FROM" +
                    " " + Application.currentSubject.getDataBases() + ",user_note WHERE " + Application.currentSubject.getDataBases() +
                    ".id=user_note.question_id");

            res = psql.executeQuery();

            while (res.next()) {
                sb.append(res.getString("id") + " " + res.getString("question") + "\r\n" +
                        res.getString("a") + "\r\n" +
                        res.getString("b") + "\r\n" +
                        res.getString("c") + "\r\n" +
                        res.getString("d") + "+\r\n" +
                        "正确答案:" + res.getString("correct_result") + "\r\n" +
                        "答案解析:" + res.getString("result_analysis") + "\r\n" +
                        "笔记:" + res.getString("note_content") + "\r\n"
                );
                sb.append("\r\n");
            }

        } finally {

            if (con != null) con.close();
            if (psql != null) psql.close();
            if (res != null) res.close();
        }

        return sb.toString();
    }

    /**
     * 加载模拟考试试题
     */
    public static List<Question> getExamData() throws SQLException {
        List<Question> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        Map<Integer, Integer> map = new HashMap<>();
        int totle = 0;
        try {
            con = getConnection();
            psql = con.prepareStatement("SELECT COUNT(id) FROM " + Application.currentSubject.getDataBases());
            res = psql.executeQuery();
            if (res.next()) {
                totle = res.getInt(1);
            }
            if (totle == 0) {
                return list;
            }
            while (map.size() < 100) {
                int question_id = 1 + (int) (Math.random() * totle);
                map.put(question_id, question_id);
            }
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                psql = con.prepareStatement("SELECT * FROM " + Application.currentSubject.getDataBases() + " WHERE  id=?");
                psql.setInt(1, entry.getValue());
                res = psql.executeQuery();
                loadQuestion(list, res);
            }

        } finally {
            if (con != null) con.close();
            if (psql != null) psql.close();
            if (res != null) res.close();
        }

        return list;
    }

    /**
     * 将题目装入Model中
     */
    private static void loadQuestion(List<Question> list, ResultSet res) throws SQLException {
        while (res.next()) {

            Question question = new Question();
            question.setOrder(res.getInt("id"));
            question.setQuestion(res.getString("question"));
            question.setA(res.getString("a"));
            question.setB(res.getString("b"));
            question.setC(res.getString("c"));
            question.setD(res.getString("d"));
            question.setChapter(res.getString("chapter"));
            question.setPicturePath(res.getString("picture_path"));
            question.setCorrectResult(res.getString("correct_result"));
            question.setResultAnalysis(res.getString("result_analysis"));
            list.add(question);

        }

    }

    /**
     * 插入数据库错题记录
     */
    public static void insertMistake(int id) throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        try {
            int times = 0;
            con = getConnection();
            psql = con.prepareStatement("SELECT times FROM mistake WHERE question_id=? AND subject_name=?");
            psql.setInt(1, id);
            psql.setString(2, Application.currentSubject.getSubjectName());
            res = psql.executeQuery();
            if (res.next()) times = res.getInt(1);
            if (times != 0)
                psql = con.prepareStatement("UPDATE mistake SET times=? WHERE question_id=? AND subject_name=?");
            else psql = con.prepareStatement("INSERT INTO mistake(times,question_id,subject_name) VALUES(?,?,?)");
            psql.setInt(1, times == 0 ? 1 : times + 1);
            psql.setInt(2, id);
            psql.setString(3, Application.currentSubject.getSubjectName());
            psql.executeUpdate();
        } finally {
            if (con != null) con.close();
            if (psql != null) psql.close();
            if (res != null) res.close();
        }

    }

    public static List<Question> getMistakeData() throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        List<Question> list = new ArrayList<>();
        List<Integer> questionIds = new ArrayList<>();
        try {
            con = getConnection();
            psql = con.prepareStatement("SELECT question_id FROM mistake WHERE subject_name=?");
            psql.setString(1, Application.currentSubject.getSubjectName());
            res = psql.executeQuery();
            while (res.next()) {
                questionIds.add(res.getInt(1));
            }
            for (int i = 0; i < questionIds.size(); i++) {
                psql = con.prepareStatement("SELECT * FROM " + Application.currentSubject.getDataBases() + " WHERE id=?");
                psql.setInt(1, questionIds.get(i));
                res = psql.executeQuery();
                loadQuestion(list, res);
            }

        } finally {
            if (con != null) con.close();
            if (psql != null) psql.close();
            if (res != null) res.close();
        }
        return list;
    }

    /**
     * 移除指定错题
     */
    public int removeMistake(int index) throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;

        int result = 0;
        try {

            con = getConnection();
            psql = con.prepareStatement("DELETE FROM mistake WHERE question_id=? AND subject_name=?");
            psql.setInt(1, index);
            psql.setString(2, Application.currentSubject.getSubjectName());
            result = psql.executeUpdate();

        } finally {
            if (con != null) con.close();
            if (psql != null) psql.close();
        }

        return result;
    }
    /**
     * 保存背刺考试记录
     *
     */
    public static void saveGrade(String fileName,int testScore) throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        System.out.println(testScore);
        try {
            con = getConnection();
            psql = con.prepareStatement("INSERT INTO grade(subject_name,score,file_path,test_date) VALUES(?,?,?,?)");
            psql.setString(1,Application.currentSubject.getSubjectName());
            psql.setInt(2,testScore);
            psql.setString(3,new File("./data/subjects/"+Application.currentSubject.getSubjectName()
                    +File.separator+"exam"+File.separator+fileName+".html").getAbsolutePath());
            psql.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            psql.executeUpdate();
        }finally {
            if (con!=null) con.close();
            if (psql!=null) psql.close();
            if (res!=null) res.close();
        }
    }
    /***
     * 读取成绩信息
     */
    public static List<GradeModel> readGrade() throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        List<GradeModel> grades = new ArrayList<>();
        try {
            con = getConnection();
            psql = con.prepareStatement("SELECT*FROM grade WHERE subject_name=?");
            psql.setString(1,Application.currentSubject.getSubjectName());
            res = psql.executeQuery();
            int index =1;
            while (res.next()){
                GradeModel model = new GradeModel();
                model.setPath(res.getString("file_path"));
                model.setOrder(index);
                model.setDate(res.getString("test_date"));
                model.setScore(res.getInt("score"));
                grades.add(model);
                index++;
            }

        }finally {
            if (con!=null) con.close();
            if (psql!=null) psql.close();
            if (res!=null)res.close();
        }
        return grades;
    }
    public static double getAverageScore() throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        ResultSet res = null;
        double averageScore = 0;
        try {
            con = getConnection();
            psql = con.prepareStatement("SELECT AVG(score) FROM grade WHERE subject_name=?");
            psql.setString(1,Application.currentSubject.getSubjectName());
            res = psql.executeQuery();
            if (res.next()) averageScore = res.getDouble(1);
        }finally {
            if (con!=null) con.close();
            if (psql!=null) psql.close();
            if (res!=null) res.close();
        }
        return averageScore;
    }

    /**
     * 删除某一条成绩记录
     * @param path
     * @return
     * @throws SQLException
     */
    public static int deleteGrade(String path) throws SQLException {
        Connection con = null;
        PreparedStatement psql = null;
        int result = 0;
        try{
            con=getConnection();
            psql = con.prepareStatement("DELETE FROM grade WHERE subject_name=? AND file_path=?");
            psql.setString(1,Application.currentSubject.getSubjectName());
            psql.setString(2,path);
            result = psql.executeUpdate();
        }finally {
            if (con!=null) con.close();
            if (psql!=null) psql.close();
        }
        return result;
    }

}
