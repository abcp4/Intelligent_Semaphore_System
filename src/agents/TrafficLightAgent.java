package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.ContainerController;
import learning.QLearning;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoTrafficLight;

import java.util.ArrayList;
import java.util.Arrays;


public class TrafficLightAgent extends Agent {

    public static boolean IS_FIXED_BEHAVIOUR = true;

    private ContainerController parentContainer;
    private ArrayList<String> neighbours;
    private int nrIntersections;
    private int nrStates;
    private int nrActions;
    private String name;

    private QLearning qTeacher;
    private TrafficLightState currentState;
    private TLController tlController;

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    public TrafficLightAgent(Sumo sumo, ContainerController mainContainer, String name, ArrayList<String> neighbours) throws Exception {
        super();
        this.name = "TrafficLight-" + name;
        parentContainer = mainContainer;
        this.neighbours = neighbours;
        // TODO: consider emergency vehicles
        // for emergency vehicles, we must add actions according to the number of intersections it could come from
        nrIntersections = neighbours.size();
        nrStates = (int) Math.pow(TrafficLightState.NR_STATES_PER_LIGHT, nrIntersections); // for green time-frames
        nrActions = (int) Math.pow(TrafficLightState.ACTIONS_BY_LIGHT, nrIntersections);  // corresponding to increase, maintain and decrease the red and green time-frames
        qTeacher = new QLearning(nrStates, nrActions);
        currentState = new TrafficLightState(nrIntersections, nrStates);
        tlController = new TLController(this, sumo, name, (ArrayList<String>) neighbours.clone(), currentState.getGreenTimeSpans());
        updateAndCleanNeighboursNames();
    }

    private void updateAndCleanNeighboursNames() {
        ArrayList<String> tlsIds = SumoTrafficLight.getIdList();
        for (int i = 0; i < neighbours.size(); i++) {
            if (Arrays.asList(tlsIds).contains(neighbours.get(i))) {
                neighbours.set(i, "TrafficLight-" + neighbours.get(i));
            } else {
                // if neighbour is not a traffic light, remove it
                neighbours.remove(i);
                i--;
            }
        }
    }

    public synchronized void requestReward() {
        for (int i = 0; i < neighbours.size(); i++) {
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new AID(neighbours.get(i), AID.ISLOCALNAME));
            request.setContent("reward");
            send(request);
        }
    }

    @Override
    protected void setup() {
        // Registration with the DF
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("TrafficLightsAgent");
        sd.setName(getName());
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            if (IS_FIXED_BEHAVIOUR) {
                WaitRequestAndReplyRewardBehaviour RewardBehaviour = new WaitRequestAndReplyRewardBehaviour(this);
                addBehaviour(RewardBehaviour);
            }
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }
        new Thread(tlController).start();
        super.setup();
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
    }

    private class WaitRequestAndReplyRewardBehaviour extends CyclicBehaviour {

        public WaitRequestAndReplyRewardBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    String content = msg.getContent();
                    if (content != null) {
                        String sender = msg.getSender().getLocalName();
                        if (content.indexOf("reward") != -1) {
                            myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received REWARD Request from " + msg.getSender().getLocalName());
                            reply.setPerformative(ACLMessage.INFORM);



                            int reward = tlController.getRewardForLane(sender.substring(13, 16) + "to" + name.substring(13, 16) + "_0");
                            reply.setContent(Integer.toString(reward));
                        } else if (content.indexOf("emmergency") != -1) {
                            myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received REWARD Request from " + msg.getSender().getLocalName());
                            reply.setPerformative(ACLMessage.INFORM);
                            // TODO: act accordingly (emmergency)
                            reply.setContent("emmergency");
                        }
                    } else {
                        myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Unexpected request [" + content + "] received from " + msg.getSender().getLocalName());
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("( UnexpectedContent (" + content + "))");
                    }
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    String content = msg.getContent();
                    if (content != null) {
                        int reward = Integer.parseInt(content);
                        // TODO: check if this is sufficient for learning purposes (according to the sender of this message...)
                        qTeacher.reinforce(currentState, reward);
                        int nextAction = qTeacher.getActionToTake(currentState.getState());
                        currentState.applyAction(nextAction);
                        tlController.updateTimeSpans(currentState.getGreenTimeSpans());
                    }
                } else {
                    myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Unexpected message [" + ACLMessage.getPerformative(msg.getPerformative()) + "] received from " + msg.getSender().getLocalName());
                    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    reply.setContent("( (Unexpected-act " + ACLMessage.getPerformative(msg.getPerformative()) + ") )");
                }
                send(reply);
            } else {
                block();
            }
        }
    }
}
