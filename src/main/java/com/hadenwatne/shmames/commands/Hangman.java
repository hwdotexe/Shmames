package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.HangmanDictionary;
import com.hadenwatne.shmames.models.HangmanGame;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Hangman extends Command {
	private List<HangmanDictionary> dictionaries;

	public Hangman() {
		super(true);

		dictionaries = App.Shmames.getStorageService().getBrainController().getHangmanDictionaries().getDictionaries();
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("hangman", "Play a game of Hangman!")
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
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		Brain brain = executingCommand.getBrain();
		Lang lang = executingCommand.getLanguage();

		switch(subCommand) {
			case "start":
				return cmdStart(brain, lang, executingCommand.getServer(), executingCommand);
			case "guess":
				return cmdGuess(brain, lang, executingCommand.getServer(), executingCommand);
			case "list":
				return cmdList(lang);
		}

		return null;
	}

	private EmbedBuilder cmdList(Lang lang) {
		StringBuilder sb = new StringBuilder();

		for(HangmanDictionary hd : dictionaries){
			if(sb.length() > 0)
				sb.append(", ");

			sb.append(hd.getName());
		}

		return response(EmbedType.INFO)
				.setDescription(lang.getMsg(Langs.HANGMAN_DICTIONARIES, new String[] {sb.toString()}));
	}

	private EmbedBuilder cmdStart(Brain brain, Lang lang, Guild server, ExecutingCommand executingCommand) {
		if(brain.getHangmanGame() != null) {
			TextChannel tc = server.getTextChannelById(brain.getHangmanGame().getChannelID());

			if(tc != null) {
				return response(EmbedType.ERROR, Errors.HANGMAN_ALREADY_STARTED.name())
						.setDescription(lang.getError(Errors.HANGMAN_ALREADY_STARTED, new String[]{tc.getAsMention()}));
			} else {
				brain.setHangmanGame(null);
			}
		}

		String dictionaryNames = executingCommand.getCommandArguments().getAsString("dictionary");
		boolean doExclude = executingCommand.getCommandArguments().getAsBoolean("exclude");
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

			if(availableDictionaries.size() == 0){
				return response(EmbedType.ERROR, Errors.NOT_FOUND.name())
						.setDescription( lang.getError(Errors.NOT_FOUND));
			}

			dictionary = availableDictionaries.get(RandomService.GetRandom(availableDictionaries.size()));
		} else {
			dictionary = dictionaries.get(RandomService.GetRandom(dictionaries.size()));
		}

		String word = dictionary.randomWord();
		String hint = dictionary.getWords().get(word);
		String dictionaryName = dictionary.getName();

		HangmanGame newGame = new HangmanGame(word, hint, dictionaryName);

		newGame.setChannel(executingCommand.getChannel().getIdLong());
		brain.setHangmanGame(newGame);

		EmbedBuilder embedBuilder = buildHangmanEmbed(lang, newGame);
		Message message = MessageService.SendMessageBlocking(executingCommand.getChannel(), embedBuilder);

		newGame.setMessage(message.getIdLong());

		if(executingCommand.hasInteractionHook()) {
			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.GENERIC_SUCCESS));
		}

		return null;
	}

	private EmbedBuilder cmdGuess(Brain brain, Lang lang, Guild server, ExecutingCommand executingCommand) {
		String guess = executingCommand.getCommandArguments().getAsString("guess").toLowerCase();
		HangmanGame game = brain.getHangmanGame();

		if (game == null) {
			return response(EmbedType.ERROR, Errors.HANGMAN_NOT_STARTED.name())
					.setDescription(lang.getError(Errors.HANGMAN_NOT_STARTED));
		}

		// Process the guess.
		if(guess.length() == 1){
			// Avoid re-guesses.
			if(game.getCorrectGuesses().contains(guess.charAt(0)) || game.getIncorrectGuesses().contains(guess.charAt(0))){
				return response(EmbedType.ERROR, Errors.HANGMAN_ALREADY_GUESSED.name())
						.setDescription(lang.getError(Errors.HANGMAN_ALREADY_GUESSED));
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
		game.updateMessage(server,buildHangmanEmbed(lang, game));

		// If the game is over, clean up.
		if(game.isFinished()) {
			brain.setHangmanGame(null);
		}

		if(executingCommand.hasInteractionHook()) {
			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.GENERIC_SUCCESS));
		}

		return null;
	}

	private @Nullable HangmanDictionary getDictionaryByName(String name) {
		for(HangmanDictionary dictionary : dictionaries) {
			if(dictionary.getName().equalsIgnoreCase(name)) {
				return dictionary;
			}
		}

		return null;
	}

	private EmbedBuilder buildHangmanEmbed(Lang lang, HangmanGame g){
		EmbedBuilder eBuilder = response(EmbedType.INFO, lang.getMsg(Langs.HANGMAN_TITLE));

		if(g.isFinished()) {
			if(g.didWin()) {
				eBuilder.setDescription("You win!");
			} else {
				eBuilder.setDescription("You lose! The word was \""+g.getWord()+"\"");
			}
		}

		eBuilder.setFooter(lang.getMsg(Langs.HANGMAN_FOOTER_GUESSED)+" "+g.getIncorrectGuesses());

//		eBuilder.setTitle(g.getDictionary()+" » "+g.getHint());
//		eBuilder.appendDescription(getHangmanASCII(g.getLivesRemaining()));
//		eBuilder.appendDescription("\n"+getWordProgressEmotes(g));

		eBuilder.addField(g.getDictionary()+" » "+g.getHint(), getHangmanASCII(g.getLivesRemaining())+"\n"+getWordProgressEmotes(g), false);

		return eBuilder;
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