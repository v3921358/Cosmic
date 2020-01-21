/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.quest.requirements;

import client.MapleCharacter;
import server.quest.MapleQuest;
import provider.MapleData;
import provider.MapleDataTool;
import server.quest.MapleQuestRequirementType;

/**
 *
 * @author Nanson
 */
public class InfoNumberRequirement extends MapleQuestRequirement {
    
    private short infoNumber;
    private int questId;
    
    public InfoNumberRequirement(MapleQuest quest, MapleData data){
        super(MapleQuestRequirementType.INFO_NUMBER);
        this.questId = quest.getId();
        processData(data);
    }

    @Override
    public boolean check(MapleCharacter chr, Integer npcid) {
        return true;
    }

    @Override
    public void processData(MapleData data) {
        infoNumber = (short)MapleDataTool.getInt(data, 0);
    }
    
    public short getInfoNumber(){
        return infoNumber;
    }
    
}
