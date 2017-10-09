function enter(pi) {
		var eim = pi.getEventInstance();
    if (pi.getMap().getMonsters().size() == 0) {
    		eim.giveEventPlayersStageReward(3);
				pi.warp(925100400,0); //next
        return(true);
    } else {
	pi.playerMessage(5, "The portal is not opened yet.");
        return(false);
    }
}