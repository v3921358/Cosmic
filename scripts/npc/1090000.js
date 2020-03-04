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
/* Kyrin
	Pirate Job Advancement
	
	Custom Quest 100009, 100011
*/

status = -1;
actionx = {"1stJob" : false, "2ndjob" : false, "2ndjobT" : false, "3thJobI" : false, "3thJobC" : false};
job = 510;
jobType = 5;

var advQuest = 0;
function start() {
    if (cm.isQuestStarted(6330)) {
        if (cm.getEventInstance() != null) {    // missing script for skill test found thanks to Jade™
            advQuest = 5;                       // string visibility thanks to iPunchEm & Glvelturall
            cm.sendNext("Not bad at all. Let's discuss this outside!");
        } else if (cm.getQuestProgress(6330, 6331) == 0) {
            advQuest = 1;
            cm.sendNext("You're ready, right? Now try to withstand my attacks for 2 minutes. I won't go easy on you. Good luck, because you will need it.");
        } else {
            advQuest = 3;
            cm.teachSkill(5121003, 0, 10, -1);
            cm.completeQuest(6330);
            
            cm.sendNext("Congratulations. You have managed to pass my test. I'll teach you a new skill called \"Super Transformation\".\r\n\r\n  #s5121003#    #b#q5121003##k");
        }
    } else if (cm.isQuestStarted(6370)) {
        if (cm.getEventInstance() != null) {
            advQuest = 6;
            cm.sendNext("Not bad at all. Let's discuss this outside!");
        } else if (cm.getQuestProgress(6370, 6371) == 0) {
            advQuest = 2;
            cm.sendNext("You're ready, right? Now try to withstand my attacks for 2 minutes. I won't go easy on you. Good luck, because you will need it.");
        } else {
            advQuest = 4;
            cm.teachSkill(5221006, 0, 10, -1);
            cm.completeQuest(6370);
            
            cm.sendNext("Congratulations. You have managed to pass my test. I'll teach you a new skill called \"Battleship\".\r\n\r\n  #s5221006#    #b#q5221006##k");
        }
    } else {
        if (cm.getJobId() == 0) {
            actionx["1stJob"] = true;
            //cm.sendNext("Want to be a #rpirate#k? There are some standards to meet. because we can't just accept EVERYONE in... #bYour level should be at least 10, with " + cm.getFirstJobStatRequirement(jobType) + " minimum#k. Let's see.");   // thanks Vcoc for noticing a need to state and check requirements on first job adv starting message
            cm.sendNext("Want to be a #rpirate#k? There are some standards to meet. because we can't just accept EVERYONE in... #bYour level should be at least 10, with 20 DEX minimum#k. Let's see.");
        } else if (cm.getLevel() >= 30 && cm.getJobId() == 500) {
            actionx["2ndJob"] = true;
            if (cm.isQuestCompleted(2191) || cm.isQuestCompleted(2192))
                cm.sendNext("I see you have done well. I will allow you to take the next step on your long road.");
            else
                cm.sendNext("The progress you have made is astonishing.");
        } else if (actionx["3thJobI"] || (cm.getPlayer().gotPartyQuestItem("JB3") && cm.getLevel() >= 70 && cm.getJobId() % 10 == 0 && parseInt(cm.getJobId() / 100) == 5 && !cm.getPlayer().gotPartyQuestItem("JBP"))){
            actionx["3thJobI"] = true;
            cm.sendNext("There you are. A few days ago, #b#p2020013##k of Ossyria talked to me about you. I see that you are interested in making the leap to the world of the third job advancement for pirates. To achieve that goal, I will have to test your strength in order to see whether you are worthy of the advancement. There is an opening in the middle of a cave on Victoria Island, where it'll lead you to a secret passage. Once inside, you'll face a clone of myself. Your task is to defeat him and bring #b#t4031059##k back with you.");
        } else if (cm.getPlayer().gotPartyQuestItem("JBP") && !cm.haveItem(4031059)){
            cm.sendNext("Please, bring me the #b#t4031059##k.");
            cm.dispose();
        } else if (cm.haveItem(4031059) && cm.getPlayer().gotPartyQuestItem("JBP")){
            actionx["3thJobC"] = true;
            cm.sendNext("Nice work. You have defeated my clone and brought #b#t4031059##k back safely. You have now proven yourself worthy of the 3rd job advancement from the physical standpoint. Now you should give this necklace to #b#p2020013##k in Ossyria to take on the second part of the test. Good luck. You'll need it.");
        } else {
            cm.sendOk("You have chosen wisely.");
            cm.dispose();
        }
    }
}

function action(mode, type, selection) {
    status++;
    if (mode == 0 && type != 1)
        status -= 2;
if (status == -1){
        start();
        return;
    } else {
        if (advQuest > 0) {
            if (advQuest < 3) {
                var em = cm.getEventManager(advQuest == 2 ? "4jship" : "4jsuper");
                if(!em.startInstance(cm.getPlayer())) {
                    cm.sendOk("Someone is already challenging the test. Please try again later.");
                }
            } else if (advQuest < 5) {
                if (advQuest == 3) {
                    cm.sendOk("It is similar to that of 'Transformation', but it's much more powerful than that. Keep training, and hope to see you around.");
                } else {
                    cm.sendOk("Unlike most of the other skills you used as a Pirate, this one definitely is different. You can actually ride the 'Battleship' and attack enemies with it. Your DEF level will increase for the time you're on board, so that'll help you tremendously in combat situations. May you become the best Gunslinger out there...");
                }
            } else {
                if (advQuest < 6) {
                    cm.setQuestProgress(6330, 6331, 2);
                } else {
                    cm.setQuestProgress(6370, 6371, 2);
                }

                cm.warp(120000101);
            }
            
            cm.dispose();
        } else {
            if (mode != 1 || status == 7 && type != 1 || (actionx["1stJob"] && status == 4) || (cm.haveItem(4031008) && status == 2) || (actionx["3thJobI"] && status == 1)){
                if (mode == 0 && status == 2 && type == 1)
                    cm.sendOk("You know there is no other choice...");
                if (!(mode == 0 && type != 1)){
                    cm.dispose();
                    return;
                }
            }
        }
    }
    if (actionx["1stJob"]){
        if (status == 0) {
            //if (cm.getLevel() >= 10 && cm.canGetFirstJob(jobType)) {
            if (cm.getLevel() >= 10 && cm.getJobId() == 0) {
                cm.sendYesNo("Oh...! You look like someone that can definitely be a part of us... all you need is a little slang, and... yeah... so, what do you think? Wanna be the Pirate?");
            } else {
                cm.sendOk("Train a bit more until you reach the base requirements and I can show you the way of the #rPirate#k.");
                cm.dispose();
            }
        } else if (status == 1){
            if (cm.canHold(2070000) && cm.canHold(1472061)){
                if (cm.getJobId() == 0){
                    cm.changeJobById(500);
                    cm.gainItem(1492000, 1);
					cm.gainItem(1482000, 1);
					cm.gainItem(2330000, 1000);
                    cm.resetStats();
                    if(cm.getPlayer().getMentorId() > 0)
                    {
                        cm.teachSkill(15111007, 30, 30, -1);
                        cm.getPlayer().remapUASkill(88);
                    }
                }
                cm.sendNext("Alright, from here out, you are a part of us! You'll be living the life of a wanderer at ..., but just be patient as soon, you'll be living the high life. Alright, it ain't much, but I'll give you some of my abilities... HAAAHHH!!!");
            } else {
                cm.sendNext("Make some room in your inventory and talk back to me.");
                cm.dispose();
            }
        } else if (status == 2) 
            cm.sendNextPrev("You've gotten much stronger now. Plus every single one of your inventories have added slots. A whole row, to be exact. Go see for it yourself. I just gave you a little bit of #bSP#k. When you open up the #bSkill#k menu on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
        else if (status == 3)
        {
            if(cm.getPlayer().getMentorId() > 0)
            {
                cm.sendNextPrev("Feel the electric rush. Harness the energy as an #bUltimate Explorer#k to reach even greater heights! Also make sure to re-log and check your #bF12#k key!");    
            }
            else
            {
                cm.sendNextPrev("Good luck.");
            }
        }
        else if (status == 4)
            cm.sendNextPrev("One more warning. Once you have chosen, you (Incomplete)");
        else
            cm.dispose();
    } else if(actionx["2ndJob"]){
        if (status == 0){
            if (cm.isQuestCompleted(2191) || cm.isQuestCompleted(2192))
                cm.sendSimple("Alright, when you have made your decision, click on [I'll choose my occupation] at the bottom.#b\r\n#L0#Please explain to me what being the Brawler is all about.\r\n#L1#Please explain to me what being the Gunslinger is all about.\r\n#L3#I'll choose my occupation!");
            else
                cm.sendNext("Good decision. You look strong, but I need to see if you really are strong enough to pass the test, it's not a difficult test, so you'll do just fine.");
        } else if (status == 1){
            if (!cm.isQuestCompleted(2191) && !cm.isQuestCompleted(2192)){
                // Pirate works differently from the other jobs. It warps you directly in.
				actionx["2ndJobT"] = true;
				cm.sendYesNo("Would you like to take the test now?");
            } else {
                if (selection < 3) {
                    cm.sendNext("Not done.");
                    status -= 2;
                } else
                    cm.sendNextPrev("You have a long road ahead of you still, but being a pirate will help you get there. Just keep that in mind and you will do fine.");
            }
        } else if (status == 2){
            if (actionx["2ndJobT"]) {
                var map = 0;
				if(cm.isQuestStarted(2191))
					map = 108000502;
				else
					map = 108000501;
                if(cm.getPlayerCount(map) > 0) {
					cm.sendOk("All the training maps are currently in use. Please try again later.");
					cm.dispose();
				} else {
					cm.warp(map);
					cm.dispose();
					return;
                }
            } else {
				if(cm.isQuestCompleted(2191))
					job = 510;
				else if(cm.isQuestCompleted(2192))
					job = 520;
					
				cm.sendYesNo("So you want to make the second job advancement as the " + (job == 510 ? "#bBrawler#k" : "#bGunslinger#k") + "? You know you won't be able to choose a different job for the 2nd job advancement once you make your decision here, right?");
			}
		} else if (status == 3){
            if (cm.haveItem(4031012))
                cm.gainItem(4031012, -1);
            
            if(job == 510) cm.sendNext("From here on out, you are a #bBrawler#k. Brawlers rule the world with the power of their bare fists...which means they need to train their body more than others. If you have any trouble training, I'll be more than happy to help.");
            else cm.sendNext("From here on out, you are a #bGunslinger#k. Gunslingers are notable for their long-range attacks with sniper-like accuracy and of course, using Guns as their primary weapon. You should continue training to truly master your skills. If you are having trouble training, I'll be here to help.");
            
            if (cm.getJobId() != job)
                cm.changeJobById(job);
        } else if (status == 4)
            cm.sendNextPrev("I have just given you a book that gives you the list of skills you can acquire as a " + (job == 510 ? "brawler" : "gunslinger") + ". Also your etc inventory has expanded by adding another row to it. Your max HP and MP have increased, too. Go check and see for it yourself.");
        else if (status == 5)
            cm.sendNextPrev("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. you'll be able to boost up the newer acquired 2nd level skills. A word of warning, though. You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure yo remember that.");
        else if (status == 6)
            cm.sendNextPrev((job == 510 ? "Brawlers" : "Gunslingers") + " need to be strong. But remember that you can't abuse that power and use it on a weaking. Please use your enormous power the right way, because... for you to use that the right way, that is much harden than just getting stronger. Please find me after you have advanced much further. I'll be waiting for you.");
    } else if (actionx["3thJobI"]){
        if (status == 0){
            if (cm.getPlayer().gotPartyQuestItem("JB3")){
                cm.getPlayer().removePartyQuestItem("JB3");
                cm.getPlayer().setPartyQuestItemObtained("JBP");
            }
            cm.sendNextPrev("Since he is a clone of myself, you can expect a tough battle ahead. He uses a number of special attacking skills unlike any you have ever seen, and it is your task to successfully take him one on one. There is a time limit in the secret passage, so it is crucial that you defeat him within the time limit. I wish you the best of luck, and I hope you bring the #b#t4031059##k with you.");
        }
    } else if (actionx["3thJobC"]){
        cm.getPlayer().removePartyQuestItem("JBP");
        cm.gainItem(4031059, -1);
        cm.gainItem(4031057, 1);
        cm.dispose();
    }
}