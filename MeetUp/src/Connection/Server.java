package Connection;
import java.util.ArrayList;

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
	
	//AID receiver = new AID(agentID,AID.ISLOCALNAME);
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
							for(int i = 0; i < r.friends.size(); i++){
								System.out.println("Friend: " + r.friends.get(i));
							}
							String content = setContent("0", rec.getSender().getName(),"52.22233 21.00690",null,null,r.friends);
							//sendMessage(propose,new AID(r.id,AID.ISLOCALNAME),content,ACLMessage.PROPOSE);
							for(int i = 0; i < r.friends.size(); i++){
								try{
								sendMessage(propose,new AID(r.friends.get(i),AID.ISLOCALNAME),content,ACLMessage.PROPOSE);
								}
								catch(Exception e){
									
								}
							}
							
							break;
						case "1":
							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.state);
							for(int i = 0; i < r.friends.size(); i++){
								System.out.println("Friend: " + r.friends.get(i));
							}
							String content1 = setContent("0", rec.getSender().getName(),"52.22233 21.00690",null,null,r.friends);
							sendMessage(inform,new AID(r.id,AID.ISLOCALNAME),content1,ACLMessage.INFORM);
							for(int i = 0; i < r.friends.size(); i++){
								try{
								sendMessage(propose,new AID(r.friends.get(i),AID.ISLOCALNAME),content1,ACLMessage.INFORM);
								}
								catch(Exception e){
									
								}
							}
							break;
						case "2":
							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.location + " " + r.time);
							for(int i = 0; i < r.friends.size(); i++){
								System.out.println("Friend: " + r.friends.get(i));
							}
							String content2 = setContent("0", rec.getSender().getName(),"52.22233 21.00690",null,null,r.friends);
							sendMessage(propose,new AID(r.id,AID.ISLOCALNAME),content2,ACLMessage.PROPOSE);
							break;
						}
						block();
					}
				}
			}
	 };
	 addBehaviour(b);
	}
	private String setContent(String type, String id, String location, String state, String time, ArrayList<String> friends){
		String content = null;
		parser.XMLParse p = new parser.XMLParse();
		//p.instance();
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
		if (friends != null)
			p.instance().friends = new ArrayList<>(friends);
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
