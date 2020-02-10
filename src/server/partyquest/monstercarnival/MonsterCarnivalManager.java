package server.partyquest.monstercarnival;

import java.util.concurrent.ScheduledFuture;
import java.util.Map;
import java.util.HashMap;

import client.MapleClient;
import server.TimerManager;
import server.partyquest.monstercarnival.MonsterCarnivalLobby;
import server.partyquest.MonsterCarnival;
import server.maps.MapleMap;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import scripting.npc.NPCScriptManager;

public class MonsterCarnivalManager {
    public static final int[] LOBBY_MAP_ID = new int[] { 980000100, 980000200, 980000300, 980000400, 980000500, 980000600 };
    public static final int[] BATTLEFIELD_MAP_ID = new int[] { 980000101, 980000201, 980000301, 980000401, 980000501, 980000601 };

    private int channel;
    private Map<Integer, MonsterCarnivalLobby> lobbies = new HashMap<>();
    private Map<Integer, MonsterCarnival> sessions = new HashMap<>();

    public MonsterCarnivalManager(int channel) {
        this.channel = channel;
    }
}