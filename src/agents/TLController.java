package agents;

import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;


public class TLController implements Runnable {
    private String name;
    private Sumo sumo;
    private ArrayList<String> neighbours;
    private TrafficLightAgent parentAgent;
    private int emergencyIndex = -1;

    private int[] greenTimeSpans;

    public TLController(TrafficLightAgent parent, Sumo sumo, String name, ArrayList<String> neighbours, int[] greenTimeSpans) {
        parentAgent = parent;
        this.name = name;
        this.sumo = sumo;
        this.neighbours = new ArrayList<>(neighbours);
        this.greenTimeSpans = new int[neighbours.size()];
        updateTimeSpans(greenTimeSpans);
        new Thread(new EmergencyChecker()).start();
    }

    public void updateTimeSpans(int[] newGreenTimeSpans) {
        synchronized (greenTimeSpans) {
            greenTimeSpans = newGreenTimeSpans.clone();
        }
    }

    @Override
    public void run() {
        int nrIntersections = neighbours.size();
        SumoTrafficLight light = new SumoTrafficLight(name);
        while (true) {
            for (int i = 0; i < nrIntersections; i++) {
                int greenTime;
                synchronized (greenTimeSpans) {
                    greenTime = greenTimeSpans[i];
                }

                String newState = buildState(i, "G");

                System.out.println("Changed " + name + " to " + newState + " for " + greenTime + " ticks");
                light.setState(newState);
                int initPhase = sumo.getCurrentSimStep() / 1000;
                int endPhase = initPhase;

                while (greenTime > (endPhase - initPhase + 5)) {
                    // if emergencyApproaching, change immediately
                    if (emergencyIndex != -1) {
                        break;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    endPhase = sumo.getCurrentSimStep() / 1000;
                }
                newState = buildState(i, "y");
                System.out.println("Changed " + name + " to " + newState + " for 5 ticks");
                light.setState(newState);

                endPhase = sumo.getCurrentSimStep() / 1000;
                while (greenTime > (endPhase - initPhase)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    endPhase = sumo.getCurrentSimStep() / 1000;
                }
                if (emergencyIndex != -1) {
                    i = emergencyIndex - 1;
                    emergencyIndex = -1;
                }
            }
            parentAgent.requestReward();
        }
    }

    private String buildState(int nr, String s) {
        int nrIntersections = neighbours.size();
        StringBuffer retStr = new StringBuffer();
        for (int i = 0; i < nrIntersections; i++) {
            if (i == nr) {
                retStr.append(stringMultiplier(s, nrIntersections - 1));
            } else if (i == nr + 1 || nr == nrIntersections - 1 && i == 0) {
                retStr.append(s + stringMultiplier("r", nrIntersections - 2));
            } else {
                retStr.append(stringMultiplier("r", nrIntersections - 1));
            }
        }

        return retStr.toString();
    }

    private String stringMultiplier(String str, int mult) {
        StringBuffer retStr = new StringBuffer();

        for (int i = 0; i < mult; i++) {
            retStr.append(str);
        }

        return retStr.toString();
    }

    public int getRewardForLane(String id) {
        SumoLane lane = new SumoLane(id);
        int numVehicles = 0;
        numVehicles += lane.getNumVehicles("nor");
        numVehicles += lane.getNumVehicles("pub") * 2;
        numVehicles += lane.getNumVehicles("eme") * 5;

        int laneDim = (int) Math.floor(lane.getLength());
        float ratio = (float) numVehicles / (float) laneDim;

        System.out.println("Reward for " + id + ": " + ratio);

        if (ratio > 0.5) {
            return 0;
        } else if (ratio > 0.25) {
            return 1;
        } else {
            return 2;
        }
    }

    private class EmergencyChecker implements Runnable {

        @Override
        public void run() {
            ArrayList<SumoLane> lanes = new ArrayList<>();
            for (String n : neighbours) {
                lanes.add(new SumoLane(n + "to" + name + "_0"));
            }
            while (true) {
                try {
                for (int i = 0; i < lanes.size(); i++) {
                    if (lanes.get(i).getNumVehicles("eme") != 0) {
                        emergencyIndex = i;
                    }
                    Thread.sleep(50);
                }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
