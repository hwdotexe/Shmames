package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;

public class Storytime implements ICommand {
	private Lang lang;
	private final HashMap<String, String> stories = createList();

	@Override
	public String getDescription() {
		return "I tell you a high-quality story.";
	}
	
	@Override
	public String getUsage() {
		return "storytime";
	}

	@Override
	public String run(String args, User author, Message message) {
		String key = Utils.getRandomStringFromSet(stories.keySet());
		String[] story = Utils.splitString(stories.get(key), MessageEmbed.VALUE_MAX_LENGTH);
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle(key);
		embed.setColor(Color.PINK);

		for(String s : story) {
			embed.addField("", s, false);
		}

		message.getChannel().sendMessage(embed.build()).queue();

		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"storytime"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
	
	private HashMap<String, String> createList() {
		HashMap<String, String> stories = new HashMap<>();

		stories.put("_Little Red Riding Hood_ by James Finn Garner", "There was once a young person named Red Riding Hood who lived with her mother on the edge of a large wood. One day her mother asked her to take a basket of fresh fruit and mineral water to her grandmother’s house –not because this was womyn’s work, mind you, but because the deed was generous and helped engender a feeling of community. Furthermore, her grandmother was not sick, but rather was in full physical and mental health and was fully capable of taking care of herself as a mature adult.\n" +
				"\n" +
				"So Red Riding Hood set off with her basket through the woods. Many people believed that the forest was a foreboding and dangerous place and never set foot in it. Red Riding Hood, however, was confident enough in her own budding sexuality that such obvious Freudian imaginery did not intimidate her.\n" +
				"\n" +
				"On the way to Grandma’s house, Red Riding Hood was accosted by a wolf, who asked her what was in her basket. She replied, “Some healthful snacks for my grandmother, who is certainly capable of taking care of herself as a mature adult.”\n" +
				"\n" +
				"The wolf said, “You know, my dear, it isn’t safe for a little girl to walk through these woods alone.”\n" +
				"\n" +
				"Red Riding Hood said, “I find your sexist remark offensive in the extreme, but I will ignore it because of your traditional status as an outcast from society, the stress of which has caused you to develop your own, entirely valid, worldview. Now, if you’ll excuse me, I must be on my way.”\n" +
				"\n" +
				"Red Riding Hood walked on along the main path. But, because his status outside society has freed him from slavish adherence to linear, Western-style thought, the wolf knew a quicker route to Grandma’s house. He burst into the house and ate Grandma, an entirely valid course of action for a carnivore such as himself. Then, unhampered by rigid, traditionalist notions of what was masculine or feminine, he put on Grandma’s nightclothes and crawled into bed.\n" +
				"\n" +
				"Red Riding Hood entered the cottage and said, “Grandma, I have brought you some fat-free, sodium-free snacks to salute you in your role of a wise and nurturing matriarch.”\n" +
				"\n" +
				"From the bed, the wolf said softly, “Come closer, child, so that I might see you.”\n" +
				"\n" +
				"Red Riding Hood said, “Oh, I forgot you are as optically challenged as a bat. Grandma, what big eyes you have!”\n" +
				"\n" +
				"“They have seen much, and forgiven much, my dear.”\n" +
				"\n" +
				"“Grandma, what a big nose you have –only relatively, of course, and certainly attractive in its own way.”\n" +
				"\n" +
				"“It has smelled much, and forgiven much, my dear.”\n" +
				"\n" +
				"“Grandma, what big teeth you have!”\n" +
				"The wolf said, “I am happy with who I am and what I am,” and leaped out of bed. He grabbed Red Riding Hood in his claws, intent on devouring her. Red Riding Hood screamed, not out of alarm at the wolf’s apparent tendency toward cross-dressing, but because of his willful invasion of her personal space.\n" +
				"\n" +
				"Her screams were heard by a passing woodchopper-person (or log-fuel technician, as he preferred to be called). When he burst into the cottage, he saw melee thereand tried to intervene. But as he raised his ax, Red Riding Hood and the wolf both stopped.\n" +
				"\n" +
				"“And just what do you think you’re doing?” asked Red Riding Hood.\n" +
				"\n" +
				"The woodchopper-person blinked and tried to answer, but no words came to him.\n" +
				"\n" +
				"“Bursting in here like a Neanderthal, trusting your weapon to do your thinking for you!” she exclaimed. “Sexist! Speciesist! How dare you assume that womyn and wolves can't solve their own problems without a man's help!”\n" +
				"When she heard Red Riding Hood's impassioned speech, Grandma jumped out of the Wolf's mouth, took the woodchopper-person's ax, and cut his head off. After this ordeal, Red Riding Hood, Grandma, and the Wolf felt a certain commonality of purpose. They decided to set up an alternative household based on mutual respect and cooperation, and they lived together in the woods happily ever after.\n");

		stories.put("_The Three Little Pigs_ by James Finn Garner", "Once there were three little pigs who lived together in mutual respect and in harmony with their environment. Using materials that were indigenous to the area, they each built a beautiful house. One pig built a house of straw, one a house of sticks, and one a house of dung, clay, and creeper vines shaped into bricks and baked in a small kiln. When they were finished, the pigs were satisfied with their work and settled back to live in peace and self-determination.\n" +
				"\n" +
				"But their idyll was soon shattered. One day, along came a big, bad wolf with expansionist ideas. He saw the pigs and grew very hungry in both a physical and an ideological sense. When the pigs saw the wolf, they ran into the house of straw. The wolf ran up to the house and banged on the door, shouting, “Little pigs, little pigs, let me in!”\n" +
				"\n" +
				"The pigs shouted back, “Your gunboat tactics hold no fear for pigs defending their homes and culture.”\n" +
				"\n" +
				"But the wolf wasn't to be denied what he thought was his manifest destiny. So he huffed and puffed and blew down the house of straw. The frightened pigs ran to the house of sticks, with the wolf in hot pursuit. Where the house of straw had stood, other wolves bought up the land and started a banana plantation.\n" +
				"\n" +
				"At the house of sticks, the wolf again banged on the door and shouted, “Little pigs, little pigs, let me in!”\n" +
				"\n" +
				"The little pigs shouted back, “Go to hell, you carnivorous, imperialistic oppressor!”\n" +
				"\n" +
				"At this, the wolf chuckled condescendingly. He thought to himself: “They are so childlike in their ways. It will be a shame to see them go, but progress cannot be stopped.”\n" +
				"\n" +
				"So the wolf huffed and puffed and blew down the house of sticks. The pigs ran to the house of bricks, with the wolf close at their heels. Where the house of sticks had stood, other wolves built a time-share condo resort complex for vacationing wolves, with each unit a fiberglass reconstruction of the house of sticks, as well as native curio shops, snorkeling, and dolphin shows.\n" +
				"\n" +
				"At the house of bricks, the wolf again banged on the door and shouted, “Little pigs, little pigs, let me in!”\n" +
				"\n" +
				"This time in response the little pigs sang songs of solidarity and wrote letters of protest to the United Nations.\n" +
				"\n" +
				"By now the wolf was getting angry at the pigs' refusal to see the situation from the carnivore’s point of view. So he huffed and puffed, and huffed and puffed, then grabbed his chest and fell over dead of a massive heart attack brought on by eating too many fatty foods.\n" +
				"\n" +
				"The three little pigs rejoiced that justice had triumphed and did a little dance around the corpse of the wolf. Their next step was to liberate their homeland. They gathered together a band of other pigs who had been forced off their lands. This new brigade of _porcinistas_ attacked the resort complex with machine guns and rocket launchers and slaughtered the cruel wolf oppressors, sending a clear signal to the rest of the hemisphere not to meddle in their internal affairs. Then the pigs set up a model socialist democracy with free education, universal health care, and affordable housing for everyone");

		stories.put("_The Frog Prince_ by James Finn Garner", "Once there was a young princess who, when she grew tired of beating her head against the male power structure at her castle, would relax by walking into the woods and sitting beside a small pond. There she would amuse herself by tossing her favorite golden ball up and down and pondering the role of the eco-feminist warrior in her era.\n" +
				"\n" +
				"One day, while she was envisioning the utopia that her queendom could become if womyn were in the positions of power, she dropped the ball, which rolled into the pond. The pond was so deep and murky she couldn't see where it had gone. She didn't cry, of course, but she made a mental note to be more careful next time.\n" +
				"\n" +
				"Suddenly she heard a voice say, “I can get your ball for you, princess.”\n" +
				"\n" +
				"She looked around, and saw the head of a frog popping above the surface of the pond. “No, no,” she said, “I would never enslave a member of another species to work for my selfish desires.”\n" +
				"\n" +
				"The frog said, “Well, what if we make a deal on a contingency basis? I'll get your ball for you if you do me a favor in return.”\n" +
				"\n" +
				"The princess gladly agreed to this most equitable arrangement. The frog dived under the water and soon emerged with the golden ball in his mouth. He spit the ball on the shore and said, “Now that I've done you a favor, I'd like to explore your views on physical attraction between the species.”\n" +
				"\n" +
				"The princess couldn't imagine what the frog was talking about. The frog continued, “You see, I am not really a frog at all. I'm really a man, but an evil sorcerer has cast a spell on me. While my frog form is no better or worse–only different –than my human form, I would so much like to be among people again. And the only thing that can break this spell is a kiss from a princess.”\n" +
				"\n" +
				"The princess thought for a moment about whether sexual harassment could take place between species, but her heart went out to the frog for his predicament. She bent down and kissed the frog on the forehead. Instantly the frog grew and changed. And there, standing in the water where the frog had been, was a man in a golf shirt and loud plaid pants–middle-aged, vertically challenged, and losing a little bit of hair on top.\n" +
				"\n" +
				"The princess was taken aback. “I'm sorry if this sounds a little classist,” she stammered, “but...what I mean to say is...don't sorcerers usually cast their spells on princes?”\n" +
				"\n" +
				"“Ordinarily, yes,” he said, “but this time the target was just an innocent businessman. You see, I'm a real estate developer, and the sorcerer thought I was cheating him in a property-line dispute. So he invited me out for a round of golf, and just as I was about to tee off, he transformed me. But my time as a frog wasn't wasted, you know. I've gotten to know every square inch of these woods, and I think it would be ideal for an office park/condo/resort complex. The location's great and the numbers crunch perfectly! The bank wouldn't lend any money to a frog, but now that I'm inhuman form again, they'll be eating out of my hand. Oh, will that be sweet! And let me tell you, this is going to be a big project! Just drain the pond, cut down about 80 percent of the trees, get easements for...”\n" +
				"\n" +
				"The frog developer was cut short when the princess shoved her golden ball back into his mouth. She then pushed him back underwater and held him there until he stopped thrashing. As she walked back to the castle, she marveled at the number of good deeds that a person could do in just one morning. And while someone might have noticed that the frog was gone, no one ever missed the real estate developer.\n");

		stories.put("_Goldilocks_ by James Finn Garner", "Through the thicket, across the river, and deep, deep in the woods, lived a family of bears–a Papa Bear, a Mama Bear, and a Baby Bear–and they all lived together anthropomorphically in a little cottage as a nuclear family. They were very sorry about this, of course, since the nuclear family has traditionally served to enslave womyn, instill a self-righteous moralism in its members, and imprint rigid notions of heterosexualist roles onto the next generation. Nevertheless, they tried to be happy and took steps to avoid these pitfalls, such as naming their offspring the non-gender-specific “Baby.”\n" +
				"\n" +
				"One day, in their little anthropomorphic cottage, they sat down to breakfast. Papa Bear had prepared big bowls of all-natural porridge for them to eat. But straight off the stove, the porridge was too thermally enhanced to eat. So they left their bowls to cool and took a walk to visit their animal neighbors.\n" +
				"\n" +
				"After the bears left, a melanin-impoverished young wommon emerged from the bushes and crept up to the cottage. Her name was Goldilocks, and she had been watching the bears for days. She was, you see, a biologist who specialized in the study of anthropomorphic bears. Atone time she had been a professor, but her aggressive, masculine approach to science–ripping off the thin veil of Nature, exposing its secrets, penetrating its essence, using it for her own selfish needs, and bragging about such violations in the letters columns of various magazines–had led to her dismissal.\n" +
				"\n" +
				"The rogue biologist had been watching the cottage for some time. Her intent was to collar the bears with radio transmitters and then follow them in their migratory and other life patterns, with an utter disregard for their personal (or rather, animal) privacy. With scientific espionage the only thing in mind, Goldilocks broke into the bears' cottage. In the kitchen, she laced the bowls of porridge with a tranquilizing potion. Then, in the bedroom, she rigged snares beneath the pillows of each bed. Her plan was to drug the bears and, when they stumbled into their bedroom to take a nap, lash radio collars to their necks as their heads hit the pillows.\n" +
				"\n" +
				"Goldilocks chortled and thought: “These bears will be my ticket to the top! I'll show those twerps at the university the kind of guts it takes to do real research!” She crouched in a corner of the bedroom and waited. And waited, and waited some more. But the bears took so long to come back from their walk that she fell asleep.\n" +
				"\n" +
				"When the bears finally came home, they sat down to eat breakfast. Then they stopped.\n" +
				"\n" +
				"Papa Bear asked, “Does your porridge smell...off, Mama?”\n" +
				"\n" +
				"Mama Bear replied, “Yes, it does. Does yours smell off, Baby?”\n" +
				"\n" +
				"Baby Bear said, “Yes, it does. It smells kind of chemical-y.”\n" +
				"\n" +
				"Suspicious, they rose from the table and went into the living room. Papa Bear sniffed. He asked, “Do you smell something else, Mama?”\n" +
				"\n" +
				"Mama Bear replied, “Yes, I do. Do you smell something else, Baby?”\n" +
				"\n" +
				"Baby Bear said, “Yes, I do. It smells musky and sweaty and not at all clean.”\n" +
				"\n" +
				"They moved into the bedroom with growing alarm. Papa Bear asked, “Do you see a snare and a radio collar under my pillow Mama?”\n" +
				"\n" +
				"Mama Bear replied, “Yes I do. Do you see a snare and a radio collar under my pillow, Baby?”\n" +
				"\n" +
				"Baby Bear said, “Yes I do, and I see the human who put them there!”\n" +
				"\n" +
				"Baby Bear pointed in the corner to where Goldilocks slept. The bears growled, and Goldilocks awoke with a start. She sprang up and tried to run, but Papa Bear caught her with a swing of his paw, and Mama Bear did the same. With Goldilocks now a mobility non possessor, Mama and Papa Bear set on her with fang and claw. They gobbled her up, and soon there was nothing left of the maverick biologist but a bit of yellow hair and a clipboard.\n" +
				"\n" +
				"Baby Bear watched with astonishment. When they were done, Baby Bear asked, “Mama, Papa, what have you done? I thought we were vegetarians.”\n" +
				"\n" +
				"Papa Bear burped. “We are,” he said, “but we're always ready to try new things. Flexibility is just one more benefit of being multicultural.”\n");

		return stories;
	}
}
