package navigation.model;

/**
 * 科目信息表
 * @subjectName 科目名称
 * @dataBa 科目所在数据库名称
 * @number 该科目题库所具有数量
 *
 */
public class Subject {
    private String subjectName;
    private String dataBases;
    private int order;
    private String status;

    public Subject(String subjectName, String dataBases, int order, String status) {
        this.subjectName = subjectName;
        this.dataBases = dataBases;
        this.order = order;
        this.status = status;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDataBases() {
        return dataBases;
    }

    public void setDataBases(String dataBases) {
        this.dataBases = dataBases;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
