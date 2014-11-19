package learning;

import agents.TrafficLightState;

import java.util.Random;

public class QLearning {

    // impact of current update (0 <= alpha <= 1)
    private static final float LEARNING_RATE = (float) 0.7;

    // importance future nrStates' Q values (0 <= lambda <= 1)
    private static final float DISCOUNT_FACTOR = (float) 0.3;

    // factor that determines the map of the possible nrActions to a probability according to their Q-Value
    private static final float SOFTMAX_TEMP = (float) 0.5;

    private float[][] qTable;
    private int nrStates = 0;
    private int nrActions = 0;


    public QLearning(int nrStates, int nrActions) {
        this.nrStates = nrStates;
        this.nrActions = nrActions;

        qTable = new float[nrStates][nrActions];
    }

    public void reinforce(State state, float val) {
        int currentState = state.getState();
        int action = state.getLastAction();
        float currentQValue = qTable[currentState][action];
        int nextState = state.testAction(action);
        int bestNextAction = getBestPossibleAction(nextState);
        float bestNextQVal = qTable[nextState][bestNextAction];
        qTable[currentState][action] = (float) (currentQValue
                + LEARNING_RATE * (val + DISCOUNT_FACTOR * (bestNextQVal - currentQValue)));
    }

    private int getBestPossibleAction(int state) {
        int bestAction = 0;
        float bestActionQ = 0;
        float[] actions = qTable[state];
        for (int i = 0; i < nrActions; i++) {
            float currentAction = actions[i];
            if (currentAction > bestActionQ) {
                bestAction = i;
                bestActionQ = currentAction;
            }
        }
        return bestAction;
    }

    public int getActionToTake(int state) {
        int nextAction = 0;
        float[] actionsProbabilities = new float[nrActions];
        float[] possibleActions = qTable[state];

        // get the denominator for softmax calculation
        float denominator = 0;
        for (int i = 0; i < nrStates; i++) {
            denominator += Math.exp(possibleActions[i] / SOFTMAX_TEMP);
        }

        // fill in the probabilities that each action has, according to their Q-value
        for (int i = 0; i < nrStates; i++) {
            actionsProbabilities[i] = (float) (Math.exp(possibleActions[i] / SOFTMAX_TEMP) / denominator);

            // update the value to be used as a range
            if (i > 0) {
                actionsProbabilities[i] += actionsProbabilities[i - 1];
            }
        }

        float randNr = new Random().nextFloat();

        for (int i = 0; i < nrStates; i++) {
            if (i == nrStates - 1) {
                nextAction = i;
                break;
            } else if (randNr < actionsProbabilities[i + 1]) {
                nextAction = i;
                break;
            }
        }
        return nextAction;
    }
}