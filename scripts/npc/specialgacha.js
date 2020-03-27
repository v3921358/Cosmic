/* @author nanson
    Name: Gachapon machine (Henesys)
    NPC ID: 
    Map(s): Henesys Market
    Info: special gacha
    Script: specialgacha.js
*/

var status;

function start(){
    status = -1;
    action(1, 0, 0);
}

function action (mode, type, selection){
    if (mode == -1 || (mode == 0 && type > 0)){
        cm.dispose();
        return;
    }
    
    if (mode == 1)
        status++;
    else
        status--;
        
    if (status == 0){
        cm.sendSimple("This is the Secret Special Gachapon. What would you like to do?\r\n#L1##bRoll the Special Gachapon#l#k\r\n#L2##bCheck the available prizes#l#k\r\n");
    }
    else if (status == 1){
        if (selection == 1){
            if (cm.haveItem(5999999)){
                cm.gainItem(5999999, -1);
                cm.doSpecialGachapon();
            }
            else {
                cm.sendOk("You don't have a Special Gachapon Ticket. Accumulate log in rewards to get a Ticket each month!");
                cm.dispose();
            }
        }
        else if (selection == 2){
            cm.checkSpecialGachapon();
        }
        else {
            cm.dispose();
            return;
        }
    }
    else {
        cm.dispose();
    }
}