importPackage(Packages.server.events.gm.MapleScavengerHunt);

var status = 0;
var golden_maple_leaf = 4000313;
var event;
var items;

function start() {
    event = cm.getEvent();
    items = event.getScavengerItems();
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

    if(event.hasPlayerCompletedEvent(cm.getPlayer())) {
        eventCompletedChat(mode, type, selection);
    } else {
        eventNotCompletedChat(mode, type, selection);
    }
}

function eventCompletedChat(mode, type, selection) {
    if(status == 1) {
        if(event.checkPlayerClaimedRewards(cm.getPlayer())) {
            cm.sendOk("Thanks again for helping me today! I hope you have a great day!");
            cm.dispose();
        } else {
            cm.sendNext("Thank you for helping me today! I have something for your troubles.");
        }
    } else if(status == 2) {
        var amount_to_gain = event.getItemRewardCount(cm.getPlayer());
        var exp = event.getExpReward(cm.getPlayer());
        if(cm.canHold(golden_maple_leaf, amount_to_gain)) {
            cm.getPlayer().gainExpNoModifiers(exp, true, true, true);
            cm.gainItem(golden_maple_leaf, amount_to_gain);
            event.completeClaimingRewards(cm.getPlayer());
            cm.sendOk("I hope you have a great day!");
        } else {
            cm.sendOk("Please make room in your ETC space.");
        }
        cm.dispose();
    }
}

function eventNotCompletedChat(mode, type, selection) {
    if(status == 1) {
        cm.sendSimple("Thank goodness you're here! I'm in dire need of help right now.'.\r\n#L0##e1.#n#b What seems to be the problem?#k#l\r\n#L1##e2.#n#b I have what you need!#k#l");
    } else if(status == 2) {
        if(selection == 0) {
            cm.sendOk("In order to create the medicine needed for Maya to recover, I need the following ingredients:\r\n" + getScavengerItemString() + "\r\nCan you go around Maple World collecting these resources for me?");
            cm.dispose();
        }
        else {
            var party = cm.getPlayer().getParty();
            if(party == null || cm.getPlayer() != party.getLeader().getPlayer()) {
                cm.sendOk("Please have your party leader speak to me!");
                cm.dispose();
            } else {
                cm.sendNext("Amazing! Let me take a look...");
            }
        }
    } else if(status == 3) {
        var count = countHasItems();
        if(count == 0) {
            cm.sendOk("Looks like you don't have any of the items I've asked for... Are you sure they are in your inventory?");
            cm.dispose();
        } else {
            cm.sendYesNo("You have " + count + "/" + items.size() + " ingredients in your inventory! Would you like to submit them now? (You can no longer submit again during this event.)");
        }
    } else if(status == 4) {
        if(event.checkPartyParticipation(cm.getPlayer().getParty())) {
            var count = countHasItems();
            if(checkPartyPresent(cm.getPlayer().getParty())) {
                consumeItems();
                event.completePartyParticipation(cm.getPlayer().getParty(), count);
                event.CongratulateTeam(cm.getPlayer().getParty());
                cm.sendOk("Please have each of your party members talk to me to claim their rewards!");
            } else {
                cm.sendOk("Make sure your party members are present.");
            }
        } else {
            cm.sendOk("One or more of your party members has already participated in this event.");
        }
        cm.dispose();
    }
}

// HELPER FUNCTIONS //
function getScavengerItemString() {
    var string = "";
    for(var i = 0; i < items.size(); i++) {
        string += (i+1) +".#i" + items[i] + "##b#t" + items[i] + "##k\r\n";
    }
    return string;
}

function countHasItems() {
    var count = 0;
    for(var i = 0; i < items.size(); i++) {
        if(cm.haveItem(items[i])) {
            count++;
        }
    }
    return count;
}

function checkPartyPresent(party) {
    var members = party.getMembers();
    for(var i = 0; i < members.size(); i++) {
        if(members[i] == null || 
            members[i].getPlayer() == null ||
            cm.getMapId() != members[i].getPlayer().getMapId()) {
            return false;
        }
    }

    return true;
}

function consumeItems() {
    var count = 0;
    for(var i = 0; i < items.size(); i++) {
        if(cm.haveItem(items[i])) {
            cm.gainItem(items[i], -1);
            count++;
        }
    }
    return count;
}