/*
 * @Name         Monster Carnival Assistant
 * @Author:      Benjixd
 * @NPC:         2042003, 2042004, 2042008, 2042009
 * @Purpose:     CPQ Challenger Ack/Deny
 */

importPackage(Packages.server.partyquest.monstercarnival);
importPackage(Packages.client);

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == -1) {
        cm.dispose();
        return;
    }

    if (status >= 1 && mode == 0) {
        cm.dispose();
        return;
    }

    // Mode is used as a Yes/No selection here
    status++;

    var carnivalManager = cm.getClient().getChannelServer().getMCManager();

    if(status == 0) {
        cm.sendYesNo(getChallengerTeamString(cm.getPlayer(), carnivalManager) + "\r\nWould you like to battle this party at the Monster Carnival?");
    }
    else if(status == 1) {
        if(mode == 1) {
            carnivalManager.ackJoinLobby(cm.getPlayer().getMapId());
        } else {
            carnivalManager.denyJoinLobby(cm.getPlayer().getMapId());
        }
        cm.dispose();
    }
}

function getChallengerTeamString(player, carnivalManager) {
    var string = "";
    var mapId = player.getMapId();
    var challengerParty = carnivalManager.getLobby(mapId).getChallenger().getMembers();

    for(var i = 0; i < challengerParty.size(); i++) {
        var ch = challengerParty[i].getPlayer();
        string += "#b" + ch.getName() + " / Level " + ch.getLevel() + " / " + ch.getJob().toString() + "#k\r\n";
    }
    return string;
}