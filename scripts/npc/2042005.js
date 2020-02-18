/*
    This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
               Matthias Butz <matze@odinms.de>
               Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/* Edited by: Benjixd
    NPC Name: Spiegelmann
    Description: CPQ2 host
*/

importPackage(Packages.server.partyquest.monstercarnival);
importPackage(Packages.server.partyquest);
importPackage(Packages.net.server.world);

var status = 0;
var minLevelReq = 60;
var CPQ2_MAP = 980030000;
var selectedMap;

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == -1) {
        cm.dispose();
        return;
    }

    if (status >= 2 && mode == 0) {
        cm.dispose();
        return;
    }

    if(mode == 1) {
        status++;
    } else {
        status--;
    }

    spiegelmannInOfficeCPQ2(mode, type, selection);
}

function spiegelmannInOfficeCPQ2(mode, type, selection) {
    var carnivalManager = cm.getClient().getChannelServer().getMCManager();
    var party = cm.getPlayer().getParty();

    if(party != null && cm.getPlayer() == party.getLeader().getPlayer()) {
        if(status == 1) {
            if(carnivalManager.isWaitingForSession(party)) {
                cm.sendOk("Your party currently has a pending request to join a lobby. Please wait for a response.");
                cm.dispose();
            } else {
                cm.sendSimple("Welcome to the 2nd Monster Carnival! The following lobbies are available for participation:\r\n" + getFreeLobbySelectionMsg(carnivalManager));    
            }
        }
        else if(status == 2) {
            if(partyRequirementsMet(party)) {
                selectedMap = selection;
                var result = carnivalManager.getLobby(selectedMap);
                if(result == null) {
                    cm.sendYesNo("Looks like the lobby is currently vacant. Would you like to go in?");
                }
                else {
                    cm.sendYesNo(getInitiatorTeamString(selectedMap, carnivalManager) + "\r\nWould you like to challenge this team?");
                }               
            }
            else {
                cm.sendOk("Your party does not meet the minimum level " + minLevelReq + " requirement.");
                cm.dispose();
            }
        }
        else if(status == 3) {
            if(carnivalManager.tryInitiateLobby(cm.getPlayer().getParty(), selectedMap, MonsterCarnival.CPQType.CPQ2)) {
                cm.dispose();
            }
            else if(carnivalManager.tryJoinLobby(cm.getPlayer().getParty(), selectedMap)) {
                cm.sendOk("Please wait...");
                cm.dispose();
            } else {
                cm.sendOk("Looks like someone is already challenging this party. Please try again.");
                cm.dispose();
            }
        }
    } else {
        cm.sendNext("Please ask your party leader to speak with me!");
        cm.dispose();
    }
}

function partyRequirementsMet(party) {
    var members = party.getMembers();

    if(members.size() < 1) {
        return false;
    }

    for(var i = 0; i < members.size(); i++) {
        if(members[i].getPlayer().getLevel() < minLevelReq) {
            return false;
        }
    }
    return true;
}

function getInitiatorTeamString(mapId, carnivalManager) {
    var string = "";
    var initParty = carnivalManager.getLobby(mapId).getInitiator().getMembers();

    for(var i = 0; i < initParty.size(); i++) {
        var ch = initParty[i].getPlayer();
        string += "#b" + ch.getName() + " / Level " + ch.getLevel() + " / " + ch.getJob().toString() + "#k\r\n";
    }
    return string;
}

function getFreeLobbySelectionMsg(carnivalManager) {
    var string = "";
    var freeMaps = carnivalManager.getAvailableMaps(MonsterCarnival.CPQType.CPQ2);
    for(var i = 0; i < freeMaps.size(); i++) {
        string += "#L" + freeMaps[i] + "##e" + (i+1) + ".#n#b" + carnivalManager.getChannel().getMapFactory().getMap(freeMaps[i]).getMapName() + "#k#l\r\n";
    }
    return string;
}