package server.events.gm.MapleScavengerHunt;

import net.server.Server;
import server.maps.MapleMap;
import server.events.gm.core.EventStep;
import server.events.gm.MapleEvent;
import tools.MaplePacketCreator;

public class HuntingStep extends EventStep {
	MapleMap map;
	MapleEvent event;

	public HuntingStep(MapleMap map, MapleEvent event) {
		this.map = map;
		this.event = event;
	}

	// Impl abstract method
	protected void executeStep()  throws InterruptedException {
		//Spawn NPC for event
	}
}

