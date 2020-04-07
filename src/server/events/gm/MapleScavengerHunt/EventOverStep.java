
package server.events.gm.MapleScavengerHunt;

import java.util.List;
import java.util.ArrayList;

import net.server.Server;
import client.MapleCharacter;
import server.maps.MapleMap;
import server.events.gm.core.EventStep;
import server.events.gm.MapleEvent;
import tools.MaplePacketCreator;

public class EventOverStep extends EventStep {
	public static final int MAP_EFFECT = 5120009;
	public static final String EVENT_NOTICE = "[EVENT] - Scavenger Hunt is now over!!";
	public static final String[] DESC = {
		"Thank you for participating!",
		"We hope to see you again!!"
	};

	MapleEvent event;
	MapleMap map;
	MapleMap exitMap;

	public EventOverStep(MapleMap map, MapleEvent event) {
		this.map = map;
		this.event = event;
	}

	// Impl abstract method
	protected void executeStep()  throws InterruptedException {
		event.closeEntry();
		Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, EVENT_NOTICE));
		for(String desc : DESC) {
			map.startMapEffect(desc, MAP_EFFECT, 7 * 1000);
			Thread.sleep(8 * 1000);
		}
	}
}