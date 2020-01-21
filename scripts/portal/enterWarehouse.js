function enter(pi) {
    if (pi.isQuestCompleted(21751) && pi.getQuestProgress(21753, 21764) == 0){
        pi.setQuestProgress(21753, 21764, 1);
    }
    pi.playPortalSound(); pi.warp(300000011,0);
    return true;
}