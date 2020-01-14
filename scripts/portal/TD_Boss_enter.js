/*
    @author nansonzheng
    Prefix all IDs in parentheses with 240070 for boss room IDs
    Portal to Bergamot fight (203 - 209)
    Portal to Dunas fight (303 - 309)
    Portal to Aufheben fight (403 - 409)
    Portal to Oberon fight (503 - 509)
    Portal to Nibelung fight (603 - 609)
*/

var dungeons = 7;

function enter(pi){
    var entered = 0;
    switch (pi.getMapId()){
        case 240070202:
            return warpTo(
                pi, 
                3728, 
                240070203, 
                "You can't see any giant robot anywhere nearby, but you have a feeling it would be back later." ,
                "The way to the Pier is blocked. Captain Edmond should know a way around..."
            );
        case 240070302:
            return warpTo(
                pi, 
                3735,
                240070303,
                "Intense fighting has developed just ahead, you should come back later when the fight is over.",
                "There's someone stranded out here, you should help him first."
            );
        case 240070402:
            return warpTo(
                pi, 
                3740,
                240070403,
                "A large group of Lords are moving nearby, best not to reveal yourself to them.",
                "There is a help request sent from nearby, you should respond to that first."
            );
        case 240070502:
            return warpTo(
                pi,
                3743,
                240070503,
                "A massive beam shot blocks your way. Maybe it will subside in a few moments.",
                "There's a time and place for everything, and something comes before defeating Oberon."
            );
        case 240070602:
            return warpTo(
                pi,
                3749,
                240070603,
                "You see the Nibelung off in the distance. Maybe it'll come back to the dock in a short while",
                "This story honestly made no sense. Anyway you need to get the quest first."
            );
         default:
            java.lang.System.err.println("Error: " + pi.getPlayer().getName() + " triggered script TD_Boss_enter.js while in map " + pi.getMapId());
            pi.message("You cannot use this portal");
            return false;
    }
    return false;
}

function warpTo(pi, prequest, baseMap, instanceFullMsg, ReqNotMetMsg){
    if (pi.isQuestStarted(prequest) || pi.isQuestCompleted(prequest)) {
        for (var i = 0; i < dungeons; i++){
             if (pi.getPlayerCount(baseMap + i) == 0){
                var emptyMap = pi.getMap(baseMap + i);
                emptyMap.resetMapObjects();
                if (pi.getParty() == null)
                    pi.warp(emptyMap.getId());
                else if (pi.isPartyLeader())
                    pi.warpParty(emptyMap.getId());
                else {
                    pi.message("Only Party Leaders may start the boss fight.");
                    return false;
                }
                emptyMap.addMapTimer(30 * 60);
                return true;
             }
        }
        pi.message(instanceFullMsg);
        return false;
    }
    pi.message(ReqNotMetMsg);
    return false;
}