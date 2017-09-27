/* Coco
*/

function start() {
    cm.getPlayer().setCS(true);
    action(1, 0, 0);
}

function action(mode, type, selection) {
    cm.sendOk("Hi, we don't do business with you anymore.");
    cm.dispose();
    return;
}