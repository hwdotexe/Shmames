package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.IInteractable;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roll extends Command implements IInteractable {
	private final Pattern dicePattern = Pattern.compile("([+\\-*/])?\\s?((\\d{1,3})?d)?(\\d{1,3})(\\^[kt]([hl])(\\d)?)?", Pattern.CASE_INSENSITIVE);

	public Roll() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return new Permission[0];
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("roll", "Roll some dice with optional modifiers.")
				.addParameters(
						new CommandParameter("dice", "The dice to roll.", ParameterType.STRING)
								.setPattern("([+\\-*/\\d\\s()d\\^tkhl]+)")
								.setExample("2d20^kh+1d4"),
						new CommandParameter("memo", "A short description of the roll.", ParameterType.STRING, false)
								.setExample("I roll for initiative!"),
						new CommandParameter("sticky", "Make this roll sticky.", ParameterType.BOOLEAN, false)
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String diceArg = execution.getArguments().get("dice").getAsString();
		OptionMapping memoMapping = execution.getArguments().get("memo");
		OptionMapping stickyMapping = execution.getArguments().get("sticky");
		Matcher dice = dicePattern.matcher(diceArg);
		List<String> diceOps = new ArrayList<String>();

		while (dice.find()) {
			diceOps.add(dice.group().replaceAll("\\s", ""));
		}

		String rollResult = processRoll(dicePattern, diceOps);
		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName(), execution.getUser().getName())
					.setDescription(rollResult);

		if(memoMapping != null) {
			builder.addField("Memo", memoMapping.getAsString(), false);
		}

		if(stickyMapping != null) {
			if(stickyMapping.getAsBoolean()) {
				Button d4 = Button.secondary("d4", "1d4");
				Button d6 = Button.secondary("d6", "1d6");
				Button d8 = Button.secondary("d8", "1d8");
				Button d20 = Button.secondary("d20", "1d20");

				builder.addLayoutComponent(ActionRow.of(d4, d6, d8, d20));
			}
		}

		Corvus.reply(execution, builder);
	}

	@Override
	public String[] getInteractionIDs() {
		return new String[]{"d4", "d6", "d8", "d20"};
	}

	@Override
	public void onButtonClick(ButtonInteraction buttonInteraction) {
		if (buttonInteraction.getMessage().getEmbeds().isEmpty()) {
			buttonInteraction.editComponents().queue();

			return;
		}

		String roll = switch (buttonInteraction.getComponentId()) {
			case "d4" -> processRoll(dicePattern, Collections.singletonList("1d4"));
			case "d6" -> processRoll(dicePattern, Collections.singletonList("1d6"));
			case "d8" -> processRoll(dicePattern, Collections.singletonList("1d8"));
			default -> processRoll(dicePattern, Collections.singletonList("1d20"));
		};

		MessageEmbed embed = buttonInteraction.getMessage().getEmbeds().get(0);
		EmbedBuilder builder = EmbedBuilder.fromData(embed.toData());
		String timeCode = "<t:" + (System.currentTimeMillis() / 1000L) + ":t> » ";

		if ((builder.getDescriptionBuilder().length() + roll.length() + System.lineSeparator().length() + timeCode.length()) <= MessageEmbed.DESCRIPTION_MAX_LENGTH) {
			builder.appendDescription(System.lineSeparator());
			builder.appendDescription(timeCode);
			builder.appendDescription(roll);
		} else {
			builder.setDescription(roll);
		}

		buttonInteraction.editMessageEmbeds(builder.build()).queue();
	}

	@Override
	public void onStringClick(StringSelectInteraction stringSelectInteraction) {

	}

	@Override
	public void onEntityClick(EntitySelectInteraction entitySelectInteraction) {

	}

	@Override
	public void onModalSubmit(ModalInteraction modalInteraction) {

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
						int roll = RandomService.GetRandom(diceSizeOrFlat) + 1;

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

					if(diceToRoll > 1) {
						diceSB.append("{");
						diceSB.append(subTotal);
						diceSB.append("}");
					}
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
