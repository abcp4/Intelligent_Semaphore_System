package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import learning.QLearning;

import java.util.Random;


public class TrafficLightAgent extends Agent {

    private int nrIntersections;
    private int nrStates;
    private int nrActions;

    private QLearning qTeacher;
    private TrafficLightState currentState;

    private Logger myLogger = Logger.getMyLogger(getClass().getName());

    public TrafficLightAgent(int nrIntersections) {
        // TODO: consider emergency vehicles
        // for emergency vehicles, we must add actions according to the number of intersections it could come from
        super();
        this.nrIntersections = nrIntersections;
        nrStates = (int) Math.pow(TrafficLightState.LIGHTS_GRANULARITY, nrIntersections); // for green time-frames
        nrActions = (int) Math.pow(TrafficLightState.ACTIONS_BY_LIGHT, nrIntersections);  // corresponding to increase, maintain and decrease the red and green time-frames
        qTeacher = new QLearning(nrStates, nrActions);
        currentState = new TrafficLightState(nrIntersections, new Random().nextInt(nrStates));
        new Thread(currentState).start();
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
                        if (content.indexOf("reward") != -1) {
                            myLogger.log(Logger.INFO, "Agent " + getLocalName() + " - Received REWARD Request from " + msg.getSender().getLocalName());
                            reply.setPerformative(ACLMessage.INFORM);
                            // TODO: calc reward
                            int reward = 0;
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
                        // TODO: array for all neighbours, only take action when all respond (need to make extra calculations)
                        int reward = Integer.parseInt(content);
                        qTeacher.reinforce(currentState, reward);
                        // TODO: according to the sender of this message...
                        int nextAction = qTeacher.getActionToTake(currentState.getState());
                        currentState.applyAction(nextAction);
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

    public void requestReward() {
        for (int i = 0; i < nrIntersections; i++) {
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            // TODO: for each neighbour, add it as destination
            request.setContent("reward");
            send(request);
        }
    }


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
            WaitRequestAndReplyRewardBehaviour RewardBehaviour = new WaitRequestAndReplyRewardBehaviour(this);
            addBehaviour(RewardBehaviour);
        } catch (FIPAException e) {
            myLogger.log(Logger.SEVERE, "Agent " + getLocalName() + " - Cannot register with DF", e);
            doDelete();
        }

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
}
