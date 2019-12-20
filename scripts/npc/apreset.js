importPackage(Packages.client);
importPackage(Packages.constants);

var status;
var player;
var ap_reset_count;
var from_attr;
var to_attr;
var starting_attr;
var delta;
var ending_attr;
var is_buying;
var jobId;
var resets_to_use;
var resets_to_buy;
var nx;
var total;
var failed;
var AP_RESET_ID = 5050000;
var AP_RESET_COST = 3100;
var MAX_STAT = 32767;
var MAX_HP_MP = 30000;


function start(){
	status = -1;
	player = cm.getPlayer();									// MapleCharacter
	delta = {hp: 0, mp: 0, str: 0, dex: 0, int_: 0, luk: 0};
	ending_attr = {hp: 0, mp: 0, str: 0, dex: 0, int_: 0, luk: 0};
	ap_reset_count = cm.getItemQuantity(AP_RESET_ID);			// int
	jobId= player.getJob().getId();								// MapleJob
	resets_to_use = 0;

	starting_attr = {
		hp: player.getMaxHp(),
		mp: player.getMaxMp(),
		str: player.getStr(),
		dex: player.getDex(),
		int_: player.getInt(),
		luk: player.getLuk()
	};

	nx = {
		prepaid: player.getCashShop().getCash(4), 	// Prepaid NX
		credit: player.getCashShop().getCash(1), 	// Credit NX
		use_prepaid: false,
		use_credit: false,
		prepaid_text: "",
		credit_text: ""
	}

	from_attr = {
		name: "",
		num: 0
	}

	to_attr = {
		name: "",
		num: 0
	}

	action(1, 0, 0);
}

function action(mode, type, selection){
	if(mode == -1 || (mode == 0 && type == 3) || (mode == 0 && type == 4)){
		cm.dispose();
		return;
	}
	else if(mode == 0)
		status--;
	else
		status++;



	if(status == 0){
		cm.sendSimple("Hi, #bAP Resets#k are bugged for some people. Until it is fixed, I am here to provide attribute-changing services.\r\n#L1#I would like to #buse#k AP Resets#l\r\n#L2#I would like to #bpurchase#k AP Resets#l");	
	}
	else if(status == 1){
		is_buying = (selection == 2);

		if(!is_buying){ 	// USING AP RESETS
			if(ap_reset_count > 0){
				cm.sendSimple("Select the attribute to #rremove#k a point:#r\r\n#L1#HP#l\r\n#L2#MP#l\r\n#L3#STR#l\r\n#L4#DEX#l\r\n#L5#INT#l\r\n#L6#LUK#l");
			}
			else{
				cm.sendOk("You don't have any #bAP Resets#k to use!");
				cm.dispose();
				return;
			}
		}
		else{	 // BUYING AP RESETS
			cm.sendGetNumber("Each #bAP Reset#k will cost #b3,100NX#k. How many would you like?", 1, 1, 1000000);
		}
	}
	else if(status == 2){
		if(!is_buying){ 	// USING AP RESETS
			from_attr.num = selection;

			if(selection == 1){
				from_attr.name = "HP";
				cm.sendSimple("Select the attribute to #badd#k a point:#b\r\n#L2#MP#l");
			}
			else if(selection == 2){
				from_attr.name = "MP";
				cm.sendSimple("Select the attribute to #badd#k a point:#b\r\n#L1#HP#l");
			}
			else if(selection == 3){
				from_attr.name = "STR";
				cm.sendSimple("Select the attribute to #badd#k a point:#b\r\n#L1#HP#l\r\n#L2#MP#l\r\n\r\n#L4#DEX#l\r\n#L5#INT#l\r\n#L6#LUK#l");
			}
			else if(selection == 4){
				from_attr.name = "DEX";
				cm.sendSimple("Select the attribute to #badd#k a point:#b\r\n#L1#HP#l\r\n#L2#MP#l\r\n#L3#STR#l\r\n\r\n#L5#INT#l\r\n#L6#LUK#l");
			}
			else if(selection == 5){
				from_attr.name = "INT";
				cm.sendSimple("Select the attribute to #badd#k a point:#b\r\n#L1#HP#l\r\n#L2#MP#l\r\n#L3#STR#l\r\n#L4#DEX#l\r\n\r\n#L6#LUK#l");
			}
			else if(selection == 6){
				from_attr.name = "LUK";
				cm.sendSimple("Select the attribute to #badd#k a point:#b\r\n#L1#HP#l\r\n#L2#MP#l\r\n#L3#STR#l\r\n#L4#DEX#l\r\n#L5#INT#l\r\n");
			}
		}
		else{ 	// BUYING AP RESETS
			resets_to_buy = selection;
			total = resets_to_buy * AP_RESET_COST;

			if(nx.prepaid >= total)
				nx.prepaid_text = "#g(" +nx.prepaid +"NX)#k";
			else
				nx.prepaid_text = "#r(" +nx.prepaid +"NX)#k";

			if(nx.credit >= total)
				nx.credit_text = "#g(" +nx.credit +"NX)#k";
			else
				nx.credit_text = "#r(" +nx.credit +"NX)#k";

			cm.sendSimple("Buying #b" +selection +" AP Reset(s)#k will cost you #b" +total +"NX#k. How would you like to pay?\r\n#L1#Using Prepaid " +nx.prepaid_text +"#l\r\n#L2#Using Credit " +nx.credit_text +"#l");
		}
	}
	else if(status == 3){
		if(!is_buying){ 	// USING AP RESETS
			to_attr.num = selection;

			if(selection == 1)
				to_attr.name = "HP"
			else if(selection == 2)
				to_attr.name = "MP"
			else if(selection == 3)
				to_attr.name = "STR"
			else if(selection == 4)
				to_attr.name = "DEX"
			else if(selection == 5)
				to_attr.name = "INT"
			else if(selection == 6)
				to_attr.name = "LUK"

			cm.sendSimple("#rRemove#k a point from #r" +from_attr.name +"#k and #badd#k one to #b" +to_attr.name +"#k?#b\r\n#L1#Yes.#l\r\n#L2#Yes, do it #emultiple#n times.#l");
		}
		else{ 	// BUYING AP RESETS
			if(selection == 1){ 	// BUYING USING PREPAID
				if(nx.prepaid >= total){
					if(cm.canHold(AP_RESET_ID, resets_to_buy)){
						player.getCashShop().gainCash(4, -1*total);
						cm.gainItem(AP_RESET_ID, resets_to_buy);
						cm.sendOk("Thank you.");
					}
					else{
						cm.sendOk("Please make sure you have enough inventory space in your CASH tab.");
					}
				}
				else{
					cm.sendOk("You don't have enough #bPrepaid NX#k to buy #b" +resets_to_buy +" AP Reset(s)#k.");
				}
			}
			else{ 	// BUYING USING CREDIT
				if(nx.credit >= total){
					if(cm.canHold(AP_RESET_ID, resets_to_buy)){
						player.getCashShop().gainCash(1, -1*total);
						cm.gainItem(AP_RESET_ID, resets_to_buy);
						cm.sendOk("Thank you.");
					}
					else{
						cm.sendOk("Please make sure you have enough inventory space in your CASH tab.");
					}
				}
				else{
					cm.sendOk("You don't have enough #NX Credit#k to buy #b" +resets_to_buy +" AP Reset(s)#k.");
				}
			}
		}
	}
	else if(status == 4){
		if(!is_buying){ 	// USING AP RESETS
			if(selection == 1){ 	// USE ONLY ONE AP RESET
				failed = false;
				resets_to_use = 1;

				// CHECKS TO MAKE SURE EVERYTHING IS IN ORDER
				if(from_attr.num == 1){		// REDUCING HP;
					delta.hp -= hpLost(1);
					failed = (starting_attr.hp + delta.hp <= 0);
				}
				else if(from_attr.num == 2){ 	// REDUCING MP
					delta.mp -= mpLost();
					failed = !canReduceMP(starting_attr.mp) && !ServerConstants.ALLOW_HP_WASHING;
				}
				else{ 	// OTHER ATTRIBUTES
					if(from_attr.num == 3) 
						delta.str -= 1;
					else if(from_attr.num == 4)
						delta.dex -= 1;
					else if(from_attr.num == 5)
						delta.int_ -= 1;
					else if(from_attr.num == 6)
						delta.luk -= 1;

					failed = !canReduceStat(from_attr.num);
				}

				if(failed){ 	// CANNOT REMOVE ATTRIBUTE POINT
					cm.sendOk("You cannot remove a point from that attribute because your stats are too low!");
				}
				else{ 	// CAN REMOVE ATTRIBUTE POINT
					if(to_attr.num == 1)
						delta.hp += hpGain(1);
					else if(to_attr.num == 2)
						delta.mp += mpGain(1);
					else if(to_attr.num == 3)
						delta.str += 1;
					else if(to_attr.num == 4)
						delta.dex += 1;
					else if(to_attr.num == 5)
						delta.int_ += 1;
					else if(to_attr.num == 6)
						delta.luk += 1;
					
					cm.gainItem(AP_RESET_ID, -1*resets_to_use);
					applyChanges();
					cm.sendOk(generateResultString());
				}
			}
			else if(selection == 2){ 	// USE MULTIPLE AP RESETS
				cm.sendGetNumber("How many times would you like to reset the selected attributes? (MAX: #b" +calculateMaxRep() +"#k)", 1, 1, ap_reset_count);
			}
		}
		else{ 	// END OF BUYING SCRIPT
			cm.dispose();
			return;
		}
	}
	else if(status == 5){
		if(resets_to_use == 1){ 	// END OF SINGLE USE RESET
			cm.dispose();
			return;
		}
		else{
			failed = false;
			resets_to_use = selection;

			// CHECKS TO MAKE SURE PLAYER CAN REMOVE ATTRIBUTES
			if(from_attr.num == 1){
				delta.hp -= hpLost(resets_to_use);
				failed = (starting_attr.hp + delta.hp <= 0);
			}
			else if(from_attr.num == 2){
				for(var i = 0; i < resets_to_use; i++){
					if(canReduceMP(starting_attr.mp + delta.mp)){
						delta.mp -= mpLost();
					}
					else{
						failed = true;
						break;
					}
				}
			}
			else{
				if(from_attr.num == 3) 
					delta.str -= resets_to_use;
				else if(from_attr.num == 4)
					delta.dex -= resets_to_use;
				else if(from_attr.num == 5)
					delta.int_ -= resets_to_use;
				else if(from_attr.num == 6)
					delta.luk -= resets_to_use;

				failed = !canReduceStat(from_attr.num);
			}

			if(failed){
				cm.sendOk("Cannot perform so many AP Resets because your stats are too low.");
			}
			else{
				if(to_attr.num == 1)
					delta.hp += hpGain(resets_to_use);
				else if(to_attr.num == 2)
					delta.mp += mpGain(resets_to_use);
				else if(to_attr.num == 3)
					delta.str += resets_to_use;
				else if(to_attr.num == 4)
					delta.dex += resets_to_use;
				else if(to_attr.num == 5)
					delta.int_ += resets_to_use;
				else if(to_attr.num == 6)
					delta.luk += resets_to_use;

				cm.gainItem(AP_RESET_ID, -1*resets_to_use);
				applyChanges();
				cm.sendOk(generateResultString());
			}
		}
	}
	else if(status == 6){
		cm.dispose();
		return;
	}
}







function hpLost(repetitions){
	var hp = 0;

	if(jobId ==  0 || jobId ==  1000 || jobId ==  2000) 	// BEGINNER or NOBLESSE or LEGEND
		hp += 12;
	else if(200 <= jobId && jobId <= 232 || 1200 <= jobId && jobId <= 1212) 	// MAGICIANS or BLAZEWIZARDS
		hp += 20;
	else if(300 <= jobId && jobId <= 322 || 1300 <= jobId && jobId <= 1312) 	// ARCHERS or WINDARCHERS
		hp += 20;
	else if(100 <= jobId && jobId <= 132 || 1100 <= jobId && jobId <= 1112) 	// WARRIORS or DAWNWARRIORS
		hp += 55;
	else if(400 <= jobId && jobId <= 422 || 1400 <= jobId && jobId <= 1412) 	// THEIVES or NIGHTWALKERS
		hp += 20;
	else if(510 <= jobId && jobId <= 512 || 1500 <= jobId && jobId <= 1512) 	// BRAWLERS or THUNDERBREAKERS
		hp += 40;
	else if(500 <= jobId && jobId <= 522) 	// PIRATES
		hp += 22;
	else if(2100 <= jobId && jobId <= 2112) 	// ARANS
		hp += 40;
	else
		hp += 20;

	hp = hp * repetitions;
	return hp;
}

function hpGain(repetitions){
	var	hp = 0;

	if(jobId ==  0 || jobId ==  1000 || jobId ==  2000) 	// BEGINNER or NOBLESSE or LEGEND
		hp += randomNum(8, 12);
	else if(200 <= jobId && jobId <= 232 || 1200 <= jobId && jobId <= 1212) 	// MAGICIANS or BLAZEWIZARDS
		hp += randomNum(10, 20);
	else if(300 <= jobId && jobId <= 322 || 1300 <= jobId && jobId <= 1312) 	// ARCHERS or WINDARCHERS
		hp += randomNum(16, 20);
	else if(100 <= jobId && jobId <= 132 || 1100 <= jobId && jobId <= 1112) 	// WARRIORS or DAWNWARRIORS
		hp += randomNum(50, 55);
	else if(400 <= jobId && jobId <= 422 || 1400 <= jobId && jobId <= 1412) 	// THEIVES or NIGHTWALKERS
		hp += randomNum(16, 20);
	else if(510 <= jobId && jobId <= 512 || 1500 <= jobId && jobId <= 1512) 	// BRAWLERS or THUNDERBREAKERS
		hp += randomNum(36, 40);
	else if(500 <= jobId && jobId <= 522) 	// PIRATES
		hp += randomNum(18, 22);
	else if(2100 <= jobId && jobId <= 2112) 	// ARANS
		hp += randomNum(36, 40);
	else
		hp += 20;

	hp = hp * repetitions;
	return hp;
}

function mpLost(){
	var mp = 0;

	if(jobId ==  0 || jobId ==  1000 || jobId ==  2000) 	// BEGINNER or NOBLESSE or LEGEND
		mp += 6;
	else if (200 <= jobId && jobId <= 232 || 1200 <= jobId && jobId <= 1212) 	// MAGICIANS or BLAZEWIZARDS
		mp += 90;
	else if (300 <= jobId && jobId <= 322 || 1300 <= jobId && jobId <= 1312) 	// ARCHERS or WINDARCHERS
		mp += 12;
	else if (100 <= jobId && jobId <= 132 || 1100 <= jobId && jobId <= 1112) 	// WARRIORS or DAWNWARRIORS
		mp += 4;
	else if (400 <= jobId && jobId <= 422 || 1400 <= jobId && jobId <= 1412) 	// THEIVES or NIGHTWALKERS
		mp += 12;
	else if (510 <= jobId && jobId <= 512 || 1500 <= jobId && jobId <= 1512) 	// BRAWLERS or THUNDERBREAKERS
		mp += 16;
	else if (500 <= jobId && jobId <= 522) 	// PIRATES
		mp += 16;
	else if (2100 <= jobId && jobId <= 2112) 	// ARANS
		mp += 5;
	else
		mp += 8;

	return mp;
}

function mpGain(repetitions){
	var mp = 0;

	if(jobId ==  0 || jobId ==  1000 || jobId ==  2000) 	// BEGINNER or NOBLESSE or LEGEND
		mp += 6;
	else if (200 <= jobId && jobId <= 232 || 1200 <= jobId && jobId <= 1212) 	// MAGICIANS or BLAZEWIZARDS
		mp += 90;
	else if (300 <= jobId && jobId <= 322 || 1300 <= jobId && jobId <= 1312) 	// ARCHERS or WINDARCHERS
		mp += 12;
	else if (100 <= jobId && jobId <= 132 || 1100 <= jobId && jobId <= 1112) 	// WARRIORS or DAWNWARRIORS
		mp += 4;
	else if (400 <= jobId && jobId <= 422 || 1400 <= jobId && jobId <= 1412) 	// THEIVES or NIGHTWALKERS
		mp += 12;
	else if (510 <= jobId && jobId <= 512 || 1500 <= jobId && jobId <= 1512) 	// BRAWLERS or THUNDERBREAKERS
		mp += 16;
	else if (500 <= jobId && jobId <= 522) 	// PIRATES
		mp += 16;
	else if (2100 <= jobId && jobId <= 2112) 	// ARANS
		mp += 5;
	else
		mp += 8;

	mp = mp * repetitions;
	return mp;
}

function canReduceMP(mp){
	if(mp < getMinMP())
		return false;
	else 
		return true;
}

function canReduceStat(id){
	if(id == 3)
		return (starting_attr.str + delta.str >= 4);
	else if(id == 4)
		return (starting_attr.dex + delta.dex >= 4);
	else if(id == 5)
		return (starting_attr.int_ + delta.int_ >= 4);
	else if(id == 6)
		return (starting_attr.luk + delta.luk >= 4);
	else
		return false;
}

function applyChanges(){
	var new_hp = 0;
	var new_mp = 0;

	// SUBTRACT ATTRIBUTE
	if(from_attr.num == 1){
		new_hp = player.getHp() + delta.hp;
		ending_attr.hp = starting_attr.hp + delta.hp;

		player.setHp(new_hp);
		player.updateSingleStat(MapleStat.HP, new_hp);
		player.setMaxHp(ending_attr.hp);
		player.updateSingleStat(MapleStat.MAXHP, ending_attr.hp);
	}
	else if(from_attr.num == 2){
		new_mp = player.getMp() + delta.mp;
		ending_attr.mp = starting_attr.mp + delta.mp;

		player.setMp(new_mp);
		player.updateSingleStat(MapleStat.MP, new_mp);
		player.setMaxMp(ending_attr.mp);
		player.updateSingleStat(MapleStat.MAXMP, ending_attr.mp);
	}
	else if(from_attr.num == 3)
		player.addStat(1, delta.str);
	else if(from_attr.num == 4)
		player.addStat(2, delta.dex);
	else if(from_attr.num == 5)
		player.addStat(3, delta.int_);
	else if(from_attr.num == 6)
		player.addStat(4, delta.luk);


	// ADD ATTRIBUTE (MAKE SURE THEY DONT GO OVER MAXIMUM STAT)
	ending_attr.hp = ((starting_attr.hp + delta.hp) > MAX_HP_MP)? MAX_HP_MP:(starting_attr.hp + delta.hp);
	ending_attr.mp = ((starting_attr.mp + delta.mp) > MAX_HP_MP)? MAX_HP_MP:(starting_attr.mp + delta.mp);
	ending_attr.str = ((starting_attr.str + delta.str) > MAX_STAT)? MAX_STAT:(starting_attr.str + delta.str);
	ending_attr.dex = ((starting_attr.dex + delta.dex) > MAX_STAT)? MAX_STAT:(starting_attr.dex + delta.dex);
	ending_attr.int_ = ((starting_attr.int_ + delta.int_) > MAX_STAT)? MAX_STAT:(starting_attr.int_ + delta.int_);
	ending_attr.luk = ((starting_attr.luk + delta.luk) > MAX_STAT)? MAX_STAT:(starting_attr.luk + delta.luk);

	if(to_attr.num == 1){
		new_hp = ((player.getHp() + delta.hp) > MAX_HP_MP)? MAX_HP_MP:(player.getHp() + delta.hp);
		
		player.setHp(new_hp);
		player.updateSingleStat(MapleStat.HP, new_hp);
		player.setMaxHp(ending_attr.hp);
		player.updateSingleStat(MapleStat.MAXHP, ending_attr.hp);
	}
	else if(to_attr.num == 2){
		new_mp = ((player.getMp() + delta.mp) > MAX_HP_MP)? MAX_HP_MP:(player.getMp() + delta.mp);

		player.setMp(new_mp);
		player.updateSingleStat(MapleStat.MP, new_mp);
		player.setMaxMp(ending_attr.mp);
		player.updateSingleStat(MapleStat.MAXMP, ending_attr.mp);
	}
	else if(to_attr.num == 3)
		player.setStr(ending_attr.str);
	else if(to_attr.num == 4)
		player.setDex(ending_attr.dex);
	else if(to_attr.num == 5)
		player.setInt(ending_attr.int_);
	else if(to_attr.num == 6)
		player.setLuk(ending_attr.luk);
}

function generateResultString(){
	var retStr = "Your stats have changed! Here's what happened...\r\n";

	if(delta.hp == 0)
		retStr += getUnchangedString("HP", ending_attr.hp, 7);
	else
		retStr += getChangedString("HP", starting_attr.hp, ending_attr.hp, 7);

	if(delta.mp == 0)
		retStr += getUnchangedString("MP", ending_attr.mp, 7);
	else
		retStr += getChangedString("MP", starting_attr.mp, ending_attr.mp, 7);

	if(delta.str == 0)
		retStr += getUnchangedString("STR", ending_attr.str, 5);
	else
		retStr += getChangedString("STR", starting_attr.str, ending_attr.str, 5);

	if(delta.dex == 0)
		retStr += getUnchangedString("DEX", ending_attr.dex, 5);
	else
		retStr += getChangedString("DEX", starting_attr.dex, ending_attr.dex, 5);

	if(delta.int_ == 0)
		retStr += getUnchangedString("INT", ending_attr.int_, 7);
	else
		retStr += getChangedString("INT", starting_attr.int_, ending_attr.int_, 7);

	if(delta.luk == 0)
		retStr += getUnchangedString("LUK", ending_attr.luk, 5);
	else
		retStr += getChangedString("LUK", starting_attr.luk, ending_attr.luk, 5);

	return retStr.slice(0, -2);
}

function getUnchangedString(name, end_val, spaces){
	return "#k" +name +":" +generateSpace(spaces) +end_val +" (unchanged)\r\n";
}

function getChangedString(name, start_val, end_val, spaces){
	if(start_val > end_val){
		return "#r" +name +"#k:" +generateSpace(spaces) +start_val +" ---> #r" +end_val +" (-" +Math.abs(end_val - start_val) +")#k\r\n";
	}
	else{
		return "#b" +name +"#k:" +generateSpace(spaces) +start_val +" ---> #b" +end_val +" (+" +Math.abs(end_val - start_val) +")#k\r\n";
	}
}

function generateSpace(num){
	var retStr = "";

	for(var i = 0; i < num; i++)
		retStr += " ";

	return retStr
}

function randomNum(lower, upper){
	return Math.floor(((upper - lower) + 1) * Math.random() + lower);
}

function getMinMP(){
	var level = player.getLevel();

	if(jobId ==  0 || jobId ==  1000 || jobId ==  2000) 							// BEGINNER or NOBLESSE or LEGEND
		return 10*level+2;
	else if(130 <= jobId && jobId <= 132) 											// SPEARMAN
		return 4*level+156;
	else if((112 <= jobId && jobId <= 122) || (1100 <= jobId && jobId <= 1112)) 	// FIGHTER or PAGE or DAWNWARRIORS
		return 4*level+56;
	else if((400 <= jobId && jobId <= 422) || (1400 <= jobId && jobId <= 1412) || (300 <= jobId && jobId <= 322) || (1300 <= jobId && jobId <= 1312)) 	// THEIF or NIGHTWALKER or BOWMAN or WINDARCHER
		return 14*level+148;
	else if((200 <= jobId && jobId <= 232) || (1200 <= jobId && jobId <= 1212)) 	// MAGICIANS or BLAZEWIZARDS 
		return 22*level+488;
	else if((500 <= jobId && jobId <= 522) || (1500 <= jobId && jobId <= 1512)) 	// PIRATES or THUNDERBREAKERS 
		return 18*level+111;
	else if(2100 <= jobId && jobId <= 2112) 										// ARANS
		return 5*level+156;
	else
		return 0;
}

function calculateMaxRep(){
	if(from_attr.num == 1)
		return Math.min(Math.floor(starting_attr.hp/hpLost(1)), ap_reset_count);
	else if(from_attr.num == 2)
		return Math.min(Math.ceil((starting_attr.mp - getMinMP())/mpLost()), ap_reset_count);
	else if(from_attr.num == 3)
		return Math.min(starting_attr.str-4, ap_reset_count);
	else if(from_attr.num == 4)
		return Math.min(starting_attr.dex-4, ap_reset_count);
	else if(from_attr.num == 5)
		return Math.min(starting_attr.int_-4, ap_reset_count);
	else if(from_attr.num == 6)
		return Math.min(starting_attr.luk-4, ap_reset_count);
}