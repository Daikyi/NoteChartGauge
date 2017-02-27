package com.daikyi.bunbot;

import java.util.Random;

public class Dice {
	
    public static String roll(String[] rolls){
    	
    	String toReturn = "";
    	
    	if(rolls.length == 0)
    		toReturn += "[ " + randomInt(1,100) + " ]";
    	else{
	    	try{
		    	for(int i = 0; (i < rolls.length && i < 10); i++){
		    		
		    		toReturn += "[ ";
		    		rolls[i] = rolls[i].trim().toLowerCase();
		    		String[] params = rolls[i].split("d");
		    		
		    		if(params.length>1){
		    		
			    		int numRolls = Integer.parseInt(params[0].trim());
			    		int diceSides = Integer.parseInt(params[1].trim());
			    		
			    		if(numRolls < 1)
			    			toReturn += "You doofus ";
			    		else if(numRolls > 20)
			    			toReturn += "Too many rolls ";
			    		else if(diceSides <= 0)
			    			toReturn += "You toss an imaginary dice with an imaginary result ";
			    		else if(diceSides == 1)
			    			toReturn += "You toss a mobius strip. ??? ";
			    		else if(diceSides > 100)
			    			toReturn += "What kind of dice is this ";
			    		else{
			    			for(int j = 0; j < numRolls; j++)
			    				toReturn += randomInt(1,diceSides) + " ";
			    		}
			    		
		    		}
		    		else
		    			toReturn += randomInt(1,100);
		    		toReturn += " ]\n";
		    	}
	    	}catch(NumberFormatException nfe){}
    	}
    	
    	return toReturn;
    }
    
	/**
	 * Obtains a random int
	 * @param low	the low bound for generation
	 * @param high	the high bound for generation
	 * @return	the random int
	 */
	public static int randomInt(int low, int high){
		return new Random().nextInt(high - low + 1) + low;
	}
}
