/*  Author: Benjixd
    NPC Name: Assistant Red
    Description: Monster Carnival 2
*/

importPackage(Packages.server.partyquest.monstercarnival);

var status = 0;
var CPQ2_MAP = 980030000;

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

    var carnivalManager = cm.getClient().getChannelServer().getMCManager();
    var lobbyMapId = cm.getPlayer().getMapId();

    if(status == 1) {
        cm.sendYesNo("Would you like to leave the 2nd Monster Carnival?");
    } else if(status == 2) {
        cm.warp(CPQ2_MAP);
        carnivalManager.healthCheckLobby(lobbyMapId);
        cm.dispose();
    }
}
