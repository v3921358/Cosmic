var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    var jobid = cm.getPlayer().getJob().getId();
    if(cm.haveItem(1902016)) {
        cm.warp(140010210, 0);
    } 
    else if (jobid == 2000 || jobid == 2110 || jobid == 2111 || jobid == 2112) {
        cm.sendOk("So you're that #bAran#k guy that's been straddling on one of my brothers, eh? Our leader wants a word with you, so get your dirty bum off of your ride and #runequip him#k!");
    }
    else {
        cm.sendOk("What is it? If you you're here to waste my time, get lost!");
    }
    
    cm.dispose();
}