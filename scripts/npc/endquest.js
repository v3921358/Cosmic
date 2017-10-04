importPackage(Packages.server.quest);

var status;
var startedQuestCount;
var tempId;
var allStartedQuestStatuses;
var questInfos = new Array();
var toEnd;

function start(){
	status = -1;
	tempId = 0;
	startedQuestCount = cm.getPlayer().getStartedQuestsSize(); //number of quest started
	allStartedQuestStatuses = cm.getPlayer().getStartedQuests(); //returns List<MapleQuestStatus>

	for(var i = 0; i < startedQuestCount; i++){
		tempId = allStartedQuestStatuses.get(i).getQuestID()

		questInfos.push({
			id: tempId,
			name: MapleQuest.getInstance(tempId).getName(),
			endNpc: MapleQuest.getInstance(tempId).getEndNPC()
		});
	}

	action(1, 0, 0);
}

function action(mode, type, selection){
	if(mode == -1 || (mode == 0 && status == 0)){
		cm.dispose();
		return;
	}
	else if (mode == 0)
		status--;
	else
		status++;


	if(status == 0){
		var str = "";

		for(var i = 0; i < questInfos.length; i++){
			str += "#L" +(i+1) +"#";
			str += "#e" +questInfos[i].id +"#n\t" +questInfos[i].name;
			str += "#l";
			str += "\r\n";
		}
		str = str.slice(0, -4);

		cm.sendSimple("Select a quest to end: \r\n#b" +str);
	}
	else if(status == 1){
		toEnd = questInfos[selection-1];
		cm.sendYesNo("Are you sure you want to terminate:\r\n#b#e" +questInfos[selection-1].id +"\t#n" +questInfos[selection-1].name);
	}
	else if(status == 2){
		MapleQuest.getInstance(toEnd.id).forceComplete(cm.getPlayer(), toEnd.endNpc);
		cm.dispose();
	}
}