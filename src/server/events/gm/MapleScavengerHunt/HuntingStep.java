package server.events.gm.MapleScavengerHunt;

import java.awt.Point;
import net.server.Server;
import server.maps.MapleMap;
import server.life.MapleNPC;
import server.life.MapleLifeFactory;
import server.events.gm.core.EventStep;
import server.events.gm.MapleEvent;
import tools.MaplePacketCreator;

public class HuntingStep extends EventStep {
    class SpawnableNPC {
        public final int id;
        public Point pos;
        public SpawnableNPC(int id, int x, int y) {
            this.id = id;
            this.pos = new Point(x, y);
        }
    }

    public static final int NPC_GUIDE_ID = 9950000;
    public static final int NPC_GUIDE_X_POS = 75;
    public static final int NPC_GUIDE_Y_POS = 38;
    public static final String HOST_NPC_SCRIPT = "maple_scavenger_hunt";
    public static final String EVENT_NOTICE = "[EVENT] - %d minutes remaining for Scavenger Hunt!";
    public static final int NOTICE_TIMES = 6;

	MapleMap map;
	MapleEvent event;

	public HuntingStep(MapleMap map, MapleEvent event) {
		this.map = map;
		this.event = event;
	}

	// Impl abstract method
	protected void executeStep()  throws InterruptedException {
		//Spawn NPC for event
		MapleNPC npc = spawnHostNPC();
		for(int i = NOTICE_TIMES; i > 0; i--) {
			Server.getInstance().broadcastMessage(MaplePacketCreator.serverNotice(6, String.format(EVENT_NOTICE, i*10)));
			Thread.sleep(60 * 10 * 1000);
		};
		this.map.destroyNPC(npc.getId());
	}

	private MapleNPC spawnHostNPC() {
		SpawnableNPC npcData = new SpawnableNPC(NPC_GUIDE_ID, NPC_GUIDE_X_POS, NPC_GUIDE_Y_POS);
		MapleNPC npc = MapleLifeFactory.getNPC(npcData.id, HOST_NPC_SCRIPT);
		if (npc != null) {
			npc.setPosition(npcData.pos);
			npc.setCy(npcData.pos.y);
			npc.setRx0(npcData.pos.x + 50);
			npc.setRx1(npcData.pos.x - 50);
			npc.setFh(this.map.getFootholds().findBelow(npcData.pos).getId());
			this.map.addMapObject(npc);
			this.map.broadcastMessage(MaplePacketCreator.spawnNPC(npc));
		}

		return npc;
	}
}

