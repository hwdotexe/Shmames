package tech.hadenw.shmamesbot.brain;

import tech.hadenw.shmamesbot.TriggerType;

public class Response {
	private TriggerType type;
	private String response;
	
	public Response(TriggerType t, String r) {
		type = t;
		response = r;
	}
	
	public TriggerType getType() {
		return type;
	}
	
	public String getResponse() {
		return response;
	}
}
