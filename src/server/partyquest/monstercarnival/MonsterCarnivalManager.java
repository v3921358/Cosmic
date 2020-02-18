package server.partyquest.monstercarnival;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.ScheduledFuture;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import client.MapleClient;
import server.TimerManager;
import server.partyquest.monstercarnival.MonsterCarnivalLobby;
import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnival.CPQType;
import server.maps.MapleMap;
import net.server.channel.Channel;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import scripting.npc.NPCScriptManager;

public class MonsterCarnivalManager {
    public static final int DELAY_START_CPQ_FROM_LOBBY = 10; //in seconds
    public static final int CPQ1_OFFICE_MAP_ID = 980000000;
    public static final int CPQ2_OFFICE_MAP_ID = 980030000;
    public static final int[] CPQ1_LOBBY_MAP_ID = new int[] {
        980000100,
        980000200,
        980000300,
        980000400,
        980000500,
        980000600
    };
    public static final int[] CPQ2_LOBBY_MAP_ID = new int[] {
        980031000,
        980032000,
        980033000
    };
    public static final int[] CPQ1_BATTLEFIELD_MAP_ID = new int[] {
        980000101,
        980000201,
        980000301,
        980000401,
        980000501,
        980000601
    };
    public static final int[] CPQ2_BATTLEFIELD_MAP_ID = new int[] {
        980031100,
        980032100,
        980033100
    };

    private Channel channel;

    private Set<MapleParty> waitingForSession = new HashSet<>();
    private Map <Integer, MonsterCarnivalLobby> lobbies = new HashMap <> ();
    private Map <Integer, MonsterCarnival> sessions = new HashMap <> ();
    private ReadWriteLock lobbyLock = new ReentrantReadWriteLock();
    private ReadWriteLock sessionLock = new ReentrantReadWriteLock();

    public MonsterCarnivalManager(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public boolean isWaitingForSession(MapleParty party) {
        return waitingForSession.contains(party);
    }

    public List<Integer> getAvailableMaps(CPQType type) {
        List<Integer> openMaps = new ArrayList<>();

        if(type == CPQType.CPQ1) {
            for(int i = 0; i < CPQ1_LOBBY_MAP_ID.length; i++) {
                healthCheckLobby(CPQ1_LOBBY_MAP_ID[i]);
                healthCheckPQ(CPQ1_BATTLEFIELD_MAP_ID[i]);

                if(!sessions.containsKey(CPQ1_BATTLEFIELD_MAP_ID[i]) &&
                    (!lobbies.containsKey(CPQ1_LOBBY_MAP_ID[i]) || lobbies.get(CPQ1_LOBBY_MAP_ID[i]).getChallenger() == null)) {
                    openMaps.add(CPQ1_LOBBY_MAP_ID[i]);
                }
            }
        }
        else if(type == CPQType.CPQ2) {
            for(int i = 0; i < CPQ2_LOBBY_MAP_ID.length; i++) {
                healthCheckLobby(CPQ2_LOBBY_MAP_ID[i]);
                healthCheckPQ(CPQ2_BATTLEFIELD_MAP_ID[i]);

                if(!sessions.containsKey(CPQ2_BATTLEFIELD_MAP_ID[i]) &&
                    (!lobbies.containsKey(CPQ2_LOBBY_MAP_ID[i]) || lobbies.get(CPQ2_LOBBY_MAP_ID[i]).getChallenger() == null)) {
                    openMaps.add(CPQ2_LOBBY_MAP_ID[i]);
                }
            }   
        }

        return openMaps;
    }

    public MonsterCarnivalLobby getLobby(int mapId) {
        lobbyLock.readLock().lock();
        try {
            return lobbies.get(mapId);    
        } finally {
            lobbyLock.readLock().unlock();
        }
    }

    public boolean tryInitiateLobby(MapleParty party, int lobbyMapId, CPQType type) {
        int officeMapId = (type == CPQType.CPQ1 ? CPQ1_OFFICE_MAP_ID : CPQ2_OFFICE_MAP_ID);
        int sessionMapId = lobbyMapId + (type == CPQType.CPQ1? 1 : 100);

        lobbyLock.writeLock().lock();
        try {
            if (!lobbies.containsKey(lobbyMapId) && 
                !sessions.containsKey(sessionMapId) && 
                !waitingForSession.contains(party)) {
                MapleMap lobbyMap = channel.getMapFactory().getMap(lobbyMapId);
                MapleMap returnTo = channel.getMapFactory().getMap(officeMapId);
                lobbies.put(lobbyMapId, new MonsterCarnivalLobby(party, lobbyMap, returnTo, type));
                waitingForSession.add(party);
                return true;
            } else {
                return false;
            }
        } finally {
            lobbyLock.writeLock().unlock();
        }
    }

    public boolean tryJoinLobby(MapleParty party, int mapId) {
        lobbyLock.writeLock().lock();
        try {
            MonsterCarnivalLobby lobby = lobbies.get(mapId);
            if(lobby != null &&
                !waitingForSession.contains(party)) {
                if(lobby.tryAddChallenger(party)) {
                    waitingForSession.add(party);
                    return true;
                }
            }
        } finally {
            lobbyLock.writeLock().unlock();
        }

        return false;
    }

    public boolean ackJoinLobby(int mapId) {
        lobbyLock.readLock().lock();
        try {
            MonsterCarnivalLobby lobby = lobbies.get(mapId);
            if(lobby != null) {
                if(lobby.ackChallenger(DELAY_START_CPQ_FROM_LOBBY)) {
                    startCarnivalPQ(lobby);
                    return true;
                }
            }
        } finally {
            lobbyLock.readLock().unlock();
        }

        return false;
    }

    public void denyJoinLobby(int mapId) {
        lobbyLock.readLock().lock();
        try {
            MonsterCarnivalLobby lobby = lobbies.get(mapId);
            if(lobby != null) {
                waitingForSession.remove(lobby.getChallenger());
                lobby.denyChallenger();
            }
        } finally {
            lobbyLock.readLock().unlock();
        }
    }

    public MonsterCarnivalLobby endLobby(int mapId) {
        lobbyLock.writeLock().lock();
        try {
            MonsterCarnivalLobby lobby = lobbies.get(mapId);
            if(lobby.getInitiator() != null) {
                waitingForSession.remove(lobby.getInitiator());
            }
            if (lobby.getChallenger() != null) {
                waitingForSession.remove(lobby.getChallenger());
            }
            lobby.endLobby();
            lobbies.remove(mapId);
            return lobby;
        } finally {
            lobbyLock.writeLock().unlock();
        }
    }

    public void startCarnivalPQ(int lobbyMapId) {
        startCarnivalPQ(getLobby(lobbyMapId));
    }

    public void startCarnivalPQ(MonsterCarnivalLobby lobby) {
        int carnivalMapId = lobby.getMap().getId() + (lobby.getCPQType() == CPQType.CPQ1 ? 1 : 100);
        TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                endLobby(lobby.getMap().getId());
                lobbyLock.writeLock().lock();
                sessionLock.writeLock().lock();
                try {
                    if(lobby != null && lobby.checkReady()){
                        MapleParty p1 = lobby.getInitiator();
                        MapleParty p2 = lobby.getChallenger();
                        MapleMap battlefield = channel.getMapFactory().getMap(carnivalMapId);
                        sessions.put(carnivalMapId, new MonsterCarnival(p1, p2, battlefield, lobby.getCPQType()));
                    }
                } finally {
                    sessionLock.writeLock().unlock();
                    lobbyLock.writeLock().unlock();
                }
            }
        }, DELAY_START_CPQ_FROM_LOBBY * 1000);
    }

    public void endCarnivalPQ(int mapId) {
        sessionLock.writeLock().lock();
        try {
            sessions.remove(mapId);
        } finally {
            sessionLock.writeLock().unlock();
        }
    }

    public void healthCheckLobby(int mapId) {
        if(lobbies.containsKey(mapId)) {
            if(!lobbies.get(mapId).healthCheck()) {
                endLobby(mapId);
            }
        }
    }

    public void healthCheckPQ(int mapId) {
        if(sessions.containsKey(mapId)) {
            if(!sessions.get(mapId).healthCheck()) {
                endCarnivalPQ(mapId);
            }
        }
    }
}