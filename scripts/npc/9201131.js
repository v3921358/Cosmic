/*
	Demon's Doorway
	The Final Battle with Amdusias
	Victoria Road: Henesys Hunting Ground III
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
		cm.sendNext("#r\t[Requirements to Enter]\r\n\r\n\t\t1.#k Job must be a Bowman or Wind Archer.\r\n\t\t#r2.#k Must be under level 40.\r\n\t\t#r3.#k Must have #b#t4032492##k.");
	}
	else if (status == 1) {
		var jobId = cm.getJobId();
		if((jobId >= 300 && jobId <= 322) || (jobId >= 1300 && jobId <= 1312) && 
			cm.getLevel() < 40 &&
			cm.hasItem(4032492)) {
			cm.sendOk("#kAll conditions have been satisfied. Do you wish to enter?");
		}
		else {
			cm.sendOk("\tPlease check if you fulfill all the requirements!");
			cm.dispose();
		}
	}
	else if (status == 2) {
		cm.warp(677000002, 2);
		cm.dispose();
	}
}