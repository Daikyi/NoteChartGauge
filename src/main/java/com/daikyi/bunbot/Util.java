package com.daikyi.bunbot;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Util {

	public static final String PATH = "res/user/users.xml"; 
	
	public enum ExplosiveAction {
			Suicide, Deflect, Nuke, Bomb, TimeOut, Explode, Dud,
			MissileIgnore, Missile, MissileSelf,
			TNT, TNTIgnore, TNTSelf
	};
	
	public static void saveUserXML(HashMap<String, UserStat> userStruct){

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("UserList");
		doc.appendChild(rootElement);

		for(String userID : userStruct.keySet()){
			
			UserStat curUser = userStruct.get(userID); 
			Element user = doc.createElement("User");
			rootElement.appendChild(user);
			
			user.setAttribute("id", userID);
			
			Element firstname = doc.createElement("username");
			firstname.appendChild(doc.createTextNode(curUser.getUsername()));
			user.appendChild(firstname);
			
			Element maxBStreak = doc.createElement("max-bomb-streak");
			maxBStreak.appendChild(doc.createTextNode(""+curUser.getMaxBombStreak()));
			user.appendChild(maxBStreak);
			
			Element curBStreak = doc.createElement("cur-bomb-streak");
			curBStreak.appendChild(doc.createTextNode(""+curUser.getCurBombStreak()));
			user.appendChild(curBStreak);
			
			Element posmoneys = doc.createElement("pos-moneys");
			posmoneys.appendChild(doc.createTextNode(""+curUser.getMoniesGained()));
			user.appendChild(posmoneys);
			
			Element minmoneys = doc.createElement("min-moneys");
			minmoneys.appendChild(doc.createTextNode(""+curUser.getMoniesLost()));
			user.appendChild(minmoneys);
			
			Element bdeaths = doc.createElement("bomb-deaths");
			bdeaths.appendChild(doc.createTextNode(""+curUser.getBombDeaths()));
			user.appendChild(bdeaths);
			
			Element mdeaths = doc.createElement("missile-deaths");
			mdeaths.appendChild(doc.createTextNode(""+curUser.getMissileDeaths()));
			user.appendChild(mdeaths);
			
			Element tdeaths = doc.createElement("tnt-deaths");
			tdeaths.appendChild(doc.createTextNode(""+curUser.getTNTDeaths()));
			user.appendChild(tdeaths);
			
			Element defuses = doc.createElement("defuses");
			defuses.appendChild(doc.createTextNode(""+curUser.getDefuses()));
			user.appendChild(defuses);
			
			Element dodgeStreak = doc.createElement("dodge-streak");
			dodgeStreak.appendChild(doc.createTextNode(""+curUser.getDodgeStreak()));
			user.appendChild(dodgeStreak);
			
			Element plungeStreak = doc.createElement("plunge-streak");
			plungeStreak.appendChild(doc.createTextNode(""+curUser.getPlungeStreak()));
			user.appendChild(plungeStreak);
		}
		

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(PATH));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
	}
	
	public static HashMap<String, UserStat> loadUserXML(){
	
		HashMap<String, UserStat> toReturn = new HashMap<String, UserStat>();
	      try {	
	          File inputFile = new File(PATH);
	          DocumentBuilderFactory dbFactory 
	             = DocumentBuilderFactory.newInstance();
	          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	          Document doc = dBuilder.parse(inputFile);
	          
	      
	          doc.getDocumentElement().normalize();
	          
	          //root element is "UserList"
	          NodeList nList = doc.getElementsByTagName("User");
	          
	          for (int temp = 0; temp < nList.getLength(); temp++) {
	        	 
	             Node nNode = nList.item(temp);
	             
	             if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            	 
	                Element eElement = (Element) nNode;
	                
	                /*
	                 * username
	                 * max-bomb-streak
	                 * cur-bomb-streak
	                 * moneys
	                 * deaths
	                 * defuses
	                 * dodge-streak
	                 */
	                
	                UserStat current = new UserStat(eElement.getElementsByTagName("username").item(0).getTextContent(), eElement.getAttribute("id"));
	                current.setCurBombStreak(Integer.parseInt(eElement.getElementsByTagName("cur-bomb-streak").item(0).getTextContent()));
	                current.setMaxBombStreak(Integer.parseInt(eElement.getElementsByTagName("max-bomb-streak").item(0).getTextContent()));
	                current.setMonies(Integer.parseInt(eElement.getElementsByTagName("pos-moneys").item(0).getTextContent()));
	                current.setLosses(Integer.parseInt(eElement.getElementsByTagName("min-moneys").item(0).getTextContent()));
	                current.setBombDeaths(Integer.parseInt(eElement.getElementsByTagName("bomb-deaths").item(0).getTextContent()));
	                current.setMissileDeaths(Integer.parseInt(eElement.getElementsByTagName("missile-deaths").item(0).getTextContent()));
	                current.setTNTDeaths(Integer.parseInt(eElement.getElementsByTagName("tnt-deaths").item(0).getTextContent()));
	                current.setDefuses(Integer.parseInt(eElement.getElementsByTagName("defuses").item(0).getTextContent()));
	                current.newDodgeStreak(Integer.parseInt(eElement.getElementsByTagName("dodge-streak").item(0).getTextContent()));
	                current.newPlungeStreak(Integer.parseInt(eElement.getElementsByTagName("plunge-streak").item(0).getTextContent()));
	                
	                toReturn.put(eElement.getAttribute("id"), current);
	             }
	          }
	       } catch (Exception e) {
	          e.printStackTrace();
	       }
	      return toReturn;
	}
	
	public static ArrayList<String> getTNT(){
		
	   	ArrayList<String> toReturn = new ArrayList<String>();
    	
    	//int rando = Dice.randomInt(1, 1000);
    	//if(rando > 990){
    	toReturn.add("one");
    	toReturn.add("two");
    	toReturn.add("three");
    	toReturn.add("four");
    	toReturn.add("five");
    	toReturn.add("six");
    	//}
    	
    	return toReturn;
	}
	
    public static ArrayList<String> getWires(){
    	
    	ArrayList<String> toReturn = new ArrayList<String>();
    	ArrayList<String> bombWireList = new ArrayList<String>();
    	
    	int rando = Dice.randomInt(1, 1000);
    	if(rando > 990){
    		bombWireList.add("!?!?");
    		bombWireList.add("?!??");
    		bombWireList.add("??");
    		bombWireList.add("??!");
    		bombWireList.add("!!!");
    		bombWireList.add("??!!");
    		bombWireList.add("!!!!");
    		bombWireList.add("?");
    		bombWireList.add("!");
    		bombWireList.add("??!??");
    		bombWireList.add("!!!?!");
    	}
    	else if(rando > 980){
    		bombWireList.add("Halogen");
    		bombWireList.add("Wiosna");
    		bombWireList.add("Kamikaze");
    		bombWireList.add("Daikyi");
    		bombWireList.add("juankristal");
    		bombWireList.add("Konner");
    		bombWireList.add("Steins");
    		bombWireList.add("EtienneXC");
    		bombWireList.add("Azlynn");
    		bombWireList.add("Staiain");
    		bombWireList.add("Hydria");
    	}
    	else if(rando > 970){
    		bombWireList.add("that_wire");
    		bombWireList.add("this_wire");
    		bombWireList.add("those_wires");
    		bombWireList.add("these_wires");
    		bombWireList.add("them_wires");
    		bombWireList.add("his_wire");
    		bombWireList.add("her_wire");
    		bombWireList.add("their_wires");
    		bombWireList.add("your_wire");
    		bombWireList.add("my_wire");
    		bombWireList.add("the_wire");
    		bombWireList.add("a_wire");
    		bombWireList.add("yonder_wires");
    		bombWireList.add("all_wires");
    	}
    	else{
        	bombWireList.add("red");
        	bombWireList.add("orange");
        	bombWireList.add("yellow");
        	bombWireList.add("green");
        	bombWireList.add("cyan");
        	bombWireList.add("blue");
        	bombWireList.add("violet");
        	bombWireList.add("brown");
        	bombWireList.add("black");
        	bombWireList.add("white");
    	}
    	
    	int numWires = Dice.randomInt(4, bombWireList.size());
    	
    	for(int i = 0; i < numWires; i++){
    		toReturn.add(bombWireList.remove(Dice.randomInt(0, bombWireList.size()-1)));
    	}
    	return toReturn;
    }
    
    public static String getBombSay(String bomber, String bombee, ExplosiveAction ba){
    	
    	switch(ba){
	    	case Suicide:
		    	String[] selfBomb = {
		    		":bomb:",
		    		bomber+", you crazy??  Well if you insist.",
		    		bomber+" gives themselves a nice bomb to defuse.",
		    		"It's your choice i guess.  Good luck, "+bomber+"!",
		    		bomber+" juggles a bit with a bomb and accidentally sets the countdown off!",
		    		"Looks like "+bomber+" has a death wish..."
		    	};
		    	return selfBomb[Dice.randomInt(0, selfBomb.length-1)];
	    	case Bomb:
		    	String[] bomb = {
		    		":bomb:",
		    		"Somebody set us up the bomb!  Get ready, "+bombee+"!",
		    		bomber+" does a slow motion bomb toss over to "+bombee+".",
		    		bomber+" sneaks a bomb into "+bombee+"'s birthday cake.",
		    		bombee+" all of a sudden finds a bomb in their lap!!",
		    		"*Cue the music* It's go time, "+bombee+".",
		    		"There's a bomb.  And it's "+bombee+"'s job to defuse it~",
		    		bomber+" decides that today is a good day to give "+bombee+" a bomb to defuse!"
		    	};
		    	return bomb[Dice.randomInt(0, bomb.length-1)];
	    	case Deflect:
	    		String[] deflectBomb = {
	    			bombee+" says \"fug you, "+bomber+"\" and sends the bomb right back!",
	    			bombee+" takes a tennis racket and serves the bomb back to "+bomber+".",
	    			"The bomb magically disappears from "+bombee+" and appears in "+bomber+"'s lap.",
	    			"Hot Potato!  "+bombee+" lobs the bomb back over to "+bomber+".",
	    			"Whoops, sorry it looks like "+bomber+" wanted the bomb after all!",
	    			"Congratulations "+bombee+", you've successfully pushed the responsibility of defusing the bomb onto "+bomber+"!"
	    		};
	    		return deflectBomb[Dice.randomInt(0, deflectBomb.length-1)];
	    	case Explode:
	    		String[] explodeBomb = {
	    			"KAbOoOoOoOoOoOm! "+bombee+" has exploded.",
	    			"YOUR HEAD ASPLODE. "+bombee+" has asploded.",
	    			"Congratulations "+bombee+" you have failed!  The bomb explodes.",
	    			"Better luck next time "+bombee+", because you've just exploded.",
	    			"Rest in spaghetti, "+bombee+".",
	    			bombee+", noooooooooo that's not the right wi-*BOOM*",
	    			bombee+" fails.  The bomb explodes.",
	    			"Nope, that's not the right one.  *Kaboooom*",
	    			"Hawawawawawawa*THOOOM*",
	    			"Nope.avi  *Explosion noises*",
	    			"C'mon "+bombee+ " are you even trying right now?  You dead.",
	    			"Rippu "+bombee+" you're pretty dead desune"
	    		};
	    		return explodeBomb[Dice.randomInt(0, explodeBomb.length-1)];
	    	case Dud:
	    		String[] dudBomb = {
	    			"THE WIRE DIDN'T DO ANYTHING.",
	    			"OH NOOOOOO- oh wait that was a dud wire.",
	    			"Kaboom?  Nah.  Just a dud.",
	    			"You cut the wire.  But nobody came.",
	    			"Total silence.  The wire did nothing",
	    			"Nothing happens. Another chance at salvation!"
	    		};
	    		return dudBomb[Dice.randomInt(0,  dudBomb.length-1)];
	    	case TimeOut:
	    		String[] timeBomb = {
	    			bombee+" takes too much time and the bomb explodes in their face.",
	    			"Tick, tock, tick, tock, BOOOOM. "+bombee+" has run out of time.",
	    			"SSssSssSSSsSSSsssSSsFOOOOM. "+"RIP "+bombee,
	    			"*Time's up!  You're dead!* - "+bombee+" has died."
	    			
	    		};
	    		return timeBomb[Dice.randomInt(0,  timeBomb.length-1)];
	    	case MissileIgnore:
	    		String[] missileIg = {
	    			bombee+" ignored the missile, and let it plow right into their face."
	    		};
	    		return missileIg[Dice.randomInt(0, missileIg.length-1)];
	    	case Missile:
	    		String[] missile = {
	    			":rocket:"
	    			
	    		};
	    		return missile[Dice.randomInt(0, missile.length-1)];
	    	case MissileSelf:
	    		String[] missileSelf = {
	    			":rocket:"
	    		};
	    		return missileSelf[Dice.randomInt(0, missileSelf.length-1)];
	    	case TNT:
	    		String[] TNT = {
		    			":fireworks:"
		    	};
	    		return TNT[Dice.randomInt(0, TNT.length-1)];
	    	case TNTSelf:
	    		String[] TNTSelf = {
	    				":fireworks:"
	    		};
	    		return TNTSelf[Dice.randomInt(0, TNTSelf.length-1)];
	    	case TNTIgnore:
	    		String[] TNTIgnore = {
	    				":fireworks:"
	    		};
	    		return TNTIgnore[Dice.randomInt(0, TNTIgnore.length-1)];
	    	default:
	    		return ":bomb:";
    	}
    }

    public static String getHelp(String helpString){
    	
    	String toReturn = "";
    	if(helpString.equals("")){
    		toReturn += "Hi there, what can I help you with?\n";
    		toReturn += "Current help topics include ( use: *!help topic* )\n";
    		toReturn += "roll, bomb, missile, tnt, bombstats, top";
    	}
    	else if(helpString.equals("roll")){
    		toReturn += "Format: *!roll [1d10, 2d8, 5d100]*\n";
    		toReturn += "The roll command simply lets you roll a set of die.\n";
    		toReturn += "Without any parameters, it will give you a roll from 1 to 100.\n";
    		toReturn += "Otherwise, you can specify any number of die sets\n";
    	}
    	else if(helpString.equals("bomb")){
    		toReturn += "Format: *!bomb [@mention]";
    		toReturn += "The bomb command allows you to gift a bomb to someone (or yourself)\n";
    		toReturn += "Without any parameters, you will bomb yourself.\n";
    		toReturn += "By adding a highlight, the bomb will be gifted to the highlighted user.\n";
    		toReturn += "The bomb will have 4-??? wires, and one wire will successfully defuse the bomb.\n";
    		toReturn += "Another wire may do absolutely nothing, and yet another wire may also return the bomb to the sender!";
    		toReturn += "The bomb will display the valid wires that you may cut.";
    	}
    	return toReturn;
    }
	

	public static String getEmail(){
		String toReturn = "";
		char[] email = {107,114,105,115,108,107,114,101,109,101,64,103,109,97,105,108,46,99,111,109};
		for(int i = 0; i < email.length; i++){
			toReturn += email[i];
		}
		return toReturn;
	}
	
	public static String getPass(){
		String toReturn = "";
		char[] pass = {117,109,112,54,118,99,109,110};
		for(int i = 0; i < pass.length; i++){
			toReturn += pass[i];
		}
		return toReturn;
	}
}
