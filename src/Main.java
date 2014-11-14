import it.polito.appeal.traci.SumoTraciConnection;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import start.ODManager;
import trasmapi.genAPI.Simulator;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinnie on 12-Nov-14.
 */
public class Main {
    private static ProfileImpl profile;
    private static ContainerController mainContainer;

    public static void main(String[] args) throws UnimplementedMethod, IOException, TimeoutException,
            InterruptedException {

        //JADE STUFF
        profile = new ProfileImpl();

        Runtime rt = new Runtime.instance();

        mainContainer = rt.createMainContainer(profile);

        ODManager manager = new ODManager(mainContainer);

        //TRASMAPI STUFF
        TraSMAPI api = new TraSMAPI();

        // SUMO STUFF
        Simulator sumo = new Sumo("guisim");
        List<String> params = new ArrayList<String>();
        params.add("-c=TlMap/map.sumo.cfg");
        sumo.addParameters(params);
        sumo.addConnections("localhost", 8870);



        api.addSimulator(sumo);
        api.launch();
        api.connect();
        api.start();



        Thread.sleep(1000);

        System.out.println("Fim.");
    }
}
