var minPlayers = 1;
var timeLimit = 30; //10 minutes
var eventTimer = 1000 * 60 * timeLimit;
var entryMap = 924010100;
var respawnMap = 240000000;
var eventMap = 913030000;

function init(){}

function setup(difficulty, lobbyId){
	var eim = em.newInstance("TheKnightThatDisappeared_" +lobbyId);
	eim.getInstanceMap(eventMap).resetFully();
	eim.getInstanceMap(eventMap).allowSummonState(false);
	respawn(eim);
	eim.startEventTimer(eventTimer);
	eim.spawnNpc(1104002, new java.awt.Point(-271, 87), eim.getInstanceMap(eventMap));
	return eim;
}

function afterSetup(eim){}

function respawn(eim){
	var map = eim.getMapInstance(entryMap);
	map.allowSummonState(true);
	map.instanceMapRespawn();
	eim.schedule("respawn", 10000);
}

function playerEntry(eim, player){
	var quietEreve = eim.getMapInstance(eventMap);
	player.changeMap(quietEreve);
}

function scheduledTimeout(eim){
	var party = eim.getPlayers();

	for(var i = 0; i < party.size(); i++)
		playerExit(eim, party.get(i));

	eim.dispose();
}

function playerRevive(eim, player){
	player.setHp(50);
	player.setStance(0);
	eim.unregisterPlayer(player);
	player.changeMap(respawnMap);
	return false;
}

function playerDead(eim, player){}

function playerDisconnected(eim, player){
	var party = eim.getPlayers();

	for(var i = 0; i < party.size(); i++){
		if(party.get(i).equals(player))
			removePlayer(eim, player);
		else
			playerExit(eim, party.get(i));
	}
	eim.dispose();
}

function monsterValue(eim, mobId){
	return -1;
}

function leftParty(eim, player){
	var party = eim.getPlayers();

	if(party.size() < minPlayers){
		for(var i = 0; i < party.size(); i++){
			playerExit(eim, party.get(i));
		}
		eim.dispose();
	}
	else{
		playerExit(eim, player);
	}
}

function disbandParty(eim){}

function playerUnregistered(eim, player){}

function playerExit(eim, player){
	eim.unregisterPlayer(player);
	player.changeMap(entryMap, 1);
}

function moveMap(eim, player){
	if(player.getMap().getId() != eventMap){
		removePlayer(eim, player);
		eim.stopEventTimer();
		eim.setEventCleared();
		eim.dispose();
	}
}

function removePlayer(eim, player){
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(entryMap);
}

function cancelSchedule(){}

function dispose(){}

function clearPQ(eim){}

function monsterKilled(mob, eim){}

function allMonstersDead(eim){}