package server.partyquest.monstercarnival.util;

import java.awt.Point;
import server.partyquest.MonsterCarnival.Team;

/**
 *
 * @author David
 */
public class GuardianSpawnPoint {

    private Point position;
    private boolean taken;
    private Team team;

    public GuardianSpawnPoint(Point a) {
        this.position = a;
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
}
