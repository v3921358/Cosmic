var status;

function start(){
	status = -1;
	action(1, 0 , 0);
}

function action(mode, type, selection){
	if(mode == -1){
		cm.dispose();
		return;
	}
	else if(mode == 0 && status == 0){
		cm.dispose();
		return;
	}
	else if(mode == 0)
		status--;
	else
		status++;


	if(status == 0){
		if(cm.isQuestStarted(2332)){
			cm.sendOk("Thank you so much for finding me!");
			cm.completeQuest(2332);
			cm.gainExp(4400 * cm.getPlayer().getExpRate());
			cm.dispose();
		}
		else{
			cm.sendOk("*Sniff sniff*");
			cm.dispose();
		}
	}
}