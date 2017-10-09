importPackage(Packages.constants);
function enter(pi) {
		var eim = pi.getEventInstance();
    if (pi.getMap().getMonsters().size() == 0) {
    		pi.giveCharacterExp(Math.floor(eim.getClearStageExp(4) * ServerConstants.PQ_EXP_MOD), pi.getPlayer());
				pi.warp(925100400,0); //next
        return(true);
    } else {
	pi.playerMessage(5, "The portal is not opened yet.");
        return(false);
    }
}