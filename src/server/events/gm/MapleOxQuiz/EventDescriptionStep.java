
package server.events.gm.MapleOxQuiz;


import net.server.Server;
import server.maps.MapleMap;
import server.events.gm.core.EventStep;
import tools.MaplePacketCreator;

public class EventDescriptionStep extends EventStep {
	public static final int MAP_EFFECT = 5120016;
	public static final String[] DESC = {
		"Welcome to OX Quiz!",
		"You'll all be given 20 True/False questions... with 10 seconds to answer each!",
		"Good luck out there - just remember, O for True, X for False!"
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

