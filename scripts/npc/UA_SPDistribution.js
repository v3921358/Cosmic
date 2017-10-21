importPackage(Packages.constants.skills);
importPackage(Packages.client);

var status;
var player;
var remainingSP;
var SPSpentCurrentJob;
var skills;
var job;
var master;

function start(){
	status = -1;
	player = cm.getPlayer();
	job = player.getJob();


	action(1, 0, 0);
}

function action(mode, type, selection){
	if(mode == -1){
		cm.dispose();
		return;
	}
	else if(mode == 0)
		status--;
	else
		status++;


	if(status == 0){
		remainingSP = player.getRemainingSp();
		playerSkills = player.getSkills();
		initializeMaster();
		SPSpentCurrentJob = calculateSPSpent();
		filterMaster();
		calculateCanLevel();

		var str = generateOptionString();
		cm.sendSimple("Skill Point: #b" +remainingSP +"#k\r\n" +str +space(1));
	}
	else if(status == 1){
		if(remainingSP == 0){
			cm.dispose();
			return;
		}
		else{
			var levelOffset = 0;
			var bonusSP = 1;

			if(job.getId() % 100 == 0 && job.getId()/100 == 2){
				levelOffset = 8;
			}
			else if(job.getId() % 100 == 0){
				levelOffset = 10;
			}
			else if(job.getId() % 10 == 0){
				levelOffset = 30;
			}
			else if(job.getId() % 10 == 1){
				levelOffset = 70;
			}
			else if(job.getId() % 10 == 2){
				levelOffset = 120;
				bonusSP = 3;
			}
			else if(job.getId() == 900 || job.getId() == 910){
				bonusSP = Infinity;
			}



			if((3*(player.getLevel() - levelOffset) + bonusSP) - (SPSpentCurrentJob + remainingSP) < 0){
				cm.sendOk("You have unspent SP from previous job. You can add a total of #b" +(3*(player.getLevel() - levelOffset) + bonusSP) +" Skill Point(s)#k into your current job.");
			}
			else if(!master[selection-1].canLevel){
				cm.sendOk("You cannot add a point into this skill. Either you lack skill prerequisite or you need a skill book to level it further.");
			}
			else{
				status--;
				player.gainSp(-1);
				player.changeSkillLevel(master[selection-1].skill, master[selection-1].entry.skillevel + 1, master[selection-1].entry.masterlevel, master[selection-1].entry.expiration);

				remainingSP = player.getRemainingSp();
				playerSkills = player.getSkills();
				initializeMaster();
				SPSpentCurrentJob = calculateSPSpent();
				filterMaster();
				calculateCanLevel();

				var str = generateOptionString();
				cm.sendSimple("Skill Point: #b" +remainingSP +"#k\r\n" +str +space(1));
			}
		}
	}
	else if(status == 2){
		cm.dispose();
		return;
	}
}


function initializeMaster(){
	var allJobSkills = SkillFactory.getJobSkills(job);
	var jobSkillSet = allJobSkills.keySet().toArray();
	var jobSkillMiscInfo = allJobSkills.values().toArray();
	var playerSkillSet = playerSkills.keySet().toArray();
	var playerSkillEntry = playerSkills.values().toArray();
	master = new Array();

	for(let i = 0; i < jobSkillSet.length; i++){
		master.push({
			id: jobSkillSet[i].getId(),
			skill: jobSkillSet[i],
			canLevel: true,
			miscInfo: jobSkillMiscInfo[i],
			entry: {skillevel: 0, masterlevel: 0, expiration: -1}
		});
	}

	if(job.getId() % 10 == 2){

		var tempMaster = new Array();

		for(let i = 0; i < master.length; i++){
			for(let j = 0; j < playerSkillSet.length; j++){
				if(master[i].id == playerSkillSet[j].getId()){

					if(playerSkillEntry[j] != null){
						master[i].entry.skillevel = playerSkillEntry[j].skillevel;
						master[i].entry.masterlevel = playerSkillEntry[j].masterlevel;
						master[i].entry.expiration = playerSkillEntry[j].expiration;
					}

					tempMaster.push(master[i]);
					playerSkillSet.splice(j, 1);
					playerSkillEntry.splice(j, 1);
					break;
				}
			}
		}

		master = tempMaster;
	}
	else{
		for(let i = 0; i < master.length; i++){
			for(let j = 0; j < playerSkillSet.length; j++){
				if(playerSkillSet[j].getId() == master[i].id){

					if(playerSkillEntry[j] != null){
						master[i].entry.skillevel = playerSkillEntry[j].skillevel;
						master[i].entry.masterlevel = playerSkillEntry[j].masterlevel;
						master[i].entry.expiration = playerSkillEntry[j].expiration;
					}

					playerSkillSet.splice(j, 1);
					playerSkillEntry.splice(j, 1);
					break;
				}
			}
		}
	}
}

function filterMaster(){
	for(let i = 0; i < master.length; i++){
		if(master[i].entry.skillevel >= master[i].skill.getMaxLevel()){
			master.splice(i, 1);
		}
	}
}

function calculateCanLevel(){
	for(let i = 0; i < master.length; i++){
		if(master[i].miscInfo.getReqSkillId() != -1){
			for(let j = 0; j < master.length; j++){
				if((master[i].miscInfo.getReqSkillId() == master[j].id) && (master[j].entry.skillevel < master[i].miscInfo.getReqSkillLv())){
					master[i].canLevel = false;
					break;
				}
			}
		}
		else if((master[i].entry.masterlevel > 0) && (master[i].entry.skillevel >= master[i].entry.masterlevel)){
			master[i].canLevel = false;
		}
	}
}

function calculateSPSpent(){
	var retVal = 0;

	for(let i = 0; i < master.length; i++){
		retVal += master[i].entry.skillevel;
	}

	return retVal;
}

function generateOptionString(){
	var retStr = "";

	for(let i = 0; i < master.length; i++){
		retStr += "#L" +(i+1) +"#";

		if(master[i].canLevel == true)
			retStr += "#F" +master[i].miscInfo.getIcon() +"#" +space(4) +"#b[" +spacer(master[i].entry.skillevel) +master[i].entry.skillevel +"/" +master[i].skill.getMaxLevel() +"]" +space(1) +"#q" +master[i].id +"##k";
		else if(!master[i].canLevel && master[i].entry.skillevel > 0)
			retStr += "#F" +master[i].miscInfo.getIcon() +"#" +space(4) +"[" +spacer(master[i].entry.skillevel) +master[i].entry.skillevel +"/" +master[i].skill.getMaxLevel() +"]" +space(1) +"#q" +master[i].id +"#";
		else
			retStr += "#F" +master[i].miscInfo.getDisabledIcon() +"#" +space(4) +"[" +spacer(master[i].entry.skillevel) +master[i].entry.skillevel +"/" +master[i].skill.getMaxLevel() +"]" +space(1) +"#q" +master[i].id +"#";

		retStr += "#l\r\n";
	}

	return retStr;
}

function spacer(num){
	if(num < 10)
		return "  ";
	else
		return "";
}

function space(num){
	var retStr = "";

	for(let i = 0; i < num; i++){
		retStr += " ";
	}

	return retStr;
}