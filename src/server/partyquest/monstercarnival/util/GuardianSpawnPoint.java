package server.partyquest.monstercarnival.util;

import java.awt.Point;
import server.partyquest.MonsterCarnival.Team;

/**
 *
 * @author David
 */
public class GuardianSpawnPoint {

    // Values to reset to
    private Point origPosition;
    private Team origTeam;

    private Point position;
    private boolean taken;
    private Team team;

    public GuardianSpawnPoint(Point a, int team) {
        this.origPosition = a;
        this.position = a;
        
        this.origTeam = Team.getTeamFromInt(team);
        this.team = Team.getTeamFromInt(team);

        this.taken = false;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
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

    public void reset() {
        this.team = this.origTeam;
        this.position = this.origPosition;
        this.taken = false;
    }
}
