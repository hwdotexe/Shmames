package tech.hadenw.shmamesbot;

public enum TriggerType {
	LOVE,
	RONALD,
	HELLO,
	GOODBYE,
	RANDOM,
	REACT,
	COMMAND;
	
	public static TriggerType byName(String name) {
		for(TriggerType names : TriggerType.values()) {
			if(names.name().equalsIgnoreCase(name)) {
				return names;
			}
		}
		
		return null;
	}
}
