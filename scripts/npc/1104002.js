/*
	This file is part of the DietStory Maple Story Server
    Copyright (C) 2017

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
	NPC: 					Eleanor
	MAP: 					Quiet Ereve (913030000)
	DESC.: 					Spawns a boss version of herself if quest conditions are met.

	AUTHOR: 				Rayden (DietStory)
	VERSION: 				MapleSolaxiaV2 (v83)
	MODIFIED: 				2017-10-11
*/

var status;
var map;
var instanceMap;
var eim;

function start(){
    status = -1;
    map = cm.getMap();
    action(1, 0, 0);
}

function action(mode, type, selection){
    if(mode == -1 || (mode == 0 && status == 0)){
        cm.dispose();
        return;
    }
    else if(mode == 0)
        status--;
    else
        status++;


    if(map.getId() != 913030000){
        cm.sendOk("I really should go, I'm short on time...");
        cm.dispose();
        map.destroyNPC(1104002);
        return;
    }
    else{
        if(status == 0){
            cm.sendNext("Ahahahaha! I thought Cygnus Knights were a group of impressive individuals, but this is way too easy. It doesn't matter how powerful Shinsoo is. Even the sturdiest dam cracks and gets demolished becaused of a small hole. If you use your head, it's that much easier to throw them off guard.");
        }
        else if(status == 1){
            cm.sendNextPrev("So what exactly did I do? Simple. I just gave this smart yet pompous knight false information. I told him the Black Witch is placing a curse on the Empress in the Dragon Forest. Well, it wasn't a total lie, since the curse did take place...");
        }
        else if(status == 2){
            cm.sendNextPrev("The curse only gets activated when the carrier of the curse enters Ereve, as in when Dunamis completed his investigation and brought the curse into the island.");
        }
        else if(status == 3){
            cm.sendNextPrev("We all knew that no matter how powerful the curse, it would bounce right out of Ereve... So why bother putting all the effort into penetrating the invisible barrier, when someone can actually bring that curse into Ereve. Hahahaha!");
        }
        else if(status == 4){
            cm.sendNextPrev("Cygnus Knights is really a group of inexperienced knights serving a 10 year old girl. They are no match for the Black Wings since we have served the Black Mage for hundreds of years.");
        }
        else if(status == 5){
            cm.sendNextPrev("It's time for you to step back as well. I don't feel like exerting too much energy into this.");
        }
        else if(status == 6){
            eim = cm.getEventInstance();
            instanceMap = eim.getInstanceMap(913030000);

            instanceMap.destroyNPC(1104002);
            instanceMap.spawnMonsterOnGroundBelow(9001010, -271, 87);

            cm.dispose();
        }
    }
}