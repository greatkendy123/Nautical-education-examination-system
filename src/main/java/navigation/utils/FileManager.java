package navigation.utils;

import navigation.model.Question;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileManager {
    /**
     * 生成模板样卷
     * @param list 测试题目
     * @param fileName 文件名
     * @param testScore 总分
     */
    public static void saveExam(List<Question> list, String fileName,int testScore) throws IOException {
        final File dir = new File("./data/subjects"+File.separator+Application.currentSubject.getSubjectName()+File.separator+"exam");
        if (!dir.exists()){
            dir.mkdirs();
        }
        final File filePath = new File(dir.getAbsolutePath() + File.separator + fileName + ".html");
        if (!filePath.exists()) filePath.createNewFile();
        StringBuffer sb = new StringBuffer("<html>" +
                "<head>" +
                "<title>航海类教育考试系统</title>" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
                "<link rel='stylesheet' href='../../../css/exam.css'/>" +
                "<head/>" +
                "<body><div id='container'>" +
                "<fieldset>" +
                "<div>" +
                "<h1 id='title'>《"+Application.currentSubject.getSubjectName()+"》考生试卷分析</h1>" +
                "<div id='info'>" +
                "<div>总题数:"+list.size()+"</div>" +
                "<div><label>试卷生成时间:"+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +"</label></div>" +
                "<div><label"+(testScore>60?" style=\"color:green\"":" style=\"color:red;\"")+">分数:"+testScore+"</label></div>" +
                "</div>" +
                "</div>" +
                "</fieldset>"
        );
        for (int i=0;i<list.size();i++){
            sb.append("<fieldset class='question'>" +
                    "<legend>第"+(i+1)+"题</legend>" +
                    "<div>" +
                    "<p>"+list.get(i).getQuestion()+"</p>"
                    +"<p>"+list.get(i).getA()+"</p>" +
                    "<p>"+list.get(i).getB()+"</p>" +
                    "<p>"+list.get(i).getC()+"</p>" +
                    "<p>"+list.get(i).getD()+"</p>" +
                    "<p>正确答案:"+list.get(i).getCorrectResult()+"</p>" +
                    "<p>你的答案:"+(list.get(i).getUserResult()==null?"":list.get(i).getUserResult())+"</p>" +
                    "<p>答案解析:"+list.get(i).getResultAnalysis()+"</p>"+
                    "<div>" +
                    "</fieldset>");
        }
        sb.append("<div alignment='center' id='support'>" +
                "<h3>飞翔的企鹅提供技术支持</h3>" +
                "</div>");
        sb.append("</div></body></html>");
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(sb.toString().getBytes());
        out.flush();
        out.close();
    }
}
