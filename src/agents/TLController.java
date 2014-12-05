package agents;

import trasmapi.genAPI.TraSMAPI;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;


public class TLController implements Runnable {
    private String name;
    private TraSMAPI api;
    private ArrayList<String> neighbours;
    private ArrayList<String> lanes;

    private final int yellowTimeSpan = 5;
    private int[] greenTimeSpans;

    public TLController(TraSMAPI api, String name, ArrayList<String> lanes, ArrayList<String> neighbours, int[] greenTimeSpans) {
        this.name = name;
        this.api = api;
        this.neighbours = new ArrayList<>(neighbours);
        this.greenTimeSpans = new int[neighbours.size()];
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
        SumoTrafficLight light;
        while (true) {
            for (int i = 0; i < nrIntersections; i++) {
                light = new SumoTrafficLight(neighbours.get(i));
                light.setState(buildState(i));
            }
        }
    }

    private String buildState(int nr) {
        int nrIntersections = neighbours.size();
        StringBuffer retStr = new StringBuffer();
        for (int i = 0; i < nr; i++) {
            if (i == nr) {
                retStr.append(stringMultiplier("G", nrIntersections));
            } else {
                retStr.append(stringMultiplier("r", nrIntersections));
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
