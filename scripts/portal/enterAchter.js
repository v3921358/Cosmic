function enter(pi) {
    if (pi.isQuestCompleted(21753) && pi.getQuestProgress(21754, 21765) != 2){
        pi.setQuestProgress(21754, 21765, 2);
    }
    pi.warp(100000201, "out02");
	pi.playPortalSound();
	return true;
}