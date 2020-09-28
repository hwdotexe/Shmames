package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.hadenwatne.discordbot.storage.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Hangman implements ICommand {
	private List<HangmanDictionary> dictionaries;
	private Lang lang;

	public Hangman(){
		dictionaries = new ArrayList<HangmanDictionary>();

		createDictionaries();
	}

	@Override
	public String getDescription() {
		return "Play a game of Hangman!";
	}
	
	@Override
	public String getUsage() {
		return "hangman <start|guess|list> [[!]dictionary[,dictionary]|letter|answer]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((start)|(guess)|(list))( !?([a-z\\s],?)+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

			if(m.group(1).equalsIgnoreCase("list")){
				StringBuilder sb = new StringBuilder();

				for(HangmanDictionary hd : dictionaries){
					if(sb.length() > 0)
						sb.append(", ");

					sb.append(hd.getName());
				}

				return "Available dictionaries: "+sb.toString()+", or leave blank to use all.";
			}else if(m.group(1).equalsIgnoreCase("start")){
				if(b.getHangmanGame() != null) {
					TextChannel tc = message.getGuild().getTextChannelById(b.getHangmanGame().getChannelID());

					return "There is already a Hangman game running in " + tc.getAsMention();
				}

				String word = "";
				String hint = "";
				String dictionary = "";

				HangmanDictionary hd = null;

				if(m.group(5) != null){
					// Try using a custom dictionary, or exclude certain dictionaries, if specified.
					String dictCmd = m.group(5).trim();

					List<HangmanDictionary> specified = new ArrayList<HangmanDictionary>();

					for(String n : dictCmd.split(",")){
						if(n.startsWith("!"))
							n = n.substring(1);

						// Find the dictionary, if it exists.
						for(HangmanDictionary d1 : dictionaries){
							if(d1.getName().equalsIgnoreCase(n)){
								specified.add(d1);
								break;
							}
						}
					}

					if(specified.size() == 0)
						return lang.getError(Errors.NOT_FOUND, true);

					if(dictCmd.startsWith("!")){
						List<HangmanDictionary> drawPool = new ArrayList<HangmanDictionary>(dictionaries);

						for(HangmanDictionary s : specified){
							if(drawPool.contains(s)){
								drawPool.remove(s);
							}
						}

						if(drawPool.size() == 0)
							return lang.getError(Errors.NOT_FOUND, true);

						hd = drawPool.get(Utils.getRandom(drawPool.size()));
					}else{
						hd = specified.get(Utils.getRandom(specified.size()));
					}
				}else{
					hd = dictionaries.get(Utils.getRandom(dictionaries.size()));
				}

				word = hd.randomWord();
				hint = hd.getWords().get(word);
				dictionary = hd.getName();

				HangmanGame newGame = new HangmanGame(word, hint, dictionary);

				sendEmbeddedMessage(message.getTextChannel(), newGame);
				b.setHangmanGame(newGame);
			}else if(m.group(1).equalsIgnoreCase("guess")){
				if(m.group(5) != null) {
					if (b.getHangmanGame() == null)
						return lang.getError(Errors.HANGMAN_NOT_STARTED, true);

					HangmanGame g = b.getHangmanGame();
					String guess = m.group(5).toLowerCase().trim();

					if(guess.length() == 1){
						// Make sure they haven't already guessed this one.
						if(g.getCorrectGuesses().contains(guess.charAt(0)) || g.getIncorrectGuesses().contains(guess.charAt(0))){
							return lang.getError(Errors.HANGMAN_ALREADY_GUESSED, true);
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
					return lang.wrongUsage(getUsage());
				}
			}
		}else{
			return lang.wrongUsage(getUsage());
		}

		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"hangman"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private void sendEmbeddedMessage(TextChannel c, HangmanGame g){
		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setAuthor(lang.getMsg(Langs.HANGMAN_TITLE));
		eBuilder.setTitle(g.getDictionary()+" » "+g.getHint());
		eBuilder.setColor(Color.WHITE);
		eBuilder.setFooter(lang.getMsg(Langs.HANGMAN_FOOTER_GUESSED)+" "+g.getIncorrectGuesses());

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

	private void createDictionaries(){
		HangmanDictionary videoGames = new HangmanDictionary("VideoGames");
		HangmanDictionary dnd = new HangmanDictionary("DnD");
		HangmanDictionary anime = new HangmanDictionary("Anime");
		HangmanDictionary media = new HangmanDictionary("Media");
		HangmanDictionary lancer = new HangmanDictionary("Lancer");

		videoGames.addWord("King Dedede", "Character");
		videoGames.addWord("Princess Zelda", "Character");
		videoGames.addWord("Ganondorf", "Character");
		videoGames.addWord("Bokoblin", "Creature");
		videoGames.addWord("Triforce", "Thing");
		videoGames.addWord("Gargoyle", "Creature");
		videoGames.addWord("Archgriffin", "Creature");
		videoGames.addWord("Godling", "Creature");
		videoGames.addWord("Grave Hag", "Creature");
		videoGames.addWord("Cockatrice", "Creature");
		videoGames.addWord("Drowner", "Creature");
		videoGames.addWord("Endrega", "Creature");
		videoGames.addWord("Moblin", "Creature");
		videoGames.addWord("Werewolf", "Creature");
		videoGames.addWord("Nightwraith", "Creature");
		videoGames.addWord("Necromancer", "Thing");
		videoGames.addWord("Dark Brotherhood", "Thing");
		videoGames.addWord("Cloud Strife", "Character");
		videoGames.addWord("Edelgard von Hresvelg", "Character");
		videoGames.addWord("Sylvain Jose Gautier", "Character");
		videoGames.addWord("Harrow", "Character");
		videoGames.addWord("Wukong", "Character");
		videoGames.addWord("Octavia", "Character");
		videoGames.addWord("Valkyr", "Character");
		videoGames.addWord("Dangerous Mute Lunatic", "Quote");
		videoGames.addWord("Combustible Lemons", "Quote");
		videoGames.addWord("Structurally Superfluous", "Quote");
		videoGames.addWord("Mean Mother Hubbard", "Quote");
		videoGames.addWord("Song of Storms", "Thing");
		videoGames.addWord("Handsome Jack", "Character");
		videoGames.addWord("Braccus Rex", "Character");
		videoGames.addWord("Solaire", "Character");
		videoGames.addWord("Lautrec", "Character");
		videoGames.addWord("Siegmeyer", "Character");
		videoGames.addWord("Link", "Character");
		videoGames.addWord("Skull Kid", "Character");
		videoGames.addWord("Tingle", "Character");
		videoGames.addWord("Kaepora Gaebora", "Character");
		videoGames.addWord("Vaati", "Character");
		videoGames.addWord("Richter Belmont", "Character");
		videoGames.addWord("Count Dracula", "Character");
		videoGames.addWord("Roald", "Character");
		videoGames.addWord("Ferdinand bon Aegir", "Character");
		videoGames.addWord("Miles Edgeworth", "Character");
		videoGames.addWord("Phoenix Wright", "Character");
		videoGames.addWord("Professor Layton", "Character");
		videoGames.addWord("Lucina", "Character");
		videoGames.addWord("Happy Mask Salesman", "Character");
		videoGames.addWord("Sephiroth", "Character");
		videoGames.addWord("Revolver Ocelot", "Character");
		videoGames.addWord("Kiryu Kazuma", "Character");
		videoGames.addWord("Darth Revan", "Character");
		videoGames.addWord("Glory to Mankind", "Quote");

		dnd.addWord("Warlock", "Thing");
		dnd.addWord("Changeling", "Creature");
		dnd.addWord("Dragonborn", "Creature");
		dnd.addWord("Mage Hand", "Spell");
		dnd.addWord("Prestidigitation", "Spell");
		dnd.addWord("Natural Twenty", "Thing");
		dnd.addWord("Tiefling", "Creature");
		dnd.addWord("Mindflayer", "Creature");
		dnd.addWord("Beholder", "Creature");
		dnd.addWord("Philter of Love", "Thing");
		dnd.addWord("Blade Ward", "Spell");
		dnd.addWord("Mending", "Spell");
		dnd.addWord("Patron", "Thing");
		dnd.addWord("Thunderwave", "Spell");
		dnd.addWord("Detect Magic", "Spell");
		dnd.addWord("Speak with Animals", "Spell");
		dnd.addWord("Ray of Enfeeblement", "Spell");
		dnd.addWord("Cockatrice", "Creature");
		dnd.addWord("Werewolf", "Creature");
		dnd.addWord("Necromancer", "Thing");
		dnd.addWord("How do You Want to Do This", "Quote");
		dnd.addWord("Eldritch Blast", "Spell");
		dnd.addWord("Goblin", "Creature");
		dnd.addWord("Tiamat", "Creature");
		dnd.addWord("Book of Shadows", "Thing");
		dnd.addWord("Sneak Attack", "Thing");
		dnd.addWord("Bag of Holding", "Thing");
		dnd.addWord("Armor Class", "Thing");
		dnd.addWord("Light Crossbow", "Thing");
		dnd.addWord("War Caster", "Thing");
		dnd.addWord("Eldritch Knight", "Thing");
		dnd.addWord("Tarrasque", "Creature");
		dnd.addWord("Flameskull", "Creature");
		dnd.addWord("Pseudodragon", "Creature");
		dnd.addWord("Crawling Claw", "Creature");
		dnd.addWord("Medusa", "Creature");
		dnd.addWord("Warforged", "Creature");
		dnd.addWord("Dragonmark", "Thing");
		dnd.addWord("Charlatan", "Thing");
		dnd.addWord("Folk Hero", "Thing");
		dnd.addWord("Multiclass", "Thing");

		anime.addWord("Tetsutetsu Tetsutetsu", "Character");
		anime.addWord("Van Hohenheim", "Character");
		anime.addWord("Edward Elric", "Character");
		anime.addWord("Roy Mustang", "Character");
		anime.addWord("Shou Tucker", "Character");
		anime.addWord("Jonathan Joestar", "Character");
		anime.addWord("Dio Brando", "Character");
		anime.addWord("Jotaro Kujo", "Character");
		anime.addWord("Guren Mk II", "Thing");
		anime.addWord("Lelouch Vi Britannia", "Character");
		anime.addWord("Lancelot", "Thing");
		anime.addWord("Suzaku Kururugi", "Character");
		anime.addWord("Over Nine Thousand", "Quote");
		anime.addWord("A Cruel Angels Thesis", "Song");
		anime.addWord("Again", "Song");
		anime.addWord("Rewrite", "Song");
		anime.addWord("Stand Proud", "Song");
		anime.addWord("Yang Wenli", "Character");
		anime.addWord("Reinhard von Lohengramm", "Character");
		anime.addWord("Hajime No Ippo", "Series");
		anime.addWord("NERV", "Thing");
		anime.addWord("Shinji Ikari", "Character");
		anime.addWord("Rin Okumura", "Character");
		anime.addWord("Eijiro Kirishima", "Character");

		media.addWord("Dolores Umbridge", "Character");
		media.addWord("Harry Potter", "Character");
		media.addWord("Mike Stoklasa", "Person");
		media.addWord("Nicolas Cage", "Person");
		media.addWord("Albus Dumbledore", "Character");
		media.addWord("Jerry Seinfeld", "Person");
		media.addWord("Darth Vader", "Character");
		media.addWord("Sheev Palpatine", "Character");
		media.addWord("Jarjar Binx", "Character");
		media.addWord("Shawshank Redemption", "Film");
		media.addWord("Morgan Freeman", "Person");
		media.addWord("Thanos", "Character");
		media.addWord("Iron Man", "Character");
		media.addWord("Peter Parker", "Character");
		media.addWord("Rowan Atkinson", "Person");
		media.addWord("Citizen Kane", "Film");
		media.addWord("Inception", "Film");
		media.addWord("The Matrix", "Film");
		media.addWord("Ricky Gervais", "Person");
		media.addWord("James Spader", "Person");
		media.addWord("A Clockwork Orange", "Book");
		media.addWord("The Hobbit", "Book");
		media.addWord("The Lion The Witch and the Wardrobe", "Book");
		media.addWord("The Da Vinci Code", "Book");
		media.addWord("To Kill a Mockingbird", "Book");
		media.addWord("War and Peace", "Book");
		media.addWord("The Diary of Anne Frank", "Book");
		media.addWord("Gone with the Wind", "Book");
		media.addWord("The Great Gatsby", "Book");
		media.addWord("Oliver Twist", "Book");
		media.addWord("A Christmas Carol", "Book");
		media.addWord("The Hound of the Baskervilles", "Book");
		media.addWord("Twenty Thousand Leagues Under the Sea", "Book");
		media.addWord("The Hunger Games", "Book");
		media.addWord("Charlie and the Chocolate Factory", "Book");
		media.addWord("Treasure Island", "Book");
		media.addWord("Harry Potter and the Sorcerers Stone", "Book");

		lancer.addWord("Sparri", "Faction");
		lancer.addWord("Albatross", "Faction");
		lancer.addWord("Ungratefuls", "Faction");
		lancer.addWord("Voladores", "Faction");
		lancer.addWord("Union", "Faction");
		lancer.addWord("Horizon Collective", "Faction");
		lancer.addWord("Mirrorsmoke Mercenary Company", "Faction");
		lancer.addWord("Karrakin Trade Baronies", "Faction");
		lancer.addWord("Second Committee", "Faction");
		lancer.addWord("Third Committee", "Faction");
		lancer.addWord("Blackbeard", "Frame");
		lancer.addWord("Drake", "Frame");
		lancer.addWord("Lancaster", "Frame");
		lancer.addWord("Nelson", "Frame");
		lancer.addWord("Raleigh", "Frame");
		lancer.addWord("Tortuga", "Frame");
		lancer.addWord("Vlad", "Frame");
		lancer.addWord("Caliban", "Frame");
		lancer.addWord("Zheng", "Frame");
		lancer.addWord("Black Witch", "Frame");
		lancer.addWord("Deaths Head", "Frame");
		lancer.addWord("Dusk Wing", "Frame");
		lancer.addWord("Metalmark", "Frame");
		lancer.addWord("Monarch", "Frame");
		lancer.addWord("Mourning Cloak", "Frame");
		lancer.addWord("Swallowtail", "Frame");
		lancer.addWord("White Witch", "Frame");
		lancer.addWord("Emperor", "Frame");
		lancer.addWord("Atlas", "Frame");
		lancer.addWord("Balor", "Frame");
		lancer.addWord("Goblin", "Frame");
		lancer.addWord("Gorgon", "Frame");
		lancer.addWord("Genghis", "Frame");
		lancer.addWord("Hydra", "Frame");
		lancer.addWord("Manticore", "Frame");
		lancer.addWord("Minotaur", "Frame");
		lancer.addWord("Pegasus", "Frame");
		lancer.addWord("Kobold", "Frame");
		lancer.addWord("Lich", "Frame");
		lancer.addWord("Iskander", "Frame");
		lancer.addWord("Saladin", "Frame");
		lancer.addWord("Tokugawa", "Frame");
		lancer.addWord("Sunzi", "Frame");
		lancer.addWord("Smith Shimano Corporation", "Manufacturer");
		lancer.addWord("Harrison Armory", "Manufacturer");
		lancer.addWord("General Massive Systems", "Manufacturer");
		lancer.addWord("Interplanetary Shipping NorthStar", "Manufacturer");
		lancer.addWord("Horus", "Manufacturer");
		lancer.addWord("Non Human Person", "Thing");
		lancer.addWord("Galsim", "Thing");
		lancer.addWord("Blinkspace", "Thing");
		lancer.addWord("Core Power", "Thing");
		lancer.addWord("The Hercynian Crisis", "Thing");

		this.dictionaries.add(videoGames);
		this.dictionaries.add(dnd);
		this.dictionaries.add(anime);
		this.dictionaries.add(media);
		this.dictionaries.add(lancer);
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