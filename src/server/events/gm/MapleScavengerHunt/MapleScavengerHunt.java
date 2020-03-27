package server.events.gm.MapleScavengerHunt;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;

import client.MapleCharacter;

import server.TimerManager;
import server.maps.MapleMap;

import server.events.gm.MapleEvent;

import tools.Randomizer;
import tools.MaplePacketCreator;
/**
 *
 * @author Benjixd
 */
public final class MapleScavengerHunt extends MapleEvent {
    public static final int LOADING_MAP_ID = 100000001;
    Set<Integer> participated = new HashSet<Integer>();
    Set<Integer> scavengerItems = new HashSet<Integer>();

    public MapleScavengerHunt(MapleMap map, int limit) {
        super(map, limit);
    }

    public void startEvent() {
        // Event Stages
        runner.register(new EventBeginStep(map, this));
        

        // Start Event
        TimerManager.getInstance().schedule(runner, 0);
    }

    public boolean tryCompletePartyParticipation(MapleParty party) {
        for(MaplePartyCharacter mpc : party.getMembers()) {
            if(participated.contains(mpc.getId())) {
                return false;
            }
        }

        for(MaplePartyCharacter mpc : party.getMembers()) {
            participated.add(mpc.getId());
        }
        return true;
    }
}
