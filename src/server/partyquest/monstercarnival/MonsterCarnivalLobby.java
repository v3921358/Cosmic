package server.partyquest.monstercarnival;

import java.util.List;
import java.util.ArrayList;

import client.MapleClient;
import server.partyquest.MonsterCarnival;
import server.maps.MapleMap;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;

public class MonsterCarnivalLobby {
    public static final int LOBBY_WAIT_TIMEOUT = 3 * 60 * 1000;
    
    private MapleParty initiator;
    private MapleParty challenger;
    private MapleMap map;

    public MonsterCarnivalLobby(MapleParty init) {
        this.initiator = init;
        startLobbyCountdown();
    }

    private void startLobbyCountdown() {

    }

    public void tryAddChallenger() {
        
    }

}