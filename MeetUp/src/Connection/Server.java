package Connection;
import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import parser.XMLRead;

public class Server extends Agent{
	@Override
	protected void setup(){
	ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
	ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
	ACLMessage informFinal = new ACLMessage(ACLMessage.INFORM);
	ACLMessage recm;
	String agentID = "Server";
	
	AID receiver = new AID(agentID,AID.ISLOCALNAME);
	 MessageTemplate mt = MessageTemplate.MatchAll();
	 Behaviour b = new CyclicBehaviour(this){
		 public void action(){
				ACLMessage rec = receive(mt);
				if(rec != null){
					if (rec.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
						//TODO algorithm here
						System.out.println(this.getAgent().getName() + ": " + rec.getSender().getName() + " - accepted");
					}
					if (rec.getPerformative() == ACLMessage.REJECT_PROPOSAL){
						//TODO algorithm here
						System.out.println(this.getAgent().getName() + ": " + rec.getSender().getName() + " - rejected");
					}
					else if( rec.getContent() != null){
						XMLRead r = new XMLRead();
						String xml = rec.getContent();
						r.Read(xml);
						switch(r.type){
						case "0":
							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.location);
							sendMessage(propose,new AID(r.id,AID.ISLOCALNAME),null,ACLMessage.PROPOSE);
							break;
						case "1":
							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.state);
							sendMessage(inform,new AID("Ag",AID.ISLOCALNAME),r.state,ACLMessage.INFORM);
							break;
						case "2":
							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.location + " " + r.time);
							sendMessage(propose,new AID("Ag",AID.ISLOCALNAME),null,ACLMessage.PROPOSE);
							break;
						}
						block();
					}
				}
			}
	 };
	 addBehaviour(b);
	}
	private void sendMessage(ACLMessage msg, AID receiver, String content, int performative){
		msg.setContent(content);
		msg.setPerformative(performative);
		msg.addReceiver(receiver);
		send(msg);
	}

}
