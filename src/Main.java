import agents.sumo.AgentsManager;
import jade.BootProfileImpl;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;
import xml.CarTrip;
import xml.TripParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static boolean JADE_GUI = true;
    private static ProfileImpl profile;
    private static ContainerController mainContainer;

    public static void main(String[] args) throws UnimplementedMethod, InterruptedException, IOException,
            TimeoutException {

        if (args.length != 1) {
            System.err.println("Provide a map path");
            System.exit(1);
        }

        if (JADE_GUI) {
            List<String> params = new ArrayList<String>();
            params.add("-gui");
            profile = new BootProfileImpl(params.toArray(new String[0]));
        } else {
            profile = new ProfileImpl();
        }

        jade.core.Runtime rt = Runtime.instance();
        mainContainer = rt.createMainContainer(profile);

        // Init TraSMAPI framework
        TraSMAPI api = new TraSMAPI();
        String map = args[0];
        //Create SUMO
        Sumo sumo = new Sumo("guisim");
        List<String> params = new ArrayList<String>();
        params.add("--no-step-log");
        params.add("--xml-validation=never");
        params.add("--device.emissions.probability=1.0");
        params.add("--tripinfo-output=maps/logs/trip.xml");
        params.add("-c=maps/" + map + "/file.sumocfg");
        sumo.addParameters(params);
        sumo.addConnections("127.0.0.1", 8820);

        //Add Sumo to TraSMAPI
        api.addSimulator(sumo);

        //Launch and Connect all the simulators added
        api.launch();

        api.connect();

        AgentsManager manager = new AgentsManager(sumo, mainContainer);
        manager.startupAgents(mainContainer);

        api.start();

        while (true) {
            if (!api.simulationStep(0)) {
                break;
            }
        }
    }
}
