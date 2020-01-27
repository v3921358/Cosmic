importPackage(Packages.server.life);

function start(ms) {

	var mobId = 9400610;
	var player = ms.getPlayer();
	var map = player.getMap();

	if(map.getMonsterById(mobId) != null){
		return;   	       
	}

	player.message("Amdusias has appeared!");
	map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new java.awt.Point(251, -841));
}