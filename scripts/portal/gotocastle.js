function enter(pi) {
	if (pi.isQuestActive(2324)) {

		var player = pi.getPlayer();

	    pi.forceCompleteQuest(2324, 1300004);
	    player.gainExp(3300 * player.getExpRate());
	    player.dropMessage(5, "Quest completed.");

	    if(pi.hasItem(2430015)){
	    	pi.gainItem(2430015, -1 * player.getItemQuantity(2430015, false));
	    }
	}
	pi.warp(106020501,0);
	return true;
}