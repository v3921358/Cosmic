package server.partyquest;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.awt.Point;
import client.MapleCharacter;
import client.MapleDisease;
import constants.string.LanguageConstants;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import server.life.MapleMonster;
import server.life.MapleLifeFactory;
import server.life.SpawnPoint;
import server.TimerManager;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.maps.MapleReactorFactory;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import server.partyquest.monstercarnival.components.MonsterCarnivalMapComponent;
import server.partyquest.monstercarnival.components.MonsterCarnivalPlayerComponent;
import server.partyquest.monstercarnival.util.GuardianSpawnPoint;
import server.partyquest.monstercarnival.util.MonsterCarnivalMob;
import tools.MaplePacketCreator;

/**
 * @author Drago (Dragohe4rt)
 * @author Benjixd
 */
public class MonsterCarnival {
    public static final int RESPAWN_INTERVAL = 10000;
    public static final int DISEASE_HIT_CHANCE = 80;
    public static final int MAX_TEAM_GUARDIANS = 4;
    public static final int CPQ1_OUT_MAP_ID = 980000010;
    public static final int CPQ2_OUT_MAP_ID = 980030010;

    public static enum GuardianSpawnCode {
        SUCCESS,
        INVALID,
        CANNOT_GUARDIAN,
        MAX_COUNT_REACHED,
        ALREADY_EXISTS
    }

    public static enum Team {
        NONE(-1),
        RED(0),
        BLUE(1)
        ;

        public final int value;
        private Team(int val) {
            this.value = val;
        }
        public static Team getTeamFromInt(int val) {
            switch(val) {
                case 0:
                    return Team.RED;
                case 1:
                    return Team.BLUE;
                default:
                    return Team.NONE;
            }
        }
    }

    public static enum CPQType {
        CPQ1,
        CPQ2
    }

    private MapleParty p1, p2;
    private MapleMap map;
    private ScheduledFuture<?> timer, respawnTask;
    private long startTime = 0;
    private int summonsR = 0, summonsB = 0;
    private int redCP, blueCP, redTotalCP, blueTotalCP;
    private CPQType type;
    private Map<Integer, MCSkill> blueTeamBuffs = new HashMap<>();
    private Map<Integer, MCSkill> redTeamBuffs = new HashMap<>();
    private List<Point> takenSpawns = new LinkedList<>();
    private ReadWriteLock redCPLock = new ReentrantReadWriteLock();
    private ReadWriteLock blueCPLock = new ReentrantReadWriteLock();

    public MonsterCarnival(MapleParty p1, MapleParty p2, MapleMap map, CPQType type) {
        try {
            int redPortal = 0;
            int bluePortal = 0;

            this.type = type;
            this.p1 = p1;
            this.p2 = p2;
            this.map = map;
            this.startTime = System.currentTimeMillis() + 10 * 60 * 1000;

            this.map.getMCMapComponent().setMC(this);

            if (MonsterCarnivalMapComponent.isPurpleCPQMap(map.getId())) {
                redPortal = 2;
                bluePortal = 1;
            }

            // Initialize RED and Blue Team
            for(MaplePartyCharacter mpc : p1.getMembers()) {
                MapleCharacter mc = mpc.getPlayer();
                mc.initializeMCPlayerComponent(this, Team.RED);
                mc.changeMap(map, map.getPortal(redPortal));
                mc.dropMessage(6, LanguageConstants.getMessage(mc, LanguageConstants.CPQEntry));
            }

            for(MaplePartyCharacter mpc : p2.getMembers()) {
                MapleCharacter mc = mpc.getPlayer();
                mc.initializeMCPlayerComponent(this, Team.BLUE);
                mc.changeMap(map, map.getPortal(bluePortal));
                mc.dropMessage(6, LanguageConstants.getMessage(mc, LanguageConstants.CPQEntry));   
            }
            
            // thanks Atoot, Vcoc for noting double CPQ functional being sent to players in CPQ start
            timer = TimerManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    timeUp();
                }
            }, map.getMCMapComponent().getTimeDefault() * 1000); // thanks Atoot for noticing an irregular "event extended" issue here
            respawnTask = TimerManager.getInstance().register(new Runnable() {
                @Override
                public void run() {
                    respawn();
                }
            }, RESPAWN_INTERVAL);

            //Initialize CP
            setTotalCP(0, Team.RED);
            setTotalCP(0, Team.BLUE);
            setCP(0, Team.RED);
            setCP(0, Team.BLUE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void respawn() {
        map.respawn();
    }
    
    public void leftParty(int charid) {
        playerDisconnected(charid);
    }

    public void playerDisconnected(int charid) {
        boolean p1Exists = false;
        boolean p2Exists = false;

        for(MapleCharacter chr : map.getAllPlayers()) {
            if(p1.getMemberById(chr.getId()) != null) {
                p1Exists = true;
            }else if(p2.getMemberById(chr.getId()) != null) {
                p2Exists = true;
            }
        }

        map.broadcastMessage(MaplePacketCreator.serverNotice(1, "Player Disconnected."));

        if(!(p1Exists && p2Exists)) {
            earlyFinish();
        }
    }

    private void earlyFinish() {
        dispose(true);
    }

    public boolean canSummon(Team team) {
        if(team == Team.RED) {
            return summonsR < map.getMCMapComponent().getMaxMobs();
        } else if(team == Team.BLUE) {
            return summonsB < map.getMCMapComponent().getMaxMobs(); 
        } else {
            return false;
        }
    }

    public void addSummon(Team team) {
        if(team == Team.RED) {
            summonsR++;
        } else if(team == Team.BLUE) {
            summonsB++;
        }
    }

    public boolean trySummon(MonsterCarnivalMob mob, Team team) {
        // Cannot summon if requirements aren't met
        if(canSummon(team)) {
           addSummon(team); 
        }
        else {
            return false;
        }
        
        Point spawnPos = getRandomSP(team);
        MapleMonster monster = MapleLifeFactory.getMonster(mob.getId());
        monster.setPosition(spawnPos);
        
        map.addMonsterSpawn(monster, 1, team.value);
        map.addAllMonsterSpawn(monster, 1, team.value);        

        return true;
    }

    private Point getRandomSP(Team team) {
        if (takenSpawns.size() > 0) {
            for (SpawnPoint sp : map.getMonsterSpawn()) {
                for (Point pt : takenSpawns) {
                    if ((sp.getPosition().x == pt.x && sp.getPosition().y == pt.y) 
                        || (sp.getTeam() != team.value && !MonsterCarnivalMapComponent.isBlueCPQMap(map.getId()))) {
                        continue;
                    } else {
                        takenSpawns.add(pt);
                        return sp.getPosition();
                    }
                }
            }
        } else {
            for (SpawnPoint sp : map.getMonsterSpawn()) {
                if (sp.getTeam() == team.value || MonsterCarnivalMapComponent.isBlueCPQMap(map.getId())) {
                    takenSpawns.add(sp.getPosition());
                    return sp.getPosition();
                }
            }
        }
        return null;
    }

    public void applySkillToEnemiesOf(MCSkill skill, Team team) {
        MapleParty enemyParty = (team == Team.RED) ? p1 : p2;
        MapleDisease disease = skill.getDisease();

        if(skill.targetAll()) {
            for(MaplePartyCharacter mpc : enemyParty.getPartyMembers()) {
                if(ThreadLocalRandom.current().nextInt(0, 100) <= DISEASE_HIT_CHANCE) {
                    MapleCharacter mc = mpc.getPlayer();
                    if(mc != null && disease != null) {
                        mc.giveDebuff(disease, skill.getSkill());
                    }    
                }
            }
        } else {
            final int rand = ThreadLocalRandom.current().nextInt(0, enemyParty.getPartyMembers().size());
            MapleCharacter target = enemyParty.getPartyMembers().get(rand).getPlayer();
            if(target != null && disease != null) {
                target.giveDebuff(disease, skill.getSkill());
            }
        }
    }
    
    public boolean canGuardian(Team team) {
        int teamReactors = 0;
        for (MapleReactor react : map.getAllReactors()) {
            if (react.getName().substring(0, 1).contentEquals(Integer.toString(team.value))) {
                teamReactors += 1;
            }
        }
        
        return teamReactors < map.getMCMapComponent().getMaxReactors();

    }

    public GuardianSpawnCode trySpawnGuardian(MCSkill skill, Team team) {
        MonsterCarnivalMapComponent mcMap = this.map.getMCMapComponent();
        GuardianSpawnPoint pt = mcMap.getNextGuardianSpawnPoint(team);
        int reactorId = 9980000 + team.value;

        if(skill == null || pt == null) {
            return GuardianSpawnCode.INVALID;
        }

        if(!canGuardian(team)) {
            return GuardianSpawnCode.CANNOT_GUARDIAN;
        }

        if(team == Team.RED && redTeamBuffs.size() >= MAX_TEAM_GUARDIANS || 
            team == Team.BLUE && blueTeamBuffs.size() >= MAX_TEAM_GUARDIANS) {
            return GuardianSpawnCode.MAX_COUNT_REACHED;
        }

        if(team == Team.RED && redTeamBuffs.containsKey(skill.getId()) ||
            team == Team.BLUE && blueTeamBuffs.containsKey(skill.getId())) {
            return GuardianSpawnCode.ALREADY_EXISTS;
        }

        if (team == Team.RED) {
            redTeamBuffs.put(skill.getId(), skill);
        } else if (team == Team.BLUE) {
            blueTeamBuffs.put(skill.getId(), skill);
        }

        try {
            MapleReactor reactor = new MapleReactor(MapleReactorFactory.getReactorS(reactorId), reactorId);
            reactor.initializeMCReactorComponent(this);
            pt.setTaken(true);
            reactor.setPosition(pt.getPosition());
            reactor.resetReactorActions();
            reactor.getMCReactorComponent().setGuardian(pt);
            reactor.getMCReactorComponent().setTeam(team);
            reactor.getMCReactorComponent().setSkill(skill);
            map.spawnReactor(reactor);
            buffMonsters(team, skill);
            reactor.hitReactor(map.getAllPlayers().get(0).getClient());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return GuardianSpawnCode.SUCCESS;
    }

    public void applyBuff(MapleMonster monster) {
        if(monster.getTeam() == Team.RED.value) {
            for(MCSkill skill : redTeamBuffs.values()) {
                skill.getSkill().applyEffect(null, monster, false, null);
            }
        } else if(monster.getTeam() == Team.BLUE.value) {
            for(MCSkill skill : blueTeamBuffs.values()) {
                skill.getSkill().applyEffect(null, monster, false, null);
            }
        }
    }

    private void buffMonsters(Team team, MCSkill skill) {
        if (skill == null) return;
        
        for (MapleMonster mob : map.getMonsters()) {
            if (mob.getTeam() == team.value) {
                skill.getSkill().applyEffect(null, mob, false, null);
            }
        }
    }

    public void dispelAllMonsters(MCSkill skill, Team team) { //dispels all mobs, cpq
        if (skill != null) {
            for (MapleMonster mons : map.getMonsters()) {
                if(mons.getTeam() == team.value) {
                    mons.dispelSkill(skill.getSkill());
                }
            }
        }
        if (team == Team.RED) {
            redTeamBuffs.remove(skill);
        } else {
            blueTeamBuffs.remove(skill);
        }
    }

    protected void dispose() {
        dispose(false);
    }

    protected void dispose(boolean warpout) {
        Channel cs = map.getChannelServer();
        MapleMap out;
        if (type == CPQType.CPQ1) {
            out = cs.getMapFactory().getMap(CPQ1_OUT_MAP_ID);
        } else {
            out = cs.getMapFactory().getMap(CPQ2_OUT_MAP_ID);
        }

        for(MapleCharacter mc : map.getAllPlayers()) {
            if(mc != null)  {
                if(mc.getMCPlayerComponent() != null) {
                    mc.disposeMCPlayerComponent();    
                }
                if(warpout) {
                    mc.changeMap(out, out.getPortal(0));
                }
            }
        }

        redTotalCP = 0;
        blueTotalCP = 0;
        p1 = null;
        p2 = null;
        map.dispose();
        map = null;

        if (this.respawnTask != null) {
            this.respawnTask.cancel(true);
            this.respawnTask = null;
        }

        if(this.timer != null) {
            this.timer.cancel(true);
            this.timer = null;
        }
    }

    private void finish(Team winner) {
        Channel cs = map.getChannelServer();

        try {
            for(MapleCharacter mc : map.getAllPlayers()) {
                if(mc != null && mc.getMCPlayerComponent() != null) {
                    mc.getMCPlayerComponent().setFestivalPoints(mc.getMCPlayerComponent().getTeam() == Team.RED ? this.redTotalCP : this.blueTotalCP);
                    if(type == CPQType.CPQ1) {
                        mc.changeMap(cs.getMapFactory().getMap(map.getId() + (winner == mc.getMCPlayerComponent().getTeam() ? 2 : 3)));
                    } else {
                        mc.changeMap(cs.getMapFactory().getMap(map.getId() + (winner == mc.getMCPlayerComponent().getTeam() ? 200 : 300)));
                    }
                }
            }
                
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timeUp() {
        Team winner;
        if(redTotalCP > blueTotalCP) {
            winner = Team.RED;
        } else if (blueTotalCP > redTotalCP) {
            winner = Team.BLUE;
        } else {
            winner = Team.getTeamFromInt(ThreadLocalRandom.current().nextInt(0, 1));
            map.broadcastMessage(MaplePacketCreator.serverNotice(1, "A RANDOM WINNER HAS BEEN CHOSEN!"));
        }

        map.killAllMonsters();
        this.respawnTask.cancel(true);
        this.respawnTask = null;

        try {
            for(MapleCharacter mc : map.getAllPlayers()) {
                if(mc != null && mc.getMCPlayerComponent() != null) {
                    mc.dispelDebuffs();
                    mc.getClient().announce(
                        MaplePacketCreator.showEffect(
                            winner == mc.getMCPlayerComponent().getTeam() ? "quest/carnival/win" : "quest/carnival/lose"));
                    mc.getClient().announce(
                        MaplePacketCreator.playSound(
                            winner == mc.getMCPlayerComponent().getTeam() ? "MobCarnival/Win" : "MobCarnival/Win"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            // Wait for a 10 seconds to warpout
            Thread.sleep(10 * 1000);            
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        finish(winner);
    }


    public ScheduledFuture<?> getTimer() {
        return this.timer;
    }

    public long getTimeLeft() {
        return (startTime - System.currentTimeMillis());
    }

    public int getTimeLeftSeconds() {
        return (int) (getTimeLeft() / 1000);
    }

    public MapleParty getRed() {
        return p1;
    }

    public void setRed(MapleParty p1) {
        this.p1 = p1;
    }

    public MapleParty getBlue() {
        return p2;
    }

    public void setBlue(MapleParty p2) {
        this.p2 = p2;
    }

    public int getTotalCP(Team team) {
        if (team == Team.RED) {
            redCPLock.readLock().lock();
            try {
                return redTotalCP;
            } finally {
                redCPLock.readLock().unlock();
            }
        } else if (team == Team.BLUE) {
            blueCPLock.readLock().lock();
            try {
                return blueTotalCP;
            } finally {
                blueCPLock.readLock().unlock();
            }
        } else {
            throw new RuntimeException("Unkown team");
        }
    }

    public void setTotalCP(int totalCP, Team team) {
        if (team == Team.RED) {
            redCPLock.writeLock().lock();
            try {
                this.redTotalCP = totalCP;
            } finally {
                redCPLock.writeLock().unlock();
            }
        } else if (team == Team.BLUE) {
            blueCPLock.writeLock().lock();
            try {
                this.blueTotalCP = totalCP;
            } finally {
                blueCPLock.writeLock().unlock();
            }
        }
    }

    public int getCP(Team team) {
        if (team == Team.RED) {
            redCPLock.readLock().lock();
            try {
                return redCP;
            } finally {
                redCPLock.readLock().unlock();
            }
        } else if (team == Team.BLUE) {
            blueCPLock.readLock().lock();
            try {
                return blueCP;
            } finally {
                blueCPLock.readLock().unlock();
            }
        } else {
            throw new RuntimeException("Unkown team: " + team);
        }
    }

    public void setCP(int cp, Team team) {
        if (team == Team.RED) {
            redCPLock.writeLock().lock();
            try {
                this.redCP = cp;
                if(this.redCP > this.redTotalCP) {
                    this.redTotalCP = this.redCP;
                }
            } finally {
                map.broadcastMessage(MaplePacketCreator.CPUpdate(true, this.redCP, this.redTotalCP, team.value));
                redCPLock.writeLock().unlock();
            }
        } else if (team == Team.BLUE) {
            blueCPLock.writeLock().lock();
            try {
                this.blueCP = cp;
                if(this.blueCP > this.blueTotalCP) {
                    this.blueTotalCP = this.blueCP;
                }
            } finally {
                map.broadcastMessage(MaplePacketCreator.CPUpdate(true, this.blueCP, this.blueTotalCP, team.value));
                blueCPLock.writeLock().unlock();
            }
        }
    }

    public void addCP(int cp, Team team) {
        if(team == Team.RED) {
            redCPLock.writeLock().lock();
            try {
                this.redCP += cp;
                if(cp > 0) {
                    this.redTotalCP += cp;
                }
            } finally {
                map.broadcastMessage(MaplePacketCreator.CPUpdate(true, this.redCP, this.redTotalCP, team.value));
                redCPLock.writeLock().unlock();
            }
        } else if(team == Team.BLUE) {
            blueCPLock.writeLock().lock();
            try {
                this.blueCP += cp;
                if(cp > 0) {
                    this.blueTotalCP += cp;
                }
            } finally {
                map.broadcastMessage(MaplePacketCreator.CPUpdate(true, this.blueCP, this.blueTotalCP, team.value));
                blueCPLock.writeLock().unlock();
            }
        }
    }
    
    public MapleMap getEventMap() {
        return this.map;
    }
}