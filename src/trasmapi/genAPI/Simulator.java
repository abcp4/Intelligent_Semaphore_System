package trasmapi.genAPI;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import trasmapi.genAPI.exceptions.TimeoutException;
import trasmapi.genAPI.exceptions.UnimplementedMethod;

public class Simulator {
	
	/**
	 * launches the Simulator Process
	 * @param params 
	 * @throws java.io.IOException
	 * @throws trasmapi.genAPI.exceptions.UnimplementedMethod
	 */
	public void launch() throws IOException, UnimplementedMethod {
		throw new UnimplementedMethod();
	}
	
	/**
	 * method used to connect to the simulator (using sockets)
	 * @throws java.net.UnknownHostException
	 * @throws java.io.IOException
	 * @throws trasmapi.genAPI.exceptions.TimeoutException
	 */
	public void connect() throws UnknownHostException, IOException, TimeoutException, UnimplementedMethod {
		throw new UnimplementedMethod();
	}
	
	/**
	 * method used to close the simulator
	 * @throws java.io.IOException
	 */
	public void close() throws UnimplementedMethod, IOException {
		throw new UnimplementedMethod();
	}
	
	/**
	 * method used to advance k steps in the simulation
	 * @param k - number of simulation steps to be simulated
	 */
	public boolean simulationStep(int k) throws UnimplementedMethod {
		throw new UnimplementedMethod();
	}

	public void addParameters(List<String> params) throws UnimplementedMethod {
		throw new UnimplementedMethod();
	}

	public void addConnections(String string, int i) throws UnimplementedMethod {
		throw new UnimplementedMethod();
		
	}

	public void getAllVehiclesIds() throws UnimplementedMethod {
		throw new UnimplementedMethod();
	}

	public void start() throws UnimplementedMethod {
		throw new UnimplementedMethod();
	}

	public int getCurrentTicks() throws UnimplementedMethod {
		throw new UnimplementedMethod();
	}

	public void subscribeTicksInt() throws UnimplementedMethod {
		throw new UnimplementedMethod();
	}
}
