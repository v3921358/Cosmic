/*  Author: Benjixd
    NPC Name: Spieglmann
    Description: Monster Carnival 1 - Victory/Lose/Exit / Kerning City NPC
*/

var status = 0;
var CPQ_MAP = 980000000;
var CPQ2_MAP = 980030000;
var KERNING_CITY_MAP = 103000000;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == -1) {
        cm.dispose();
        return;
    }

    if (mode == 0 && type > 0) {
        cm.dispose();
        return;
    }

    if(mode == 1) {
        status++;
    } else {
        status--;
    }

    if(cm.getPlayer().getMapId()  == KERNING_CITY_MAP) {
        spiegelmannInTown(mode, type, selection);
    } else {
        spiegelmannInExitMap(mode, type, selection);
    }
}

function spiegelmannInExitMap(mode, type, selection) {
    var exitMapId = cm.getPlayer().getMapId();

    if(status == 1) {
        // Victorious
        if(exitMapId % 10 == 3) {
            cm.sendOk("Congratulations on your win in Monster Carnival! I hope you had a good time!");
        }
        // Loss 
        else if(exitMapId % 10 == 4) {
            cm.sendOk("You'll get 'em next time!");
        } 
        // Default
        else {
            cm.sendOk("I can get you out of here.")
        }
    } else if(status == 2) {
        cm.warp(CPQ_MAP);
        cm.dispose();
    }
}

function spiegelmannInTown(mode, type, selection) {
    if(status == 1) {
        cm.sendNext("Haha! I am Spiegelmann, the creator of this Monster Carnival. Would you like to try it out?");
    } else if(status == 2) {
        cm.sendSimple("Which monster carnival would you like to participate in?\r\n#L0##e1.#n#b The Monster Carnival#k#l\r\n#L1##e2.#n#b The 2nd Monster Carnival#k#l");
    } else if (status == 3) {
        cm.getPlayer().saveLocation("MIRROR");
        if(selection == 0) {
            cm.warp(CPQ_MAP);
        } else if(selection == 1) {
            cm.warp(CPQ2_MAP);
        }
        cm.sendNext("Good luck out there!");
        cm.dispose();
    }
}
