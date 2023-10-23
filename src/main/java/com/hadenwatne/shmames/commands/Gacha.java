package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.service.LoggingService;
import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.*;
import com.hadenwatne.shmames.enums.*;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaCharacter;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class Gacha extends Command {
	public Gacha() {
		super(true, false, true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
								.addParameters(
										new CommandParameter("times", "Roll multiple times with one command", ParameterType.INTEGER, false)
												.setExample("5")
								)
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
								.addParameters(
										new CommandParameter("id", "The ID of the Gacha prize to delete.", ParameterType.STRING)
												.setExample("id")
								)
								.build()
				)
				.addSubCommandGroups(
						new SubCommandGroup("banner", "View the current RATE UP prizes.")
								.addAlias("b")
								.addSubCommands(
										new CommandStructure("view", "View the current RATE UP banner.")
												.addAlias("v")
												.build(),
										new CommandStructure("add", "Add a new prize to the banner.")
												.addAlias("a")
												.addParameters(
														new CommandParameter("id", "The ID of the prize to add", ParameterType.STRING)
																.setExample("abc123")
												)
												.build(),
										new CommandStructure("image", "Sets the banner's optional display image.")
												.addAlias("i")
												.addParameters(
														new CommandParameter("url", "A URL of the image to use.", ParameterType.STRING)
																.setPattern(RegexPatterns.URL.getPattern())
																.setExample("//image.jpg")
												)
												.build(),
										new CommandStructure("clear", "Clear the current banner.")
												.addAlias("c")
												.build()

								)
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		Guild server = executingCommand.getServer();
		Brain brain = executingCommand.getBrain();
		Language language = executingCommand.getLanguage();
		String subCommand = executingCommand.getSubCommand();
		String subCommandGroup = executingCommand.getSubCommandGroup();

		switch(subCommandGroup){
			case "banner":
				return cmdBanner(server, language, brain, executingCommand);
		}

		switch (subCommand) {
			case "roll":
				return cmdRoll(server, language, brain, executingCommand);
			case "profile":
				return cmdProfile(server, language, brain, executingCommand);
			case "list":
				return cmdList(server, language, brain, executingCommand);
			case "create":
				return cmdCreate(server, language, brain, executingCommand);
			case "delete":
				return cmdDelete(server, language, brain, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdRoll(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		GachaUser user = GachaService.GetGachaUser(brain, executingCommand.getAuthorUser());
		int rollTimes = Math.max(1, executingCommand.getCommandArguments().getAsInteger("times"));

		if (user.getUserPoints() >= (GachaService.ROLL_COST * rollTimes)) {
			user.subtractUserPoints(GachaService.ROLL_COST * rollTimes);

			StringBuilder result = new StringBuilder();
			EmbedBuilder response = response(EmbedType.SUCCESS);

			for (int i = 0; i < rollTimes; i++) {
				double roll = RandomService.GetRandom();

				// Increase the roll if triggering pity system.
				if (user.getPityCounter() > GachaService.PITY_THRESHOLD) {
					roll += (user.getPityCounter() - GachaService.PITY_THRESHOLD) * 0.05;
				}

				GachaRarity rolledRarity = GachaRarity.matchRarity(roll);
				List<GachaCharacter> possibleCharacters = new ArrayList<>();

				for (GachaCharacter gc : brain.getGachaCharacters()) {
					if (gc.getGachaCharacterRarity().getRarityValue() == rolledRarity.getRarityValue()) {
						possibleCharacters.add(gc);

						// Add this character again if they are on the banner.
						if (brain.getGachaBanner().getCharacters().contains(gc.getGachaCharacterID())) {
							possibleCharacters.add(gc);
						}
					}
				}

				GachaCharacter rolledCharacter = RandomService.GetRandomObjectFromList(possibleCharacters);

				boolean isNew = !user.hasCharacter(rolledCharacter);
				user.addCharacterToInventory(rolledCharacter);

				// Refund a little if this is not a new character.
				if (!isNew) {
					user.addUserPoints(GachaService.GetRarityDuplicateRefund(rolledCharacter.getGachaCharacterRarity()));
				}

				if (rolledCharacter.getGachaCharacterRarity().getRarityValue() <= GachaRarity.RARE.getRarityValue()) {
					user.incrementPityCounter();
				} else {
					user.resetPityCounter();
				}

				String rarityEmote = GachaService.GetRarityEmoji(rolledCharacter.getGachaCharacterRarity());

				if (rollTimes == 1) {
					for (int emote = 0; emote < 10; emote++) {
						result.append(rarityEmote);
					}

					result.append(System.lineSeparator());
					result.append(System.lineSeparator());
					result.append(isNew ? ":sparkles:【NEW】" : "");
					result.append(" **" + rolledCharacter.getGachaCharacterName() + "**");
					result.append(System.lineSeparator());
					result.append(System.lineSeparator());

					for (int emote = 0; emote < 10; emote++) {
						result.append(rarityEmote);
					}

					response.setThumbnail(rolledCharacter.getGachaCharacterImageURL())
							.setFooter(rolledCharacter.getGachaCharacterRarity().name() + " • Owned x" + user.getUserGachaInventory().get(rolledCharacter.getGachaCharacterID()) + " • " + user.getUserPoints() + " Coins remaining")
							.addField("You got " + rolledCharacter.getGachaCharacterName() + "!", "> " + rolledCharacter.getGachaCharacterDescription(), false);
				} else {
					if (result.length() > 0) {
						result.append(System.lineSeparator());
					}

					result.append(isNew ? ":sparkles:" : "");
					result.append(" ");
					result.append(rarityEmote);
					result.append(" **" + rolledCharacter.getGachaCharacterName() + "**");
				}
			}

			if (rollTimes > 1) {
				response.setFooter("Rolled x" + rollTimes + " • " + user.getUserPoints() + " Coins remaining");
			}

			response.setDescription(result.toString());

			return response;
		} else {
			return response(EmbedType.ERROR, ErrorKeys.GACHA_NO_COINS.name())
					.setDescription(language.getError(ErrorKeys.GACHA_NO_COINS));
		}
	}

	private EmbedBuilder cmdProfile(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		GachaUser user = GachaService.GetGachaUser(brain, executingCommand.getAuthorUser());
		List<GachaCharacter> characters = brain.getGachaCharacters();
		HashMap<String, Integer> userInventory = user.getUserGachaInventory();
		StringBuilder sb = new StringBuilder();

		for (GachaCharacter gc : characters) {
			if(userInventory.containsKey(gc.getGachaCharacterID())) {
				if (sb.length() > 0) {
					sb.append(System.lineSeparator());
				}

				String rarityEmote = GachaService.GetRarityEmoji(gc.getGachaCharacterRarity());

				sb.append(rarityEmote);
				sb.append(" **");
				sb.append(gc.getGachaCharacterName());
				sb.append("** x");
				sb.append(userInventory.get(gc.getGachaCharacterID()));
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

			for (String field : gachaList) {
				if (response.getFields().size() == 1) {
					response.addField("Your Collection", field, false);
				} else {
					response.addField("", field, false);
				}
			}

			executingCommand.replyFile(file, "profile.png", response);

			return null;
		} catch (IOException e) {
			LoggingService.LogException(e);

			return response(EmbedType.ERROR, ErrorKeys.BOT_ERROR.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.BOT_ERROR));
		}
	}

	private EmbedBuilder cmdList(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		boolean showID = ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember());
		StringBuilder sb = new StringBuilder();

		for (GachaCharacter gc : brain.getGachaCharacters()) {
			if (sb.length() > 0) {
				sb.append(System.lineSeparator());
			}

			String rarityEmote = GachaService.GetRarityEmoji(gc.getGachaCharacterRarity());

			sb.append(rarityEmote);
			sb.append(" **");
			sb.append(gc.getGachaCharacterName());
			sb.append("**");

			if (showID) {
				sb.append(" [_");
				sb.append(gc.getGachaCharacterID());
				sb.append("_]");
			}
		}

		List<String> gachaList = PaginationService.SplitString(sb.toString(), MessageEmbed.VALUE_MAX_LENGTH);

		EmbedBuilder response = response(EmbedType.INFO);

		if (showID) {
			response.setFooter("Admin Mode: IDs are displayed");
		}

		for (String field : gachaList) {
			if (response.getFields().size() == 0) {
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
			Collections.reverse(brain.getGachaCharacters());

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.ITEM_ADDED));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private EmbedBuilder cmdDelete(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember())) {
			ExecutingCommandArguments args = executingCommand.getCommandArguments();
			String id = args.getAsString("id");

			for (GachaCharacter gc : brain.getGachaCharacters()) {
				if (gc.getGachaCharacterID().equals(id)) {

					// Remove ownership.
					for(GachaUser gu : brain.getGachaUsers()) {
						if(gu.getUserGachaInventory().containsKey(gc.getGachaCharacterID())) {
							gu.getUserGachaInventory().remove(gc.getGachaCharacterID());
						}
					}

					brain.getGachaCharacters().remove(gc);

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.ITEM_REMOVED, new String[]{gc.getGachaCharacterName()}));
				}
			}

			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private EmbedBuilder cmdBanner(Guild server, Language language, Brain brain, ExecutingCommand executingCommand) {
		ExecutingCommandArguments args = executingCommand.getCommandArguments();
		String subCommand = executingCommand.getSubCommand();

		switch (subCommand) {
			case "view":
				if (brain.getGachaBanner().getCharacters().size() > 0) {
					StringBuilder sb = new StringBuilder();

					for (String gcid : brain.getGachaBanner().getCharacters()) {
						for (GachaCharacter gc : brain.getGachaCharacters()) {
							if (gc.getGachaCharacterID().equals(gcid)) {
								String rarityEmote = GachaService.GetRarityEmoji(gc.getGachaCharacterRarity());

								if (sb.length() > 0) {
									sb.append(System.lineSeparator());
								}

								sb.append(rarityEmote);
								sb.append(" ");
								sb.append(gc.getGachaCharacterName());

								break;
							}
						}
					}

					EmbedBuilder response = response(EmbedType.SUCCESS);

					if (brain.getGachaBanner().getURL() != null) {
						response.setImage(brain.getGachaBanner().getURL());
					}

					response.addField("Rate Up Characters", sb.toString(), false);

					return response;
				} else {
					return response(EmbedType.INFO, ErrorKeys.GACHA_NO_BANNER.name())
							.setDescription(language.getError(ErrorKeys.GACHA_NO_BANNER));
				}
			case "add":
				if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember())) {
					String id = executingCommand.getCommandArguments().getAsString("id");

					for (GachaCharacter gc : brain.getGachaCharacters()) {
						if (gc.getGachaCharacterID().equals(id)) {
							brain.getGachaBanner().addCharacter(id);

							return response(EmbedType.SUCCESS)
									.setDescription(language.getMsg(LanguageKeys.ITEM_ADDED));
						}
					}

					return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
							.setDescription(language.getError(ErrorKeys.NOT_FOUND));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
			case "image":
				if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember())) {
					String url = executingCommand.getCommandArguments().getAsString("url");

					brain.getGachaBanner().setURL(url);

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.ITEM_ADDED));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
			case "clear":
				if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.MANAGE_GACHA), executingCommand.getAuthorMember())) {
					brain.setGachaBanner(null);

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.GENERIC_SUCCESS));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
							.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
				}
		}

		return null;
	}
}