package server.partyquest.monstercarnival.util;

import java.awt.Point;
import server.partyquest.MonsterCarnival.Team;

/**
 *
 * @author Benjixd
 */
public class MonsterCarnivalMobSpawnPoint {
    private Point position;
    private Team team;

    public MonsterCarnivalMobSpawnPoint(Point a, int team) {
        this.position = a;
        this.team = Team.getTeamFromInt(team);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
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
