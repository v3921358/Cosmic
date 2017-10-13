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
	NPC: 					Fallen Knight
	MAP: 					Cave of Black Witches (924010100)
	DESC.: 					Teleports the Cygnus Knight to fight the Black Witch.

	AUTHOR: 				Rayden (DietStory)
	VERSION: 				MapleSolaxiaV2 (v83)
	MODIFIED: 				2017-10-11
*/

var status;

function start(){
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection){
    var black_witch = cm.getEventManager("TheKnightThatDisappeared");
    black_witch.setProperty("player", cm.getPlayer().getName());
    black_witch.startInstance(cm.getPlayer());
    cm.dispose();
    return;
}