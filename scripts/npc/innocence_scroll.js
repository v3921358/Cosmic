importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.constants);
importPackage(Packages.tools);

var REQUIRED_INNOCENCE_SCROLL = 2049610;
var status;
var item;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == -1) {
        cm.dispose();
        return;
    }

    if (mode == 0 && type > 0) {
        cm.dispose();
        return;
    }

    if(mode == 1) {
        status++;
    } else {
        status--;
    }

    if(status == 0) {
    	cm.sendNext("Hi! Until #binnocence scrolls#k #i2049610# can be properly implemented, I will be helping you out with them today!");
    }
    else if(status == 1) {
    	if(cm.getItemQuantity(REQUIRED_INNOCENCE_SCROLL) > 0) {
    		cm.sendNext("Would you like to use one #b#t2049610##k #i2049610# on one of your following equips?");
    	} else {
    		cm.sendOk("It looks like you don't have any #b#t2049610##k #i2049610#.");
    		cm.dispose();
    	}
    }
    else if(status == 2) {
    	cm.sendSimple("Which equip do you want to reset?\r\n\r\n" + getEquipsString(cm.getPlayer()));
    }
    else if(status == 3) {
        if(selection != null && selection > 0) {
            item = cm.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(trueEquipPos(selection));
            cm.sendYesNo("Are you sure you want to reset #b#t" + item.getItemId() + "##k #i" + item.getItemId() + "#?");
        }
        else {
            cm.dispose();
        }
    }
    else if(status == 4) {
        item.resetStats();
        cm.gainItem(REQUIRED_INNOCENCE_SCROLL, -1);
        cm.sendOk("Innocence to your item #b#t" + item.getItemId() + "##k #i" + item.getItemId() + "# has been applied.");
        cm.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getScrollEffect(cm.getPlayer().getId(), Equip.ScrollResult.SUCCESS, false));
        cm.getPlayer().forceUpdateItem(item);
        cm.dispose();
    }
}

function getEquipsString(player) {
	var equips = player.getInventory(MapleInventoryType.EQUIPPED);
	var string = "";
	for each(var item in equips) {
        string += "#L" + positiveEquipPos(item.getPosition()) + "##i" + item.getItemId() + "# #b#t" + item.getItemId() + "##k\r\n";
	}
	return string;
}

function positiveEquipPos(slot) {
    if(slot < 0) {
        return -1 * slot;    
    } else {
        return slot;
    }
}

function trueEquipPos(slot) {
    if(slot < 0) {
        return slot;
    } else {
        return -1 * slot;
    }
}