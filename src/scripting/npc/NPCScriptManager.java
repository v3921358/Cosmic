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
package scripting.npc;

import client.MapleCharacter;
import client.MapleClient;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import scripting.AbstractScriptManager;
import tools.FilePrinter;
import tools.MaplePacketCreator;

/**
 *
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {

    private static final String SCRIPT_FORMAT = "npc/%s.js";
    private static final String CONVERSATION_MANAGER_VAR = "cm";
    private static final String ENTRY_POINT = "start";
    private static final String CONTINUE_POINT = "action";
    private static NPCScriptManager instance = new NPCScriptManager();

    private Map<MapleClient, NPCConversationManager> cms = new HashMap<>();
    private Map<MapleClient, Invocable> scripts = new HashMap<>();

    public synchronized static NPCScriptManager getInstance() {
        return instance;
    }

    public boolean start(MapleClient c, int npc, MapleCharacter chr) {
        return start(c, npc, null, chr);
    }
    
    public boolean start(MapleClient c, int npc, int oid, MapleCharacter chr) {
        return start(c, npc, oid, null, chr);
    }
    
    public boolean start(MapleClient c, int npc, String fileName, MapleCharacter chr) {
        return start(c, npc, -1, fileName, chr);
    }

    public boolean start(MapleClient c, int npc, int oid, String fileName, MapleCharacter chr) {
        String path = String.format(SCRIPT_FORMAT, (fileName == null ? Integer.toString(npc) : fileName));

        if (cms.containsKey(c)) {
            dispose(c);
        }

        if (c.canClickNPC() && scriptExists(path)) {
            NPCConversationManager cm = new NPCConversationManager(c, npc, oid, fileName);
            Invocable script = getInvocable(path, c);
            if(script == null) {
                dispose(c);
                return false;
            }
            c.setClickedNPC();

            cms.put(c, cm);
            ((ScriptEngine) script).put(CONVERSATION_MANAGER_VAR, cm);
            scripts.put(c, script);

            try {
                try {
                script.invokeFunction(ENTRY_POINT);
                }
                catch(final NoSuchMethodException nsm) {
                    script.invokeFunction(ENTRY_POINT, chr);
                }    
            }
            catch(final UndeclaredThrowableException | NoSuchMethodException ute) {
                FilePrinter.printError(FilePrinter.NPC + npc + ".txt", ute);
                dispose(c);
                return false;
            }
            catch (Exception e) {
                FilePrinter.printError(FilePrinter.NPC + npc + ".txt", e);
                dispose(c);
                return false;
            }
        } else {
            c.announce(MaplePacketCreator.enableActions());
            return false;
        }

        return true;
    }

    public void action(MapleClient c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.setClickedNPC();
                iv.invokeFunction(CONTINUE_POINT, mode, type, selection);
            } catch (ScriptException | NoSuchMethodException t) {
                if (getCM(c) != null) {
                    FilePrinter.printError(FilePrinter.NPC + getCM(c).getNpc() + ".txt", t);
                }
                dispose(c);
            }
        }
    }

    public void dispose(NPCConversationManager cm) {
        MapleClient c = cm.getClient();
        String path = String.format(SCRIPT_FORMAT, (cm.getScriptName() == null ? cm.getNpc() : cm.getScriptName()));
        
        c.getPlayer().setCS(false);
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        cms.remove(c);
        scripts.remove(c);
        resetContext(path, c);
    }

    public void dispose(MapleClient c) {
        if (cms.get(c) != null) {
            dispose(cms.get(c));
        }
    }

    public NPCConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }

}
