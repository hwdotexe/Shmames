package com.hadenwatne.discordbot.storage;

public class ForumWeaponObj {
	private String itemName;
	private String itemLink;
	private String serverID;
	private int uses;
	
	public ForumWeaponObj(String i, String l, String id) {
		this.itemName = i;
		this.itemLink = l;
		this.serverID = id;
		this.uses = 0;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public String getItemLink() {
		return itemLink;
	}
	
	public void setItemLink(String newLink) {
		itemLink = newLink;
	}
	
	public String getServerID() {
		return serverID;
	}
	
	public int getUses() {
		return this.uses;
	}
	
	public void IncreaseUse() {
		this.uses += 1;
	}
}
