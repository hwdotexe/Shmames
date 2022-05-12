package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ShmamesService {
	/**
	 * Increments the tally for a given emote.
	 * @param b The Brain to tally the emote in.
	 * @param id The Emote ID.
	 */
	public static void IncrementEmoteTally(Brain b, String id) {
		if (b.getEmoteStats().containsKey(id)) {
			b.getEmoteStats().put(id, b.getEmoteStats().get(id) + 1);
		} else {
			b.getEmoteStats().put(id, 1);
		}
	}

	/**
	 * Checks whether the member complies with the setting's permission
	 * requirements, if applicable.
	 * @param setting The setting to check.
	 * @param member The user to check.
	 * @return A boolean representing whether the user complies.
	 */
	public static boolean CheckUserPermission(Guild server, BotSetting setting, Member member) {
		if(server != null) {
			if (setting.getType() == BotSettingType.ROLE) {
				if (App.IsDebug)
					return true;

				if (member != null) {
					// Always return true for administrators regardless of setting.
					if(member.hasPermission(Permission.ADMINISTRATOR)) {
						return true;
					}

					String roleString = setting.getAsString();

					// If the role requires administrator, make sure they are admin.
					if (roleString.equals("administrator")) {
						return member.hasPermission(Permission.ADMINISTRATOR);
					}

					Role role = setting.getAsRole(server);

					// Check if the user has the given role.
					if(server.getPublicRole().getIdLong() == role.getIdLong()) {
						return true;
					}

					return member.getRoles().contains(role);
				}
			}
		}

		return false;
	}

	/**
	 * Returns a list of other Guilds that the provided Guild is connected to via
	 * Shmames Family, excluding the Guild that was passed in.
	 * @param serverBrain The Brain of the Guild being passed in.
	 * @param server The Guild to search with.
	 * @return A list of connected Guilds, exclusively.
	 */
	public static List<Guild> GetConnectedFamilyGuilds(Brain serverBrain, Guild server) {
		List<Guild> guilds = new ArrayList<>();

		for(String fid : serverBrain.getFamilies()){
			Family f = App.Shmames.getStorageService().getMotherBrain().getFamilyByID(fid);

			for(long mg : f.getMemberGuilds()) {
				if(mg != server.getIdLong()) {
					Guild familyGuild = App.Shmames.getJDA().getGuildById(mg);

					if(familyGuild != null) {
						guilds.add(familyGuild);
					} else {
						// The guild came back null, so remove it from the Family.
						f.getMemberGuilds().remove(mg);
					}
				}
			}
		}

		return guilds;
	}

	/**
	 * Returns an Emote from the server's Family, if one can be found.
	 * @param emoteName The name of the emote to search for.
	 * @param serverBrain The server's brain to use for the search.
	 * @param server The server.
	 * @return An Emote, or null if one cannot be found.
	 */
	public static Emote GetFamilyEmote(String emoteName, Brain serverBrain, Guild server) {
		// Check the server passed in first.
		List<Emote> serverEmotes = server.getEmotesByName(emoteName, true);

		if (serverEmotes.size() > 0) {
			return serverEmotes.get(0);
		} else {
			// Check the rest of the family.
			List<Guild> familyServers = GetConnectedFamilyGuilds(serverBrain, server);

			for(Guild familyServer : familyServers) {
				List<Emote> familyEmotes = familyServer.getEmotesByName(emoteName, true);

				if (familyEmotes.size() > 0) {
					return familyEmotes.get(0);
				}
			}

			return null;
		}
	}
}
