package navigation.model;

public class Caption {
    private String secondTile;
    private String threeTitle;

    public Caption(String secondTile, String threeTitle) {
        this.secondTile = secondTile;
        this.threeTitle = threeTitle;
    }

    public String getSecondTile() {
        return secondTile;
    }

    public void setSecondTile(String secondTile) {
        this.secondTile = secondTile;
    }

    public String getThreeTitle() {
        return threeTitle;
    }

    public void setThreeTitle(String threeTitle) {
        this.threeTitle = threeTitle;
    }

}
