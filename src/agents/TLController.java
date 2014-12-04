package agents;

import trasmapi.genAPI.TrafficLight;
import trasmapi.sumo.SumoTrafficLight;
import trasmapi.sumo.protocol.Constants;

import java.util.ArrayList;



public class TLController implements Runnable {

    private static final int MILLIS_MULTIPLIER = 1000;
    private static final int YELLOW_SPAN = 5;

    private String name;
    private ArrayList<String> neighbours;
    private ArrayList<String> lanes;

    private final int yellowTimeSpan = 5;
    private int[] greenTimeSpans;
    // private int[] redTimeSpans;

    public TLController(String name, ArrayList<String> lanes, ArrayList<String> neighbours, int[] greenTimeSpans) {
        this.name = name;
        this.neighbours = new ArrayList<>(neighbours);
        this.greenTimeSpans = new int[neighbours.size()];
        //redTimeSpans = new int[neighbours.size()];
        updateTimeSpans(greenTimeSpans);
    }

    public void updateTimeSpans(int[] newGreenTimeSpans) {
        synchronized (greenTimeSpans) {
            greenTimeSpans = newGreenTimeSpans.clone();
            /*for (int i = 0; i < greenTimeSpans.length; i++) {
                int redSpanAccumulator = 0;
                for (int j = 0; j < greenTimeSpans.length; j++) {
                    if (i != j) {
                        redSpanAccumulator += greenTimeSpans[j];
                    }
                }
                redTimeSpans[i] = redSpanAccumulator;
            }*/
        }
    }

    @Override
    public void run() {
        int nrIntersections = neighbours.size();
        SumoTrafficLight light;
        try {
            while (true) {
                for (int i = 0; i < nrIntersections; i++) {
                    light = new SumoTrafficLight(neighbours.get(i));
                    //light.setState(Constants.TLPHASE_GREEN);
                    // TODO: change to green traffic light i
                    Thread.sleep(greenTimeSpans[i] * MILLIS_MULTIPLIER);
                    // TODO: change to yellow traffic light i
                    Thread.sleep(yellowTimeSpan * MILLIS_MULTIPLIER);
                    // TODO: change to red traffic light i
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
