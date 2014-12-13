package xml;

/**
 * Created by Vinnie on 05-Dec-14.
 */
public class CarTrip {
    public int id;
    public String vehicleType;
    public float depart;
    public String departLane;
    public float departPos;
    public float departSpeed;
    public float departDelay;
    public float arrival;
    public String arrivalLane;
    public float arrivalPos;
    public float arrivalSpeed;
    public float duration;     // avegage
    public float routeLength;  //  speed
    public float waitSteps;         // average waiting periods
    public float CO;                //average CO
    public float CO2;                //average CO2
    public float HC;                //average HC
    public float PMx;                //average PMx
    public float NOx;                //average NOx
    public float fuel;                //average fuel

    //σ = √[ ∑(x-mean)2 / N ]

    public void XMLCar() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public float getDepart() {
        return depart;
    }

    public void setDepart(float depart) {
        this.depart = depart;
    }

    public String getDepartLane() {
        return departLane;
    }

    public void setDepartLane(String departLane) {
        this.departLane = departLane;
    }

    public float getDepartPos() {
        return departPos;
    }

    public void setDepartPos(float departPos) {
        this.departPos = departPos;
    }

    public float getDepartSpeed() {
        return departSpeed;
    }

    public void setDepartSpeed(float departSpeed) {
        this.departSpeed = departSpeed;
    }

    public float getDepartDelay() {
        return departDelay;
    }

    public void setDepartDelay(float departDelay) {
        this.departDelay = departDelay;
    }

    public float getArrival() {
        return arrival;
    }

    public void setArrival(float arrival) {
        this.arrival = arrival;
    }

    public String getArrivalLane() {
        return arrivalLane;
    }

    public void setArrivalLane(String arrivalLane) {
        this.arrivalLane = arrivalLane;
    }

    public float getArrivalPos() {
        return arrivalPos;
    }

    public void setArrivalPos(float arrivalPos) {
        this.arrivalPos = arrivalPos;
    }

    public float getArrivalSpeed() {
        return arrivalSpeed;
    }

    public void setArrivalSpeed(float arrivalSpeed) {
        this.arrivalSpeed = arrivalSpeed;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(float routeLength) {
        this.routeLength = routeLength;
    }

    public float getWaitSteps() {
        return waitSteps;
    }

    public void setWaitSteps(float waitSteps) {
        this.waitSteps = waitSteps;
    }

    public float getCO() {
        return CO;
    }

    public void setCO(float CO) {
        this.CO = CO;
    }

    public float getCO2() {
        return CO2;
    }

    public void setCO2(float CO2) {
        this.CO2 = CO2;
    }

    public float getHC() {
        return HC;
    }

    public void setHC(float HC) {
        this.HC = HC;
    }

    public float getPMx() {
        return PMx;
    }

    public void setPMx(float PMx) {
        this.PMx = PMx;
    }

    public float getNOx() {
        return NOx;
    }

    public void setNOx(float NOx) {
        this.NOx = NOx;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }
}
