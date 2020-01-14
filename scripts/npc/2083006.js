/*
    @author Ronan, nansonzheng
    Time Gate - Tera Forest
    Neo City quest line 
    -- Dialogues are made up --
*/

var quests = [3719, 3724, 3730, 3736, 3742, 3748];
var array = ["<Year 2021> Average Town Entrance", "<Year 2099> Midnight Harbor Entrance", "<Year 2215> Bombed City Center Retail District", "<Year 2216> Ruined City Intersection", "<Year 2230> Dangerous Tower Lobby", "<Year 2503> Air Battleship Bow"];
var limit;


function start(){
    if (!cm.haveItem(4001393) || !cm.isQuestCompleted(3718)){
        cm.sendOk("The Time Gate does not react to your presence. Perhaps a special item is needed to activate it...");
        cm.dispose();
        return;
    }
    var talk = "Please select a destination.\r\n"
    for (limit = 0; limit < quests.length; limit++){
        if (!cm.isQuestCompleted(quests[limit]))
            break;
    }    
    if (limit == 0){
        cm.sendSimple("You must first prove your valor against #bGatekeeper Nex#k before you are allowed to use the Time Gate.");
        cm.dispose();
        return;
    }
    for (var i = 0; i < limit; i++){
        talk += "#L" + i + "# #b" + array[i] + "#k#l\r\n";
    }

    cm.sendSimple(talk);
}

function action(mode, type, selection){
    if (mode == -1 || (mode == 0 && type > 0)){
        cm.dispose();
        return;
    }
    if (selection >= 0 && selection <= 5){
        cm.warp(240070100 + selection * 100, 1);
        cm.dispose();
        return;
    }
    cm.sendSimple("The Time Gate glitched out because of a weird selection you made. Maybe you should try again with a proper choice.");
    cm.dispose();
    return;
}