package heering.helloaloe;

/**
 * Created by Matthew on 11/30/2014.
 */
public class Plant {

    private long plantID;
    private String plantType;
    private String plantNickName;
    private int summerSchedule;
    private String plantLastWatered;
    private String plantImage;
    private int winterSchedule;
    private String soilType;
    private String lightType;
    private String waterDes;

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

    public int getSummerSchedule() {
        return summerSchedule;
    }

    public void setSummerSchedule(int summerSchedule) {
        this.summerSchedule = summerSchedule;
    }

    public int getWinterSchedule() {
        return winterSchedule;
    }

    public void setWinterSchedule(int winterSchedule) {
        this.winterSchedule = winterSchedule;
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

    public String getSoilType(){
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public void setLight(String light) {
        this.lightType = light;
    }

    public String getLightType(){
        return lightType;
    }

    public void setWaterDes(String waterDes) {
        this.waterDes = waterDes;
    }

    public String getWaterDes(){
        return waterDes;
    }
}
