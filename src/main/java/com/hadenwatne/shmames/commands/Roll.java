package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Utils;

public class Roll implements ICommand {
	private final CommandStructure commandStructure;
	private final Pattern dicePattern = Pattern.compile("([+\\-*/])?\\s?((\\d{1,3})?d)?(\\d{1,3})(\\^[kt]([hl])(\\d)?)?", Pattern.CASE_INSENSITIVE);

	public Roll() {
		this.commandStructure = CommandBuilder.Create("roll", "Roll some dice with optional modifiers.")
				.addParameters(
						new CommandParameter("dice", "The dice to roll.", ParameterType.STRING)
								.setPattern("([+\\-*/\\d\\s()d\\^tkhl]+)")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`roll 1d20`\n" +
				"`roll 2d20^kh`\n" +
				"`roll 4d6^kh3`\n" +
				"`roll 2d20^kl`\n" +
				"`roll 2d20^kh+5-1d4`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String diceArg = data.getArguments().getAsString("dice");
		Matcher dice = dicePattern.matcher(diceArg);
		List<String> diceOps = new ArrayList<String>();

		while (dice.find()) {
			diceOps.add(dice.group().replaceAll("\\s", ""));
		}

		try {
			if (data.getMessagingChannel().hasOriginMessage()) {
				data.getMessagingChannel().getOriginMessage().delete().queue();
			}
		} catch (Exception ignored) {
		}

		String rollResult = processRoll(dicePattern, diceOps);

		return ":game_die: " + data.getAuthor().getAsMention() + "\n> " + rollResult;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}

	private String processRoll(Pattern p, List<String> diceRolls) {
		int runningTotal = 0;
		StringBuilder rollSB = new StringBuilder();

		for (String dRollString : diceRolls) {
			Matcher m = p.matcher(dRollString);

			if (m.find()) {
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
				if (diceToRoll == 0)
					diceToRoll = 1;

				if (isDice) {
					// Random
					HashMap<Integer, Integer> rolls = new HashMap<Integer, Integer>();

					diceSB.append(diceToRoll);
					diceSB.append("d");
					diceSB.append(diceSizeOrFlat);
					diceSB.append(" [");

					for (int i = 0; i < diceToRoll; i++) {
						int roll = Utils.getRandom(diceSizeOrFlat) + 1;

						rolls.put(i, roll);
					}

					switch (takeHighLow) {
						case 'h':
							if (takeHighLowCount > 0) {
								// Safety
								if (rolls.size() < takeHighLowCount)
									takeHighLowCount = rolls.size();

								List<Integer> highestRolls = new ArrayList<Integer>();
								HashMap<Integer, Integer> tempRolls = new HashMap<Integer, Integer>(rolls);

								for (int i = 0; i < takeHighLowCount; i++) {
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
							if (takeHighLowCount > 0) {
								// Safety
								if (rolls.size() < takeHighLowCount)
									takeHighLowCount = rolls.size();

								List<Integer> lowestRolls = new ArrayList<Integer>();
								HashMap<Integer, Integer> tempRolls = new HashMap<Integer, Integer>(rolls);

								for (int i = 0; i < takeHighLowCount; i++) {
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
							for (int roll : rolls.values()) {
								subTotal += roll;
							}

							diceSB.append(drawRollResults(rolls, null, diceSizeOrFlat));
					}

					diceSB.append("]");
					diceSB.append("(");
					diceSB.append(subTotal);
					diceSB.append(")");
				} else {
					// Flat modifier.
					subTotal += diceSizeOrFlat;
					diceSB.append(diceSizeOrFlat);
				}

				switch (operation) {
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

				if (rollSB.length() > 0) {
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
		for (int key : map.keySet()) {
			if (map.get(key) == check) {
				return key;
			}
		}

		return -1;
	}

	private String drawRollResults(HashMap<Integer, Integer> rolls, List<Integer> keepRolls, int toBold) {
		StringBuilder sb = new StringBuilder();

		for (int rollNum : rolls.keySet()) {
			int roll = rolls.get(rollNum);

			if (sb.length() > 0) {
				sb.append(", ");
			}

			if (keepRolls != null && !keepRolls.contains(rollNum)) {
				sb.append("~~");
			}

			if (roll == toBold || (toBold == 20 && roll == 1)) {
				sb.append("**");
				sb.append(roll);
				sb.append("**");
			} else {
				sb.append(roll);
			}

			if (keepRolls != null && !keepRolls.contains(rollNum)) {
				sb.append("~~");
			}
		}
		return sb.toString();
	}
}
