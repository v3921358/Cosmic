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
/* 	
	NPC Name: 		Big Headward
        Map(s): 		Victoria Road : Henesys Hair Salon (100000104)
	Description: 		Random haircut
*/

var status = 0;
var mhair = Array(32410, /*Overgrown Hair*/
                  32390, /*Bed Head Hair*/
                  33260, /*Neinheart Hair*/
                  36920, /*Randy Hair*/
                  36910, /*Black Daredevil Hair*/
                  //43010, /*Pastel Goth*/
                  36910,
                  35650 /*Toth Hair*/);
var fhair = Array(37560, /*Bow Bleached Hair*/
                  37640, /*Eternal Wind Hair*/
                  32650, /*Asuna Hair Hair*/
                  33420, /*Full Margate Hair*/
                  31890, /*Short Twin Tails*/
                  //41090, /*Prim Air Hair*/
                  34370 /*Shaggy Bobbed Hair*/);
var hairnew = Array();

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            cm.dispose();
            return;
        }
        
        if (mode == 1)
            status++;
        else
            status--;
        
        if (status == 0) {
            cm.sendSimple("Welcome! If you have a #b#t5150040##k, I can give you a random experimental hair style I've been working on!\r\n#L0#Preview possible styles!#l\r\n#L1#I'm ready for my haircut! (Uses #i5150040# #t5150040#)#l");
        } else if (status == 1) {
            if (selection == 0) {
                var styles = Array();
                if (cm.getPlayer().getGender() == 0)
                    for(var i = 0; i < mhair.length; i++)
                        styles.push(mhair[i] + parseInt(cm.getPlayer().getHair()% 10));
                if (cm.getPlayer().getGender() == 1)
                    for(var i = 0; i < fhair.length; i++)
                        styles.push(fhair[i] + parseInt(cm.getPlayer().getHair() % 10));   
                cm.sendStyle("These are the styles you can possibly get today, but even I don't know what you'll end up with!", styles);
                cm.dispose();
            }
            else if (selection == 1) {
                if (cm.haveItem(5150040) == true) {
                    hairnew = Array();
                    if (cm.getPlayer().getGender() == 0) {
                        for(var i = 0; i < mhair.length; i++) {
                            hairnew.push(mhair[i] + parseInt(cm.getPlayer().getHair() % 10));
                        }
                    }
                    else {
                        for(var i = 0; i < fhair.length; i++) {
                            hairnew.push(fhair[i] + parseInt(cm.getPlayer().getHair() % 10));
                        }
                    }
                    
                    cm.gainItem(5150040, -1);
                    var newHairId;

                    do{ // Makes sure the hair salon actually changes your hair.
                       newHairId = hairnew[Math.floor(Math.random() * hairnew.length)]
                    } while (newHairId == cm.getPlayer().getHair());

                    cm.setHair(newHairId);
                    cm.sendOk("Enjoy your new and improved hairstyle!");
                } else {
                    cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }                
                cm.dispose();
            }
        }
    }
}
