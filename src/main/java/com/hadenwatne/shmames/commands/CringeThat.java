package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CringeThat extends Command {
	private final String[] creepyAsterisks = new String[]{"*nuzzles*", "*soft*", "*nosebleed*", "*sobs*", "*meows*", "*smiles*", "*boops*", "*shy*", "*sniffs*", "*pounces*", "*cuddles*", "*hugs*", "*poke*", "*purr*",
			"*curious*", "*moves closer*", "*licks*", "*stares*", "*gag*", "*bites lip*"};
	private final String[] creepyOwos = new String[]{"Owo", ">w<", "UwU", "OwO", "x3", ">^<", ";3", "^~^"};
	private final HashMap<String, String> cringeDict = new HashMap<>();

	public CringeThat() {
		super(false);

		cringeDict.put("food", "numsies");
		cringeDict.put("can't", "nu can");
		cringeDict.put("have", "hab");
		cringeDict.put("and", "an");
		cringeDict.put("stupid", "stoopi");
		cringeDict.put("dumb", "no smart");
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("cringethat", "Rewrite a previous message in a cringy way.")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})")
								.setExample("^^^"),
						new CommandParameter("times", "Number of cringe iterations", ParameterType.INTEGER, false)
								.setExample("2")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		int messages = executingCommand.getCommandArguments().getAsString("position").length();
		int times = executingCommand.getCommandArguments().getAsInteger("times");
		int iterations = times > 0 ? times : 1;

		Message toCringe = MessageService.GetMessageIndicated(executingCommand, messages);
		String cringe = toCringe.getContentDisplay();

		return response(EmbedType.INFO).setDescription(runCringeProcess(cringe, iterations));
	}

	private String runCringeProcess(String cringe, int iterations) {
		for (int i = 0; i < iterations; i++) {
			// Replace words from the Cringe Dictionary first.
			for (String word : cringeDict.keySet()) {
				cringe = cringe.replace(word, cringeDict.get(word));
			}

			// Insert random text face expressions (25% chance)
			if (RandomService.GetRandom(4) == 1) {
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
					.replace("ve ", "b ")
					.replace("th ", "f ");

			// Chooses a random word to stutter.
			for (int s = 0; s < RandomService.GetRandom(4) + 1; s++) {
				int pos = getRandomWordStartPosition(cringe);

				if (pos > 0)
					cringe = cringe.substring(0, pos) + cringe.charAt(pos) + "-" + cringe.substring(pos);
			}

			// Insert a random amount of creepy asterisks.
			for (int c = 0; c < RandomService.GetRandom(3); c++) {
				String rItem = creepyAsterisks[RandomService.GetRandom(creepyAsterisks.length)];
				int pos = getRandomWordStartPosition(cringe);

				if (pos > 0)
					cringe = cringe.substring(0, pos) + "`" + rItem + "`" + " " + cringe.substring(pos);
			}

			// Insert a random amount of creepy OwOs
			for (int o = 0; o < RandomService.GetRandom(3); o++) {
				String rItem = creepyOwos[RandomService.GetRandom(creepyOwos.length)];
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
			String rWord = safeWords.get(RandomService.GetRandom(safeWords.size()));

			return src.indexOf(rWord);
		} else {
			return -1;
		}
	}
}