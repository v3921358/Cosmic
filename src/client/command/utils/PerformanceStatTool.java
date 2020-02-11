package client.command.utils;

import client.MapleCharacter;

import net.server.channel.Channel;
import net.server.world.World;
import server.TimerManager;
import scripting.event.EventScriptManager;
import scripting.event.EventManager;


public class PerformanceStatTool {
	public static void PrintTimerManagerTasksToPlayer(MapleCharacter player) {
		long active_tasks = TimerManager.getInstance().getActiveCount();
		int queued_tasks = TimerManager.getInstance().getQueuedTasks();
		long completed_tasks = TimerManager.getInstance().getCompletedTaskCount();
		long total_tasks = TimerManager.getInstance().getTaskCount();

		player.message(String.format("Active Tasks:\t%d", active_tasks));
		player.message(String.format("Queued Tasks:\t%d", queued_tasks));
		player.message(String.format("Completed Tasks:\t%d", completed_tasks));
		player.message(String.format("Total Tasks:\t%d", total_tasks));
	}

	public static void PrintEventInstanceTasksToPlayer(MapleCharacter player) {
		for(Channel c : player.getClient().getWorldServer().getChannels()) {
			EventScriptManager em = c.getEventSM();

			player.message(String.format("Channel %d", c.getId()));
			player.message("======");
			player.message(String.format("Workers:\t%d", em.countEventWorkers()));
			player.message(String.format("Events Loaded:\t%d", em.countEvents()));
			player.message(String.format("Events Ready:\t%d", em.countEventReadyInstances()));
			player.message(String.format("Events Active:\t%d", em.countEventActiveInstances()));
		}		
	}
}