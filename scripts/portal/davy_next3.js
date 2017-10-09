function enter(pi) {
		var eim = pi.getEventInstance();
    if (pi.getMap().getMonsters().size() == 0) {
    		pi.giveExp(eim.getClearStageExp(4) * pi.getPlayer().getExpRate() * ServerConstants.PQ_EXP_MOD);
				pi.warp(925100400,0); //next
        return(true);
    } else {
	pi.playerMessage(5, "The portal is not opened yet.");
        return(false);
    }
}