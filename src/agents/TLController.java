package agents;

import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;


public class TLController implements Runnable {
    private String name;
    private Sumo sumo;
    private ArrayList<String> neighbours;
    private ArrayList<String> lanes;

    private int[] greenTimeSpans;

    public TLController(Sumo sumo, String name, ArrayList<String> lanes, ArrayList<String> neighbours, int[] greenTimeSpans) {
        this.name = name;
        this.sumo = sumo;
        this.neighbours = new ArrayList<>(neighbours);
        this.greenTimeSpans = new int[neighbours.size()];
        this.lanes = lanes;
        updateTimeSpans(greenTimeSpans);
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

                String newState = buildState(i);

                System.out.println("Changed " + name + " to " + newState + " for " + greenTime + " ticks");
                light.setState(newState);
                int initPhase = sumo.getCurrentSimStep();
                int endPhase = initPhase;

                while (greenTime > (endPhase - initPhase)) {
                    endPhase = sumo.getCurrentSimStep();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String buildState(int nr) {
        int nrIntersections = neighbours.size();
        StringBuffer retStr = new StringBuffer();
        for (int i = 0; i < nrIntersections; i++) {
            if (i == nr) {
                retStr.append(stringMultiplier("G", nrIntersections - 1));
            } else if (i == nr + 1 || nr == nrIntersections - 1 && i == 0) {
                retStr.append("G" + stringMultiplier("r", nrIntersections - 2));
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
}
