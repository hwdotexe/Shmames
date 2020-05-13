package tech.hadenw.discordbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Utils;

public class Roll implements ICommand {
	@Override
	public String getDescription() {
		return "Roll some dice! Try `roll a d20`, or `roll 2d8+1d4`!";
	}
	
	@Override
	public String getUsage() {
		return "roll a d<#>[+#]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher roll = Pattern.compile("^((\\d{1,3})*(d\\d{1,3}))([\\+\\-](\\d{1,3})(d\\d{1,3})*)*$").matcher(args);
		
		if(roll.matches()) {
			try {
				int diceQuantity = 1;
				
				// command: 1d20+2d6
				// g1: 1d20
				// g2: 1
				// g3: d20
				// g4: +2d6
				// g5: 2
				// g6: d6
				
				// This group is optional.
				if(roll.group(2) != null) {
					diceQuantity = Integer.parseInt(roll.group(2));
				}
				
				// Take the dice size and strip the preceding "d".
				int diceSize = Integer.parseInt(roll.group(3).substring(1));
				
				// Prepare variables.
				List<Integer> baseRolls = new ArrayList<Integer>();
				List<Integer> modRolls = new ArrayList<Integer>();
				int modBase = -1;
				int modDiceSize = -1;
				int modOperation = 1;
				int modSum = 0;
				int baseSum = 0;
				
				// Process modifier dice.
				if(roll.group(4) != null) {
					// Set the base modifier, or the number of modifier dice.
					modBase = Integer.parseInt(roll.group(5));
					
					// Set the size of the modifier dice.
					if(roll.group(6) != null) {
						modDiceSize = Integer.parseInt(roll.group(6).substring(1));
					}
					
					// Set the operation (plus or minus).
					if(roll.group(4).substring(0,1).equals("-"))
						modOperation = -1;
					
					// Apply modifiers.
					if(modDiceSize > 0) {
						// Roll the modifier dice and add to the modifier sum.
						for(int i=0; i<modBase; i++) {
							int r = Utils.getRandom(modDiceSize)+1;
							modRolls.add(r);
							modSum += r;
						}
					}else {
						// User is modifying with a flat amount.
						modRolls.add(modBase);
						modSum = modBase;
					}
				}
				
				// Roll the base dice
				for(int i=0; i<diceQuantity; i++) {
					int r = Utils.getRandom(diceSize)+1;
					baseRolls.add(r);
					baseSum += r;
				}
				
				// Use the modifier operation.
				modSum *= modOperation;
				
				String result = ":game_die: "+author.getAsMention()+" -> "+diceQuantity+"d"+diceSize+" "+baseRolls.toString();
				
				if(modRolls.size() > 0) {
					result += modOperation == 1 ? " + " : " - ";
					
					if(modDiceSize > 0)
						result += modBase+"d"+modDiceSize+" "+modRolls.toString();
					else
						result += modBase;
				}
				
				return result+" = **"+(baseSum+modSum)+"**";
			}catch(Exception ex) {
				ex.printStackTrace();
				return Errors.BOT_ERROR;
			}
		}else {
			return "Try `roll a d20`!";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"roll a", "roll"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W&&[^\\+\\-]]", "").toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}