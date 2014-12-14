package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import learning.QLearning;
import trasmapi.sumo.Sumo;
import trasmapi.sumo.SumoTrafficLight;
import utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;


public class TrafficLightAgent extends Agent {

    public static boolean IS_FIXED_BEHAVIOUR = false;

    private ArrayList<String> neighbours;
    private int nrIntersections;
    private int nrStates;
    private int nrActions;
    private String name;

    private QLearning qTeacher;
    private TrafficLightState currentState;
    private TLController tlController;

    public TrafficLightAgent(Sumo sumo, String name, ArrayList<String> neighbours) throws Exception {
        super();
        this.name = "TrafficLight-" + name;
        this.neighbours = (ArrayList<String>) neighbours.clone();
        // for emergency vehicles, we must add actions according to the number of intersections it could come from
        nrIntersections = neighbours.size();
        nrStates = (int) Math.pow(TrafficLightState.NR_STATES_PER_LIGHT, nrIntersections); // for green time-frames
        nrActions = (int) Math.pow(TrafficLightState.ACTIONS_BY_LIGHT, nrIntersections);  // corresponding to increase, maintain and decrease the red and green time-frames
        if (!IS_FIXED_BEHAVIOUR) {
            qTeacher = new QLearning(name, nrStates, nrActions);
        }
        currentState = new TrafficLightState(name, nrIntersections, nrStates);
        tlController = new TLController(this, sumo, name, (ArrayList<String>) neighbours.clone(), currentState.getGreenTimeSpans());
        updateAndCleanNeighboursNames();


        Logger.logAgents("Agent " + name + " created; controlling " + nrIntersections + " and with neighbours " + this.neighbours);
    }

    private void updateAndCleanNeighboursNames() {
        ArrayList<String> tlsIds = SumoTrafficLight.getIdList();
        for (int i = 0; i < neighbours.size(); i++) {
            if (Arrays.asList(tlsIds.toArray()).contains(neighbours.get(i))) {
                neighbours.set(i, "TrafficLight-" + neighbours.get(i));
            } else {
                // if neighbour is not a traffic light, remove it
                neighbours.remove(i);
                i--;
            }
        }
    }

    public void sendReward(String id, int reward) {
        if (IS_FIXED_BEHAVIOUR) {
            return;
        }
        for (String n: neighbours) {
            if (neighbours.indexOf(id) != 0) {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(new AID(n, AID.ISLOCALNAME));
                request.setContent("reward " + reward);
                Logger.logAgents(name + " - Sent reward to " + n + " with value " + reward);
                send(request);

                break;
            }
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
            if (!IS_FIXED_BEHAVIOUR) {
                WaitRequestAndReplyRewardBehaviour RewardBehaviour = new WaitRequestAndReplyRewardBehaviour(this);
                Logger.logAgents(name + " - added LEARNING behaviour");

                addBehaviour(RewardBehaviour);
            } else {
                Logger.logAgents(name + " - added FIXED behaviour");
            }
        } catch (FIPAException e) {
            Logger.logAgents("SEVERE - Agent " + getLocalName() + " - Cannot register with DF");
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

    public void alertNeighbourOfEmergency() {
        if (IS_FIXED_BEHAVIOUR) {
            return;
        }
        for (String n : neighbours) {
            Logger.logAgents(name + " - Warned neighbour about emergency: " + n);
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.addReceiver(new AID(n, AID.ISLOCALNAME));
            request.setContent("emergency " + name);
            send(request);
            Logger.logAgents(name + " - Sent emergency request to " + n);
        }
    }

    public void updateState() {
        int nextAction = qTeacher.getActionToTake(currentState.getState());
        currentState.applyAction(nextAction);
        tlController.updateTimeSpans(currentState.getGreenTimeSpans());
    }

    private class WaitRequestAndReplyRewardBehaviour extends CyclicBehaviour {
        public WaitRequestAndReplyRewardBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                String sender = msg.getSender().getLocalName();
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    String content = msg.getContent();
                    if (content != null) {
                        if (content.indexOf("reward") != -1) {
                            int reward = Integer.parseInt(content.substring(7));
                            Logger.logAgents("REQUEST - Agent " + getLocalName() + " - Received REWARD from " + msg.getSender().getLocalName() + " with value " + reward);
                            qTeacher.reinforce(currentState, reward);
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent("updated");
                            Logger.logAgents(name + " - sent reward confirmation to " + sender);
                        } else if (content.indexOf("emergency") != -1) {
                            Logger.logAgents("REQUEST - Agent " + getLocalName() + " - Received EMERGENCY Request from " + msg.getSender().getLocalName());
                            String neighbour = content.substring(10);
                            boolean actuated = tlController.comingEmergencyAction(neighbour);
                            reply.setPerformative(ACLMessage.INFORM);
                            if (actuated) {
                                reply.setContent("emergency accepted");
                            } else {
                                reply.setContent("emergency ignored");
                            }
                        }
                    } else {
                        Logger.logAgents("INFO - Agent " + getLocalName() + " - Unexpected request[" + content + "]received from" + msg.getSender().getLocalName());
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("( UnexpectedContent (" + content + "))");
                    }
                } else if (msg.getPerformative() == ACLMessage.INFORM) {
                    String content = msg.getContent();
                    if (content != null) {
                        if (content.indexOf("emergency accepted") != -1) {
                            // do nothing
                            Logger.logAgents("INFO - Agent " + name + " received emergency accepted of  from " + msg.getSender());
                        } else if (content.indexOf("emergency ignored") != -1) {
                            // do nothing
                            Logger.logAgents("INFO - Agent " + name + " received emergency ignored from " + msg.getSender());
                        } else if (content.indexOf("updated") != -1) {
                            // do nothing
                            Logger.logAgents("INF - Agent " + name + " received reward update confirmation from " + msg.getSender());
                        }
                    }
                } else {
                    Logger.logAgents("INFO - Agent " + getLocalName() + " - Unexpected message [" + ACLMessage.getPerformative(msg.getPerformative()) + "] received from " + msg.getSender().getLocalName());
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
