package tech.hadenw.shmamesbot.brain;

public class ForumWeaponObj {
	private String itemName;
	private String itemLink;
	private String serverID;
	
	public ForumWeaponObj(String i, String l, String id) {
		this.itemName = i;
		this.itemLink = l;
		this.serverID = id;
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
}
