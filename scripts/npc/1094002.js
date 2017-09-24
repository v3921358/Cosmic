/*
    Author: Kevin (DietStory v1.02)
    NPC: Bush - Abel Glasses Quest
*/

var status;

function start(mode, type, selection){
	status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

	if(mode == -1){
		cm.dispose();
	}
	else{
		if(mode == 0 && status == 0){
			cm.dipose();
			return;
		}

		if(mode == 1){
			status++;
		}
		else{
			status--;
		}

		if(status == 0){
			if(!cm.isQuestStarted(2186) || cm.hasItem(4031853)){
				cm.sendOk("You found nothing of interest...");
				cm.dispose();
			}
			else{
				cm.sendYesNo("You found #b#t4031853##k. Would you like to take #b#t4031853##k?  #i4031853#");
			}
		}
		if(status == 1){
			if(cm.getPlayer().canHold(4031853)){
				cm.gainItem(4031853, 1);
				cm.sendOk("You have taken #b#t4031853##k  #i4031853#");
			}
			else{
				cm.sendOk("No free inventory spot available. Please make room in your ETC inventory.");
			}
			cm.dispose();
		}
	}
}