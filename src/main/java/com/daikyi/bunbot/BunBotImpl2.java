package com.daikyi.bunbot;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.login.LoginException;

import com.daikyi.bunbot.Explosives.ExplosiveStatus;
import com.daikyi.bunbot.UserStat.UserComp;
import com.daikyi.rhythmgauge.FileParser;
import com.daikyi.rhythmgauge.OSUFileParser;
import com.daikyi.rhythmgauge.SMFileParser;
import com.daikyi.rhythmgauge.difficulty.SMDiffRater;
import com.daikyi.rhythmgauge.timing.SMFile;

import net.dv8tion.jda.*;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.Message.Attachment;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.*;
import net.dv8tion.jda.events.message.MessageEmbedEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.user.UserNameUpdateEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.requests.Requester;
import net.dv8tion.jda.utils.InviteUtil;
/**
 * Spaghet code.  Trying to unspaghet and comment it
 * @author christopher.lim
 *
 */
public class BunBotImpl2 extends ListenerAdapter
{
	/* Bot info and general user parameters (mainly IDs/Names of certain channels/users) */
	private static JDA jda;
	private static final char ESCAPE = '!';
	private static HashMap<String, UserStat> users = new HashMap<String, UserStat>();
	private static final String OWNER = "123602371233185792";
	private static final String SPAM = "152143642587430913"; 
	//private static final String BOMB_CHAN = "153956447649136641";
	private static final String BOMB_CHAN= "153956447649136641";
	public static final String BOT_USERNAME = "Yahagi";
	
	/* Person being bombed (so the client can verify that the
	 * correct person is inputting the explosion response commands */
	private UserStat bombed = null;
	
	/**
	 * Load the saved user stats and get the bot to log in and such
	 * @param args
	 */
    public static void main(String[] args){
    	
        try{
        	
        	users = Util.loadUserXML();
            jda = new JDABuilder()
                    .setEmail(Util.getEmail())
                    .setPassword(Util.getPass())
                    .addListener(new BunBotImpl2())
                    .buildAsync();
            
        }catch (IllegalArgumentException e){	//email/password missing
        }catch (LoginException e){}				//incorrect email/password
    }//end main
    
    @Override
    public void onMessageEmbed(MessageEmbedEvent event){
    	
        //process urls for images or w/e. nothing to do here
    }//end onMessageEmbed
    
    @Override
    /**
     * Processes invites.  Only join the thing if the invite comes from the owner
     */
    public void onInviteReceived(InviteReceivedEvent event){
    	
        if (event.getMessage().getAuthor().getId().equals(OWNER))
            InviteUtil.join(event.getInvite(), event.getJDA(), null);
    }//end onInviteReceived

    @Override
    /**
     * When disconnected and reconnected, make sure the bot is playing w/ the owner
     */
    public void onReconnect(ReconnectedEvent event){
    	
    	event.getJDA().getAccountManager().setGame("with "+users.get(OWNER).getUsername());
    }//end onReconnect
    
    @Override
    /**
     * When actually starting up, make sure the bot is playing w/ the owner
     */
    public void onReady(ReadyEvent event){
    	
    	event.getJDA().getAccountManager().setGame("with "+users.get(OWNER).getUsername());
    }//end onReady
    
    @Override
    /**
     * If a username changes their name, update them in the user database
     */
	public void onUserNameUpdate(UserNameUpdateEvent unue){
    	
    	//getUser handles the updating
    	getUser(unue.getUser());
    	
    	//if the user is the owner, then update playing status
    	if(unue.getUser().getId().equals(OWNER))
    		jda.getAccountManager().setGame("with "+users.get(OWNER).getUsername());
	}//end onUserNameUpdate
    
    @Override
    /**
     * The meat.  Processes any message that it receives.
     * 
     *
     * 1) If there is an attachment, go to difficulty calculator
     * 2) Validation Checks
	 * 		- Chatroom is valid (spam or bomb)
	 * 		- Message isn't sent by the bot itself
	 * 		- Lines less than length 2 (impossible to be a command)
	 * 		- Lines that don't start with the Command prefix
	 * 3) If validation section is passed, then process the command
     */
    public void onMessageReceived(MessageReceivedEvent mre){
    	
    	//some setup variables for readability
        Message message = mre.getMessage();
        String content = message.getContent();
        MessageChannel sendTo = mre.getChannel();
        System.out.println(message.getChannelId());
        //if there's an attachment, try to do a difficulty calculation
        if(message.getAttachments() != null && !message.getAttachments().isEmpty())
        	diffCalc(mre);
        
        //Check to make sure the room is valid, and also ignore accidntal self commands
        //and lines that are too short to be commands, or aren't commands at all        
        if(!mre.isPrivate()){
        	if(!message.getChannelId().equals(SPAM) && !message.getChannelId().equals(BOMB_CHAN))
        		return;
        }
        if(mre.getAuthor().getUsername().equals(BOT_USERNAME)
          		 || content.length() < 2 || content.charAt(0) != ESCAPE)
			return;
        
        //command w/ parameters (minus the command prefix)
        String[] splitContent = content.substring(1).trim().toLowerCase().split(" ");
        
        User request = mre.getAuthor();
        StringBuilder builder = new StringBuilder();
        
        //roll dice command, can be done in any channel
        //format -> !roll #d#, #d#, #d#
        if(splitContent[0].equals("roll")){
        	builder.append(Dice.roll(content.substring(5).split(",")));
        	sendTo.sendMessage(builder.toString());
        }//end roll
        
        /*
         * Bomb Channel commands.
		 * format -> !explosivetype @mention
         * Types of Explosives:
         * 		Bomb
         * 			Gives a user a bomb, they must select a wire to cut to defuse
         * 		Missile
         * 			Fires a homing missile, they must dodge to send the missile back
         * 		TNT
         * 			Russian roulette with TNT plungers
         * 
         * !bombstats (optional) @mention
         * !top
         * !help
         * Only 1 explosive can be running at a time
         */
        else if(message.getChannelId().equals(BOMB_CHAN) || message.isPrivate()){
        	
        	ExplosiveStatus explStatus = Explosives.getStatus();
        	
        	//bombs a user.
	        if(splitContent[0].equals("bomb") && explStatus==ExplosiveStatus.None && !message.isPrivate())
	        	bombed = Explosives.bomb(message, sendTo);
	        
	        else if(splitContent[0].equals("help")){
	        	String toParse = "";
	        	if(splitContent.length > 1)
	        		toParse = splitContent[1];
	        	helpSend(toParse, message.getAuthor());
	        }
	       
	        //owner command to manually defuse whatever explosive is active
	        else if(splitContent[0].equals("defuse") && request.getId().equals(OWNER)){
	        	
	        	Explosives.setStatus(ExplosiveStatus.None);
	        	Explosives.defuse();
	        	builder.append("Explosive defused by a higher power");
	        	sendTo.sendMessage(builder.toString());
	        }
	        
	        //launches a missile at a user.
	        else if(splitContent[0].equals("missile") && explStatus==ExplosiveStatus.None && !message.isPrivate())
	        	bombed = Explosives.missile(message, sendTo);
	  
	        //gets statistics for a given user
	        else if(splitContent[0].equals("bombstats"))
	        	Explosives.bombStats(message, request, users);
	        
	        //gets leaderboards for a given thing
	        else if(splitContent[0].equals("top")){
	        	
	        	String toParse = "";
	        	if(splitContent.length > 1)
	        		toParse = splitContent[1];
	        	leaderboards(toParse, sendTo);	
	        }
	        
	        //sets up a tnt battle with another user
	        else if(splitContent[0].equals("tnt") && explStatus==ExplosiveStatus.None && !message.isPrivate())
	        	bombed = Explosives.tnt(message, sendTo);
	        
	        //nukes another user....?
	        else if(splitContent[0].equals("nuke") && !message.isPrivate())
	        	sendTo.sendMessage("shhhh....");
	        
	        //sets up a landmine....?
	        else if(splitContent[0].equals("landmine") && !message.isPrivate())
	        	sendTo.sendMessage("?");
	        
	        //if no other legit command is used, check to see if the command is actually one of the valid choices
	        //and call the appropriate explosion resolution method.
	        else if(!message.isPrivate() && Explosives.validChoice(splitContent[0])){
	        	
	        	if(explStatus!=ExplosiveStatus.None && users.get(request.getId()).equals(bombed)){
	        		
	        		switch(explStatus){
	        		
		        		case Bomb:
		        			bombed = Explosives.cutWire(message, sendTo);
		        			break;
		        		case Missile:
		        			bombed = Explosives.dodgeMissile(message, sendTo);
		        			break;
		        		case TNT:
		        			bombed = Explosives.plunge(message, sendTo);
		        		default:
		        			break;
	        		}//end switch
	        	}//end if
	        }//end explosive choice if tree
	        
	        //save the user XML because it was a command in the bomb channel
	        Util.saveUserXML(users);
        }//end bomb channel processing
    }//end onMessageReceived
    
    private void helpSend(String arg, User u){

    	StringBuilder sb = new StringBuilder();
    	sb.append(Util.getHelp(arg));
    	u.getPrivateChannel().sendMessage(sb.toString());
    }
    
    /**
     * Tries to first parse a rhythm game file (currently supporting .sm and ??.osu??)
     * then spits out a difficulty calculation for the file.  very bad metric and it sucks
     * @param mre
     */
    private void diffCalc(MessageReceivedEvent mre){
    	
        Message message = mre.getMessage();
 
        //idk if there's support for multiple attachments yet but might as well
    	for(Attachment a : message.getAttachments()){
        		
    		//this is going to start taking up space having new files for each thing
        	File download = new File("res/sm/"+a.getId()+"_"+a.getFileName());
        	download.delete();

        	//download the file via external helper, then parse, and diffcalc
        	if(downloadSM(download, a)){
        	
				FileParser file = null;
				
				//call the correct parser pls
				if(download.getName().substring(download.getName().length()-2).equals("sm"))
					file = new SMFileParser(download);
				else if(download.getName().substring(download.getName().length()-3).equals("osu"))
					file = new OSUFileParser(download);
				else
					return;

				//rate it with the other things
				file.parseFile();
				SMFile song = (SMFile)file.getSong();
				SMDiffRater fileDiff = new SMDiffRater(song.getChart(0));
				StringBuilder builder = new StringBuilder();
				builder.append(song.getTitle() + " Calculated Difficulty:\n" + fileDiff.calculateChartDifficulty());
				mre.getChannel().sendMessage(builder.toString());
        	}//end section that runs only if downloaded properly
    	}//end attachment loop
    }//end diffCalc
    
    /**
     * Helper method to download an SM file
     * @param file
     * @param a
     * @return
     */
    private boolean downloadSM(File file, Attachment a){
    	
        InputStream in = null;
        try{
        	
            URL url = new URL(a.getUrl());
            URLConnection con;
            if (jda.getGlobalProxy() == null)
                con = url.openConnection();
            else
                con = url.openConnection(new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(jda.getGlobalProxy().getAddress(), jda.getGlobalProxy().getPort())));
            con.addRequestProperty("user-agent", Requester.USER_AGENT);
            in = con.getInputStream();
            Files.copy(in, Paths.get(file.getAbsolutePath()));
            return true;
        } catch (Exception e){}	//fuck that stupid goddamn exception
        finally{
            if (in != null)
                try {in.close();} catch(Exception ignored) {}
        }
        return false;
    }   
  
    /**
     * gets a user's statistic structure via their user object
     * @param u
     * @return
     */
    public static UserStat getUser(User u){
    	
    	//if the user exists in the structure (keys done by id) then update username, and return userstat
    	if(users.containsKey(u.getId())){
    		
    		UserStat us = users.get(u.getId());
    		if (!us.getUsername().equals(u.getUsername()))
    			us.setNewUserName(u.getUsername());		
    		return users.get(u.getId());
    	}//end if
    	
    	//user does not exist, so create a new entry for them
    	UserStat newUser = new UserStat(u.getUsername(), u.getId());
    	users.put(u.getId(), newUser);
    	return newUser;
    }//end getUser
    
    /**
     * displays the leaderboards to a channel.
     * @param arg
     * @param mc
     */
    public void leaderboards(String arg, MessageChannel mc){
    	
    	StringBuilder sb = new StringBuilder();
    	ArrayList<UserStat> use = new ArrayList<UserStat>();
		for(String userID : users.keySet())
			use.add(users.get(userID));
		
    	//deaths, defuses, monies, streak, dodges
    	if(arg.equals("deaths")){
    		use.sort(UserComp.Deaths);
    		sb.append("Top death counts\n");
    	}
    	else if(arg.equals("defuses")){
    		use.sort(UserComp.Defuses);
    		sb.append("Top defuse counts\n");
    	}
    	else if(arg.equals("monies")){
    		use.sort(UserComp.Monies);
    		sb.append("Top monies\n");
    	}
    	else if(arg.equals("bombstreaks")){
    		use.sort(UserComp.Streak);
    		sb.append("Top bomb defuse streaks\n");
    	}
    	else if(arg.equals("dodges")){
    		use.sort(UserComp.Dodges);
    		sb.append("Top missile dodge streaks\n");
    	}
    	else if(arg.equals("plunges")){
    		use.sort(UserComp.Plunges);
    		sb.append("Top TNT plunger streaks\n");
    	}
    	else{
    		sb.append("Current implemented leaderboards:\n"
    			+ "deaths, defuses, monies, bombstreaks, dodges, plunges");
        	mc.sendMessage(sb.toString());
        	return;
    	}
    	
    	for(int i = 1; (i<=5 && i <= use.size()); i++){
    		UserStat u = use.get(i-1);
    		sb.append(i+": (");
    		    	if(arg.equals("deaths"))
    		    		sb.append(u.getTotalDeaths());
    		    	else if(arg.equals("defuses"))
    		    		sb.append(u.getDefuses());
    		    	else if(arg.equals("monies"))
    		    		sb.append(u.getMonies());
    		    	else if(arg.equals("bombstreaks"))
    		    		sb.append(u.getMaxBombStreak());
    		    	else if(arg.equals("dodges"))
    		    		sb.append(u.getDodgeStreak());
    		    	else if(arg.equals("plunges"))
    		    		sb.append(u.getPlungeStreak());
    		sb.append(") "+u.getUsername()+"\n");
    	}
    	mc.sendMessage(sb.toString());
    }
}
