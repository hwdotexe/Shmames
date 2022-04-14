package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.HangmanDictionary;
import com.hadenwatne.shmames.models.HangmanGame;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.enums.Errors;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hangman implements ICommand {
	private List<HangmanDictionary> dictionaries;
	private final CommandStructure commandStructure;

	public Hangman() {
		dictionaries = App.Shmames.getStorageService().getBrainController().getHangmanDictionaries().getDictionaries();

		this.commandStructure = CommandBuilder.Create("hangman", "Play a game of Hangman!")
				.addSubCommands(
						CommandBuilder.Create("start", "Begin a new game.")
								.addParameters(
										new CommandParameter("dictionary", "The word list to choose from.", ParameterType.STRING, false)
												.setPattern("[a-z0-9,]+")
												.setExample("mainDictionary"),
										new CommandParameter("exclude", "Use true if you want to exclude these dictionaries.", ParameterType.BOOLEAN, false)
												.setExample("true")
								)
								.build(),
						CommandBuilder.Create("guess", "Submit a guess for a letter or solution.")
								.addParameters(
										new CommandParameter("guess", "A letter, word, or phrase.", ParameterType.STRING)
												.setExample("e")
								)
								.build(),
						CommandBuilder.Create("list", "List available dictionaries.")
								.build()
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		ShmamesSubCommandData subCommand = data.getSubCommandData();
		String subCmd = subCommand.getCommandName();

		switch(subCmd.toLowerCase()) {
			case "start":
				return cmdStart(brain, lang, data.getServer(), data.getMessagingChannel(), subCommand);
			case "guess":
				return cmdGuess(brain, lang, data.getServer(), data.getMessagingChannel(), subCommand);
			case "list":
				return cmdList();
			default:
				return "";
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private String cmdList() {
		StringBuilder sb = new StringBuilder();

		for(HangmanDictionary hd : dictionaries){
			if(sb.length() > 0)
				sb.append(", ");

			sb.append(hd.getName());
		}

		return "Available dictionaries: "+sb.toString()+", or leave blank to use all.";
	}

	private String cmdStart(Brain brain, Lang lang, Guild server, ShmamesCommandMessagingChannel messagingChannel, ShmamesSubCommandData subCommand) {
		if(brain.getHangmanGame() != null) {
			TextChannel tc = server.getTextChannelById(brain.getHangmanGame().getChannelID());

			if(tc != null) {
				return "There is already a Hangman game running in " + tc.getAsMention();
			} else {
				brain.setHangmanGame(null);
			}
		}

		String dictionaryNames = subCommand.getArguments().getAsString("dictionary");
		boolean doExclude = subCommand.getArguments().getAsBoolean("exclude");
		HangmanDictionary dictionary = null;

		// Select the word list to pull from for this Hangman puzzle.
		if(dictionaryNames != null) {
			List<HangmanDictionary> availableDictionaries = new ArrayList<>();

			if(doExclude) {
				// Add all except the ones mentioned.
				for(HangmanDictionary dict : dictionaries) {
					boolean shouldAdd = true;

					for (String n : dictionaryNames.split(",")) {
						if(dict.getName().equalsIgnoreCase(n)){
							shouldAdd = false;
						}
					}

					if(shouldAdd) {
						availableDictionaries.add(dict);
					}
				}
			} else {
				// Only add the mentioned dictionaries.
				for (String n : dictionaryNames.split(",")) {
					HangmanDictionary namedDictionary = getDictionaryByName(n);

					if (namedDictionary != null) {
						availableDictionaries.add(namedDictionary);
					}
				}
			}

			if(availableDictionaries.size() == 0)
				return lang.getError(Errors.NOT_FOUND, true);

			dictionary = availableDictionaries.get(RandomService.GetRandom(availableDictionaries.size()));
		} else {
			dictionary = dictionaries.get(RandomService.GetRandom(dictionaries.size()));
		}

		String word = dictionary.randomWord();
		String hint = dictionary.getWords().get(word);
		String dictionaryName = dictionary.getName();

		HangmanGame newGame = new HangmanGame(word, hint, dictionaryName);

		newGame.setChannel(messagingChannel.getChannel().getIdLong());
		sendEmbeddedMessage(messagingChannel, lang, newGame);
		brain.setHangmanGame(newGame);

		return "";
	}

	private String cmdGuess(Brain brain, Lang lang, Guild server, ShmamesCommandMessagingChannel messagingChannel, ShmamesSubCommandData subCommand) {
		String guess = subCommand.getArguments().getAsString("guess").toLowerCase();
		HangmanGame game = brain.getHangmanGame();

		if (game == null)
			return lang.getError(Errors.HANGMAN_NOT_STARTED, true);

		// If there is a slash command, don't make it Ephemeral.
		if(messagingChannel.hasHook()) {
			messagingChannel.getHook().setEphemeral(false);
		}

		// Process the guess.
		if(guess.length() == 1){
			// Avoid re-guesses.
			if(game.getCorrectGuesses().contains(guess.charAt(0)) || game.getIncorrectGuesses().contains(guess.charAt(0))){
				return lang.getError(Errors.HANGMAN_ALREADY_GUESSED, true);
			}

			// Process their letter guess.
			if(game.getWord().contains(guess)){
				game.getCorrectGuesses().add(guess.charAt(0));
			}else{
				game.getIncorrectGuesses().add(guess.charAt(0));
				game.removeLife();
			}
		} else {
			// They are guessing the puzzle.
			if(game.getWord().equalsIgnoreCase(guess)){
				// They guessed the puzzle. Add all of the remaining letters to the list.
				for(char c : game.getWord().toCharArray()){
					if(!game.getCorrectGuesses().contains(c)) {
						game.getCorrectGuesses().add(c);
					}
				}
			}else{
				// They guessed wrong.
				game.removeLife();
			}
		}

		// Update the game state if it is over.
		if(game.getLivesRemaining() == 0 || game.didWin()) {
			game.finish();
		}

		// Send an updated game board.
		game.deleteMessage(server);
		sendEmbeddedMessage(messagingChannel, lang, game);

		// If the game is over, clean up.
		if(game.isFinished()) {
			brain.setHangmanGame(null);
		}

		return "";
	}

	private @Nullable HangmanDictionary getDictionaryByName(String name) {
		for(HangmanDictionary dictionary : dictionaries) {
			if(dictionary.getName().equalsIgnoreCase(name)) {
				return dictionary;
			}
		}

		return null;
	}

	private void sendEmbeddedMessage(ShmamesCommandMessagingChannel messagingChannel, Lang lang, HangmanGame g){
		EmbedBuilder eBuilder = new EmbedBuilder();

		if(g.isFinished()) {
			if(g.didWin()) {
				eBuilder.setAuthor("You win!");
			} else {
				eBuilder.setAuthor("You lose! The word was \""+g.getWord()+"\"");
			}
		} else {
			eBuilder.setAuthor(lang.getMsg(Langs.HANGMAN_TITLE));
		}

		eBuilder.setTitle(g.getDictionary()+" » "+g.getHint());
		eBuilder.setColor(Color.WHITE);
		eBuilder.setFooter(lang.getMsg(Langs.HANGMAN_FOOTER_GUESSED)+" "+g.getIncorrectGuesses());

		eBuilder.appendDescription(getHangmanASCII(g.getLivesRemaining()));
		eBuilder.appendDescription("\n"+getWordProgressEmotes(g));


		messagingChannel.sendMessage(eBuilder, (message -> g.setMessage(message.getIdLong())), (error -> {}));
	}

	private String getWordProgressEmotes(HangmanGame g){
		StringBuilder sb = new StringBuilder();

		for(int i=0; i<g.getWord().length(); i++){
			char ch = g.getWord().charAt(i);

			if(ch == ' '){
				sb.append(":heavy_minus_sign: ");
				continue;
			}

			if(g.getCorrectGuesses().contains(ch)){
				sb.append(":regional_indicator_").append(ch).append(": ");
			}else{
				sb.append(":grey_question: ");
			}
		}

		return sb.toString();
	}

	private String getHangmanASCII(int lives){
		switch(lives){
			case 5:
				return "```  _______\n" +
						" |/      |\n" +
						" |      (_)\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						"_|___```";
			case 4:
				return "```  _______\n" +
						" |/      |\n" +
						" |      (_)\n" +
						" |      \\|\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						"_|___```";
			case 3:
				return "```  _______\n" +
						" |/      |\n" +
						" |      (_)\n" +
						" |      \\|/\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						"_|___```";
			case 2:
				return "```  _______\n" +
						" |/      |\n" +
						" |      (_)\n" +
						" |      \\|/\n" +
						" |       |\n" +
						" |\n" +
						" |\n" +
						"_|___```";
			case 1:
				return "```  _______\n" +
						" |/      |\n" +
						" |      (_)\n" +
						" |      \\|/\n" +
						" |       |\n" +
						" |      /\n" +
						" |\n" +
						"_|___```";
			case 0:
				return "```  _______\n" +
						" |/      |\n" +
						" |      (_)\n" +
						" |      \\|/\n" +
						" |       |\n" +
						" |      / \\\n" +
						" |\n" +
						"_|___```";
			default:
				return "```  _______\n" +
						" |/      |\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						" |\n" +
						"_|___```";
		}
	}
}