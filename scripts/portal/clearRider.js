/*
    @author nansonzheng
    Aran_2ndmount event exit portal
*/

function enter(pi) {
    if (pi.isQuestStarted(21610)){
        pi.setQuestProgress(21610, 21619, 0);
    }
    pi.warp(211050000, 4);
    return true;
}