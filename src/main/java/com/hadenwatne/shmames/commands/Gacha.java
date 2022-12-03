package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.*;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaCharacter;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Gacha extends Command {
	private static final int PITY_THRESHOLD = 5;

	public Gacha() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		CommandParameter rarity = new CommandParameter("rarity", "The rarity of this prize.", ParameterType.SELECTION);

		for (GachaRarity r : GachaRarity.values()) {
			rarity.addSelectionOptions(r.name());
		}

		return CommandBuilder.Create("gacha", "Collect exciting prizes at random!")
				.addSubCommands(
						CommandBuilder.Create("roll", "Exchange your points for a chance at a prize!")
								.addAlias("r")
								.build(),
						CommandBuilder.Create("view", "View your Gacha Inventory.")
								.addAlias("v")
								.build(),
						CommandBuilder.Create("list", "Browse a list of all possible prizes.")
								.addAlias("v")
								.build(),
						CommandBuilder.Create("create", "Create a new Gacha prize for others to collect.")
								.addAlias("c")
								.addParameters(
										new CommandParameter("name", "Exchange your points for a chance at a prize!", ParameterType.STRING)
												.setExample("Sparkle Princess"),
										rarity
												.setExample("COMMON"),
										new CommandParameter("image", "An image to represent this prize.", ParameterType.STRING)
												.setPattern(RegexPatterns.URL.getPattern())
												.setExample("image_URL"),
										new CommandParameter("description", "A description of this prize.", ParameterType.STRING)
												.setExample("Can jump almost 3 feet.")
								)
								.build(),
						CommandBuilder.Create("delete", "Delete a Gacha prize from the pool.")
								.addAlias("d")
								.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		Guild server = executingCommand.getServer();
		Brain brain = executingCommand.getBrain();
		Language language = executingCommand.getLanguage();
		String subCommand = executingCommand.getSubCommand();

		switch (subCommand) {
			case "roll":
				return cmdRoll(server, language, brain, executingCommand);
			case "view":
				return cmdView(server, language, brain, executingCommand);
			case "list":
				return cmdList(server, language, brain, executingCommand);
			case "create":
				return cmdCreate(server, language, brain, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdRoll(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		GachaUser user = getGachaUser(brain, executingCommand.getAuthorUser());

		double roll = RandomService.GetRandom();

		// Increase the roll if triggering pity system.
		if(user.getPityCounter() > PITY_THRESHOLD) {
			roll += (user.getPityCounter() - PITY_THRESHOLD) * 0.05;
		}

		GachaRarity rolledRarity = GachaRarity.matchRarity(roll);
		List<GachaCharacter> possibleCharacters = new ArrayList<>();

		// Option A: Build a pool of possible characters, and pull from it at random.
//		for(GachaCharacter gc : brain.getGachaCharacters()) {
//			if(gc.getGachaCharacterRarity().getRarityValue() <= rolledRarity.getRarityValue()) {
//				possibleCharacters.add(gc);
//			}
//		}

		// Option B: Pull from a pool of characters within the rolled rarity category.
		for(GachaCharacter gc : brain.getGachaCharacters()) {
			if(gc.getGachaCharacterRarity().getRarityValue() == rolledRarity.getRarityValue()) {
				possibleCharacters.add(gc);
			}
		}

		GachaCharacter rolledCharacter = RandomService.GetRandomObjectFromList(possibleCharacters);

		user.addCharacterToInventory(rolledCharacter);

		if(rolledCharacter.getGachaCharacterRarity().getRarityValue() <= GachaRarity.RARE.getRarityValue()) {
			user.incrementPityCounter();
		} else {
			user.resetPityCounter();
		}

		return response(EmbedType.SUCCESS)
				.setThumbnail(rolledCharacter.getGachaCharacterImageURL())
				.setDescription("**Rarity: **"+rolledCharacter.getGachaCharacterRarity().name())
				.addField("You rolled "+rolledCharacter.getGachaCharacterName()+"!", rolledCharacter.getGachaCharacterDescription(), false);
	}

	private EmbedBuilder cmdView(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		GachaUser user = getGachaUser(brain, executingCommand.getAuthorUser());
		StringBuilder sb = new StringBuilder();

		for(String gckey : user.getUserGachaInventory().keySet()) {
			List<GachaCharacter> characters = brain.getGachaCharacters();

			for(GachaCharacter gc : characters) {
				if(gc.getGachaCharacterID().equals(gckey)) {
					if(sb.length() > 0) {
						sb.append(System.lineSeparator());
					}

					sb.append(gc.getGachaCharacterName());
					sb.append(" • [rolled **");
					sb.append(user.getUserGachaInventory().get(gckey));
					sb.append("** times]");

					break;
				}
			}
		}

		return response(EmbedType.INFO)
				.addField("Your Collection", sb.toString(), false);
	}

	private EmbedBuilder cmdList(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		StringBuilder sb = new StringBuilder();

		for(GachaCharacter gc : brain.getGachaCharacters()) {
			if(sb.length() > 0) {
				sb.append(System.lineSeparator());
			}

			sb.append(gc.getGachaCharacterName());
		}

		return response(EmbedType.INFO)
				.addField("Possible Prizes", sb.toString(), false);
	}

	private EmbedBuilder cmdCreate(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember())) {
			brain.getGachaCharacters().add(new GachaCharacter("Popukar", "Test desc", "https://cdn.discordapp.com/avatars/528078229161115671/fe4d4eeb070ea9746691807d54e7026b.png", GachaRarity.COMMON));
			brain.getGachaCharacters().add(new GachaCharacter("Toxic Sludge", "Test desc", "https://cdn.discordapp.com/avatars/528078229161115671/fe4d4eeb070ea9746691807d54e7026b.png", GachaRarity.UNCOMMON));
			brain.getGachaCharacters().add(new GachaCharacter("Nuclear Sludge", "Test desc", "https://cdn.discordapp.com/avatars/528078229161115671/fe4d4eeb070ea9746691807d54e7026b.png", GachaRarity.RARE));
			brain.getGachaCharacters().add(new GachaCharacter("Platinum Sludge", "Test desc", "https://cdn.discordapp.com/avatars/528078229161115671/fe4d4eeb070ea9746691807d54e7026b.png", GachaRarity.VERY_RARE));
			brain.getGachaCharacters().add(new GachaCharacter("Broca via Recruitment", "Test desc", "https://cdn.discordapp.com/avatars/528078229161115671/fe4d4eeb070ea9746691807d54e7026b.png", GachaRarity.LEGENDARY));

			return response(EmbedType.SUCCESS)
					.setDescription("It worked :D:D:D");
		}else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private GachaUser getGachaUser(Brain brain, User user) {
		for(GachaUser gu : brain.getGachaUsers()) {
			if(gu.getUserID() == user.getIdLong()) {
				return gu;
			}
		}

		GachaUser ngu = new GachaUser(user.getIdLong());

		brain.getGachaUsers().add(ngu);

		return ngu;
	}
}
