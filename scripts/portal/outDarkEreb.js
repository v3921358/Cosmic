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

var questStarted;
var EleanorKillCount, mobsAlive;
var eim;

function enter(pi){

    questStarted = pi.isQuestStarted(20407);
    questFinished = pi.isQuestCompleted(20407);
    EleanorKillCount = pi.getQuestProgress(20407, 9001010);
    EleanorAlive = (pi.getMap().countMonster(9001010) > 0);
    eim = pi.getEventInstance();

    //Quest is not started || quest is started, but Eleanor has not spawned yet || quest started and Eleanor had been defeated
    if((!questStarted && !questFinished) || (questStarted && EleanorKillCount == 0 && !EleanorAlive) || (questStarted && EleanorKillCount > 0)){
        eim.stopEventTimer();
        eim.dispose();

        //Taking player back to the map with the Fallen Knight
        pi.playPortalSound();
        pi.warp(924010100, 1);
        return true;
    }
    //Quest started AND Eleanor had been spawned but not yet defeated
    else if(questStarted && EleanorKillCount == 0 && EleanorAlive){
        pi.getPlayer().message("You sworn an oath to defeat the Black Mage, stand your ground and FIGHT!");   //No pussy
        return false;
    }
    //Quest done
    else if(questFinished){
        eim.stopEventTimer();
        eim.dispose();

        //Taking player to Ereve
        pi.playPortalSound();
        pi.warp(130000000, 1);
        return true;
    }
    else{
        pi.getPlayer().message("Hmm... You shouldn't be in here");
        pi.playPortalSound();
        pi.warp(130000000, 1);
        return true;
    }
}