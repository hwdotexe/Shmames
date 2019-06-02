package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class Roll implements ICommand {
	@Override
	public String getDescription() {
		return "Just roll some dice.";
	}
	
	@Override
	public String getUsage() {
		return "roll a d<#>[+#]";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^(\\d{1,2})?d\\d{1,3}([\\+\\-](\\d{1,2}d)?\\d{1,2})?$").matcher(args).matches()) {
			try {
				String input = args.replaceAll("\\+", "p").replaceAll("\\-", "m");
				int dice = 1;
				
				if(Pattern.compile("^\\d{1,2}").matcher(input).find()) {
					//begins with a number, so they want to roll that many dice.
					Matcher m = Pattern.compile("^(\\d{1,2})").matcher(input);
					m.find();
					dice = Integer.parseInt(m.group(1));
				}
				
				Matcher m1 = Pattern.compile("d(\\d{1,2})").matcher(input);
				m1.find();
				int diceSize = Integer.parseInt(m1.group(1));
				
				List<Integer> baseRolls = new ArrayList<Integer>();
				List<Integer> modRolls = new ArrayList<Integer>();
				int baseSum = 0;
				int modSum = 0;
				int modOP = 1;
				int modDice = 1;
				int modDiceSize = 0;
				
				if(Pattern.compile("[mp]").matcher(input).find()) {
					// Has additional numbers or dice to roll.
					
					// Are we rolling more dice or adding a flat number?
					
					Matcher m2 = Pattern.compile("[mp](\\d{1,2}(d\\d{1,2})?)$").matcher(input);
					m2.find();
					String input2 = m2.group(1);
					
					if(input.contains("m"))
						modOP = -1;
					
					Matcher m3 = Pattern.compile("(\\d{1,2})").matcher(input2);
					m3.find();
					modDice = Integer.parseInt(m3.group(1));
					
					// If this doesn't find anything, then we aren't rolling dice - just using a flat number.
					Matcher m4 = Pattern.compile("d(\\d{1,2})").matcher(input2);
					if(m4.find())
						modDiceSize = Integer.parseInt(m4.group(1));
					
					if(modDiceSize > 0) {
						// Roll the modifier dice
						for(int i=0; i<modDice; i++) {
							modRolls.add(Utils.getRandom(modDiceSize)+1);
						}
						
						for(int r : modRolls) {
							modSum += r;
						}
					}else {
						// User is modifying with a flat amount.
						modRolls.add(modDice);
						modSum = modDice;
					}
				}
				
				// Roll the base dice
				for(int i=0; i<dice; i++) {
					baseRolls.add(Utils.getRandom(diceSize)+1);
				}
				
				for(int r : baseRolls) {
					baseSum += r;
				}
				
				// Apply modifiers
				modSum *= modOP;
				//baseSum += modSum;
				
				String result = ":game_die: "+author.getAsMention()+" -> "+dice+"d"+diceSize+" "+baseRolls.toString();//+" ("+baseSum+")";
				
				if(modRolls.size() > 0) {
					result += modOP == 1 ? " + " : " - ";
					
					if(modDiceSize > 0)
						result += modDice+"d"+modDiceSize+" "+modRolls.toString();//+" ("+modSum+")";
					else
						result += modDice;
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