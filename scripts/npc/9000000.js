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
/*      Author:                 Xterminator, Moogra, Benjixd
	NPC Name: 		Paul
	Map(s): 		Maple Road: Southperry (60000)
	Description: 	        Event Assistant
*/
importPackage(Packages.server.events.gm)

var status = 0;
var golden_maple_leaf_id = 4000313;

var chaos_scroll_cost = 3;
var innocence_scroll_cost = 7;
var onyx_apple_cost = 10;
var white_scroll_cost = 50;

var chaos_scroll_id = 2049100;
var innocence_scroll_id = 2049610; // 100%
var onyx_apple_id = 2022179;
var white_scroll_id = 2340000;


function start() {
    cm.sendNext("Hey, I'm #bPaul#k, if you're not busy and all ... then can I hang out with you? I heard there are people gathering up around here for an #revent#k but I don't want to go there by myself ... Well, do you want to go check it out with me?");
}

function maybeTradeGoldenMapleLeaf(cm, item_cost, item_name, item_id) {
    if (cm.haveItem(golden_maple_leaf_id, item_cost)) {
        cm.gainItem(golden_maple_leaf_id, -1 * item_cost);
        cm.gainItem(item_id, 1);
        cm.sendOk("You just got a " + item_name);
    } else {
        cm.sendOk("Looks like you don't have enough Golden Maple Leaves for that item.");
    }
    cm.dispose();
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 2 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 1) {
            cm.sendSimple("Huh? What kind of an event? Well, that's...\r\n" +
                          "#L0##e1.#n#b What kind of an event is it?#k#l\r\n" +
                          "#L1##e2.#n#b Alright, let's go!#k#l\r\n" +
                          "#L2##e3.#n#b Trade for items.");

        } else if (status == 2) {
            if (selection == 0) {
                cm.sendNext("DietStory will be regularly hosting scheduled events everyday! " +
                            "If you catch a notice, be sure to join in. Who knows what you might get?");
                cm.dispose();
            } else if (selection == 1) {
				var event = cm.getClient().getWorldServer().getEvent();
                if(event != null) {
                    cm.getPlayer().saveLocation("EVENT");
                    if(cm.getClient().getChannel() != event.getMap().getChannel()) {
                        cm.sendNext("Come to Channel " + event.getMap().getChannel() + " and try again!");
                    }
                    else if(event.tryEnterEvent(cm.getPlayer())) {
                        cm.sendNext("I hope you have a great time at the event!");
                    } else {
                        cm.sendNext("Sorry! Looks like the event has already begun or we've reached the limited amount of players for this event.");
                    }
                }
                else {
                    cm.sendNext("Either the event has not been started, you already have the #bScroll of Secrets#k, or you have already participated in this event within the last 24 hours. Please try again later!");
                }
                cm.dispose();
			} else if (selection == 2) {
			    /* TODO: Add items to trade for, currently its set to Chaos scrolls */
			    var trade_msg = "You currently have #c" + golden_maple_leaf_id +"# #i" + golden_maple_leaf_id +
			                    "#\r\nWhat item would you like to trade for?\r\n" +
			                    "#rWARNING: If you click \"Next\" you will trade for a Chaos Scroll.#k \r\n" +
                                "#L0##e#n" + chaos_scroll_cost + "#i" + golden_maple_leaf_id + "#   for #i" + chaos_scroll_id + "#\r\n" +
                                "#L1##e#n" + innocence_scroll_cost + "#i" + golden_maple_leaf_id + "#   for #i" + innocence_scroll_id + "#\r\n" +
                                "#L2##e#n" + onyx_apple_cost + "#i" + golden_maple_leaf_id + "#   for #i" + onyx_apple_id + "#\r\n" +
                                "#L3##e#n" + white_scroll_cost + "#i" + golden_maple_leaf_id + "#   for #i" + white_scroll_id + "#\r\n";

                cm.sendSimple(trade_msg);
            }
        } else if (status == 3) {
			if (selection == 0) {
                maybeTradeGoldenMapleLeaf(cm, chaos_scroll_cost, "Chaos Scroll", chaos_scroll_id);
            } else if (selection == 1) {
                maybeTradeGoldenMapleLeaf(cm, innocence_scroll_cost, "Innocence Scroll", innocence_scroll_id);
            } else if (selection == 2) {
                maybeTradeGoldenMapleLeaf(cm, onyx_apple_cost, "Onyx Apple", onyx_apple_id);
            } else if (selection == 3) {
                maybeTradeGoldenMapleLeaf(cm, white_scroll_cost, "White Scroll", white_scroll_id);
            }
            cm.dispose();
       }
    }
}