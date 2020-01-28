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
package scripting;

import client.MapleClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import constants.ServerConstants;
import tools.FilePrinter;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {
    private static final String ROOT_PATH = "scripts/%s";
    private static final String NASHORN_LOAD_MODULE = "load('nashorn:mozilla_compat.js');" + System.lineSeparator();

    private ScriptEngineFactory sef;

    protected AbstractScriptManager() {
        sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();
    }

    protected String getFullPath(String path) {
        return String.format(ROOT_PATH, path);
    }

    protected boolean scriptExists(String path) {
        File script = new File(getFullPath(path));
        return script.exists();
    }

    protected Invocable getInvocable(String path, MapleClient c) {
        ScriptEngine engine = null;

        if (c != null) {
            engine = c.getScriptEngine(path);
        }

        if (engine == null) {
            File scriptFile = new File(getFullPath(path));
            engine = sef.getScriptEngine();

            if (!scriptFile.exists()) {
                return null;
            }

            if (c != null) {
                c.setScriptEngine(path, engine);
            }

            try {
                FileReader fr = new FileReader(scriptFile);
                engine.eval(NASHORN_LOAD_MODULE);
                engine.eval(fr);
                fr.close();
            } catch (final ScriptException | IOException | UndeclaredThrowableException t) {
                FilePrinter.printError(FilePrinter.INVOCABLE + path.substring(12, path.length()), t, path);
                return null;
            } catch (Exception e) {
                FilePrinter.printError(FilePrinter.INVOCABLE + path.substring(12, path.length()), e, path);
                return null;
            }
        }

        return (Invocable) engine;
    }

    protected void resetContext(String path, MapleClient c) {
        c.removeScriptEngine(path);
    }
}
