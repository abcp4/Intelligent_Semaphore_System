package agents;

import learning.State;
import trasmapi.genAPI.TrafficLight;

public class TrafficLightState implements State {
    private static int LIGHTS_MIN_TIME = 20;
    private static int LIGHTS_MAX_TIME = 60;
    public static int LIGHTS_GRANULARITY = (LIGHTS_MAX_TIME - LIGHTS_MIN_TIME) / 5;
    public static int ACTIONS_BY_LIGHT = 3; // decrease, maintain and increase actions


    private int nrIntersections;

    // 20 - 60
    private int[] greenTimeSpans;
    private int state;
    private int lastAction;

    public TrafficLightState(int nrIntersections, int state) {
        lastAction = 0;
        this.nrIntersections = nrIntersections;
        greenTimeSpans = new int[nrIntersections];
        updateState(state);
    }

    public int getGreenTimeSpan(int index) {
        return greenTimeSpans[index];
    }

    public int[] getGreenTimeSpans() {
        return greenTimeSpans.clone();
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public int getLastAction() {
        return lastAction;
    }

    private void updateState(int state) {
        this.state = state;
        for (int i = 0; i < nrIntersections; i++) {
            greenTimeSpans[i] = LIGHTS_MIN_TIME + ((state / (int) Math.pow(LIGHTS_GRANULARITY, i)) % LIGHTS_GRANULARITY);
        }
    }

    /**
     * @param action
     * @return obtained state
     */
    public int applyAction(int action) {
        int newState = testAction(action);
        updateState(newState);
        lastAction = action;
        return newState;
    }

    public int testAction(int action) {
        int newState = state;

        for (int i = 0; i < nrIntersections; i++) {
            int decodedAction = (action / (int) Math.pow(ACTIONS_BY_LIGHT, i)) % ACTIONS_BY_LIGHT;

            switch (decodedAction) {
                case 0:
                    if (getGreenTimeSpan(i) > LIGHTS_MIN_TIME) {
                        newState -= (int) Math.pow(ACTIONS_BY_LIGHT, i);
                    }
                    break;
                /*
                case 1 -> do nothing
                 */
                case 2:
                    if (getGreenTimeSpan(i) < LIGHTS_MAX_TIME) {
                        newState -= (int) Math.pow(ACTIONS_BY_LIGHT, i);
                    }
                    break;
            }
        }
        return newState;
    }
}
