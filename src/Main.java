//import it.polito.appeal.traci.SumoTraciConnection;
import jade.BootProfileImpl;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import trasmapi.genAPI.Simulator;
import trasmapi.genAPI.TraSMAPI;
import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;
import trasmapi.sumo.Sumo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static boolean JADE_GUI = false;
    private static ProfileImpl profile;
    private static ContainerController mainContainer;

    public static void main(String[] args) throws UnimplementedMethod, InterruptedException, IOException, TimeoutException {

        if(JADE_GUI){
            List<String> params = new ArrayList<String>();
            params.add("-gui");
            profile = new BootProfileImpl(params.toArray(new String[0]));
        } else
            profile = new ProfileImpl();

        jade.core.Runtime rt = Runtime.instance();
        mainContainer = rt.createMainContainer(profile);

        TLManager manager = new TLManager(mainContainer);

        try {

            mainContainer.acceptNewAgent("MANAGER#1", manager).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
            return;
        }
        //"\uD83D\uDCA9"
        TraSMAPI api = new TraSMAPI();

        //Create SUMO
        Simulator sumo = new Sumo("guisim");
        List<String> params = new ArrayList<String>();
        params.add("-c=manhattan/bettermanhattan/file.sumocfg");
        sumo.addParameters(params);
        sumo.addConnections("localhost", 8820);

        //Add Sumo to TraSMAPI
        api.addSimulator(sumo);

        //Launch and Connect all the simulators added
        api.launch();

        /*
            Foder a gui toda. Por logo um agente para cada luz
         */

        api.connect();

        api.start();

        Thread.sleep(1000);

        manager.loadTls();


        while(true)
            if(!api.simulationStep(0))
                break;
    }
}
