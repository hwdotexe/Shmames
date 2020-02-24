package tech.hadenw.discordbot.storage;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import tech.hadenw.discordbot.Shmames;

import java.util.List;

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
			if(v.equalsIgnoreCase("administrator") || v.equalsIgnoreCase("everyone")) {
				value = v.toLowerCase();
				return true;
			}else {
				for(Role r : Shmames.getJDA().getGuildById(b.getGuildID()).getRoles()) {
					if(r.getName().equalsIgnoreCase(v)) {
						value = r.getName();
						return true;
					}
				}
				
				return false;
			}
		case CHANNEL:
			if(v.startsWith("#"))
				v = v.replace("#", ""); // Replace all occurrences

			List<TextChannel> tc = Shmames.getJDA().getGuildById(b.getGuildID()).getTextChannelsByName(v, true);
			
			if(tc.size() == 1) {
				value = tc.get(0).getId();
				return true;
			}
			
			return false;
		case EMOTE:
			if(v.startsWith(":"))
				v = v.replace(":", ""); // Replace all occurrences

			List<Emote> em = Shmames.getJDA().getGuildById(b.getGuildID()).getEmotesByName(v, true);

			if(em.size() == 1) {
				value = em.get(0).getId();
				return true;
			}
			
			return false;
		default:
			if(v.length() > 0) {
				value = v;
				return true;
			} else {
				value = null;
				return false;
			}
		}
	}
	
	private boolean isBoolean(String test) {
		if(test.toLowerCase().equals("true") || test.toLowerCase().equals("false"))
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
