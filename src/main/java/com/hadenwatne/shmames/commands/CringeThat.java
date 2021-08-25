package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
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
import net.dv8tion.jda.api.entities.Message;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Utils;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class CringeThat implements ICommand {
	private final CommandStructure commandStructure;
	private final String[] creepyAsterisks = new String[]{"*nuzzles*", "*soft*", "*nosebleed*", "*sobs*", "*meows*", "*smiles*", "*boops*", "*shy*", "*sniffs*", "*pounces*", "*cuddles*", "*hugs*", "*poke*", "*purr*",
			"*curious*", "*moves closer*", "*licks*", "*stares*", "*gag*", "*bites lip*"};
	private final String[] creepyOwos = new String[]{"Owo", ">w<", "UwU", "OwO", "x3", ">^<", ";3", "^~^"};
	private HashMap<String, String> cringeDict = new HashMap<>();

	public CringeThat() {
		this.commandStructure = CommandBuilder.Create("cringethat")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("(\\^{1,15})"),
						new CommandParameter("times", "Number of cringe iterations", ParameterType.INTEGER, false)
				)
				.build();

		cringeDict.put("food", "numsies");
		cringeDict.put("can't", "nu can");
		cringeDict.put("have", "hab");
		cringeDict.put("and", "an");
		cringeDict.put("stupid", "stoopi");
		cringeDict.put("dumb", "no smart");
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Rewrite a previous message in a cringy way. Use `^` symbols to specify the " +
				"message to rewrite.";
	}

	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`cringethat ^^^`\n" +
				"`cringethat ^ 5`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		int messages = data.getArguments().getAsString("position").length();
		int times = data.getArguments().getAsInteger("times");
		int iterations = times > 0 ? times : 1;

		Message originMessage = data.getMessagingChannel().getOriginMessage();

		try {
			List<Message> messageHistory = originMessage.getChannel().getHistoryBefore(originMessage, messages).complete().getRetrievedHistory();
			Message toCringe = messageHistory.get(messageHistory.size() - 1);
			String cringe = toCringe.getContentDisplay();

			return runCringeProcess(cringe, iterations);
		} catch (InsufficientPermissionException ex) {
			ex.printStackTrace();

			return lang.getError(Errors.NO_PERMISSION_BOT, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}

	private String runCringeProcess(String cringe, int iterations) {
		for (int i = 0; i < iterations; i++) {
			// Replace words from the Cringe Dictionary first.
			for (String word : cringeDict.keySet()) {
				cringe = cringe.replace(word, cringeDict.get(word));
			}

			// Insert random text face expressions (25% chance)
			if (Utils.getRandom(4) == 1) {
				Pattern wordEndsInO = Pattern.compile("\\b(.+o)\\b");

				String[] cringeWords = cringe.split(" ");
				String[] cringeWordReplacements = new String[cringeWords.length];

				for (int cwi = 0; cwi < cringeWords.length; cwi++) {
					Matcher mat = wordEndsInO.matcher(cringeWords[cwi]);

					if (mat.find()) {
						cringeWordReplacements[cwi] = cringeWords[cwi] + "wo";
					} else {
						cringeWordReplacements[cwi] = cringeWords[cwi];
					}
				}

				StringBuilder newCringe = new StringBuilder();

				for (String w : cringeWordReplacements) {
					if (newCringe.length() > 0)
						newCringe.append(" ");

					newCringe.append(w);
				}

				// Override the working message text with the face expression set.
				cringe = newCringe.toString();
			}

			// Adjust hard sounds to appear more infantile.
			cringe = cringe.replace("r", "w")
					.replace("R", "W")
					.replace("l", "w")
					.replace("L", "W")
					.replace(" th", " d")
					.replace("th ", "f ");

			// Chooses a random word to stutter.
			for (int s = 0; s < Utils.getRandom(4) + 1; s++) {
				int pos = getRandomWordStartPosition(cringe);

				if (pos > 0)
					cringe = cringe.substring(0, pos) + cringe.charAt(pos) + "-" + cringe.substring(pos);
			}

			// Insert a random amount of creepy asterisks.
			for (int c = 0; c < Utils.getRandom(3); c++) {
				String rItem = creepyAsterisks[Utils.getRandom(creepyAsterisks.length)];
				int pos = getRandomWordStartPosition(cringe);

				if (pos > 0)
					cringe = cringe.substring(0, pos) + "`" + rItem + "`" + " " + cringe.substring(pos);
			}

			// Insert a random amount of creepy OwOs
			for (int o = 0; o < Utils.getRandom(3); o++) {
				String rItem = creepyOwos[Utils.getRandom(creepyOwos.length)];
				int pos = getRandomWordStartPosition(cringe);

				if (pos > 0)
					cringe = cringe.substring(0, pos) + "`" + rItem + "`" + " " + cringe.substring(pos);
			}
		}

		return cringe;
	}

	// Returns the position of a word that is safe to interject text into
	private int getRandomWordStartPosition(String src) {
		String[] words = src.split(" ");
		List<String> safeWords = new ArrayList<String>();

		Pattern p = Pattern.compile("\\b([^\\W\\d\\s_]+)\\b");

		for (String word : words) {
			if (p.matcher(word.trim()).find()) {
				safeWords.add(word);
			}
		}

		if (safeWords.size() > 0) {
			String rWord = safeWords.get(Utils.getRandom(safeWords.size()));

			return src.indexOf(rWord);
		} else {
			return -1;
		}
	}
}