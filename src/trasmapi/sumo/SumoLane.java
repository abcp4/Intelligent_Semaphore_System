package trasmapi.sumo;

import trasmapi.genAPI.Lane;

public class SumoLane extends Lane {
	
	float length;
	
	public SumoLane(String id) {
		this.id = id;
	}
	
	public void setMaxSpeed(float val) {
		
	}
	
	/**
	 * method used to retrieve all the lane ids in the loaded network
	 * @return String[] - a String[] with all the lane ids
	 */
	public static String[] getLaneIdList() {
		return null;
		
	}

	/**
	 * returns a list of the vehicles in this lane
	 * @return the list of vehicles in this lane
	 */
	public SumoVehicle[] vehiclesList() {
		return null;
		
	}
	
	/**
	 * returns the number of vehicles stopped in this Lane
	 * @param minVel - threshold velocity to be considered stopped
	 * @return number of stopped vehicles
	 */
	public int getNumVehiclesStopped(Double minVel) {
		return 0;
	
	}
	
	/**
	 * returns the number of vehicles in this Lane
	 * @return number of stopped vehicles
	 */
	public int getNumVehicles() {
		SumoVehicle[] vl = vehiclesList();
		int sum = 0;
		for (int i = 0; i<vl.length; i++)
			sum++;
		return sum;
	}
	
	public boolean equals(SumoLane s) {
		return this.id.equals(s.id);
	}
	
	public String toString() {
		return id;
	}

	public void loadLength(){
	
	}
}
