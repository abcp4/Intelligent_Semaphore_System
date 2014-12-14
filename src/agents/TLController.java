package agents;

import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoLane;
import trasmapi.sumo.SumoTrafficLight;
import utils.Logger;

import java.util.ArrayList;
import java.util.Random;

public class TLController implements Runnable {

    private final double LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB;

    private String name;
    private Sumo sumo;
    private ArrayList<String> neighbours;
    private TrafficLightAgent parentAgent;
    private Integer emergencyIndex = -1;

    private int[] greenTimeSpans;

    public TLController(TrafficLightAgent parent, Sumo sumo, String name, ArrayList<String> neighbours, int[] greenTimeSpans) {
        LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB = 1.0 / (float) neighbours.size();
        parentAgent = parent;
        this.name = name;
        this.sumo = sumo;
        this.neighbours = new ArrayList<>(neighbours);
        this.greenTimeSpans = new int[neighbours.size()];
        updateTimeSpans(greenTimeSpans);

        if (!TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            new Thread(new EmergencyChecker()).start();
        }
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
        int savedIndex = -1;
        int firstI = new Random().nextInt(nrIntersections);
        while (true) {
            for (int i = firstI; i < nrIntersections; i++) {
                control(light, i, savedIndex, nrIntersections);
            }

            for (int i = 0; i < firstI; i++) {
                control(light, i, savedIndex, nrIntersections);
            }
        }
    }

    private void control(SumoTrafficLight light, int i, int savedIndex, int nrIntersections) {
        int greenTime;
        synchronized (greenTimeSpans) {
            greenTime = greenTimeSpans[i];
        }

        if (!TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            new Thread(new Rewarder(i)).start();
        }

        String newState = buildState(i, "G");

        Logger.logSumo(name + " - Changed to " + newState + " for " + greenTime + " ticks");
        light.setState(newState);
        int initPhase = sumo.getCurrentSimStep() / 1000;
        int endPhase = initPhase;

        while (greenTime > (endPhase - initPhase)) {

            // if emergencyApproaching, change immediately
            if (emergencyIndex != -1 && emergencyIndex != i) {
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
        Logger.logSumo(name + " - Changed to " + newState + " for 5 ticks");
        light.setState(newState);
        initPhase = sumo.getCurrentSimStep() / 1000;
        endPhase = initPhase;

        while (5 > (endPhase - initPhase)) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            endPhase = sumo.getCurrentSimStep() / 1000;
        }
        if (!TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            synchronized (emergencyIndex) {
                if (emergencyIndex != -1) {
                    if (emergencyIndex == i) {
                        if (savedIndex != -1) {
                            i = savedIndex - 1;
                            savedIndex = -1;
                        }
                        emergencyIndex = -1;
                        Logger.logSumo(name + " going back to normal");
                    } else {
                        savedIndex = (i + 1) % nrIntersections;
                        i = emergencyIndex - 1;
                    }
                }
            }
        }
        if (!TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            parentAgent.updateState();
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
        if (TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            return -1;
        }
        SumoLane lane = new SumoLane(id);
        int numVehicles = lane.getNumVehicles();
        int numPubVehicles = lane.getNumVehicles("pub");

        numVehicles = numVehicles + 2 * numPubVehicles;

        int laneDim = (int) Math.floor(lane.getLength());
        float ratio = (float) numVehicles / (float) laneDim;

        if (ratio > 0.03) {
            return -50;
        } else if (ratio > 0.01) {
            return 100;
        } else {
            return 10;
        }
    }

    public boolean comingEmergencyAction(String neighbour) {
        if (TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            return false;
        }
        // if there isn't currently any emergency, and with a probability of 30 %, it will listen for emergencies
        float rand = new Random().nextInt();
        if (rand < LISTEN_TO_NEIGHBOURS_EMERGENCIES_PROB) {
            for (int i = 0; i < neighbours.size(); i++) {
                if (neighbour.indexOf(neighbours.get(i)) != -1) {
                    synchronized (emergencyIndex) {
                        emergencyIndex = i;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private class Rewarder implements Runnable {
        int i;

        public Rewarder(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            int reward = getRewardForLane(neighbours.get(i) + "to" + name + "_0");
            parentAgent.sendReward(neighbours.get(i), reward);
        }
    }

    private class EmergencyChecker implements Runnable {

        @Override
        public void run() {
            ArrayList<SumoLane> lanes = new ArrayList<>();
            for (int i = 0; i < neighbours.size(); i++) {
                lanes.add(new SumoLane(neighbours.get(i) + "to" + name + "_0"));
            }
            Logger.logSumo("Traffic light " + name + " started checking for emergencies");
            while (true) {
                try {
                    for (int i = 0; i < lanes.size(); i++) {
                        if (lanes.get(i).getNumVehicles("eme") > 0) {
                            Logger.logSumo(name + " - Emmergency at " + neighbours.get(i) + "to" + name + "_0");
                            synchronized (emergencyIndex) {
                                emergencyIndex = i;
                            }
                            parentAgent.alertNeighbourOfEmergency();
                            while (emergencyIndex == i)
                                Thread.sleep(5);
                        }
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}