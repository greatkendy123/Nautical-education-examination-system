package navigation.utils;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import navigation.model.Caption;
import navigation.model.Subject;

import java.net.InetAddress;
import java.util.*;

public class Application {
    /**
     * caption集合
     */
    public static List<Map<String, Caption>> captios = new ArrayList<>();

    /**
     * 科目信息集合
     */
    public static List<Subject> subjects = new ArrayList<>();
    /**
     * 当前科目信息
     */
    public static Subject currentSubject = null;
    /***
     *
     * 本机ip地址
     *
     */
    public static String ipAddress = InetAddress.getLoopbackAddress().getHostAddress();

    /***
     * 服务器ip地址
     *
     */
    public static String server = "localhost:8080";

    private static long startTime = 0;
    private static long endTime = 0;
    private static long midTime = 0;

    /**
     * 根据type获取相应的caption
     *
     * @param type
     * @return
     */
    public static Caption getCaption(String type) {

        for (Map<String, Caption> caption : Application.captios) {
            for (Map.Entry<String, Caption> item : caption.entrySet()) {
                if (item.getKey().equals(type)) {
                    return item.getValue();
                }
            }
        }
        return new Caption("", "");
    }
    /**
     * 定时器负责记录考试用时
     *
     */
    public static void recordTime(JFXButton button, int length, Timer timer,JFXButton handOn){
         startTime = System.currentTimeMillis();
         endTime = length*60*1000+startTime;
           midTime = (endTime-startTime)/1000;
         timer.schedule(new TimerTask() {
             @Override
             public void run() {
                 --midTime;
                 if (midTime >= 0) {
                     Platform.runLater(() -> button.setText(midTime / 60 / 60 % 60 + ":" + midTime / 60 % 60 + ":" + midTime % 60));
                 } else {
                     timer.cancel();
                     Platform.runLater(()-> handOn.fire());
                 }
             }
         },0,1000);

    }
}
