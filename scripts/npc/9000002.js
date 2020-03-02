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
var status = 0;
var golden_maple_leaf = 4000313;

function start() {
    status = -1;
    action(1, 0, 0);
}

function actionWithReward(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }else if (mode == 0){
        cm.dispose();
    }else{
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendNext("Bam bam bam bam!! You have won the game from the \r\n#bEVENT#k. Congratulations on making it this far!");
        } else if (status == 1) {
            cm.sendNext("You'll be awarded the #bScroll of Secrets#k as the winning prize. On the scroll, it has secret information written in ancient characters.");
        } else if (status == 2) {
            cm.sendNext("The Scroll of Secrets can be deciphered by #rChun Ji#k or \r\n#rGeanie#k at Ludibrium. Bring it with you and something good's bound to happen.");
        } else if (status == 3) {
        if (cm.canHold(4031019)) {
            cm.gainItem(4031019);
            cm.warp(cm.getPlayer().getSavedLocation("EVENT"));
            cm.dispose();
        } else {
            cm.sendNext("I think your Etc window is full. Please make room, then talk to me.");
        }
        } else if (status == 4) {
            cm.dispose();
        }
    }
}

function getRewardThenWarp(cm, location) {
    if (cm.canHold(golden_maple_leaf)) {
        if (cm.getPlayer().getEventRanking()) == 1) {
            cm.gainItem(golden_maple_leaf, 10);
        } else if (cm.getPlayer().getEventRanking()) == 2) {
            cm.gainItem(golden_maple_leaf, 5);
        } else if (cm.getPlayer().getEventRanking()) == 3) {
            cm.gainItem(golden_maple_leaf, 3);
        } else if (cm.getPlayer().getEventRanking()) == 4) {
            cm.gainItem(golden_maple_leaf, 2);
        } else if (cm.getPlayer().getEventRanking()) == 5) {
            cm.gainItem(golden_maple_leaf, 1);
        }
        cm.getPlayer().setEventRanking(0);
        cm.warp(location);
        cm.dispose();
    } else {
        cm.sendNext("I think your Etc window is full. Please make room, then talk to me.");
    }
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }else if (mode == 0){
        cm.dispose();
    }else{
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            cm.sendNext("Congratulations on making it this far! We hope you enjoyed the event!!");
        } else if (status == 1) {
            var location = cm.getPlayer().getSavedLocation("EVENT");
            if(location == -1) {
                location = 104000000; // Lith Harbor
            }
            getRewardThenWarp(cm, location);
            
            cm.dispose();
        }
    }
}

