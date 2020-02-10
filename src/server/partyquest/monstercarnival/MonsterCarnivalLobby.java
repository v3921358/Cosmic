package server.partyquest.monstercarnival;

import java.util.concurrent.ScheduledFuture;

import client.MapleClient;
import client.MapleCharacter;
import server.TimerManager;
import server.partyquest.MonsterCarnival;
import server.maps.MapleMap;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import scripting.npc.NPCScriptManager;

public class MonsterCarnivalLobby {
    public static final int LOBBY_WAIT_TIMEOUT = 3 * 60 * 1000;
    public static final int[] NPC_CHAT_ASSISTANT = new int[] { 2042003, 2042004, 2042008, 2042009 }; // Assistants
    public static final String CHALLENGE_CONVERSATION = "cpq_challenge";
    
    private ScheduledFuture<?> timer;
    private MapleParty initiator;
    private MapleParty challenger;
    private MapleMap map;
    private MapleMap returnTo;
    private boolean isWaiting;

    public MonsterCarnivalLobby(MapleParty init, MapleMap map, MapleMap returnTo) {
        this.initiator = init;
        this.map = map;
        this.returnTo = returnTo;
        this.isWaiting = true;
        timer = startLobbyCountdown();
    }

    public MapleParty getInitiator() {
        return initiator;
    }

    public MapleParty getChallenger() {
        return challenger;
    }

    public boolean getIsWaiting() {
        return isWaiting;
    }

    public boolean tryAddChallenger(MapleParty party) {
        if(challenger != null) {
            return false;
        }
        
        challenger = party;
        NPCScriptManager.getInstance().start(
            initiator.getLeader().getPlayer().getClient(), 
            getAssistantInMap(), 
            "cpq_challenge", 
            null);

        return true;
    }

    public void resetChallenger() {
        challenger = null;
    }

    public boolean healthCheck() {
        if(initiator.getLeader() == null || initiator.getLeader().getPlayer().getMap() != map) {
            return false;
        }
        return true;
    }

    public void endLobby() {
        isWaiting = false;
        if(!timer.isDone()) {
            timer.cancel(true);
            timer = null;
        }
    }

    private int getAssistantInMap() {
        for(int npcId : NPC_CHAT_ASSISTANT) {
            if(map.containsNPC(npcId)) {
                return npcId;
            }
        }
        return NPC_CHAT_ASSISTANT[0]; //default
    }

    private ScheduledFuture<?> startLobbyCountdown() {
        return TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                kickAll();
                endLobby();
            }
        }, LOBBY_WAIT_TIMEOUT);
    }

    private void kickAll() {
        for(MapleCharacter chr : map.getAllPlayers()) {
            chr.changeMap(returnTo);
        }
    }
}