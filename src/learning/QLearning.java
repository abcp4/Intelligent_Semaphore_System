package learning;

public class QLearning {

    // impact of current update (0 <= alpha <= 1)
    private static final float LEARNING_RATE = (float) 0.7;

    // importance future states' Q values (0 <= lambda <= 1)
    private static final double DISCOUNT_FACTOR = (float) 0.3;

    private float[][] reinforcements;
    private float[][] qTable;
    private int states = 0;
    private int actions = 0;


    public QLearning(int states, int actions) {
        this.states = states;
        this.actions = actions;

        reinforcements = new float[states][actions];
        qTable = new float[states][actions];
    }

    public void reinforce(int state, int action, float val) {
        float bestActionInNextState = getBestAction(state);
        float currentQValue = qTable[state][action];
        qTable[state][action] = (float) (currentQValue
                        + LEARNING_RATE * (val + DISCOUNT_FACTOR * (bestActionInNextState - currentQValue)));
    }

    private float getBestAction(int state) {
        float bestAction = 0;
        float[] actions = qTable[state];
        for (int i = 0; i < actions.length; i++) {
            float currentAction = actions[i];
            if (currentAction > bestAction) {
                bestAction = currentAction;
            }
        }
        return bestAction;
    }

    public static void main(String[] args) {
        QLearning teacher = new QLearning(3, 3);

        for (int i = 0; i < teacher.reinforcements.length; i++) {
            for (int j = 0; j < teacher.reinforcements[i].length; j++) {
                System.out.println(teacher.reinforcements[i][j] + " ");
            }
            System.out.println();
        }
    }
}
