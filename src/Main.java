import jade.BootProfileImpl;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import agents.sumo.AgentsManager;
import trasmapi.genAPI.Simulator;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;
import xml.TripParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static boolean JADE_GUI = true;
    private static ProfileImpl profile;
    private static ContainerController mainContainer;

    public static void main(String[] args) throws UnimplementedMethod, InterruptedException, IOException, TimeoutException {

        TripParser tp = new TripParser();
        tp.getCarData("C:\\Users\\Vinnie\\Projects\\AIAD\\manhattan\\bettermanhattan\\logs\\trip.xml");

        /*if(JADE_GUI){
            List<String> params = new ArrayList<String>();
            params.add("-gui");
            profile = new BootProfileImpl(params.toArray(new String[0]));
        } else
            profile = new ProfileImpl();

        jade.core.Runtime rt = Runtime.instance();
        mainContainer = rt.createMainContainer(profile);

        // Init TraSMAPI framework
        TraSMAPI api = new TraSMAPI();

        //Create SUMO
        Simulator sumo = new Sumo("guisim");
        List<String> params = new ArrayList<String>();
        params.add("--device.emissions.probability=1.0");
        params.add("--tripinfo-output=manhattan/bettermanhattan/logs/trip.xml");
        params.add("-c=manhattan/bettermanhattan/file.sumocfg");
        sumo.addParameters(params);
        sumo.addConnections("localhost", 8820);

        //Add Sumo to TraSMAPI
        api.addSimulator(sumo);

        //Launch and Connect all the simulators added
        api.launch();

        api.connect();

        api.start();

        Thread.sleep(1000);

        AgentsManager manager = new AgentsManager(api, mainContainer);

        manager.startupAgents(mainContainer);

        while(true)
            if(!api.simulationStep(0))
                break;*/
    }
}
