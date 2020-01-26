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
package scripting.map;

import client.MapleClient;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import scripting.AbstractScriptManager;

public class MapScriptManager extends AbstractScriptManager {

    private static final String SCRIPT_FORMAT_FIRST_ENTER = "map/onFirstUserEnter/%s.js";
    private static final String SCRIPT_FORMAT_ENTER = "map/onUserEnter/%s.js";
    private static final String ENTRY_POINT = "start";
    private static MapScriptManager instance = new MapScriptManager();
    private Map<String, Invocable> scripts = new HashMap<>();

    private MapScriptManager() {
        super();
    }

    public static MapScriptManager getInstance() {
        return instance;
    }

    public void reloadScripts() {
        scripts.clear();
    }

    public boolean scriptExists(String scriptName, boolean firstUser) {
        if (firstUser) {
            return super.scriptExists(String.format(SCRIPT_FORMAT_FIRST_ENTER, scriptName));    
        }
        else {
            return super.scriptExists(String.format(SCRIPT_FORMAT_ENTER, scriptName));
        }
    }

    public void getMapScript(MapleClient c, String scriptName, boolean firstUser) {
        Invocable script = null;
        String path = String.format((firstUser? SCRIPT_FORMAT_FIRST_ENTER : SCRIPT_FORMAT_ENTER), scriptName);

        if (scripts.containsKey(scriptName)) {
            script = scripts.get(scriptName);
        } else if(scriptExists(path)) {
            script = getInvocable(path, null);
            scripts.put(scriptName, script);
        } else {
            c.announce(MaplePacketCreator.enableActions());
        }

        if(script != null) {
            try {
                script.invokeFunction(ENTRY_POINT, new MapScriptMethods(c));
            } catch (final ScriptException | NoSuchMethodException | UndeclaredThrowableException ute) {
                FilePrinter.printError(FilePrinter.MAP_SCRIPT + path + ".txt", ute);
            } catch(final Exception e) {
                FilePrinter.printError(FilePrinter.MAP_SCRIPT + path + ".txt", e);
            }
        }
    }
}