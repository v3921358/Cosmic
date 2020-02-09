package server.partyquest.monstercarnival.components;

import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnival.Team;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import server.partyquest.monstercarnival.util.GuardianSpawnPoint;

public class MonsterCarnivalReactorComponent {
	private Team team;
    private MCSkill skill;
    private GuardianSpawnPoint guardian;
    private MonsterCarnival mc;

    public MonsterCarnivalReactorComponent(MonsterCarnival mc) {
        this.mc = mc;
    }

	public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public MCSkill getSkill() {
        return skill;
    }

    public void setSkill(MCSkill skill) {
        this.skill = skill;
    }

    public GuardianSpawnPoint getGuardian() {
        return guardian;
    }

    public void setGuardian(GuardianSpawnPoint guardian) {
        this.guardian = guardian;
    }

    public void dispelAllMonsters() {
        mc.dispelAllMonsters(skill, team);
    }
}