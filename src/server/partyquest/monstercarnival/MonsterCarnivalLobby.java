package server.partyquest.monstercarnival;

import java.util.concurrent.ScheduledFuture;

import client.MapleClient;
import client.MapleCharacter;
import server.TimerManager;
import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnival.CPQType;
import server.partyquest.monstercarnival.MonsterCarnivalManager;
import server.maps.MapleMap;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import scripting.npc.NPCScriptManager;
import tools.MaplePacketCreator;

public class MonsterCarnivalLobby {
    public static final int LOBBY_WAIT_TIMEOUT = 3 * 60; //in seconds
    public static final int[] NPC_CHAT_ASSISTANT = new int[] { 2042003, 2042004, 2042008, 2042009 }; // Assistants
    public static final String CHALLENGE_CONVERSATION = "cpq_challenge";
    
    private ScheduledFuture<?> timer;
    private MapleParty initiator;
    private MapleParty challenger;
    private MapleMap map;
    private MapleMap returnTo;
    private CPQType type;
    private boolean isWaiting;

    public MonsterCarnivalLobby(MapleParty init, MapleMap map, MapleMap returnTo, CPQType type) {
        this.initiator = init;
        this.map = map;
        this.returnTo = returnTo;
        this.isWaiting = true;
        this.type = type;

        warpToLobby(initiator);
        map.broadcastMessage(MaplePacketCreator.getClock(LOBBY_WAIT_TIMEOUT));
        timer = startLobbyCountdown();
    }

    public MapleParty getInitiator() {
        return initiator;
    }

    public MapleParty getChallenger() {
        return challenger;
    }

    public MapleMap getMap() {
        return map;
    }

    public CPQType getCPQType() {
        return type;
    }

    public boolean getIsWaiting() {
        return isWaiting;
    }

    public void warpToLobby(MapleParty p) {
        MapleCharacter leader = p.getLeader().getPlayer();

        for(MaplePartyCharacter mpc : p.getMembers()) {
            MapleCharacter mc = mpc.getPlayer();
            if(mc.getMap() == leader.getMap()) {
                mc.changeMap(map);
            }
        }
    }

    public boolean tryAddChallenger(MapleParty party) {
        if(challenger != null || !isWaiting) {
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

    public boolean ackChallenger(int startDelay) {
        if(challenger != null && challenger.getLeader() != null && isWaiting) {
            warpToLobby(challenger);
            prePQDelay(startDelay);
            return true;
        }

        return false;
    }

    public void denyChallenger() {
        NPCScriptManager.getInstance().start(
            challenger.getLeader().getPlayer().getClient(), 
            getAssistantInMap(), 
            "cpq_challenge_deny", 
            null);

        challenger = null;
    }

    public boolean healthCheck() {
        if(initiator.getLeader() == null || 
            initiator.getLeader().getPlayer().getMap() != map || 
            !initiator.getLeader().isOnline() ||
            !isWaiting) {
            isWaiting = false;
            kickAll();
            return false;
        }
        return true;
    }

    public void prePQDelay(int startDelay) {
        map.broadcastMessage(MaplePacketCreator.getClock(startDelay));
    }

    public void endLobby() {
        if(timer != null && !timer.isDone()) {
            timer.cancel(true);
            timer = null;
        }
        isWaiting = false;
    }

    public boolean checkReady() {
        // Check both parties present and leaders in Lobby map
        if(initiator != null && challenger != null) {
            if(initiator.getLeader() != null && challenger.getLeader() != null) {
                return initiator.getLeader().getPlayer().getMap() == map && challenger.getLeader().getPlayer().getMap() == map;
            }
        }
        return false;
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
        }, LOBBY_WAIT_TIMEOUT * 1000);
    }

    private void kickAll() {
        for(MapleCharacter chr : map.getAllPlayers()) {
            chr.changeMap(returnTo);
        }
    }
}