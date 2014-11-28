package start;

import agents.TrafficLightAgent;
import jade.wrapper.ContainerController;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;

public class AgentsManager {
    ArrayList<TrafficLightAgent> agents;
    // TODO: get all semaphores
    // TODO: get all lanes
    // TODO: get all configure neighbours

        /*TLManager manager = new TLManager(mainContainer);

        try {

            mainContainer.acceptNewAgent("MANAGER#1", manager).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
            return;
        }*/

    public AgentsManager(ContainerController mainContainer) {
        ArrayList<String> tlsIds = SumoTrafficLight.getIdList();


        for(String ltId: tlsIds) {

        }
    }
}
