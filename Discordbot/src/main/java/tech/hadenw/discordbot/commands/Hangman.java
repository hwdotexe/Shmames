package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.storage.HangmanGame;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hangman implements ICommand {
	private String[] puzzleDict;

	public Hangman(){
		puzzleDict = new String[] {"Warlock", "Changeling", "Dragonborn", "Mage Hand", "Prestidigitation", "Natural Twenty", "Tiefling", "King Dedede", "Princess Zelda",
		"Ganondorf", "Bokoblin", "Triforce", "Mindflayer", "Philter of Love", "Blade Ward", "Mending", "Thunderclap", "Detect Magic", "Speak with Animals", "Ray of Enfeeblement",
		"Gargoyle", "Archgriffin", "Godling", "Grave Hag", "Cockatrice", "Drowner", "Endrega", "Werewolf", "Nightwraith", "Marauder", "Necromancer", "Dark Brotherhood",
		"Tetsutetsu Tetsutetsu", "Van Hohenheim", "Edward Elric", "Roy Mustang", "Shou Tucker", "Jonathan Joestar", "Dio Brando", "Jotaro Kujo", "Ludo Bagman", "Dolores Umbridge",
		"Bellatrix Lestrange", "Braccus Rex", "Handsome Jack", "Cloud Strife", "Edelgard von Hresvelg", "Sylvain Jose Gautier", "Mike Stoklasa", "General Massive Systems",
		"Harrison Armory", "Harrow", "Wukong", "Octavia", "Valkyr", "Dangerous Mute Lunatic", "Combustible Lemons", "Structurally Superfluous", "Mean Mother Hubbard",
		"Song of Storms", "Your Mom"};
	}

	@Override
	public String getDescription() {
		return "Play a game of Hangman!";
	}
	
	@Override
	public String getUsage() {
		return "hangman <start|guess> [letter|answer]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((start)|(guess))( [a-z\\s]+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

			if(m.group(1).equalsIgnoreCase("start")){
				if(b.getHangmanGame() != null) {
					TextChannel tc = message.getGuild().getTextChannelById(b.getHangmanGame().getChannelID());
					return "There is already a Hangman game running in " + tc.getAsMention();
				}

				String word = puzzleDict[Utils.getRandom(puzzleDict.length)];
				HangmanGame newGame = new HangmanGame(word);

				sendEmbeddedMessage(message.getTextChannel(), newGame);
				b.setHangmanGame(newGame);
			}else if(m.group(1).equalsIgnoreCase("guess")){
				if(m.group(4) != null) {
					if (b.getHangmanGame() == null)
						return "There isn't a Hangman game running! Try starting one.";

					HangmanGame g = b.getHangmanGame();
					String guess = m.group(4).toLowerCase().trim();

					if(guess.length() == 1){
						// Make sure they haven't already guessed this one.
						if(g.getCorrectGuesses().contains(guess.charAt(0)) || g.getIncorrectGuesses().contains(guess.charAt(0))){
							return "You've already guessed that letter!";
						}

						// Ok now continue
						if(g.getWord().contains(guess)){
							g.getCorrectGuesses().add(guess.charAt(0));
						}else{
							g.getIncorrectGuesses().add(guess.charAt(0));
							g.removeLife();
						}
					} else {
						if(g.getWord().equalsIgnoreCase(guess)){
							// They've guessed the puzzle. Update the message and then end the game.
							for(char c : g.getWord().toCharArray()){
								g.getCorrectGuesses().add(c);
							}

							g.deleteMessage(message.getGuild());
							sendEmbeddedMessage(message.getTextChannel(), g);

							b.setHangmanGame(null);
							return "You win!";
						}else{
							g.removeLife();
						}
					}

					// Update the board
					g.deleteMessage(message.getGuild());
					sendEmbeddedMessage(message.getTextChannel(), g);

					// Do they win?
					boolean win = true;
					for(char c : g.getWord().toCharArray()){
						if(!g.getCorrectGuesses().contains(c)) {
							win = false;
							break;
						}
					}

					if(win){
						b.setHangmanGame(null);
						return "You win!";
					}

					// Do they lose?
					if(g.getLivesRemaining() == 0){
						b.setHangmanGame(null);
						return "You lose! The word was \""+g.getWord()+"\"";
					}
				}else{
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
				}
			}
		}else{
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}

		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"hangman"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private void sendEmbeddedMessage(TextChannel c, HangmanGame g){
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setAuthor("Let's Play Hangman!");
		eBuilder.setColor(Color.WHITE);
		eBuilder.setFooter("Already guessed: "+g.getIncorrectGuesses());

		eBuilder.appendDescription(getHangmanASCII(g.getLivesRemaining()));
		eBuilder.appendDescription("\n"+getWordProgressEmotes(g));

		MessageEmbed embed = eBuilder.build();
		MessageAction ma = c.sendMessage(embed);
		Message m = ma.complete();

		g.setMessage(c.getIdLong(), m.getIdLong());
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
