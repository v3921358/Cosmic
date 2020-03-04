/**
 * CoCo
 * Apresetter and other functionality
 */
importPackage(Packages.client);
importPackage(Packages.constants);

var status;
var player;
var attrs;
var selectedAttr;
var selectedType = -1;

function start(){
	status = -1;
	player = cm.getPlayer();
	attrs = {
	    1: player.getStr(),
		2: player.getDex(),
		3: player.getInt(),
		4:player.getLuk()
    };
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
        cm.sendSimple("Let's do business.  What would you like to do?\r\n#L0#AP reset#l");    
	} else if (status == 1) {
        selectedType = selection;
        if(selectedType == 0){
            cm.sendSimple("Which stat would you like to reset?\r\n#L1#Str#l\r\n#L2#Dex#l\r\n#L3#Int#l\r\n#L4#Luk#l");
        }
	} else if(status == 2){
        if(selectedType == 0){
		    selectedAttr = selection;
            cm.sendGetNumber("How much of it would you like reset?", 1, 1, 1000);
        }
    } else if(status == 3){
        if(selectedType == 0){
            var maxAP = attrs[selectedAttr] - 5;
            if(selection > maxAP){
                if(maxAP === 0) {
                    cm.sendOk("Your stat is at its lowest resettable amount");	
                } else {
                    cm.sendOk("Please enter a number equal to or less than " + maxAP);
                }
            } else {
                // subtract AP based on selection
                player.addStat(selectedAttr, (selection * -1));
                // give back AP
                player.gainAp(selection);

                cm.sendOk("Stat has been reset, and " + selection + "AP has been returned.");     
            }
        }
    } else {
		cm.dispose();
	}
}