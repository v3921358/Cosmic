package server.partyquest.monstercarnival.util;

public class MonsterCarnivalMob {
	private int mobId;
	private int requiredCP;

	public MonsterCarnivalMob(int id, int req) {
		this.mobId = id;
		this.requiredCP = req;
	}

	public int getId() {
		return mobId;
	}

	public int getRequiredCP() {
		return requiredCP;
	}
}