package trasmapi.sumo;

import trasmapi.genAPI.Lane;
import trasmapi.genAPI.exceptions.WrongCommand;
import trasmapi.sumo.protocol.*;

import java.io.IOException;
import java.util.ArrayList;

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
	public ArrayList<String> vehiclesList() {
        Command cmd = new Command(Constants.CMD_GET_LANE_VARIABLE);

        Content cnt = new Content(0x12, id);

        cmd.setContent(cnt);
        ArrayList<String> nada =new ArrayList<String>();
        //cmd.print("SetMaxSpeed");
        cmd.print("message sent from lane \n");
        RequestMessage reqMsg = new RequestMessage();
        reqMsg.addCommand(cmd);

        try {

            ResponseMessage rspMsg = SumoCom.query(reqMsg);
            Content content = rspMsg.validate( (byte)  Constants.CMD_GET_LANE_VARIABLE, (byte) Constants.RESPONSE_GET_LANE_VARIABLE,
                    (byte) 0x12, (byte)  Constants.TYPE_STRINGLIST);
                System.out.println("response message e esta\n");
                rspMsg.print();
           nada=  content.getStringList();

            return nada;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WrongCommand e) {
            e.printStackTrace();
        }

        return nada;
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
		return vehiclesList().size();
		/*int sum = 0;
		for (int i = 0; i<vl.length; i++)
			sum++;
		return sum;*/
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
