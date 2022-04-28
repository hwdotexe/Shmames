package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.services.DataService;
import net.dv8tion.jda.api.entities.*;

public class BotSetting {
	private BotSettingName name;
	private BotSettingType type;
	private String value;
	
	public BotSetting(BotSettingName n, BotSettingType t, String v) {
		name=n;
		type=t;
		value=v;
	}
	
	public BotSettingName getName() {
		return name;
	}
	
	public BotSettingType getType() {
		return type;
	}
	
	public String getAsString() {
		return value;
	}

	public boolean getAsBoolean() {
		if(DataService.IsBoolean(value)) {
			return Boolean.parseBoolean(value);
		}

		return false;
	}

	public int getAsNumber() {
		if(DataService.IsInteger(value)) {
			return Integer.parseInt(value);
		}

		return -1;
	}

	public Role getAsRole(Guild server) {
		if(DataService.IsLong(value)) {
			return server.getRoleById(value);
		}

		return null;
	}

	public TextChannel getAsChannel(Guild server) {
		if(DataService.IsLong(value)) {
			return server.getTextChannelById(value);
		}

		return null;
	}

	public Emote getAsEmote(Guild server) {
		if(DataService.IsLong(value)) {
			return server.getEmoteById(value);
		}

		return null;
	}
	
	public boolean setValue(String v, Brain b) {
		switch(type) {
		case BOOLEAN:
			if(DataService.IsBoolean(v)) {
				value = v;
				return true;
			}
			
			return false;
		case NUMBER:
			if(DataService.IsInteger(v)) {
				if(Integer.parseInt(v) > -1) {
					value = v;
					return true;
				}
			}
			
			return false;
		case ROLE:
			if(v.equalsIgnoreCase("administrator")) {
				value = v.toLowerCase();
				return true;
			} else {
				if(DataService.IsLong(v)) {
					Role role = App.Shmames.getJDA().getGuildById(b.getGuildID()).getRoleById(v);

					if (role != null) {
						value = v;
						return true;
					}
				}
				
				return false;
			}
		case CHANNEL:
			if(DataService.IsLong(v)) {
				TextChannel channel = App.Shmames.getJDA().getGuildById(b.getGuildID()).getTextChannelById(v);

				if (channel != null) {
					value = v;
					return true;
				}
			}
			
			return false;
		case EMOTE:
			if(DataService.IsLong(v)) {
				Emote emote = App.Shmames.getJDA().getGuildById(b.getGuildID()).getEmoteById(v);

				if (emote != null) {
					value = v;
					return true;
				}
			}
			
			return false;
		default:
			if(v.length() > 0) {
				value = v.toLowerCase();
				return true;
			} else {
				value = null;
				return false;
			}
		}
	}
}
