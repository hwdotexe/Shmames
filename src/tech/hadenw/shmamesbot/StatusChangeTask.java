package tech.hadenw.shmamesbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import tech.hadenw.shmamesbot.brain.MotherBrain;

public class StatusChangeTask extends TimerTask{
	
	public StatusChangeTask() {
		Calendar c = Calendar.getInstance();
		Timer t = new Timer();
		
    	c.setTime(new Date());
    	
    	// Run this now, and then again every 24 hours
		t.schedule(this, c.getTime(), 86400000);
	}
	
	public void run() {
		MotherBrain mb = Shmames.getBrains().getMotherBrain();
		String action = Utils.randomItem(mb.getStatuses().keySet());
		GameType t = mb.getStatuses().get(action);
		Shmames.getJDA().getPresence().setGame(Game.of(t, action));
	}
}