package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.*;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaCharacter;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gacha extends Command {
	private static final int PITY_THRESHOLD = 50;
	private static final int ROLL_COST = 5;

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
						CommandBuilder.Create("profile", "View your Gacha stats!")
								.addAlias("p")
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
			case "profile":
				return cmdProfile(server, language, brain, executingCommand);
			case "list":
				return cmdList(server, language, brain, executingCommand);
			case "create":
				return cmdCreate(server, language, brain, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdRoll(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		GachaUser user = getGachaUser(brain, executingCommand.getAuthorUser());

		if (user.getUserPoints() >= ROLL_COST) {
			user.subtractUserPoints(ROLL_COST);

			double roll = RandomService.GetRandom();

			// Increase the roll if triggering pity system.
			if (user.getPityCounter() > PITY_THRESHOLD) {
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
			for (GachaCharacter gc : brain.getGachaCharacters()) {
				if (gc.getGachaCharacterRarity().getRarityValue() == rolledRarity.getRarityValue()) {
					possibleCharacters.add(gc);
				}
			}

			GachaCharacter rolledCharacter = RandomService.GetRandomObjectFromList(possibleCharacters);

			boolean isNew = !user.hasCharacter(rolledCharacter);
			user.addCharacterToInventory(rolledCharacter);

			if (rolledCharacter.getGachaCharacterRarity().getRarityValue() <= GachaRarity.RARE.getRarityValue()) {
				user.incrementPityCounter();
			} else {
				user.resetPityCounter();
			}

			String rarityEmote = getRarityEmoji(rolledCharacter.getGachaCharacterRarity());

			return response(EmbedType.SUCCESS)
					.setThumbnail(rolledCharacter.getGachaCharacterImageURL())
					.setDescription((rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + System.lineSeparator())
							+ System.lineSeparator()
							+ (isNew ? ":sparkles:【NEW】" : "")
							+ " **" + rolledCharacter.getGachaCharacterName() + "**"
							+ System.lineSeparator()
							+ (System.lineSeparator() + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote + rarityEmote))
					.setFooter(rolledCharacter.getGachaCharacterRarity().name() + " • Owned x" + user.getUserGachaInventory().get(rolledCharacter.getGachaCharacterID()))
					.addField("You got " + rolledCharacter.getGachaCharacterName() + "!", "> " + rolledCharacter.getGachaCharacterDescription(), false);
		} else {
			return response(EmbedType.ERROR, "TOO_POOR")
					.setDescription("You're too poor to roll the gacha.");
		}
	}

	private EmbedBuilder cmdProfile(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		GachaUser user = getGachaUser(brain, executingCommand.getAuthorUser());
		StringBuilder sb = new StringBuilder();

		for (String gckey : user.getUserGachaInventory().keySet()) {
			List<GachaCharacter> characters = brain.getGachaCharacters();

			for (GachaCharacter gc : characters) {
				if (gc.getGachaCharacterID().equals(gckey)) {
					if (sb.length() > 0) {
						sb.append(System.lineSeparator());
					}

					String rarityEmote = getRarityEmoji(gc.getGachaCharacterRarity());

					sb.append(rarityEmote);
					sb.append(" **");
					sb.append(gc.getGachaCharacterName());
					sb.append("** x");
					sb.append(user.getUserGachaInventory().get(gckey));

					break;
				}
			}
		}

		if (sb.length() == 0) {
			sb.append(language.getError(ErrorKeys.ITEMS_NOT_FOUND));
		}

		try {
			InputStream file = new URL(executingCommand.getAuthorUser().getEffectiveAvatarUrl()).openStream();
			List<String> gachaList = PaginationService.SplitString(sb.toString(), MessageEmbed.VALUE_MAX_LENGTH);

			EmbedBuilder response = response(EmbedType.INFO)
					.addField(App.Shmames.getBotName() + "Coin:tm: Balance", ":coin: " + user.getUserPoints(), false)
					.setThumbnail("attachment://profile.png")
					.setFooter("@" + executingCommand.getAuthorUser().getAsTag());

			for(String field : gachaList) {
				if(response.getFields().size() == 1) {
					response.addField("Your Collection", field, false);
				} else {
					response.addField("", field, false);
				}
			}

			executingCommand.replyFile(file, "profile.png", response);

			return null;
		}catch (Exception e) {
			LoggingService.LogException(e);

			return response(EmbedType.ERROR, ErrorKeys.BOT_ERROR.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.BOT_ERROR));
		}
	}

	private EmbedBuilder cmdList(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		boolean showID = ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember());
		StringBuilder sb = new StringBuilder();

		for(GachaCharacter gc : brain.getGachaCharacters()) {
			if(sb.length() > 0) {
				sb.append(System.lineSeparator());
			}

			String rarityEmote = getRarityEmoji(gc.getGachaCharacterRarity());

			sb.append(rarityEmote);
			sb.append(" **");
			sb.append(gc.getGachaCharacterName());
			sb.append("**");

			if(showID) {
				sb.append(" [_");
				sb.append(gc.getGachaCharacterID());
				sb.append("_]");
			}
		}

		List<String> gachaList = PaginationService.SplitString(sb.toString(), MessageEmbed.VALUE_MAX_LENGTH);

		EmbedBuilder response = response(EmbedType.INFO);

		if(showID) {
			response.setFooter("Admin Mode: IDs are displayed");
		}

		for(String field : gachaList) {
			if(response.getFields().size() == 0) {
				response.addField("Possible Prizes", field, false);
			} else {
				response.addField("", field, false);
			}
		}

		return response;
	}

	private EmbedBuilder cmdCreate(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember())) {
			ExecutingCommandArguments args = executingCommand.getCommandArguments();
			String name = args.getAsString("name");
			String image = args.getAsString("image");
			String description = args.getAsString("description");
			String rarityString = args.getAsString("rarity");
			GachaRarity rarity = rarityString != null ? GachaRarity.valueOf(rarityString) : GachaRarity.COMMON;

			brain.getGachaCharacters().add(new GachaCharacter(name, description, image, rarity));

			Collections.sort(brain.getGachaCharacters());

			return response(EmbedType.SUCCESS)
					.setDescription("`"+name+"` was created successfully.");
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

		// New users get 10 free rolls. Yay!
		ngu.addUserPoints(ROLL_COST * 10);

		brain.getGachaUsers().add(ngu);

		return ngu;
	}

	private String getRarityEmoji(GachaRarity rarity) {
		switch (rarity) {
			case UNCOMMON:
				return ":green_circle:";
			case RARE:
				return ":blue_circle:";
			case VERY_RARE:
				return ":purple_circle:";
			case LEGENDARY:
				return ":orange_circle:";
			default:
				return ":white_circle:";
		}
	}
}
