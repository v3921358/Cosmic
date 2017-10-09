function enter(pi) {
	var eim = pi.getEventInstance();

	if (pi.getMap().getMonsters().size() == 0) {
								eim.giveEventPlayersStageReward(0);
                pi.warp(925100100,0); //next
                return(true);
        } else {
                pi.playerMessage(5, "The portal is not opened yet.");
                return(false);
        }
}