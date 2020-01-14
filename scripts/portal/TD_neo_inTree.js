/*
    @author nansonzheng
    
    Tera Forest - Old Tree in Tera Forest
    Nex the Time Guard
    Solo battle (assumed)
*/
var quests = [3719, 3724, 3730, 3736, 3742, 3748];
var dungeons = 5;

function enter(pi) {
    if (pi.getMapId() == 240070000){
        for (var i = 0; i  < quests.length; i++){
            if (pi.isQuestStarted(quests[i])){
                return warpTo(pi, 240070010 + i * 10);
            }
        }
        if (pi.isQuestCompleted(3719)){
            pi.message("You have no business with Nex right now.");
            return false;
        }
        else {
            pi.message("A strange force is preventing you from entering.");
            return false;
        }
    }
    else {
        pi.playPortalSound();
        pi.warp(240070000, 'in00');
        return true;
    }
}

function warpTo(pi, mapid) {
    if (pi.getPlayerCount(mapid) == 0){
        var destination = pi.getMap(mapid);
        destination.resetMapObjects();
        pi.warp(destination.getId());
        destination.addMapTimer(180);
        return true;
    }
    pi.message("Nex appears to have some business with other adventurers. Why not try again later?");
    return false;
}