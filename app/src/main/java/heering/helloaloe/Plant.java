package heering.helloaloe;

/**
 * Created by Matthew on 11/30/2014.
 */
public class Plant {

    private long plantID;
    private String plantType;
    private String plantNickName;
    private int plantSchedule;
    private String plantLastWatered;
    private String plantImage;

    public long getPlantID() {
        return plantID;
    }

    public void setPlantID(long plantID) {
        this.plantID = plantID;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getPlantImage() {
        return plantImage;
    }

    public void setPlantImage(String image) {
        this.plantImage = image;
    }

    public String getPlantNickName() {
        return plantNickName;
    }

    public void setPlantNickName(String plantNickName) {
        this.plantNickName = plantNickName;
    }

    public int getPlantSchedule() {
        return plantSchedule;
    }

    public void setPlantSchedule(int plantSchedule) {
        this.plantSchedule = plantSchedule;
    }

    public String getPlantLastWatered() {
        return plantLastWatered;
    }

    public void setPlantLastWatered(String plantLastWatered) {
        this.plantLastWatered = plantLastWatered;
    }

    @Override
    public String toString() {
        return this.plantType;
    }
}
