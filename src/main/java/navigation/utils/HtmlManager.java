package navigation.utils;

import navigation.model.Question;

public class HtmlManager {
    public static String productHtml(Question question){
        String html="<html><head>" +
                "<title>航海类教育考试系统V30" +
                "</title><meta charset=utf-8/>" +
                "<style>" +
                "div{" +
                "margin:10.0;" +
                "}" +
                "li{font-size:20.0;" +
                "margin-bottom:3.0;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div id='question'><h3>"+question.getQuestion()+"</h3></div>" +
                "<div>" +
                "<ul>" +
                "<li>"+question.getA()+"</li>" +
                "<li>"+question.getB()+"</li>" +
                "<li>"+question.getC()+"</li>" +
                "<li>"+question.getD()+"</li>" +
                "</ul>" +
                "<h4>正确答案:"+question.getCorrectResult()+"</h4>" +
                "<h4>你的答案："+(question.getUserResult()==null?"":question.getUserResult())+"</h4>" +
                "<h4>答案解析:"+(question.getResultAnalysis()==null?"":question.getResultAnalysis())+"</h4></div>" +
                "</body>" +
                "</html>";
        return html;
    }
    public static String productOutputHtml(Question question){
        String html=
                "<div id='question'><h3>"+question.getQuestion()+"</h3></div>" +
                "<div>" +
                "<ul>" +
                "<li>"+question.getA()+"</li>" +
                "<li>"+question.getB()+"</li>" +
                "<li>"+question.getC()+"</li>" +
                "<li>"+question.getD()+"</li>" +
                "</ul>" +
                "<h4>正确答案:"+question.getCorrectResult()+"</h4>" +
                "<h4>你的答案："+(question.getUserResult()==null?"":question.getUserResult())+"</h4>" +
                "<h4>答案解析:"+(question.getResultAnalysis()==null?"":question.getResultAnalysis())+"</h4></div>";
        return html;
    }
}
