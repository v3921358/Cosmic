/*	
	NPC Name: 		Luke
	Map: 			101000000
	Quest: 			Catching the Suspect
*/

var status;

function start(mode, type, selection) {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if(mode == -1 || (mode == 0 && status == 0)){
		cm.dispose();
		return;
	}

	if(mode == 0)
		status--;
	else
		status++;

	if(cm.isQuestStarted(28177)) {
		suspectDialogue(mode, type, selection);
	}
	else {
		defaultDialogue(mode, type, selection);
	}
}

// Catching the Suspect
function suspectDialogue(mode, type, selection) {
	if (status == 0) {
		if (!cm.hasItem(4032479) && cm.canHold(4032479)) {
			cm.gainItem(4032479, 1);
			cm.sendOk("Huh, are you looking for me? Chief Stan sent you here, right? But hey, I am not the suspect you seek. If I have some proof? Here, take this and return it to #b#p1012003##k.");
		}
		else if (!cm.hasItem(4032479)) {
			cm.sendOk("Hey, make a slot available before talking to me.");
		}
		else {
			cm.sendOk("I trust my badge was convincing enough.");
		}
		cm.dispose();
	}
} 

// default
function generalDialogue(mode, type, selection) {
	if(status == 0) {
		cm.sendOk("Gah! I wasn't sleeping!\n\n\nWasn't ... sleeping ... Zzzzz");
		cm.dispose();	
	}
}