/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.quest.actions;

import client.MapleCharacter;
import client.MapleQuestStatus;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuest;
import server.quest.MapleQuestActionType;

/**
 *
 * @author Nanson
 */
public class InfoAction extends MapleQuestAction {
    String progressData;
    
    public InfoAction(MapleQuest quest, MapleData data){
        super(MapleQuestActionType.INFO, quest);
        processData(data);
    }

    @Override
    public void run(MapleCharacter chr, Integer extSelection) {
        MapleQuestStatus qs = chr.getQuest(MapleQuest.getInstance(questID));
        qs.setProgress(questID, progressData);
    }

    @Override
    public void processData(MapleData data) {
        progressData = MapleDataTool.getString(data, "0");
    }
    
}
