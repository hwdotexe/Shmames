package tech.hadenw.shmamesbot.commands;

public class Reload implements ICommand {

	@Override
	public String getUsage() {
		return "reload";
	}

	@Override
	public String run(String args) {
		return "Test";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"reload"};
	}
}
