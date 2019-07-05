package tech.hadenw.shmamesbot.brain;

import tech.hadenw.shmamesbot.Shmames;

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
			value = "false";
			return false;
		case NUMBER:
			if(isNumber(v)) {
				if(Integer.parseInt(v) > -1) {
					value = v;
					return true;
				}
			}
			value = "0";
			return false;
		case CHANNEL:
			if(v.startsWith("#"))
				v = v.replace("#", ""); // Replace all occurances
			
			if(Shmames.getJDA().getGuildById(b.getGuildID()).getTextChannelsByName(v, true).size() == 1) {
				value = v.toLowerCase();
				return true;
			}
			
			v = "general";
			
			return false;
		case EMOTE:
			if(v.startsWith(":"))
				v = v.replace(":", ""); // Replace all occurances
			
			if(Shmames.getJDA().getGuildById(b.getGuildID()).getEmotesByName(v, true).size() == 1) {
				value = v.toLowerCase();
				return true;
			}
			
			v = "dedede";
			
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