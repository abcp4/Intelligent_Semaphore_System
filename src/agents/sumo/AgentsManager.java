package agents.sumo;

import agents.TrafficLightAgent;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;
import java.util.HashSet;

public class AgentsManager {
    ArrayList<TrafficLightAgent> agents = new ArrayList<>();
    // TODO: get all lanes
    // TODO: get all configure neighbours

        /*TLManager manager = new TLManager(mainContainer);

        try {

            mainContainer.acceptNewAgent("MANAGER#1", manager).agents.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
            return;
        }*/

    public AgentsManager(ContainerController mainContainer) {
        ArrayList<String> tlsIds = SumoTrafficLight.getIdList();

        for (String tlId : tlsIds) {
            System.out.println("Traffic light: " + tlId);
            SumoTrafficLight tl = new SumoTrafficLight(tlId);
            System.out.println("State: " + tl.getState());
            HashSet<String> lanes;
            lanes = new HashSet(tl.getControlledLanes());

            ArrayList<String> neighbours = new ArrayList<>();

            System.out.println("Neighbours: " + lanes.size());
            for (String l : lanes) {
                String n = getSrcFromLaneId(l);
                System.out.println(n);
                neighbours.add(n);
            }

            TrafficLightAgent agent;

            try {
                agent = new TrafficLightAgent(tlId, new ArrayList<>(lanes), neighbours);

                agents.add(agent);
                mainContainer.acceptNewAgent("TrafficLight-" + tlId, agent);

                System.out.println("Added " + agent.getLocalName());

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            System.out.println();
        }
    }

    public void startupAgents(ContainerController mainContainer) {
        try {
            for (TrafficLightAgent agent : agents) {
                mainContainer.getAgent(agent.getLocalName()).start();
                System.out.println("Started " + agent.getLocalName());
            }
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }

    private static String getSrcFromLaneId(String laneId) {
        return laneId.split("to")[0];
    }

    private static String getDestFromLaneId(String laneId) {
        return laneId.split("to")[1].split("_")[0];
    }
}
