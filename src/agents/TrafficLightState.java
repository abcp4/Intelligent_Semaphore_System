package agents;

import learning.State;

public class TrafficLightState implements Runnable, State {
    // TODO: verify thread safety
    private static final int MILLIS_MULTIPLIER = 1000;
    private static int LIGHTS_MIN_TIME = 20;
    private static int LIGHTS_MAX_TIME = 60;
    public static int LIGHTS_GRANULARITY = (LIGHTS_MAX_TIME - LIGHTS_MIN_TIME) / 5;
    public static int ACTIONS_BY_LIGHT = 3; // decrease, maintain and increase


    private int nrIntersections;

    // 20 - 60
    private int[] greenSpans;
    private final int yellowSpan = 5;
    private int state;
    private int lastAction;

    public TrafficLightState(int nrIntersections, int state) {
        lastAction = 0;
        this.nrIntersections = nrIntersections;
        greenSpans = new int[nrIntersections];
        updateState(state);
    }

    public int getYellowSpan() {
        return yellowSpan;
    }

    public int getGreenSpan(int index) {
        return greenSpans[index];
    }

    public void incGreenSpan(int index, int greenSpan) {
        this.greenSpans[index] += greenSpan;
    }

    public void decGreenSpan(int index, int greenSpan) {
        this.greenSpans[index] -= greenSpan;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (int i = 0; i < nrIntersections; i++) {
                    // TODO: change to green traffic light i
                    Thread.sleep(greenSpans[i] * MILLIS_MULTIPLIER);
                    // TODO: change to yellow traffic light i
                    Thread.sleep(yellowSpan * MILLIS_MULTIPLIER);
                    // TODO: change to red traffic light i
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            greenSpans[i] = LIGHTS_MIN_TIME + ((LIGHTS_GRANULARITY / (int) Math.pow(LIGHTS_GRANULARITY, i))
                    % LIGHTS_GRANULARITY);
        }
    }

    /**
     *
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
        int newState = 0;
        // TODO
        return newState;
    }
}
