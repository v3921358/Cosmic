/*
 * @Name         Monster Carnival Assistant
 * @Author:      Benjixd
 * @NPC:         2042003, 2042004, 2042008, 2042009
 * @Purpose:     CPQ Challenger Deny notice
 */

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if(mode == -1) {
        cm.dispose();
        return;
    }

    if (status >= 1 && mode == 0) {
        cm.dispose();
        return;
    }

    if(mode == 1) {
        status++;
    } else {
        status--;
    }

    if(status == 0) {
        cm.sendOk("Your Monster Carnival request has been denied.");
        cm.dispose();
    }
}