package com.hadenwatne.shmames.models;

import com.google.gson.annotations.SerializedName;
import com.hadenwatne.shmames.enums.ResponseType;
import com.hadenwatne.shmames.enums.TriggerType;

public class Response {
	@SerializedName(value = "triggerType", alternate = "type")
	private TriggerType triggerType;
	private String response;
	private ResponseType responseType;
	
	public Response(TriggerType triggerType, String response, ResponseType responseType) {
		this.triggerType = triggerType;
		this.response = response;
		this.responseType = responseType;
	}
	
	public TriggerType getTriggerType() {
		return triggerType;
	}

	public ResponseType getResponseType() {
		if(this.responseType == null) {
			this.responseType = ResponseType.TEXT;
		}

		return responseType;
	}

	public String getResponse() {
		return response;
	}
}
