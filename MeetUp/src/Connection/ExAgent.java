package Connection;
import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ExAgent extends Agent{
	@Override
	protected void setup(){
	ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
	ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
	ACLMessage informFinal = new ACLMessage(ACLMessage.INFORM);
	ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
	ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);

	String agentID = "Server";
	//String content = null;
	AID receiver = new AID(agentID,AID.ISLOCALNAME);
	Behaviour b = new CyclicBehaviour(this){
		 public void action(){
			 MessageTemplate mt = MessageTemplate.MatchAll();
			 ACLMessage rec = receive(mt);
			 if(rec != null){
				 System.out.println(this.getAgent().getName() + " recieved from " + rec.getSender().getName());
				 if (rec.getPerformative() == ACLMessage.INFORM){
					 accept.addReceiver(new AID(agentID, AID.ISLOCALNAME));
						send(accept);
				 }
			 }
		 }
	};
		Behaviour b1 = new OneShotBehaviour(this){
			public void action(){
				String content = setContent("0", "Ag2","14545.334 1243.123",null,null);
				sendMessage(inform,receiver,content,ACLMessage.REQUEST);
				System.out.println(this.getAgent().getName() + " to:" + receiver.getName() + " " + content);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				addBehaviour(b);
			}
		};
		addBehaviour(b1);		
	}

	private String setContent(String type, String id, String location, String state, String time){
		String content = null;
		parser.XMLParse p = new parser.XMLParse();
		if (type != null)
			p.instance().type = type;
		if (id != null)
			p.instance().id = id;
		if (location != null)
			p.instance().location = location;
		if (state != null)
			p.instance().state = state;
		if (time != null)
			p.instance().time = time;
		content = p.Parse();
		return content;
	}
	private void sendMessage(ACLMessage msg, AID receiver, String content, int performative){
		msg.setContent(content);
		msg.setPerformative(performative);
		msg.addReceiver(receiver);
		send(msg);
	}
}
