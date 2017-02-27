package com.daikyi.bunbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.daikyi.bunbot.Util.ExplosiveAction;

import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;

public class Explosives {
	
	private static ArrayList<String> choices;
	private static String correctChoice;
	private static String dudWire;
	private static String deflectWire;
	private static Timer explosionTimer;
	private static ExplosiveStatus explosiveActive = ExplosiveStatus.None;
	private static int explosiveStreak;
	private static int countdown;
	
	private static UserStat attacker;
	private static UserStat target;
	
	enum ExplosiveStatus {
		Bomb, Missile, None, TNT
	};
	
    public static UserStat cutWire(Message m, MessageChannel mc){
        
    	String wire = m.getContent().substring(1).split(" ")[0].toLowerCase();
    	StringBuilder sb = new StringBuilder();

    	if(wire.equals(correctChoice.toLowerCase())){
    		
    		explosionTimer.cancel();
    		explosiveActive = ExplosiveStatus.None;
    		
    		int monies = Dice.randomInt(11, 19) * choices.size();
        	sb.append("Congratulations, "+target.getUsername()+", you successfully defused the bomb!  You earned $"+monies+" monies");
        	target.setDefuses(target.getDefuses()+1);
        	target.setCurBombStreak(target.getCurBombStreak()+1);
        	target.gainMonies(monies);
        	sb.append("\nYou have "+target.getMonies()+" monies, and have defused "+target.getDefuses()+" bomb(s)");
    	}
    	else if(wire.equals(dudWire)){
    		
    		sb.append(Util.getBombSay("", target.getUsername(), ExplosiveAction.Dud)+"\n"+target.getUsername()+", pick another wire to cut before it's too late!\n(");
    		choices.remove(dudWire);
    		    	
	    	for(int i = 0; i < choices.size(); i++)
	    		sb.append(" !" + choices.get(i));
	    	sb.append(" )");
    	}
    	else if(wire.equals(deflectWire)){
    		
    		sb.append(Util.getBombSay(attacker.getUsername(), target.getUsername(), ExplosiveAction.Deflect)+" Now <@"+attacker.getID()+"> needs to cut a wire!\n(");
    		choices.remove(deflectWire);	
	    	for(int i = 0; i < choices.size(); i++)
	    		sb.append(" !" + choices.get(i));
	    	sb.append(" )");
	    	UserStat temp = attacker;
	    	attacker = target;
	    	target = temp;
    	}
    	else{
    		explosionTimer.cancel();
    		explosiveActive = ExplosiveStatus.None;
    		
        	sb.append(Util.getBombSay("",target.getUsername(),ExplosiveAction.Explode)+"\nThe correct wire was " + correctChoice);
        	target.bombDeath();
        	target.setCurBombStreak(0);
        	
        	int monies = (int)(.35*(Dice.randomInt(7, 12) * (13-choices.size())/3));
        	target.loseMonies(monies);
        	sb.append("\n"+target.getUsername()+" has lost " + monies + " monies");
        	sb.append("\n"+target.getUsername()+" has died " + target.getTotalDeaths() + " time(s) so far");
    	}
    	mc.sendMessage(sb.toString());
    	return target;
    }
    
    public static UserStat dodgeMissile(Message m, MessageChannel mc){
        
    	String dodge = m.getContent().substring(1).split(" ")[0].toLowerCase();
    	StringBuilder sb = new StringBuilder();
    	
    	if(!dodge.equals(correctChoice)){
    		
    		explosionTimer.cancel();
    		explosiveStreak++;
    		
    		//int monies = Dice.randomInt(5, 25) * bombWires.size();
        	sb.append(target.getUsername()+", successfully dodges the missile! ("+explosiveStreak+" dodge streak)\n"
        			+ "The missile turns around and heads");
        	
        	if(attacker == target)
        		sb.append(" right back towards you!");
        	else
        		sb.append(" towards "+attacker.getUsername()+"!");
        	sb.append("\n"+attacker.getUsername()+", pick a direction to dodge!\n(");
        	
        	for(int i = 0; i < choices.size(); i++){
        		sb.append(" !" + choices.get(i));
        	}
        	countdown -= Dice.randomInt(4, 6);
        	if(countdown < 5)
        		countdown = 5;
        	sb.append(" )\nYou have " + countdown + " seconds before the missile hits!");
        	correctChoice = choices.get(Dice.randomInt(0, 2));
        	
        	target.newDodgeStreak(explosiveStreak);
        	
        	UserStat temp = attacker;
        	attacker = target;
        	target = temp;
        	
        	explosionTimer = new Timer();
    		explosionTimer.schedule(new Explosive(mc), countdown * 1000);
    	}
    	else{
    		explosionTimer.cancel();
    		explosiveActive = ExplosiveStatus.None;
    		
    		//TODO balance things double probability = Math.pow((2.0/3.0), explosiveStreak)*(1/3);
        	//sb.append(Util.getBombSay("",bombed.getLastUsername(),BombAction.Explode)+"\nThe correct wire was " + correctWire);
    		sb.append("You dodged!  And you dodged right into the missile's path!  you died. ("+explosiveStreak+" dodge streak!)");
    		target.missileDeath();
        	//bombed.setCurBombStreak(0);
        	
        	if(target != attacker){
        		
        		int moneySteal = target.getMonies()/10;
        		if(explosiveStreak < 1)
        			moneySteal /= 3;
        		sb.append("\n"+attacker.getUsername()+" has stolen "+moneySteal+" monies from "+target.getUsername());
        		attacker.gainMonies(moneySteal);
        		target.loseMonies(moneySteal);
        	}
        	
        	sb.append("\n"+target.getUsername()+" has died " + target.getTotalDeaths() + " time(s) so far");
    	}
    	mc.sendMessage(sb.toString());
    	return target;
    }

    static class Explosive extends TimerTask{
    	private MessageChannel mc;
    	public Explosive(MessageChannel mc){
    		this.mc = mc;
    	}
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
    		StringBuilder sb = new StringBuilder();
    		
    		switch(explosiveActive){
    		
    		case Bomb:
    			sb.append(Util.getBombSay("", target.getUsername(), ExplosiveAction.TimeOut)+"\nThe correct wire was " + correctChoice);
    			target.setCurBombStreak(0);
    			target.bombDeath();
    			break;
    		case Missile:
    			sb.append(Util.getBombSay("", target.getUsername(), ExplosiveAction.MissileIgnore)+"\n");
    			target.missileDeath();
    			break;
    		case TNT:
    			sb.append(Util.getBombSay("", target.getUsername(), ExplosiveAction.TNTIgnore)+"\n");
    			target.tntDeath();
    			break;
			default:
				break;
    		}

        	sb.append("\n"+target.getUsername()+" has died " + target.getTotalDeaths() + " time(s) so far");
        	explosiveActive = ExplosiveStatus.None;
        	mc.sendMessage(sb.toString());
		}
    }
    
    public static boolean validChoice(String toCompare){
    	
    	for(int i = 0; i < choices.size(); i++){
    		if(choices.get(i).toLowerCase().equals(toCompare.toLowerCase()))
    			return true;
    	}
    	return false;
    }
    
    public static void bombStats(Message m, User u, HashMap<String, UserStat> users){
    	
    	List<User> mentions = m.getMentionedUsers();
    	UserStat toDisp;
    	StringBuilder builder = new StringBuilder();
    	
    	if(mentions.size() == 0)
    		toDisp = users.get(u.getId());
    	else
    		toDisp = users.get(mentions.get(0).getId());

    	if(toDisp != null){
    		builder.append(toDisp.getUsername()+"'s bomb stats:");
    		builder.append("\nTotal Deaths: "+toDisp.getTotalDeaths());
    		builder.append("\nBomb Deaths: "+toDisp.getBombDeaths());
    		builder.append("\nMissile Deaths: "+toDisp.getMissileDeaths());
    		builder.append("\nTNT Deaths: "+toDisp.getTNTDeaths());
    		builder.append("\nAccumulated Monies: "+toDisp.getMonies());
    		builder.append("\nGained Monies: "+toDisp.getMoniesGained());
    		builder.append("\nLost Monies: "+toDisp.getMoniesLost());
    		builder.append("\nTotal Defuses: "+toDisp.getDefuses());
    		builder.append("\nLongest Bomb Streak: "+toDisp.getMaxBombStreak());
    		builder.append("\nLongest Missile Streak: "+toDisp.getDodgeStreak());
    		builder.append("\nMost Plungers Pressed: "+toDisp.getPlungeStreak());
    	}
    	else
    		builder.append("You don't seem to have any bomb stats...");
    	
    	u.getPrivateChannel().sendMessage(builder.toString());
    }
    	
    public static UserStat missile(Message m, MessageChannel mc){
    	
    	attacker = BunBotImpl2.getUser(m.getAuthor());
    	explosiveStreak = 0;
    	
    	StringBuilder sb = new StringBuilder();
    	List<User> mentions = m.getMentionedUsers();
    	
    	if(explosiveActive != ExplosiveStatus.None)
    		return null;
    	
    	ExplosiveAction ba = ExplosiveAction.Missile;
    	choices = new ArrayList<String>();
    	choices.add("left");
    	choices.add("still");
    	choices.add("right");
    	
    	explosiveActive = ExplosiveStatus.Missile;
    	
    	if(mentions.size() == 0){
    		ba = ExplosiveAction.MissileSelf;
    		target = attacker;
    	}
    	else if(mentions.get(0).getUsername().equals(BunBotImpl2.BOT_USERNAME)){
    			
			sb.append("DEFLECTION INITIATED\n");
    		ba = ExplosiveAction.MissileSelf;
    		target = attacker;
    	}
    	else if(mentions.get(0).getOnlineStatus().equals(OnlineStatus.ONLINE))
    		target = BunBotImpl2.getUser(mentions.get(0));
    	else{
			sb.append("user not active");
	    	mc.sendMessage(sb.toString());
	    	explosiveActive = ExplosiveStatus.None;
	    	return null;
    	}
    		
    	countdown = Dice.randomInt(30,35);
    	//correct gets you hit here
    	correctChoice = choices.get(Dice.randomInt(0, 2));
    	System.out.println(correctChoice);
    	deflectWire = "";
    	dudWire = "";
    	sb.append(Util.getBombSay(attacker.getUsername(),target.getUsername(),ba)+"\n"+target.getUsername()+", pick a direction to dodge!\n(");
    	
    	for(int i = 0; i < choices.size(); i++){
    		sb.append(" !" + choices.get(i));
    	}
    	sb.append(" )\nYou have " + countdown + " seconds before the missile hits!");

    	mc.sendMessage(sb.toString());
    	
    	explosionTimer = new Timer();
		explosionTimer.schedule(new Explosive(mc), countdown * 1000);
		return target;
    }

	public static UserStat bomb(Message m, MessageChannel mc){
		
		attacker = BunBotImpl2.getUser(m.getAuthor());
		
		StringBuilder sb = new StringBuilder();
		List<User> mentions = m.getMentionedUsers();
		if(explosiveActive != ExplosiveStatus.None)
			return null;
		
		choices = Util.getWires();
		
		ExplosiveAction ba=ExplosiveAction.Bomb;
	    explosiveActive = ExplosiveStatus.Bomb;
	    
	    if(mentions.size() == 0){
	    	
			target = BunBotImpl2.getUser(m.getAuthor());
			attacker = target;
			ba=ExplosiveAction.Suicide;
	    }
	    else if(mentions.get(0).getUsername().equals(BunBotImpl2.BOT_USERNAME)){
	
			target = BunBotImpl2.getUser(m.getAuthor());
			attacker = target;
			sb.append("DEFLECTION INITIATED\n");
			ba=ExplosiveAction.Suicide;
		}
		else if(mentions.get(0).getOnlineStatus().equals(OnlineStatus.ONLINE))
			target = BunBotImpl2.getUser(mentions.get(0));
		else{
			sb.append("user not active");
	    	mc.sendMessage(sb.toString());
	    	explosiveActive = ExplosiveStatus.None;
	    	return null;
		}
		
		if(target == attacker)
			ba = ExplosiveAction.Suicide;
		
		countdown = Dice.randomInt(30,45);
		ArrayList<String> tempWires = new ArrayList<String>(choices);
		
		correctChoice = tempWires.remove(Dice.randomInt(0, tempWires.size()-1));
		if(attacker.getUsername() != BunBotImpl2.BOT_USERNAME && attacker != target)
			deflectWire = tempWires.remove(Dice.randomInt(0, tempWires.size()-1));
		else
			deflectWire = "";
		dudWire = tempWires.remove(Dice.randomInt(0, tempWires.size()-1));
		sb.append(Util.getBombSay(attacker.getUsername(),target.getUsername(),ba)+"\n"+target.getUsername()+", pick a wire to cut, and pray\n(");
		System.out.println(correctChoice);
		for(int i = 0; i < choices.size(); i++){
			sb.append(" !" + choices.get(i));
		}
		sb.append(" )\nYou have " + countdown + " seconds to pick one of the listed wires!");
		//sb.append("<@"+bombed.getId()+">");
		mc.sendMessage(sb.toString());

		explosionTimer = new Timer();
		explosionTimer.schedule(new Explosive(mc), countdown * 1000);
		
		return target;
	}  
	
	public static UserStat tnt(Message m, MessageChannel mc){
		
		attacker = BunBotImpl2.getUser(m.getAuthor());
		explosiveStreak = 0;
		StringBuilder sb = new StringBuilder();
		List<User> mentions = m.getMentionedUsers();
		if(explosiveActive != ExplosiveStatus.None)
			return null;
		
		choices = Util.getTNT();
		
	    explosiveActive = ExplosiveStatus.TNT;
	    
	    if(mentions.size() == 0)
	    	target = attacker;
	    else if(mentions.get(0).getUsername().equals(BunBotImpl2.BOT_USERNAME)){
	
	    	sb.append("DEFLECTION INITIATED");
			target = attacker;
		}
		else if(mentions.get(0).getOnlineStatus().equals(OnlineStatus.ONLINE))
			target = BunBotImpl2.getUser(mentions.get(0));
		else{
			sb.append("user not active");
	    	mc.sendMessage(sb.toString());
	    	explosiveActive = ExplosiveStatus.None;
	    	return null;
		}
		
		ExplosiveAction ea = ExplosiveAction.TNT;
		if(target == attacker)
			ea = ExplosiveAction.TNTSelf;

		countdown = Dice.randomInt(5 * choices.size(), 6 * choices.size());
		ArrayList<String> plungers = new ArrayList<String>(choices);
		
		correctChoice = plungers.remove(Dice.randomInt(0, plungers.size()-1));
		sb.append(Util.getBombSay(attacker.getUsername(),target.getUsername(),ea)+"\n"+target.getUsername()+", pick a plunger to plunge\n(");
		System.out.println(correctChoice);
		
		for(int i = 0; i < choices.size(); i++){
			sb.append(" !" + choices.get(i));
		}
		sb.append(" )\nYou have " + countdown + " seconds to pick one of the plungers!");
		//sb.append("<@"+bombed.getId()+">");
		mc.sendMessage(sb.toString());
	
		explosionTimer = new Timer();
		explosionTimer.schedule(new Explosive(mc), countdown * 1000);
		return target;
	}
	
    public static UserStat plunge(Message m, MessageChannel mc){
        
    	String plunger = m.getContent().substring(1).split(" ")[0].toLowerCase();
    	StringBuilder sb = new StringBuilder();
    	
    	explosionTimer.cancel();
    	
		sb.append("You push down plunger "+plunger+"...");
		mc.sendMessage(sb.toString());
		sb = new StringBuilder();
		
		boomSilence(3);
    	
    	if(!plunger.equals(correctChoice)){
    		
    		explosiveStreak++;
    		
        	sb.append("Nothing happens. ("+explosiveStreak+" plunger streak)\n");
        	
        	if(!attacker.equals(target))
        			sb.append("It's " + attacker.getUsername() + "'s turn to plunge!\n");
        	
        	if(choices.size() == 2){
        		sb.append("The plungers have been reset...\n(");
        		choices = Util.getTNT();
        		correctChoice = choices.get(Dice.randomInt(0, choices.size()-1));
        		System.out.println(correctChoice);
        	}
        	else{
        		sb.append("(");
        		choices.remove(plunger);
        	}
        	
        	for(int i = 0; i < choices.size(); i++){
        		sb.append(" !" + choices.get(i));
        	}
        	
        	countdown = Dice.randomInt(2 * choices.size()+20, 3 * choices.size()+20);
        	sb.append(" )\nYou have " + countdown + " seconds to plunge a plunger!");
        	
        	target.newPlungeStreak(explosiveStreak);
        	
        	UserStat temp = attacker;
        	attacker = target;
        	target = temp;
        	
        	explosionTimer = new Timer();
    		explosionTimer.schedule(new Explosive(mc), countdown * 1000);
    	}
    	else{
    		explosiveActive = ExplosiveStatus.None;
    		boomSilence(1);
    		mc.sendMessage("THREE");
    		boomSilence(1);
    		mc.sendMessage("TWO");
    		boomSilence(1);
    		mc.sendMessage("ONE");
    		boomSilence(1);
        	//sb.append(Util.getBombSay("",bombed.getLastUsername(),BombAction.Explode)+"\nThe correct wire was " + correctWire);
    		sb.append("**KABOOOOOOOM** ("+explosiveStreak+" plungers plunged!)");
    		target.tntDeath();
        	//bombed.setCurBombStreak(0);
        	
        	if(target != attacker){
        		
        		int moneySteal = target.getMonies()/10;
        		if(explosiveStreak < 1)
        			moneySteal /= 3;
        		sb.append("\n"+attacker.getUsername()+" has stolen "+moneySteal+" monies from "+target.getUsername());
        		attacker.gainMonies(moneySteal);
        		target.loseMonies(moneySteal);
        	}
        	
        	sb.append("\n"+target.getUsername()+" has died " + target.getTotalDeaths() + " time(s) so far");
    	}
    	mc.sendMessage(sb.toString());
    	return target;
    }
	
	public static void defuse(){
		if(explosionTimer != null)
			explosionTimer.cancel();
	}
	
	public static ExplosiveStatus getStatus(){
		return explosiveActive;
	}
	
	public static void setStatus(ExplosiveStatus es){
		explosiveActive = es;
	}
	
	private static void boomSilence(int i){
		try {
			TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
		}
	}
}
