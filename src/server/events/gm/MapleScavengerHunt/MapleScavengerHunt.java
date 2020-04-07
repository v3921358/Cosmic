package server.events.gm.MapleScavengerHunt;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;
import java.util.stream.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import constants.ExpTable;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;

import client.MapleCharacter;

import server.TimerManager;
import server.maps.MapleMap;
import server.maps.MapleMapFactory;
import server.life.MapleMonster;

import server.events.gm.MapleEvent;

import tools.Randomizer;
import tools.MaplePacketCreator;
import tools.DatabaseConnection;
/**
 *
 * @author Benjixd
 */
public final class MapleScavengerHunt extends MapleEvent {
    protected class Score {
        Boolean claimed;
        Integer score;

        public Score(Integer score) {
            this.score = score;
            this.claimed = false;
        }

        int getScore() {
            return score;
        }

        Boolean hasClaimed() {
            return claimed;
        }

        int claim() {
            if(!claimed) {
                return score;
            } else {
                return 0;
            }
        }

        void setClaimed(Boolean val) {
            claimed = val;
        }
    }

    public static final int MAP_EFFECT = 5120009;
    public static final int LOADING_MAP_ID = 100000001;
    public static final int SCAVENGER_ITEMS = 6;
    public static final String QUERY_HIGHEST_DROPRATE_ITEM = "SELECT itemid FROM drop_data WHERE dropperid = ? AND questid = 0 AND itemid != 0 AND chance = (SELECT MAX(chance) FROM drop_data WHERE dropperid = ? AND questid = 0 AND itemid != 0)";
    public static final String CONGRATS_TEAM = "Congratulations to %s and team for finishing Maple Scavenger Hunt!!";
    Map<Integer, Score> participated = new HashMap<Integer, Score>();
    Set<Integer> scavengerItems = new HashSet<Integer>();
    MapleMapFactory mapFactory;


    public MapleScavengerHunt(MapleMapFactory mmf, MapleMap map, int limit) {
        super(map, limit);
        this.mapFactory = mmf;
    }

    public void startEvent() {
        // Event Stages
        clearParticipated();
        randomizeScavengerItems();
        System.out.println(scavengerItems);
        runner.register(new EventBeginStep(map, this));
        runner.register(new EventDescriptionStep(map));
        runner.register(new HuntingStep(map, this));
        runner.register(new EventOverStep(map, this));
        
        // Start Event
        TimerManager.getInstance().schedule(runner, 0);
    }

    public List<Integer> getScavengerItems() {
        return Collections.unmodifiableList(scavengerItems.stream().collect(Collectors.toList()));
    }

    // Public methods
    public boolean checkPartyParticipation(MapleParty party) {
        for(MaplePartyCharacter mpc : party.getMembers()) {
            if(participated.containsKey(mpc.getId())) {
                return false;
            }
        }

        return true;
    }

    public void completePartyParticipation(MapleParty party, int numCleared) {
        for(MaplePartyCharacter mpc : party.getMembers()) {
            participated.put(mpc.getId(), new Score(numCleared));
        }
    }

    public void CongratulateTeam(MapleParty party) {
        map.startMapEffect(String.format(CONGRATS_TEAM, party.getLeader().getName()), MAP_EFFECT, 7 * 1000);
    }

    public Boolean hasPlayerCompletedEvent(MapleCharacter mc) {
        return participated.containsKey(mc.getId());
    }

    public long getExpReward(MapleCharacter mc) {
        Score s = participated.get(mc.getId());
        if(s != null) {
            return getExpForParticipant(mc, s.claim());
        } else {
            return 0;
        }
    }

    public Integer getItemRewardCount(MapleCharacter mc) {
        Score s = participated.get(mc.getId());
        if(s != null) {
            return getRewardAmt(s.claim());
        } else {
            return 0;
        }
    }

    public void completeClaimingRewards(MapleCharacter mc) {
        participated.get(mc.getId()).setClaimed(true);
    }

    public Boolean checkPlayerClaimedRewards(MapleCharacter mc) {
        Score s = participated.get(mc.getId());
        if(s != null) {
            return s.hasClaimed();
        }
        return false;
    }

    // Private methods
    private void clearParticipated() {
        participated.clear();
    }

    private MapleMap getRandomValidMap(List<MapleData> data) {
        // TODO: Fix hard coded map check to include areas: Victoria Island, Ossyria, Elin, Singapore, MasteriaGL
        String validStreets[] = {"victoria", "ossyria", "elin", "singapore", "MasteriaGL", "jp"};
        Set<String> valid = new HashSet<>(Arrays.asList(validStreets));

        while(true) {
            MapleData random = data.get(ThreadLocalRandom.current().nextInt(0, data.size()));
            if(valid.contains(random.getName())) {
                MapleData randomMapData = random.getChildren().get(ThreadLocalRandom.current().nextInt(0, random.getChildren().size()));
                try {
                    MapleMap randomMap = mapFactory.getMap(Integer.parseInt(randomMapData.getName()));
                    if( randomMap.getForcedReturnId() == 999999999 && randomMap.countMonsters() > 0) {
                        return randomMap;
                    }    
                } catch (NullPointerException e) {
                    // Found maps that don't exist in game
                }
            }
        }
    }

    private void randomizeScavengerItems() {
        List<MapleData> availableMaps = mapFactory.getMapNames();

        scavengerItems.clear();
        while(scavengerItems.size() < SCAVENGER_ITEMS) {
            MapleMap randomMap = getRandomValidMap(availableMaps);
            List<MapleMonster> monsters = randomMap.getMonsters();
            MapleMonster randomMonster = monsters.get(ThreadLocalRandom.current().nextInt(0, monsters.size()));
            try(Connection con = DatabaseConnection.getConnection()){
                try(PreparedStatement ps = con.prepareStatement(QUERY_HIGHEST_DROPRATE_ITEM)){
                    ps.setInt(1, randomMonster.getId());
                    ps.setInt(2, randomMonster.getId());
                    try(ResultSet rs = ps.executeQuery()) {
                        if(rs.next()) {
                            scavengerItems.add(rs.getInt("itemid"));
                        }
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }    
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private long getExpForParticipant(MapleCharacter chr, int numCleared) {
        int requiredExp = ExpTable.getExpNeededForLevel(chr.getLevel());
        // Modified Sigmoid Exp % curve
        return numCleared * Math.round(
            requiredExp * (2 / (1 + Math.exp(
                2.5 * ((double)chr.getLevel())/ExpTable.MAX_LEVEL
            )))
        ) / 3;
    }

    private int getRewardAmt(int numCleared) {
        int amount_to_gain = 0;
        if(numCleared == 6) {
            amount_to_gain = 10;
        } else if(numCleared == 5) {
            amount_to_gain = 5;
        } else if(numCleared == 4) {
            amount_to_gain = 3;
        } else if(numCleared == 3) {
            amount_to_gain = 2;
        } else if(numCleared == 2) {
            amount_to_gain = 1;
        }

        return amount_to_gain;
    }
}
