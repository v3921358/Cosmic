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
	QUEST: 					Chasing the Knight's Target
	NPC: 					Neinheart (1101002)
	MAP: 					Ereve (130000000)
	DESC.: 					The first quest in a series of quests that allows Cygnus Knights to create an Ultimate Adventurer.

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
		qm.sendNext("It's been a while since I last saw you. I can't even recognize you now, seeing how powerful you have become since our last meeting. I can honestly say that you just might be one of the most powerful Knights in all of Cygnus Knights, Chief Knights included. Okay, enough pleasantries. Let's get down to business.");
	}
	else if(status == 1){
		qm.sendNextPrev("It's a new mission. According to the information we acquired, a member of the #rBlack Wings#k is targeting the Empress. In order to prevent that, Advanced Knight #bDunamis#k has been secretly tracing that individual, but it doesn't look too good from here.");
	}
	else if(status == 2){
		qm.sendAcceptDecline("If it's Victoria Island, at least we know everything that goes on there. This one's Ossyria, where not even the intelligence officials know everything inside out. This means the Advanced Knight will need help. Please provide help to Dunamis. The last place she contacted was at #b#m211000000##k, so try looking for Dunamis.");
	}
	else if(status == 3){
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.sendOk("Well, I may have said it in a joking manner, but it is true that you are one of the most talented knights in all of Cygnus Knights. That's why an important mission like this is given to a talented individual in Cygnus Knights. I believe in you. Good Luck.");
	}
	else if(status == 4){
		qm.dispose();
		return;
	}
}

function end(mode, type, selection){}