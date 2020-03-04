/*
    This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
    NPC Name:       Pietro
    Map(s):         Receiving the reward for the event (109050000)
    Description:    Event Assistant
*/
var status = 0;
var golden_maple_leaf = 4000313;

function start() {
    status = -1;
    action(1, 0, 0);
}

function getRewardThenWarp(cm, location) {
    var amount_to_gain = 0;
    if (cm.getLatestEventPlacing() == 1) {
        amount_to_gain = 10;
    } else if (cm.getLatestEventPlacing() == 2) {
        amount_to_gain = 5;
    } else if (cm.getLatestEventPlacing() == 3) {
        amount_to_gain = 3;
    } else if (cm.getLatestEventPlacing() == 4) {
        amount_to_gain = 2;
    } else if (cm.getLatestEventPlacing() == 5) {
        amount_to_gain = 1;
    }

    if (cm.canHold(golden_maple_leaf, amount_to_gain)) {
        cm.gainItem(golden_maple_leaf, amount_to_gain);
        cm.setLatestEventPlacing(0);
        cm.warp(location);
        cm.dispose();
    } else {
        cm.sendNext("I think your Etc window is full. Please make room, then talk to me.");
    }
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else if (mode == 0) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            cm.sendNext("Congratulations on making it this far! We hope you enjoyed the event!!");
        } else if (status == 1) {
            var location = cm.getPlayer().getSavedLocation("EVENT");
            if (location == -1) {
                location = 104000000; // Lith Harbor
            }
            getRewardThenWarp(cm, location);
            cm.dispose();
        }
    }
}

