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
function enter(pi) {
    // papulatus quest either started or completed and have ludibrium medal 4031172 and num_instances enter < 2
    if (!(pi.isQuestStarted(7103) || pi.isQuestCompleted(7103))) {
        pi.getPlayer().dropMessage("You do not have the required quest to face Papulatus.");
        return false;
    }
    if (!pi.getPlayer().haveItem(4031172)) {
        pi.getPlayer().dropMessage("You do not have the required item(s) to face Papulatus.");
        return false;
    }
    if (!pi.getPlayer().mayEnterBoss(8500001)){
        pi.getPlayer().dropMessage("You have fought Papulatus more than the number of available times.");
        return false;
    }
    var papuMap = pi.getClient().getChannelServer().getMapFactory().getMap(220080001);
    if (papuMap.getCharacters().size() == 0) {
        pi.getPlayer().dropMessage("The room is empty. A perfect opportunity to challenge the boss.");
        papuMap.resetReactors();
    } else { // someone is inside
        for (var i = 0; i < 3; i++) {
            if (papuMap.getMonsterById(8500000 + i) != null) {
                pi.getPlayer().dropMessage("Someone is fighting Papulatus.");
                return false;
            }
        }
    }
    pi.warp(220080001, "st00");
    pi.getPlayer().addBossEntry(8500001);
    return true;
}