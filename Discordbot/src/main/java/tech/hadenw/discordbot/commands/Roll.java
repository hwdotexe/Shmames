package tech.hadenw.discordbot.commands;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
		Pattern dicePattern = Pattern.compile("([+\\-*/])?((\\d{1,3})?d)?(\\d{1,3})(\\^[kt]([hl])(\\d)?)?", Pattern.CASE_INSENSITIVE);
		Matcher cmdFormat = Pattern.compile("^([+\\-*/\\d ()d\\^tkhl]+)$", Pattern.CASE_INSENSITIVE).matcher(args);
		Matcher dice = dicePattern.matcher(args);

		if(cmdFormat.matches()){
			List<String> diceOps = new ArrayList<String>();

			while(dice.find()){
				diceOps.add(dice.group());
			}

			try {
				message.delete().queue();
			} catch (Exception ignored) {}

			String rollResult = processRoll(dicePattern, diceOps);
			String resultMessage = ":game_die: "+author.getAsMention()+"\n> "+rollResult;

			return resultMessage;
		}else{
			return "Try `roll a d20`!";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"roll a", "roll"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}

	private String processRoll(Pattern p, List<String> diceRolls){
		int runningTotal = 0;
		StringBuilder rollSB = new StringBuilder();

		for(String dRollString : diceRolls){
			Matcher m = p.matcher(dRollString);

			if(m.find()) {
				String opGroup = m.group(1);
				String diceGroup = m.group(3);
				String takeHighLowGroup = m.group(6);
				String takeHighLowCountGroup = m.group(7);

				char operation = opGroup != null && opGroup.length() > 0 ? opGroup.charAt(0) : '+';
				int diceToRoll = diceGroup != null ? Integer.parseInt(diceGroup) : 1;
				boolean isDice = m.group(2) != null && m.group(2).endsWith("d");
				int diceSizeOrFlat = Integer.parseInt(m.group(4));
				char takeHighLow = takeHighLowGroup != null ? takeHighLowGroup.charAt(0) : 'a';
				int takeHighLowCount = takeHighLowCountGroup != null ? Integer.parseInt(takeHighLowCountGroup) : 0;

				int subTotal = 0;
				StringBuilder diceSB = new StringBuilder();

				// Run some safety checks first.
				if(diceToRoll == 0)
					diceToRoll = 1;

				if(isDice){
					// Random
					HashMap<Integer, Integer> rolls = new HashMap<Integer,Integer>();

					diceSB.append(diceToRoll);
					diceSB.append("d");
					diceSB.append(diceSizeOrFlat);
					diceSB.append(" [");

					for(int i=0; i<diceToRoll; i++){
						int roll = Utils.getRandom(diceSizeOrFlat)+1;

						rolls.put(i, roll);
					}

					switch(takeHighLow) {
						case 'h':
							if(takeHighLowCount > 0) {
								// Safety
								if(rolls.size() < takeHighLowCount)
									takeHighLowCount = rolls.size();

								List<Integer> highestRolls = new ArrayList<Integer>();
								HashMap<Integer, Integer> tempRolls = new HashMap<Integer,Integer>(rolls);

								for(int i=0; i<takeHighLowCount; i++){
									int max = Collections.max(tempRolls.values());
									int roll = getMapKeyFromValue(tempRolls, max);

									highestRolls.add(roll);
									tempRolls.remove(roll);

									subTotal += max;
								}

								diceSB.append(drawRollResults(rolls, highestRolls, diceSizeOrFlat));
							} else {
								// Take highest
								int max = Collections.max(rolls.values());
								int roll = getMapKeyFromValue(rolls, max);

								List<Integer> indexes = new ArrayList<Integer>();
								indexes.add(roll);

								subTotal += max;
								diceSB.append(drawRollResults(rolls, indexes, diceSizeOrFlat));
							}
							break;
						case 'l':
							if(takeHighLowCount > 0) {
								// Safety
								if(rolls.size() < takeHighLowCount)
									takeHighLowCount = rolls.size();

								List<Integer> lowestRolls = new ArrayList<Integer>();
								HashMap<Integer, Integer> tempRolls = new HashMap<Integer,Integer>(rolls);

								for(int i=0; i<takeHighLowCount; i++){
									int min = Collections.min(tempRolls.values());
									int roll = getMapKeyFromValue(tempRolls, min);

									lowestRolls.add(roll);
									tempRolls.remove(roll);

									subTotal += min;
								}

								diceSB.append(drawRollResults(rolls, lowestRolls, diceSizeOrFlat));
							} else {
								// Take lowest
								int min = Collections.min(rolls.values());
								int roll = getMapKeyFromValue(rolls, min);

								List<Integer> indexes = new ArrayList<Integer>();
								indexes.add(roll);

								subTotal += min;
								diceSB.append(drawRollResults(rolls, indexes, diceSizeOrFlat));
							}
							break;
						default:
							for(int roll : rolls.values()){
								subTotal += roll;
							}

							diceSB.append(drawRollResults(rolls, null, diceSizeOrFlat));
					}

					diceSB.append("]");
				}else{
					// Flat modifier.
					subTotal += diceSizeOrFlat;
					diceSB.append(diceSizeOrFlat);
				}

				switch(operation){
					case '-':
						runningTotal -= subTotal;
						break;
					case '*':
						runningTotal *= subTotal;
						break;
					case '/':
						runningTotal /= subTotal;
						break;
					default:
						runningTotal += subTotal;
				}

				if(rollSB.length() > 0) {
					rollSB.append(" ");
					rollSB.append(operation);
					rollSB.append(" ");
				}

				rollSB.append(diceSB.toString());
			}
		}

		rollSB.append(" = **");
		rollSB.append(runningTotal);
		rollSB.append("**");

		return rollSB.toString();
	}

	private int getMapKeyFromValue(HashMap<Integer, Integer> map, int check) {
		for(int key : map.keySet()) {
			if(map.get(key) == check){
				return key;
			}
		}

		return -1;
	}

	private String drawRollResults(HashMap<Integer, Integer> rolls, List<Integer> keepRolls, int toBold) {
		StringBuilder sb = new StringBuilder();

		for(int rollNum : rolls.keySet()) {
			int roll = rolls.get(rollNum);

			if(sb.length() > 0){
				sb.append(", ");
			}

			if(keepRolls != null && !keepRolls.contains(rollNum)){
				sb.append("~~");
			}

			if(roll == toBold){
				sb.append("**");
				sb.append(roll);
				sb.append("**");
			}else{
				sb.append(roll);
			}

			if(keepRolls != null && !keepRolls.contains(rollNum)){
				sb.append("~~");
			}
		}
		return sb.toString();
	}
}