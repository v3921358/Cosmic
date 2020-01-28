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
package scripting.reactor;

import client.MapleClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.lang.reflect.UndeclaredThrowableException;

import javax.script.Invocable;
import javax.script.ScriptException;
import javax.script.ScriptEngine;

import scripting.AbstractScriptManager;
import server.maps.MapleReactor;
import server.maps.ReactorDropEntry;
import tools.DatabaseConnection;
import tools.FilePrinter;

/**
 * @author Lerk
 */
public class ReactorScriptManager extends AbstractScriptManager {

    private static final String SCRIPT_FORMAT = "reactor/%s.js";
    private static final String REACTOR_MANAGER_VAR = "rm";
    private static final String HIT_ENTRY = "hit";
    private static final String ACT_ENTRY = "act";
    private static final String TOUCH_ENTRY = "touch";
    private static final String UNTOUCH_ENTRY = "untouch";

    private static ReactorScriptManager instance = new ReactorScriptManager();
    private Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

    public synchronized static ReactorScriptManager getInstance() {
        return instance;
    }
    
    public void onHit(MapleClient c, MapleReactor reactor) {
        Invocable iv = getInvocable(String.format(SCRIPT_FORMAT, Integer.toString(reactor.getId())), c);
        ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);

        if (iv != null) {
            ((ScriptEngine) iv).put(REACTOR_MANAGER_VAR, rm);
            try {
                iv.invokeFunction(HIT_ENTRY);
            } catch (final NoSuchMethodException nsme){
                // -- Reactors don't have to do anything when hit
            } catch(final NullPointerException | UndeclaredThrowableException ute) {
                FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", ute);
            } catch(final Exception e) {
                FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", e);
            }   
        }
    }

    public void act(MapleClient c, MapleReactor reactor) {
        Invocable iv = getInvocable(String.format(SCRIPT_FORMAT, Integer.toString(reactor.getId())), c);
        ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);

        if (iv != null) {
            ((ScriptEngine) iv).put(REACTOR_MANAGER_VAR, rm);
            try {
                iv.invokeFunction(ACT_ENTRY);
            } catch(final NullPointerException | UndeclaredThrowableException ute) {
                FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", ute);
            } catch(final Exception e) {
                FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", e);
            }   
        }
    }

    public List<ReactorDropEntry> getDrops(int rid) {
        List<ReactorDropEntry> ret = drops.get(rid);
        if (ret == null) {
            ret = new LinkedList<>();
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("SELECT itemid, chance, questid FROM reactordrops WHERE reactorid = ? AND chance >= 0")) {
                    ps.setInt(1, rid);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            ret.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
                        }
                    }
                }
                
                con.close();
            } catch (Throwable e) {
                FilePrinter.printError(FilePrinter.REACTOR + rid + ".txt", e);
            }
            drops.put(rid, ret);
        }
        return ret;
    }

    public void clearDrops() {
        drops.clear();
    }

    public void touch(MapleClient c, MapleReactor reactor) {
        touching(c, reactor, true);
    }

    public void untouch(MapleClient c, MapleReactor reactor) {
        touching(c, reactor, false);
    }

    public synchronized void touching(MapleClient c, MapleReactor reactor, boolean touching) {
        Invocable iv = getInvocable(String.format(SCRIPT_FORMAT, Integer.toString(reactor.getId())), c);
        ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);

        if (iv != null) {
            ((ScriptEngine) iv).put(REACTOR_MANAGER_VAR, rm);
            try {
                if(touching) {
                    iv.invokeFunction(TOUCH_ENTRY);    
                } else {
                    iv.invokeFunction(UNTOUCH_ENTRY);
                }
            } catch(final NullPointerException | UndeclaredThrowableException ute) {
                FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", ute);
            } catch(final Exception e) {
                e.printStackTrace();
                FilePrinter.printError(FilePrinter.REACTOR + reactor.getId() + ".txt", e);
            }   
        }
    }
}