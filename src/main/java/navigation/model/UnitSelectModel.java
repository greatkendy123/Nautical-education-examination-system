package navigation.model;

public class UnitSelectModel {
    private int order;
    private  String title;
    private  String time;
    private  int score;
    private int times;

    public UnitSelectModel(int order, String title, String time, int score, int times) {

        this.order = order;
        this.title = title;
        this.time = time;
        this.score = score;
        this.times = times;

    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return  this.order+" "+this.getTitle()+" "+this.getTimes()+" "+this.getScore();
    }
}
