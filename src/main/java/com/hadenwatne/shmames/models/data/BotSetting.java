package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.services.DataService;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

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
	
	public String getValue() {
		return value;
	}
	
	public boolean setValue(String v, Brain b) {
		switch(type) {
		case BOOLEAN:
			if(isBoolean(v)) {
				value = v;
				return true;
			}
			
			return false;
		case NUMBER:
			if(isNumber(v)) {
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
			}else {
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
	
	private boolean isBoolean(String test) {
		if(test.equalsIgnoreCase("true") || test.equalsIgnoreCase("false"))
			return true;
		
		return false;
	}
	
	private boolean isNumber(String test) {
		try {
			Integer.parseInt(test);
			return true;
		}catch(Exception e) {
			return false;
		}
	}
}
