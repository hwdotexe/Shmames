package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.storage.Family;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FamilyCmd implements ICommand {
	@Override
	public String getDescription() {
		return "Manage your "+ Shmames.getBotName() + " Family";
	}
	
	@Override
	public String getUsage() {
		return "family <create|add|view|remove> [name|code]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((create)|(add)|(view)|(remove))( [a-z0-9\\-]+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		//g1 - command
		//g6 - args

		// The Family object has an ID
		// Each server will record a list of IDs it belongs to

		if(m.find()){
			switch(m.group(1).toLowerCase()){
				case "create": // create famName
					if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
						if (m.group(6) != null) {
							String name = m.group(6).trim().toLowerCase();

							for (Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
								if (f.getFamilyOwner() == author.getIdLong()) {
									if (f.getFamName().equals(name)) {
										return "You already own a family with that name! Please choose a different name.";
									}
								}
							}

							Family newFam = new Family(UUID.randomUUID().toString(), name, author.getIdLong());
							newFam.addToFamily(message.getGuild());

							// Add the Family to the system
							Shmames.getBrains().getMotherBrain().getServerFamilies().add(newFam);
							Shmames.getBrains().getBrain(message.getGuild().getId()).getFamilies().add(newFam.getFamID());

							return "The Family was created! Now let's go add other servers!.";
						} else {
							return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
						}
					}else{
						return Errors.NO_PERMISSION_USER;
					}
				case "add": // add famName|code
					if (m.group(6) != null) {
						String arg = m.group(6).trim().toLowerCase();
						Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

						for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()){
							if(f.getFamilyOwner()==author.getIdLong() && f.getFamName().equalsIgnoreCase(arg)){
								author.openPrivateChannel().queue((c) -> c.sendMessage("**Join Code for "+f.getFamName()+"**\n" +
										"`"+f.getNewJoinCode()+"`\n" +
										"_Use this one-time code to join a server to the Family._").queue());
								return "Sent a new Join Code to your DMs!";
							}

							if(f.validateCode(arg)){
								f.clearCode();

								if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
									if(!b.getFamilies().contains(f.getFamID())){
										f.addToFamily(message.getGuild());
										b.getFamilies().add(f.getFamID());

										return "Added **"+message.getGuild().getName()+"** to the **"+f.getFamName()+"** Family!";
									}else{
										return "This server is already a member of **"+f.getFamName()+"**!\n(The code has been invalidated for security purposes)";
									}
								}else{
									return Errors.NO_PERMISSION_USER+"\n(The code has been invalidated for security purposes)";
								}
							}
						}

						return "Invalid Family name or Join Code!";
					}else{
						return "Use `add <family name>` to get a new Join Code.\n" +
								"Use `add <join code>` to join a server to a Family.";
					}
				case "view": // [famName]
					if (m.group(6) != null) {
						String name = m.group(6).trim().toLowerCase();

						for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()) {
							if (f.getFamilyOwner() == author.getIdLong() && f.getFamName().equalsIgnoreCase(name)) {
								StringBuilder sb = new StringBuilder();
								sb.append("**The \"");
								sb.append(f.getFamName());
								sb.append("\" Family contains the following servers:**");

								for(long g : f.getMemberGuilds()){
									Guild guild = Shmames.getJDA().getGuildById(g);

									// Quick null check!
									if(guild == null){
										f.getMemberGuilds().remove(g);
										break;
									}

									sb.append("\n> ");
									sb.append(guild.getName());
								}

								return sb.toString();
							}
						}

						return Errors.NOT_FOUND;
					}else{
						if(message.getMember().hasPermission(Permission.ADMINISTRATOR) || Shmames.isDebug) {
							Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

							StringBuilder sb = new StringBuilder();
							sb.append("**This server belongs to the following families:**");

							for(String id : b.getFamilies()){
								for(Family f : Shmames.getBrains().getMotherBrain().getServerFamilies()){
									if(f.getFamID().equals(id)){
										sb.append("\n> ");
										sb.append(f.getFamName());
										break;
									}
								}
							}

							return sb.toString();
						}else{
							return Errors.NO_PERMISSION_USER;
						}
					}
				case "remove":
					// If no args, assume this server. User must be an Admin.
					// If an arg, assume another server. User must be the family owner.
					return "Command under construction!";
				default:
					return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}

		return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"family"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
