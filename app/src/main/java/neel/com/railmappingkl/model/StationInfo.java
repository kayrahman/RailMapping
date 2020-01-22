package neel.com.railmappingkl.model;

public class StationInfo {

    private String title;
    private float distance;

    public StationInfo() {
    }

    public StationInfo(String title, float distance) {
        this.title = title;
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
