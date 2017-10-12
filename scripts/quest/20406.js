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
	QUEST: 					The Knight That Disappeared
	NPC: 					Neinheart (1101002)
	MAP: 					Ereve (130000000)
	DESC.: 					Neinheart heard nothing from Dunamis, and asks the player to return to the Cave of Black Witches.

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
		qm.sendNext("Hmm? What are you doing here? Aren't you supposed to be out there helping Dunamis? ...What? Dunamis' mission is complete? Only a note has been left behind saying that he has already gone back to Ereve? What are you talking about? #rDunamis never returned to Ereve#k.");
	}
	else if(status == 1){
		qm.sendAcceptDecline("Something doesn't add up. The fact that Dunamis, who's ALWAYS on top of things, disappeared without leaving his contact information, was suspicious enough for me to assign you to him, and now... Hmmm... So you're saying that the last remnants of Dunamis is at the cave of Black Witch, right?");
	}
	else if(status == 2){
		qm.sendOk("Please re-enter #bBlack Witch's cave#k, and search for any other types of evidence left behind by Dunamis. Who knows, you may have missed something. We, too, will do our best to find him.");
		qm.forceStartQuest();
		qm.forceCompleteQuest();
	}
	else if(status == 3){
		qm.dispose();
		return;
	}
}

function end(mode, type, selection){}