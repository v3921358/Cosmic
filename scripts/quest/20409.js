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
	QUEST: 					Empress' Grace
	NPC: 					Cygnus (1101000)
	MAP: 					Ereve (130000000)
	DESC.: 					The quest which players collect Peridots for the Empress.

	AUTHOR: 				Rayden (DietStory)
	VERSION: 				MapleSolaxiaV2 (v83)
	MODIFIED: 				2017-10-13
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
        qm.sendNext("#b#h ##k, have you been looking for me?");
    }
    else if(status == 1){
        qm.sendNextPrev("#bYes, I heard I can become a stronger Cygnus Knight by receiving your grace.", 2);
    }
    else if(status == 2){
        qm.sendAcceptDecline("As the Empress of this place, I can bestow my grace upon my knights. However, the power of my grace has weakened. Would you help me restore my power?");
    }
    else if(status == 3){
        qm.forceStartQuest();
        qm.sendNext("My abilities come from a gem called Peridot. But because I've bestowed grace upon so many knights, its power is getting gradually weaker.");
    }
    else if(status == 4){
        qm.sendNextPrev("#bHarps#k and #bBlood Harps#k often carry Peridots that have been powered by the sun. Please bring #b10 Peridots#k to me.");
    }
    else if(status == 5){
        qm.dispose();
        return;
    }
}

function end(mode, type, selection){
    if(mode == -1 || (mode == 0 && type == 12)){    //Terminates conversation if player clicks 'End Chat' or 'Decline' button.
        qm.dispose();
        return;
    }
    else if(mode == 0)                              //Goes back when 'Prev' is clicked.
        status--;
    else
        status++;


    var peridotCount = qm.getItemQuantity(4032861);

    if(status == 0){
        if(peridotCount < 10)
            qm.sendOk("I need #b10 Peridots#k to regain my power, please.");
        else
            qm.sendNext("Did you bring the Peridots that have been powered by the sun? If so, I shall give you my grace.");
    }
    else if(status == 1){
        if(peridotCount < 10){
            qm.dispose();
            return;
        }
        else{
            qm.gainItem(4032861, -10);
            qm.sendOk("You were supposed to get some permanent buff, but it doesn't exist in this version. So, that's too bad LOL.");
        }
    }
    else if(status == 2){
        qm.dispose();
        return;
    }
}