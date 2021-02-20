package com.hadenwatne.shmames.storage;

import com.hadenwatne.shmames.TriggerType;

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

	// TODO part of a conversion effort.
	public void setType(TriggerType t){
		type=t;
	}
}
