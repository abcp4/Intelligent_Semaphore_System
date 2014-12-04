package addons;

import java.util.ArrayList;

import trasmapi.genAPI.TrafficLight;
import trasmapi.genAPI.Vehicle;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TlAgent extends Agent{


	private static final long serialVersionUID = 6095960260125307076L;

	private String id;
	public TrafficLight tl;

	private TLGUI tlGUI;

	public ArrayList<String> controlledLanes;



	public TlAgent(String tlID) {

		super();

		try {
			
			System.out.println("garbage.TlAgent id: "+this.id);

			tl = new SumoTrafficLight(tlID);

			controlledLanes = tl.getControlledLanes();

			System.out.println("lanes: " + controlledLanes);
			SumoLane pilas = new SumoLane(controlledLanes.get(0));
            Vehicle cenas[]= pilas.vehiclesList();
			System.out.println("vehiculos na lane" + pilas.getId() + ": " + pilas.getNumVehicles());

            for(int i=0;i<cenas.length;i++){
                System.out.println("veiculo com id " + cenas[i].id);

                System.out.println("tipo= "+ cenas[i].getTypeId());
                System.out.println("co2 emissions " +cenas[i].getCO2emission() + "mg/s");
            }

			tlGUI = new TLGUI(this);

			tlGUI.setVisible(true);
			
		} catch (UnimplementedMethod e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see jade.core.Agent#setup()
	 */
	@Override
	protected void setup() {

		DFAgentDescription ad = new DFAgentDescription();
		ad.setName(getAID()); //agentID
		System.out.println("AID: "+ad.getName());

		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName()); //nome do agente    
		System.out.println("Nome: "+sd.getName());

		sd.setType("TrafficLightManager");
		System.out.println("Tipo: "+sd.getType()+"\n\n\n");

		ad.addServices(sd); 

		try {
			DFService.register(this, ad);
		} catch(FIPAException e) {
			e.printStackTrace();
		}


		super.setup();
	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);  
		} catch(FIPAException e) {
			e.printStackTrace();
		}
		super.takeDown();
	}

}
