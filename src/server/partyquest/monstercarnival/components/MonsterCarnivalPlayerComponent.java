package server.partyquest.monstercarnival.components;

import java.util.List;
import java.util.ArrayList;

import client.MapleClient;
import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnival.Team;
import server.partyquest.monstercarnival.util.GuardianSpawnPoint;
import server.partyquest.monstercarnival.util.MonsterCarnivalMob;
import tools.MaplePacketCreator;


public class MonsterCarnivalPlayerComponent {
    private int myCP = 0;
    private int myTotalCP = 0;
    private int festivalPoints = 0;
    private Team team;
    private MonsterCarnival mc;
    private MapleClient client;

    public MonsterCarnivalPlayerComponent(MapleClient c, MonsterCarnival mc, Team team) {
        this.team = team;
        this.mc = mc;
        this.client = c;
    }

    // Getters & Setters
    public int getCP() {
        return myCP;
    }

    public int getTotalCP() {
        return myTotalCP;
    }

    public int getFestivalPoints() {
        return this.festivalPoints;
    }

    public void setFestivalPoints(int points) {
        this.festivalPoints = points;
    }

    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public void setTeam(int team) {
        this.team = Team.getTeamFromInt(team);
    }

    public MonsterCarnival getMonsterCarnival() {
        return mc;
    }
    public void setMonsterCarnival(MonsterCarnival mc) {
        this.mc = mc;
    }

    // Public methods
    public void dispose() {
        myCP = 0;
        myTotalCP = 0;
        mc = null;
        client = null;
    }

    public void gainCP(int val) {
        myCP = Math.max(0, myCP + val);
        if(val > 0) {
            myTotalCP += val;
        }
        mc.addCP(val, team);
        client.announce(MaplePacketCreator.CPUpdate(false, getCP(), getTotalCP(), getTeam().value));
    }
}