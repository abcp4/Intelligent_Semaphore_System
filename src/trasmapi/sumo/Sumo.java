package trasmapi.sumo;


import trasmapi.genAPI.Simulator;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;

import java.io.IOException;
import java.util.List;

public class Sumo extends Simulator {

    public SumoCom comm;
    String simulator;

    public Sumo(String sim) {
        simulator = sim;
        comm = new SumoCom();
    }

    public void launch() {
        comm.launch(simulator);
    }

    public void connect() throws TimeoutException {
        comm.connect();
    }

    public void close() throws IOException {
        comm.close();
    }

    public boolean simulationStep(int k) {
        return comm.simStep(k);
    }

    public void addParameters(List<String> paramsP) {
        comm.params = paramsP;
    }

    public void addConnections(String add, int portP) {
        comm.port = portP;
    }

    public void start() {
        comm.start(0);
    }

    public synchronized int getCurrentTicks() {
        return comm.getTicks();
    }

    public int getCurrentSimStep() {
        return comm.getCurrentSimStep();
    }

    public void subscribeTicksInt() throws UnimplementedMethod {
        comm.subscribeTicks();
    }
}
