/*
	Demon's Doorway
	Treasure, and Crocell the Demon
	Victoria Road: The Forest East of Henesys
*/

var status;

function start(){
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection){
	if (mode == -1 || (mode == 0 && status == 0)) {
		cm.dispose();
		return;
	}

	if (mode == 1)
		status++;
	else
		status--;

	if(status == 0) {
		cm.sendNext("#r\t[Requirements to Enter]\r\n\r\n\t\t1.#k Job must be a Pirate or Thunder Breaker.\r\n\t\t#r2.#k Must be under level 40.\r\n\t\t#r3.#k Must have #b#t4032494##k.");
	}
	else if (status == 1) {
		var jobId = cm.getJobId();
		if(((jobId >= 500 && jobId <= 522) || (jobId >= 1500 && jobId <= 1512)) && 
			cm.getLevel() < 40 &&
			cm.hasItem(4032494)) {
			cm.sendOk("#kAll conditions have been satisfied. Do you wish to enter?");
		}
		else {
			cm.sendOk("\tPlease check if you fulfill all the requirements!");
			cm.dispose();
		}
	}
	else if (status == 2) {
		cm.warp(677000006, 2);
		cm.dispose();
	}
}