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
	QUEST: 					Chief Knight of the Empress
	NPC: 					Cygnus (1101000)
	MAP: 					Ereve (130000000)
	DESC.: 					End of the Cygnus Knights level 120 Ultimate Adventurer quest chain.

	AUTHOR: 				Rayden (DietStory)
	VERSION: 				MapleSolaxiaV2 (v83)
	MODIFIED: 				2017-10-11
*/

var status = -1;

function start(mode, type, selection){
    if(mode == -1 || (mode == 0 && type == 12)){    //Terminates conversation if player clicks 'End Chat' or 'Decline' button.
        qm.dispose();
        return;
    }
    else if(mode == 0)                              //Goes back when 'Prev' is clicked.
        status--;
    else
        status++;


    if(status == 0){
        qm.sendNext("#h #... First of all, thank you for your great work. IF it weren't for you, I... I wouldn't be safe from the curse of the Black Witch. Thank you so much.");
    }
    else if(status == 1){
        qm.sendNextPrev("If nothing else, this chain of events makes on thing crystal clear, you have put in countless hours of hard work to better yourself and contribute to the Cygnus Knights.");
    }
    else if(status == 2){
        qm.sendAcceptDecline("To celebrate your hard work and accomplishments... I would like to award you a new title. Will you... accept this?");
    }
    else if(status == 3){
        if(qm.canHold(1142069)){
            qm.forceStartQuest();
            qm.forceCompleteQuest();
            qm.gainItem(1142069, 1);
            qm.sendOk("#h #. For courageously battling the Black Mage, I will appoint you as the new Chief Knight of Cygnus Knights from this moment forward. Please use your power and authority wisely to help protect the citizens of Maple World.");
        }
        else{
            qm.sendOk("Please free a space in your EQP inventory.");
        }
    }
    else if(status == 4){
        qm.dispose();
        return;
    }
}