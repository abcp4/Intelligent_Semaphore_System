package learning;

public interface State {
    public int getState();
    public int getLastAction();
    public int testAction(int action);
}
