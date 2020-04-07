package server.events.gm.MapleScavengerHunt;


import net.server.Server;
import server.maps.MapleMap;
import server.events.gm.core.EventStep;
import tools.MaplePacketCreator;

public class EventDescriptionStep extends EventStep {
	public static final int MAP_EFFECT = 5120026;
	public static final String[] DESC = {
		"Welcome to Maple Scavenger Hunt!",
		"Bruce has discovered a new medicine that could cure Maya!",
		"There's only one problem however...",
		"We need you to gather the ingredients!",
		"Team up to a party of 6 members to divide up the work - Good Luck!!"
	};

	MapleMap map;

	public EventDescriptionStep(MapleMap map) {
		this.map = map;
	}

	// Impl abstract method
	protected void executeStep()  throws InterruptedException {
		for(String desc : DESC) {
			map.startMapEffect(desc, MAP_EFFECT, 7 * 1000);
			Thread.sleep(8 * 1000);
		}
	}
}

