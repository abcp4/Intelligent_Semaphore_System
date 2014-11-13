//import it.polito.appeal.traci.SumoTraciConnection;
import trasmapi.genAPI.Simulator;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws UnimplementedMethod, IOException, TimeoutException,
            InterruptedException {
        //TRASMAPI STUFF
        TraSMAPI api = new TraSMAPI();

        // SUMO STUFF
        Simulator sumo = new Sumo("guisim");
        List<String> params = new ArrayList<String>();
        params.add("-c=quickstart/data/quickstart.sumocfg");
        sumo.addParameters(params);
        sumo.addConnections("localhost", 8870);


        api.addSimulator(sumo);
        api.launch();
        api.connect();
        api.start();

        Thread.sleep(1000);
        System.out.println("Chego.");
    }
}
