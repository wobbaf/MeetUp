package parser;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLRead {
	private XMLRead instance;
	public String[] list;
	public String id;
	public String state;
	public String location;
	public String time;
	public String type;
	//public String[] friends = new String[5];
	public ArrayList<String> friends = new ArrayList<String>();
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList getFriends() {
		return friends;
	}
	public void setFriends(ArrayList friends) {
		this.friends = friends;
	}
	public XMLRead(){
		instance = this;
	}
	public void Read(String xml){
		if(xml!=null)
		{
		    try  
		    {  
		    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder builder = factory.newDocumentBuilder();
		    	InputSource in = new InputSource(new StringReader(xml));
		    	Document document = builder.parse(in);
		    	Element rootElement = document.getDocumentElement();
		        NodeList nodeList = document.getElementsByTagName("friends")
		                .item(0).getChildNodes();
		            // get the immediate child (1st generation)
		            for (int i = 0; i < nodeList.getLength(); i++)
		            switch (nodeList.item(i).getNodeType()) { 
		            case Node.ELEMENT_NODE:
		            	//System.out.println(nodeList.getLength());
		                Element element = (Element) nodeList.item(i);
		                //System.out.println("element name: " + element.getNodeName());
		                // check the element name
		                if (element.getNodeName().equalsIgnoreCase("friend"))
		                {
		                	
		                //System.out.println("element name:" + element.getNodeName() + " id: " + element.getAttribute("id"));
		                friends.add(element.getAttribute("id"));
		                }
		                break;
		            }
		    	type = getString("type",rootElement);
		        id = getString("id",rootElement);
		        
		    	switch(type){
		    		case "0":
				        location = getString("location",rootElement);
				        break;
		    		case "1":
				        state = getString("state",rootElement);
				        break;
		    		case "2":
				        location = getString("location",rootElement);
				        time = getString("time",rootElement);
				        break;
		    	}
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    } 
		}
	   }
	protected static String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();
            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }
}
