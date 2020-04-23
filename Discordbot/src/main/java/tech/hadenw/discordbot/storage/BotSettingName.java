package tech.hadenw.discordbot.storage;

public enum BotSettingName {
	PIN_CHANNEL,
	DEV_ANNOUNCE_CHANNEL,
	MUTE_DEV_ANNOUNCES,
	PIN_POLLS,
	REMOVAL_EMOTE,
	REMOVAL_THRESHOLD,
	APPROVAL_THRESHOLD,
	APPROVAL_EMOTE,
	ALLOW_MODIFY,
	ALLOW_NICKNAME;

	public static boolean contains(String opt){
		for(BotSettingName v : BotSettingName.values()){
			if(v.toString().equalsIgnoreCase(opt)){
				return true;
			}
		}

		return false;
	}
}
