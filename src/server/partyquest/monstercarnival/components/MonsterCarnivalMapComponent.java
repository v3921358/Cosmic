package server.partyquest.monstercarnival.components;

import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.ArrayList;

import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnival.Team;
import server.partyquest.monstercarnival.util.GuardianSpawnPoint;
import server.partyquest.monstercarnival.util.MonsterCarnivalMob;
import server.partyquest.monstercarnival.util.MonsterCarnivalMobSpawnPoint;

public class MonsterCarnivalMapComponent {
	private int maxMobs;
	private int deathCP;
	private int maxReactors;
	private int timeDefault;
	private int timeExpand;
    private MonsterCarnival mc;

	private final List<Integer> skillIds = new ArrayList();
    private final List<GuardianSpawnPoint> guardianSpawns = new ArrayList<>();
    private final List<MonsterCarnivalMobSpawnPoint> mobSpawnPoints = new ArrayList<>();
    private final List<MonsterCarnivalMob> mobsToSpawn = new ArrayList<>();

	// ==== Static Methods ==== //
	public static boolean isCPQMap(int mapId) {
        switch (mapId) {
            case 980000101:
            case 980000201:
            case 980000301:
            case 980000401:
            case 980000501:
            case 980000601:
                return true;
        }

        return false;
	}

	public static boolean isCPQ2Map(int mapId) {
        switch (mapId) {
            case 980031100:
            case 980032100:
            case 980033100:
                return true;
        }

        return false;
	}

    public static boolean isBlueCPQMap(int mapId) {
        switch (mapId) {
            case 980000501:
            case 980000601:
            case 980031200:
            case 980032200:
            case 980033200:
                return true;
        }
        return false;
    }

    public static boolean isPurpleCPQMap(int mapId) {
        switch (mapId) {
            case 980000301:
            case 980000401:
            case 980031200:
            case 980032200:
            case 980033200:
                return true;
        }
        return false;
    }

	// ==== Setters & Getters ==== //
    public void setMC(MonsterCarnival mc) {
        this.mc = mc;
    }

    public MonsterCarnival getMC() {
        return mc;
    }

    public final MonsterCarnivalMob getMob(int index) {
        if (index < 0 || index >= mobsToSpawn.size()) {
            return null;
        } else {
            return mobsToSpawn.get(index);
        }
    }

	public final int getMaxMobs() {
        return maxMobs;
    }

    public void setMaxMobs(int maxMobs) {
        this.maxMobs = maxMobs;
    }

	public final int getDeathCP() {
        return deathCP;
    }

    public void setDeathCP(int deathCP) {
        this.deathCP = deathCP;
    }

    public final int getMaxReactors() {
        return maxReactors;
    }

    public void setMaxReactors(int maxReactors) {
        this.maxReactors = maxReactors;
    }

    public final int getTimeDefault() {
        return timeDefault;
    }

    public void setTimeDefault(int timeDefault) {
        this.timeDefault = timeDefault;
    }

    public final int getTimeExpand() {
        return timeExpand;
    }

    public void setTimeExpand(int timeExpand) {
        this.timeExpand = timeExpand;
    }

	public void addGuardianSpawnPoint(GuardianSpawnPoint a) {
        this.guardianSpawns.add(a);
    }

    public final List<Integer> getSkillIds() {
        return skillIds;
    }

    public final Integer getSkillId(int index) {
        if(index < 0 || index >= skillIds.size()) {
            return null;
        }
        else {
            return skillIds.get(index);
        }
    }

    public void addSkillId(int id) {
        this.skillIds.add(id);
    }

    public final void addMobSpawn(int mobId, int spendCP) {
    	this.mobsToSpawn.add(new MonsterCarnivalMob(mobId, spendCP));
    }

    public final void addMobSpawnPoint(MonsterCarnivalMobSpawnPoint sp) {
        this.mobSpawnPoints.add(sp);
    }

    public List<MonsterCarnivalMobSpawnPoint> getMobSpawnPoints() {
        return this.mobSpawnPoints;
    }

    // public methods
    public GuardianSpawnPoint getNextGuardianSpawnPoint(Team team) {
        for(GuardianSpawnPoint gsp : this.guardianSpawns) {
            if((gsp.getTeam() == team || gsp.getTeam() == Team.NONE)  && !gsp.isTaken()) {
                return gsp;
            }
        }
        return null;
    }

    public GuardianSpawnPoint getRandomGuardianSpawnPoint(Team team) {
        List<GuardianSpawnPoint> valid = new ArrayList<>();
        for(GuardianSpawnPoint gsp : this.guardianSpawns) {
            if((gsp.getTeam() == team || gsp.getTeam() == Team.NONE)  && !gsp.isTaken()) {
                valid.add(gsp);
            }
        }

        if(valid.size() > 0) {
            return valid.get(ThreadLocalRandom.current().nextInt(0, valid.size()));    
        }
        else {
            return null;
        }
    }

    public void reset() {
        this.mc = null;
        for(GuardianSpawnPoint gsp : this.guardianSpawns) {
            gsp.reset();
        }
    }
}