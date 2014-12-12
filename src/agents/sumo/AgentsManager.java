package agents.sumo;

import agents.TrafficLightAgent;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class AgentsManager {
    ArrayList<TrafficLightAgent> agents = new ArrayList<>();

    public AgentsManager(Sumo sumo, ContainerController mainContainer) {
        ArrayList<String> tlsIds = SumoTrafficLight.getIdList();

        for (String tlId : tlsIds) {
            System.out.println("Traffic light: " + tlId);
            SumoTrafficLight tl = new SumoTrafficLight(tlId);
            HashSet<String> lanes;
            lanes = new HashSet(tl.getControlledLanes());

            ArrayList<String> neighbours = new ArrayList<>();

            System.out.println("Neighbours: " + lanes.size());
            for (String l : lanes) {
                String n = getSrcFromLaneId(l);
                System.out.println(n);
                neighbours.add(n);
            }

            neighbours = reorderNeighbours(tlId, neighbours);

            TrafficLightAgent agent;

            try {
                agent = new TrafficLightAgent(sumo, tlId, neighbours);

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

    private ArrayList<String> reorderNeighbours(String pos, ArrayList<String> neighbours) {
        ArrayList<String> orderedNeighbours = new ArrayList<>();

        int col = Integer.parseInt(pos.split("/")[0]);
        int line = Integer.parseInt(pos.split("/")[1]);

        String upper = Integer.toString(col) + "/" + Integer.toString(line + 1);
        String righter = Integer.toString(col + 1) + "/" + Integer.toString(line);
        String below = Integer.toString(col) + "/" + Integer.toString(line - 1);
        String lefter = Integer.toString(col - 1) + "/" + Integer.toString(line);

        if (neighbours.contains(upper)) {
            orderedNeighbours.add(upper);
        }
        if (neighbours.contains(righter)) {
            orderedNeighbours.add(righter);
        }
        if (neighbours.contains(below)) {
            orderedNeighbours.add(below);
        }
        if (neighbours.contains(lefter)) {
            orderedNeighbours.add(lefter);
        }

        return orderedNeighbours;
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
