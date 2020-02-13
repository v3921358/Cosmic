/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleDisease;
import java.awt.Point;
import java.util.List;
import net.AbstractMaplePacketHandler;
import net.server.world.MapleParty;
import net.server.world.MaplePartyCharacter;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.partyquest.MapleCarnivalFactory;
import server.partyquest.MapleCarnivalFactory.MCSkill;
import server.partyquest.MonsterCarnival;
import server.partyquest.MonsterCarnival.GuardianSpawnCode;
import server.partyquest.monstercarnival.components.MonsterCarnivalMapComponent;
import server.partyquest.monstercarnival.components.MonsterCarnivalPlayerComponent;
import server.partyquest.monstercarnival.util.GuardianSpawnPoint;
import server.partyquest.monstercarnival.util.MonsterCarnivalMob;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

/**
    *@author Drago (Dragohe4rt)
    *@author Benjixd
*/

public final class MonsterCarnivalHandler extends AbstractMaplePacketHandler {

    private int handleMonsterSpawn(MapleClient c, int mobNum) {
        final MonsterCarnivalMob mob = c.getPlayer().getMap().getMCMapComponent().getMob(mobNum);
        final MonsterCarnivalPlayerComponent mcPlayer = c.getPlayer().getMCPlayerComponent();
        final MonsterCarnival mcpq = mcPlayer.getMonsterCarnival();

        if(mob == null || mcPlayer.getCP() < mob.getRequiredCP()) {
            c.announce(MaplePacketCreator.CPQMessage((byte) 1));
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }

        if(mcpq.trySummon(mob, mcPlayer.getTeam())) {
            c.announce(MaplePacketCreator.enableActions());
        }
        else {
            c.announce(MaplePacketCreator.CPQMessage((byte) 2));
            c.announce(MaplePacketCreator.enableActions());
        }
        return mob.getRequiredCP();
    }

    private int handleDebuff(MapleClient c, int skillNum) {
        final Integer skillId = c.getPlayer().getMap().getMCMapComponent().getSkillId(skillNum);
        final MonsterCarnivalPlayerComponent mcPlayer = c.getPlayer().getMCPlayerComponent();
        final MonsterCarnival mcpq = mcPlayer.getMonsterCarnival();

        // Check invalid Skill
        if(skillId == null) {
            c.getPlayer().dropMessage(5, "Invalid skill use ignored.");
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }

        // Check skill requirements met
        final MCSkill skill = MapleCarnivalFactory.getInstance().getSkill(skillId);
        if(mcPlayer.getCP() < skill.getRequiredCP()) {
            c.announce(MaplePacketCreator.CPQMessage((byte) 1));
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }

        mcpq.applySkillToEnemiesOf(skill, mcPlayer.getTeam());

        c.announce(MaplePacketCreator.enableActions());
        return skill.getRequiredCP();
    }

    private int handleProtectors(MapleClient c, int skillNum) {
        final MCSkill skill = MapleCarnivalFactory.getInstance().getGuardian(skillNum);
        final MonsterCarnivalPlayerComponent mcPlayer = c.getPlayer().getMCPlayerComponent();
        final MonsterCarnival mcpq = mcPlayer.getMonsterCarnival();

        //Check skill requirements
        if(skill == null || mcPlayer.getCP() < skill.getRequiredCP()) {
            c.announce(MaplePacketCreator.CPQMessage((byte) 1));
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }

        GuardianSpawnCode code = mcpq.trySpawnGuardian(skill, mcPlayer.getTeam());
        if(code == GuardianSpawnCode.SUCCESS) {
            c.announce(MaplePacketCreator.enableActions());
            return skill.getRequiredCP();
        }
        else if(code == GuardianSpawnCode.CANNOT_GUARDIAN) {
            c.announce(MaplePacketCreator.CPQMessage((byte) 2));
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }
        else if(code == GuardianSpawnCode.INVALID) {
            c.announce(MaplePacketCreator.CPQMessage((byte) 3));
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }
        else if(code == GuardianSpawnCode.MAX_COUNT_REACHED || code == GuardianSpawnCode.ALREADY_EXISTS) {
            c.announce(MaplePacketCreator.CPQMessage((byte) 4));
            c.announce(MaplePacketCreator.enableActions());
            return 0;
        }
        
        return 0;
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        try {
            int tab = slea.readByte();
            int num = slea.readByte();
            MonsterCarnivalPlayerComponent mcPlayer = c.getPlayer().getMCPlayerComponent();

            int neededCP = 0;
            if (tab == 0) {
                neededCP = handleMonsterSpawn(c, num);
            } else if (tab == 1) { //debuffs
                neededCP = handleDebuff(c, num);
            } else if (tab == 2) { //protectors
                neededCP = handleProtectors(c, num);
            }

            if(neededCP != 0) {
                mcPlayer.gainCP(-neededCP);
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.playerSummoned(c.getPlayer().getName(), tab, num));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}