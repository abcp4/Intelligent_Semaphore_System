package agents;

import learning.State;
import utils.Logger;

import java.util.Arrays;
import java.util.Random;

public class TrafficLightState implements State {
    public static final int LIGHTS_GRANULARITY = 8;
    public static final int ACTIONS_BY_LIGHT = 3; // decrease, maintain and increase actions
    private static final int LIGHTS_MIN_TIME = 20;
    private static final int LIGHTS_MAX_TIME = 60;
    public static final int NR_STATES_PER_LIGHT = (LIGHTS_MAX_TIME - LIGHTS_MIN_TIME) / LIGHTS_GRANULARITY + 1;
    private int nrIntersections;

    // 20 - 60
    private int[] greenTimeSpans;
    private int state;
    private int lastAction;
    private String name;

    public TrafficLightState(String name, int nrIntersections, int nrStates) {
        this.name = name;
        lastAction = 0;
        this.nrIntersections = nrIntersections;
        greenTimeSpans = new int[nrIntersections];
        /*if (!TrafficLightAgent.IS_FIXED_BEHAVIOUR) {
            updateState(new Random().nextInt(nrStates));
        } else {*/
            state = 0;
            for (int i = 0; i < nrIntersections; i++) {
                state += Math.pow(NR_STATES_PER_LIGHT, i) * 2;
            }
            updateState(state);
        //}
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
            greenTimeSpans[i] = LIGHTS_MIN_TIME + (((state / (int) Math.pow(NR_STATES_PER_LIGHT, i)) % NR_STATES_PER_LIGHT) * LIGHTS_GRANULARITY);
        }
        Logger.logLearning(name + " - updated to state " + state + " " + Arrays.toString(greenTimeSpans));
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
        Logger.logLearning(name + " - applying action " + action);

        for (int i = 0; i < nrIntersections; i++) {
            int decodedAction = (action / (int) Math.pow(ACTIONS_BY_LIGHT, i)) % ACTIONS_BY_LIGHT;
            switch (decodedAction) {
                case 0:
                    if (getGreenTimeSpan(i) > LIGHTS_MIN_TIME) {
                        newState -= (int) Math.pow(NR_STATES_PER_LIGHT, i);

                        Logger.logLearning(name + " - decreased light " + i);
                    }
                    break;
                case 1:
                    Logger.logLearning(name + " - kept light " + i);
                    break;
                case 2:
                    if (getGreenTimeSpan(i) < LIGHTS_MAX_TIME) {
                        newState += (int) Math.pow(NR_STATES_PER_LIGHT, i);
                        Logger.logLearning(name + " - increased light " + i);
                    }
                    break;
            }
        }


        return newState;
    }
}
