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
package scripting.portal;

import client.MapleClient;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import server.MaplePortal;
import tools.FilePrinter;

import scripting.AbstractScriptManager;

public class PortalScriptManager extends AbstractScriptManager {

    private static final String SCRIPT_FORMAT = "portal/%s.js";
    private static PortalScriptManager instance = new PortalScriptManager();
    private Map<String, PortalScript> scripts = new HashMap<>();

    private PortalScriptManager() {
        super();
    }

    public static PortalScriptManager getInstance() {
        return instance;
    }

    public boolean executePortalScript(MaplePortal portal, MapleClient c) {
        String scriptName = portal.getScriptName();
        String path = String.format(SCRIPT_FORMAT, scriptName);
        PortalScript script = null;

        if(scripts.containsKey(scriptName)) {
            script = scripts.get(scriptName);
        } else if(scriptExists(path)) {
            script = getInvocable(path, null).getInterface(PortalScript.class);
            scripts.put(scriptName, script);
        }

        if(script != null) {
            try {
                return script.enter(new PortalPlayerInteraction(c, portal));
            } catch(final UndeclaredThrowableException ute) {
                FilePrinter.printError(FilePrinter.PORTAL + portal.getScriptName() + ".txt", ute);
            } catch(final Exception e) {
                FilePrinter.printError(FilePrinter.PORTAL + portal.getScriptName() + ".txt", e);
            }
        }

        return false;
    }

    public void reloadPortalScripts() {
        scripts.clear();
    }
}