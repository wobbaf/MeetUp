package Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.example.sample.*;
import com.example.sample.PlaceFinding.DistrictsSearch;
import com.example.sample.User.Initiator;
import com.example.sample.User.Invited;
import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import parser.XMLRead;

public class Server extends Agent{
	
	Initiator initiator; 
	List<Invited> agreedInvitedUsers = new ArrayList<>();
	int ctr = 0;
	long startTime = System.currentTimeMillis();
	boolean timeout = false;
	@Override
	protected void setup(){
	ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
	ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
	ACLMessage informFinal = new ACLMessage(ACLMessage.INFORM);
	ACLMessage recm;
	String agentID = "Server";
	
	 MessageTemplate mt = MessageTemplate.MatchAll();
	 
	 Behaviour b = new CyclicBehaviour(this){
		 public void action(){
				ACLMessage rec = receive(mt);
				if(rec != null){
					if( rec.getContent() != null){
						XMLRead r = new XMLRead();
						String xml = rec.getContent();
						r.Read(xml);
						switch(r.type){
						case "0":
							String[] location = r.location.split(" ");
							if(location.length<2)
								System.out.println("Wrong location");
									
							com.example.sample.Utils.Location<String,String> loc = new com.example.sample.Utils.Location<String,String>(location[0],location[1]);
							
							
							initiator = new Initiator(null,r.id,loc,r.placeType,r.friends);
							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.location);
							for(int i = 0; i < r.friends.size(); i++){
								System.out.println("Friend: " + r.friends.get(i));
								//TODO: add to list of Initiator field
							}
							
						
							
							//dodac type place
							
							String content = setContent("0", rec.getSender().getLocalName(),null,null,null,r.friends);
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
							if(r.location != null){
							System.out.println(this.getAgent().getName() + " to: " + r.id + " LOCATION: " + r.location + " state: " + r.state);
							for(int i = 0; i < r.friends.size(); i++){
								System.out.println("Friend: " + r.friends.get(i));
							}
							String[] locationInvited = (r.location).split(" ");
							if(locationInvited.length<2)
								System.out.println("Wrong location intvited user");
									
							com.example.sample.Utils.Location<String,String> locUser = new com.example.sample.Utils.Location<String,String>(locationInvited[0],locationInvited[1]);
							
							Invited currInvited = new Invited(null,r.id,locUser);
							agreedInvitedUsers.add(currInvited);
							
							if((r.friends.size() == agreedInvitedUsers.size()) || System.currentTimeMillis() - startTime >= 180000){
								DistrictsSearch DS = new DistrictsSearch();
								
								 int bestDistrictForAllUsers = DS.BestPlace(initiator, agreedInvitedUsers);
								 Stack<String> allbestPlacesForThem = DS.findClosestPlaceInBestDistrict(bestDistrictForAllUsers);
								
								 System.out.println(allbestPlacesForThem.toString());
								 agreedInvitedUsers.clear();
								 
								}
							// add as invited bool, iterate over list of invited and check ifAgreed 
							String content1 = setContent("1", rec.getSender().getName(),null,"accepting",null,r.friends);
							sendMessage(inform,new AID(r.id,AID.ISLOCALNAME),content1,ACLMessage.INFORM);
							for(int i = 0; i < r.friends.size(); i++){
								try{
								sendMessage(propose,new AID(r.friends.get(i),AID.ISLOCALNAME),content1,ACLMessage.INFORM);
								}
								catch(Exception e){
									
								}
							}
							}
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
