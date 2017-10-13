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
	QUEST: 					The Cave of the Black Witch
	NPC: 					Frightening Marble (2081013)
	MAP: 					Cave of Black Witches (924010000)
	DESC.: 					This quest sends you back to Ereve after talking to the crystal ball.

	AUTHOR: 				Rayden (DietStory)
	VERSION: 				MapleSolaxiaV2 (v83)
	MODIFIED: 				2017-10-11
*/

var status = -1;

function start(mode, type, selection){
	if(mode == -1 || (mode == 0 && type == 12)){ 	//Terminates conversation if player clicks 'End Chat' or 'Decline' button.
		qm.dispose();
		return;
	}
	else if(mode == 0)								//Goes back when 'Prev' is clicked.
		status--;
	else
		status++;


	if(status == 0){
		qm.sendAcceptDecline("#b(A crystal ball is placed at the center of the cave. It's so dark inside that it's barely visible. Let's get up close and see what is looks like.)");
	}
	else if(status == 1){
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.sendOk("#b(As soon as I touch the crystal, a rough, raspy voice entered.)\r\n#kThis is Dunamis. I was unable to spot the origin of the curse, but I did find the device used for it, so I'm sending it straight to Ereve. I'm leaving this message in case a Knight is sent here to search for items. You may now return to Ereve.");
	}
	else if(status == 2){
		qm.dispose();
		return;
	}
}

function end(mode, type, selection){}