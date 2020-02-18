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
    Description: Monster Carnival Assistant & CP1 host
*/

importPackage(Packages.server.partyquest.monstercarnival);
importPackage(Packages.server.partyquest);
importPackage(Packages.net.server.world);

var status = 0;
var minLevelReq = 30;
var CPQ_MAP = 980000000;
var CPQ2_MAP = 980030000;
var selectedMap;

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

    if(cm.getPlayer().getMapId() == CPQ_MAP) {
        spiegelmannInOfficeCPQ1(mode, type, selection);
    } else {
        spiegelmannInTown(mode, type, selection);
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

function spiegelmannInOfficeCPQ1(mode, type, selection) {
    var carnivalManager = cm.getClient().getChannelServer().getMCManager();
    var party = cm.getPlayer().getParty();

    if(party != null && cm.getPlayer() == party.getLeader().getPlayer()) {
        if(status == 1) {
            if(carnivalManager.isWaitingForSession(party)) {
                cm.sendOk("Your party currently has a pending request to join a lobby. Please wait for a response.");
                cm.dispose();
            } else {
                cm.sendSimple("Welcome to Monster Carnival! The following lobbies are available for participation:\r\n" + getFreeLobbySelectionMsg(carnivalManager));    
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
            if(carnivalManager.tryInitiateLobby(cm.getPlayer().getParty(), selectedMap, MonsterCarnival.CPQType.CPQ1)) {
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
    var freeMaps = carnivalManager.getAvailableMaps(MonsterCarnival.CPQType.CPQ1);
    for(var i = 0; i < freeMaps.size(); i++) {
        string += "#L" + freeMaps[i] + "##e" + (i+1) + ".#n#b" + carnivalManager.getChannel().getMapFactory().getMap(freeMaps[i]).getMapName() + "#k#l\r\n";
    }
    return string;
}