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

importPackage(Packages.java);
importPackage(Packages.java.util);
importPackage(Packages.java.time);
importPackage(Packages.java.time.temporal);
importPackage(Packages.server);

var status = 0;
var NUM_CHOICES_GENDER_BASED = 4;
var NUM_UNIVERSAL_CHOICES = 4;
var BASE_HAIR_VALUE = 30000;
var MIN_HAIR_TYPE_1 = 30000;
var MAX_HAIR_TYPE_1 = 39000;
var MIN_HAIR_TYPE_2 = 39000;
var MAX_HAIR_TYPE_2 = 50000;
var BLACK_LIST = [32150]; 

var mhair = new Array();
var fhair = new Array();
var hairnew = new Array();

function start() {
    status = -1;
    var week = ChronoUnit.WEEKS.between ( 
            LocalDate.ofEpochDay ( 0 ) , 
            LocalDate.now( ZoneOffset.UTC ) 
        );

    if(cm.getPlayer().getGender() == 0) {
        initMaleHair(week);
        print("Male hairs of the week:" + mhair);
    } else if(cm.getPlayer().getGender() == 1) {
        initFemaleHair(week);
        print("Female hairs of the week:" + fhair);
    }
    
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

function initMaleHair(week) {
    var prng = new Random(week * BASE_HAIR_VALUE * 'm');

    // Gender Based Choice
    for(var i = 0; i < NUM_CHOICES_GENDER_BASED; i++) {
        while(true) {
            var randomHair = (prng.nextInt(MAX_HAIR_TYPE_1/10 - MIN_HAIR_TYPE_1/10) + MIN_HAIR_TYPE_1/10) * 10;
            if(isMaleHairType1(randomHair) && 
                MapleItemInformationProvider.getInstance().getName(randomHair) != null &&
                !checkExists(mhair, randomHair) &&
                !checkExists(BLACK_LIST, randomHair)) 
            {
                mhair.push(randomHair);
                break;
            }
        }
    }

    // Universal Based Choice
    for(var i = 0; i < NUM_UNIVERSAL_CHOICES; i++) {
        var retry = 0;
        while(true) {
            var randomHair = (prng.nextInt(MAX_HAIR_TYPE_2/10 - MIN_HAIR_TYPE_2/10) + MIN_HAIR_TYPE_2/10) * 10;
            if(MapleItemInformationProvider.getInstance().getName(randomHair) != null &&
                !checkExists(mhair, randomHair) &&
                !checkExists(BLACK_LIST, randomHair)) 
            {
                mhair.push(randomHair);
                break;
            } else if(retry > 1000) {
                break;
            }
            else {
                retry++;
            }
        }
    }
}

function initFemaleHair(week) {
    var prng = new Random(week * BASE_HAIR_VALUE * 'f');

    // Gender Based Choice
    for(var i = 0; i < NUM_CHOICES_GENDER_BASED; i++) {
        var retry = 0;
        while(true) {
            var randomHair = (prng.nextInt(MAX_HAIR_TYPE_1/10 - MIN_HAIR_TYPE_1/10) + MIN_HAIR_TYPE_1/10) * 10;
            if(!isMaleHairType1(randomHair) && 
                MapleItemInformationProvider.getInstance().getName(randomHair) != null &&
                !checkExists(fhair, randomHair) &&
                !checkExists(BLACK_LIST, randomHair)) 
            {
                fhair.push(randomHair);
                break;
            } else if(retry > 100) {
                break;
            }
            else {
                retry++;
            }
        }
    }

    // Universal Based Choice
    for(var i = 0; i < NUM_UNIVERSAL_CHOICES; i++) {
        while(true) {
            var randomHair = (prng.nextInt(MAX_HAIR_TYPE_2/10 - MIN_HAIR_TYPE_2/10) + MIN_HAIR_TYPE_2/10) * 10;
            if(MapleItemInformationProvider.getInstance().getName(randomHair) != null &&
                !checkExists(fhair, randomHair) &&
                !checkExists(BLACK_LIST, randomHair)) {
                fhair.push(randomHair);
                break;
            }
        }
    }
}

function isMaleHairType1(id) {
    if (id % 10 != 0) {
        return false;
    }
    if (id == 33030 || id == 33160 || id == 33590) {
        return false;
    }
    if (id / 1000 == 30 || id / 1000 == 33 || (id / 1000 == 32 && id >= 32370) || id / 1000 == 36 || (id / 1000 == 37 && id >= 37160 && id <= 37170)) {
        return true;
    }
    switch (id) {
        case 32160:
        case 32330:
        case 34740:
            return true;
    }
    return false;
}

function checkExists(array, element) {
    for(var i = 0; i < array.length; i++) {
        if(array[i] == element) {
            return true;
        }
    }

    return false;
}