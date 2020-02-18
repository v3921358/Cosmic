/*  Author: Benjixd
    NPC Name: Spieglmann
    Description: Monster Carnival 2 - Victory/Lose/Exit
*/

var status = 0;
var CPQ2_OFFICE_MAP = 980030000;

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

    var exitMapId = cm.getPlayer().getMapId();

    if(status == 1) {
        // Victorious
        if(exitMapId % 1000 == 300) {
            cm.sendOk("Congratulations on your win in the 2nd Monster Carnival! I hope you had a good time!");
        }
        // Loss 
        else if(exitMapId % 1000 == 400) {
            cm.sendOk("You'll get 'em next time!");
        } 
        // Default
        else {
            cm.sendOk("I can get you out of here.")
        }
    } else if(status == 2) {
        cm.warp(CPQ2_OFFICE_MAP);
        cm.dispose();
    }
}
