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
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import parser.XMLRead;

public class Server extends Agent{

	Initiator initiator; 
	List<Invited> agreedInvitedUsers = new ArrayList<>();
	int ctr = 0;
	long startTime = System.currentTimeMillis();
	boolean timeout = false;
	boolean flag = false;
	int gbad = 0;
	ArrayList<String> friends = new ArrayList<String>();
	ArrayList<String> present = new ArrayList<String>();
	Stack<String> allbestPlacesForThem;
	ArrayList<String> done = new ArrayList<String>();
	 AMSAgentDescription [] agents = null;
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
						if(r.type != null){

						    try {
						        SearchConstraints c = new SearchConstraints();
						        c.setMaxResults ( new Long(-1) );
						        agents = AMSService.search( this.myAgent, new AMSAgentDescription (), c );
						    }
						    catch (Exception e) {}
						    for (int j=0; j<agents.length;j++){
							     present.add(agents[j].getName().getLocalName());
							}
						switch(r.type){
						case "0":
							friends = r.friends;
							friends.retainAll(present);
							String[] location = r.location.split(" ");
							if(location.length<2)
								System.out.println("Wrong location");

							com.example.sample.Utils.Location<String,String> loc = new com.example.sample.Utils.Location<String,String>(location[0],location[1]);

							System.out.println(this.getAgent().getName() + " to: " + r.id + " " + r.location);
							for(int i = 0; i < r.friends.size(); i++){
								System.out.println("Friend: " + r.friends.get(i));
								//TODO: add to list of Initiator field
							}
							String time = r.time;


							//dodac type place
							//String type
							//String id
							//String location
							//String state
							//String time
							//String placeId
							//String placeType
							//ArrayList<String> friends
							//ArrayList<String> favPlaces
							try{
							String content = setContent("0", rec.getSender().getLocalName(),null,null,null,null,null,r.friends,null);
							//sendMessage(propose,new AID(r.id,AID.ISLOCALNAME),content,ACLMessage.PROPOSE);
							for(int i = 0; i < r.friends.size(); i++){
								try{
									sendMessage(propose,new AID(r.friends.get(i),AID.ISLOCALNAME),content,ACLMessage.PROPOSE);
									if(present.contains(r.friends.get(i))){
									System.out.println("Inv sent to: " + r.friends.get(i));
									}
								}
								catch(Exception e){
								
								}
							}
							}
							catch(Exception e){
							}
							String[] locationInvite = (r.location).split(" ");
							com.example.sample.Utils.Location<String,String> locInit = new com.example.sample.Utils.Location<String,String>(locationInvite[0],locationInvite[1]);
							
							Invited init = new Invited(null,rec.getSender().getLocalName(),locInit,true);
							agreedInvitedUsers.add(init);
							initiator = new Initiator(r.getfavPlaces(),r.id,locInit,r.placeType,r.friends, r.time);
							break;
						case "1":
								System.out.println(this.getAgent().getName() + " to: " + r.id + " LOCATION: " + r.location + " state: " + r.state);
								String[] locationInvited = (r.location).split(" ");
								if(locationInvited.length<2){
									System.out.println("Wrong location intvited user");
								}
								com.example.sample.Utils.Location<String,String> locUser = new com.example.sample.Utils.Location<String,String>(locationInvited[0],locationInvited[1]);
								if(r.state.equals("accepting")){
									agreedInvitedUsers.add(new Invited(null,r.id,locUser,true));
									//String contentx = setContent("2", r.id, locUser.getLatitude().toString()
									//		+" "+locUser.getLongitude().toString(), r.state, null, null, null, null, null);
									//sendMessage(inform,new AID(initiator.getId(),AID.ISLOCALNAME),contentx,ACLMessage.INFORM);
									//System.out.println("1Sent message with who agreed to: " + initiator.getId());
							
								}
								else if (r.state.equals("decline")){
									agreedInvitedUsers.add(new Invited(null,r.id,null,false));
									//String contentx = setContent("2", r.id, null, r.state, null, null, null, null, null);
									//sendMessage(inform,new AID(initiator.getId(),AID.ISLOCALNAME),contentx,ACLMessage.INFORM);
									//System.out.println("Sent message with who declined to: " + initiator.getId());
								}
							
							if(agreedInvitedUsers.size() >0){
								for(int i = 0;  i < agreedInvitedUsers.size(); i++){
									if (i < friends.size()){
									String state;
									if(agreedInvitedUsers.get(agreedInvitedUsers.size()-1).hasConfirmed() == true){
										state = "accepting";
									}
									else{
										state = "decline";
									}
									String contentx = setContent("2", agreedInvitedUsers.get(agreedInvitedUsers.size()-1).getId(), agreedInvitedUsers.get(agreedInvitedUsers.size()-1).getLocation().getLatitude().toString()
											+" "+agreedInvitedUsers.get(agreedInvitedUsers.size()-1).getLocation().getLongitude().toString(), state, null, null, null, null, null);
									sendMessage(inform,new AID( agreedInvitedUsers.get(0).getId(),AID.ISLOCALNAME),contentx,ACLMessage.INFORM);
									System.out.println("2Sent message with who agreed to: " + initiator.getId());
									}
								}
							}
							
							//todo wyjebac timer poza BEHAVIOUR
								if((friends.size()+1 == agreedInvitedUsers.size()) || System.currentTimeMillis() - startTime >= 180000){
									System.out.println("Strating computations...");
									for(int i = 0; i < agreedInvitedUsers.size(); i++){
									System.out.println(agreedInvitedUsers.get(i).getId());
									}
									DistrictsSearch DS = new DistrictsSearch();
									//agreedInvitedUsers.remove(0);
									Stack<Integer> bestDistrictForAllUsers = DS.BestPlace(initiator, agreedInvitedUsers);
									System.out.println(bestDistrictForAllUsers.toString());
								
									long [] inviterTimeTravel = new long[1];
									allbestPlacesForThem = DS.findClosestPlaceInBestDistrict(bestDistrictForAllUsers,inviterTimeTravel);
									
									System.out.println(allbestPlacesForThem.toString());
									if(allbestPlacesForThem == null)
									{
										//TODO send message there is no such place in warsaw. 
									}

									if(initiator.getTime()  < inviterTimeTravel[0])
									{
										//send message that he wont make it in time.									
									}
									if(allbestPlacesForThem.isEmpty()){}
									else{
									String contentt = setContent("3", null, null, null, null, allbestPlacesForThem.peek().toString(), null, r.friends, null);
									System.out.println(initiator.getId() +" placeprop " + contentt);
									XMLRead ex = new XMLRead();
									String eml = contentt;
									ex.Read(eml);
									System.out.println(ex.placeId);
									//sendMessage(propose,new AID(initiator.getId(),AID.ISLOCALNAME),contentt,ACLMessage.INFORM);
									if(!flag){
									for(int i = 0; i < agreedInvitedUsers.size(); i++){
										try{
											sendMessage(propose,new AID(agreedInvitedUsers.get(i).getId(),AID.ISLOCALNAME),contentt,ACLMessage.INFORM);
											System.out.println("Message w/ place id sent to: " + agreedInvitedUsers.get(i).getId() );
										}
										catch(Exception e){

										}
									}
									flag = true;
									//System.out.println(allbestPlacesForThem.toString());
									//agreedInvitedUsers.clear();
									}
									}

								}
								//(String type, String id, String location, String state, String time, String placeId, String placeType, ArrayList<String> friends, ArrayList<String> favPlaces)
								
							// add as invited bool, iterate over list of invited and check ifAgreed 
							break;
						case "4":
							if(r.state != null && !done.contains(rec.getSender().getLocalName().toString())){
								done.add(rec.getSender().getLocalName().toString());
								System.out.println(this.getAgent().getName() + " to: " + r.id + " LOCATION: " + r.location + " state: " + r.state);
								for(int i = 0; i < agreedInvitedUsers.size(); i++){
									if(agreedInvitedUsers.get(i).hasConfirmed() == false)
										agreedInvitedUsers.remove(i);
								}
								Invited currInvited;
								if(r.state.equals("accepting")){
									ctr++;
									gbad++;
								}
								else if(r.state.equals("decline")){
									ctr++;
									gbad--;
								}
								System.out.println("USR: "+agreedInvitedUsers.size());
								System.out.println("CTR: "+ctr);
								System.out.println("GBAD: "+gbad);
								if(((agreedInvitedUsers.size() == ctr) && gbad >=0) || System.currentTimeMillis() - startTime >= 180000){
									//TODO send last msg to everyone with time and place
									String contentt3 = setContent("5", null, null, null, null, allbestPlacesForThem.pop().toString(), null, r.friends, null);
									System.out.println(initiator.getId() +" placeprop " + contentt3);
									for(int i = 0; i < agreedInvitedUsers.size(); i++){
										try{
											sendMessage(propose,new AID(agreedInvitedUsers.get(i).getId(),AID.ISLOCALNAME),contentt3,ACLMessage.INFORM);
											System.out.println("Message w/ place id sent to: " + agreedInvitedUsers.get(i).getId() );
										}
										catch(Exception e){
										}
									}
									ctr = 0;
									gbad = 0;
									done.clear();
									while (myAgent.receive() != null) {
										;
										}
								}
								else if((agreedInvitedUsers.size() == ctr) && gbad < 0){
									//TODO loop this shit up
									allbestPlacesForThem.pop();
									String contentt2 = setContent("4", null, null, null, null, allbestPlacesForThem.pop().toString(), null, r.friends, null);
									System.out.println(initiator.getId() +" placeprop " + contentt2);
									for(int i = 0; i < agreedInvitedUsers.size(); i++){
										try{
											sendMessage(propose,new AID(agreedInvitedUsers.get(i).getId(),AID.ISLOCALNAME),contentt2,ACLMessage.INFORM);
											System.out.println("Message w/ place id sent to: " + agreedInvitedUsers.get(i).getId() );
										}
										catch(Exception e){

										}
									}
									ctr = 0;
									gbad = 0;
									done.clear();
									while (myAgent.receive() != null) {
										;
										}
								}
							}
							break;
						}	
						block();
					}
						
					}
				}

			}
		};
		addBehaviour(b);
	}
	private String setContent(String type, String id, String location, String state, String time, String placeId, String placeType, ArrayList<String> friends, ArrayList<String> favPlaces){
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
		if (placeType != null)
			p.instance().placeType = placeType;
		if (placeId != null)
			p.instance().placeId = placeId;
		if (friends != null)
			p.instance().friends = new ArrayList<>(friends);
		if (favPlaces != null)
			p.instance().favPlaces = new ArrayList<>(favPlaces);
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
