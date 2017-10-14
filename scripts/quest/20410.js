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
	QUEST: 					Ultimate Explorer
	NPC: 					Cygnus (1101000)
	MAP: 					Ereve (130000000)
	DESC.: 					The quest that actually makes the Ultimate Explorer.

	AUTHOR: 				Rayden (DietStory)
	VERSION: 				MapleSolaxiaV2 (v83)
	MODIFIED: 				2017-10-13
*/

importPackage(Packages.net.server.handlers.login);

var status = -1;
var name;
var success;

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
        qm.sendNext("Hello, Chief Knight. Currently, Maple World is in great danger. We need a bigger army to protect this place from the Black Mage. And to build a stronger army, I decide to ally with the Explorer Chiefs. We created the Ultimate Explorer with our combined powers.");
    }
    else if(status == 1){
        qm.sendAcceptDecline("The Ultimate Explorer starts at Lv. 50 and is born with very special skills. Would you like to be reborn as an Ultimate Explorer?");
    }
    else if(status == 2){
        qm.sendGetText("What is the name of your Ultimate Explorer?");
    }
    else if(status == 3){
        name = qm.getText();

        /* CREATE CHARACTER HERE!!! */
        success = CreateCharHandler.CreateUltimateExplorer(qm.getClient(), qm.getPlayer().getGender() == 0, name);
        

        if(success){
            qm.forceStartQuest();
            qm.forceCompleteQuest();
            qm.sendOk("You've created an Ultimate Explorer. You can select your Ultimate Explorer if you reboot the client. If you delete your Ultimate Explorer, you #rcannot#k create it again.");
        }
        else{
            qm.sendOk("Failed to create new character.\r\n\r\nEither the name is already taken, or you do not have any available character slots.");
        }
    }
    else if(status == 4){
        qm.dispose();
        return;
    }
}