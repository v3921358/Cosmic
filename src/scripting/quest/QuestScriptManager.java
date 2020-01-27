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
package scripting.quest;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import scripting.AbstractScriptManager;
import server.quest.MapleQuest;
import tools.FilePrinter;
import client.MapleClient;
import client.MapleQuestStatus;

/**
 *
 * @author RMZero213
 */
public class QuestScriptManager extends AbstractScriptManager {
	private static final String SCRIPT_FORMAT = "quest/%s.js"; 
	private static final String QUEST_MANAGER_VAR = "qm";
	private static final String START_ENTRY = "start";
	private static final String END_ENTRY = "end";

	private Map<MapleClient, QuestActionManager> qms = new HashMap<>();
	private Map<MapleClient, Invocable> scripts = new HashMap<>();
	private static QuestScriptManager instance = new QuestScriptManager();

	public synchronized static QuestScriptManager getInstance() {
		return instance;
	}

	public void start(MapleClient c, short questid, int npc) {
		MapleQuest quest = MapleQuest.getInstance(questid);
		String path = String.format(SCRIPT_FORMAT, Short.toString(questid)); 

		if (!c.getPlayer().getQuest(quest).getStatus().equals(MapleQuestStatus.Status.NOT_STARTED)) {
			dispose(c);
			return;
		}

		if (qms.containsKey(c)) {
			return;
		}

		if(c.canClickNPC()) {
			if(!scriptExists(path)) {
				FilePrinter.printError(FilePrinter.QUEST_UNCODED, "Quest " + questid + " is uncoded.\r\n");
				return;
			}

			QuestActionManager qm = new QuestActionManager(c, questid, npc, true);
			Invocable script = getInvocable(path, c);
			if (script == null) {
				dispose(c);
				return;
			}
			c.setClickedNPC();

			qms.put(c, qm);
			((ScriptEngine) script).put(QUEST_MANAGER_VAR, qm);
			scripts.put(c, script);

			try {
				script.invokeFunction(START_ENTRY, (byte) 1, (byte) 0, 0);
			} catch(final UndeclaredThrowableException | NoSuchMethodException ute) {
				FilePrinter.printError(FilePrinter.QUEST + questid + ".txt", ute);
				dispose(c);
			} catch(final Exception e) {
				FilePrinter.printError(FilePrinter.QUEST + questid + ".txt", e);
				dispose(c);
			}
		}
	}

	public void start(MapleClient c, byte mode, byte type, int selection) {
		Invocable iv = scripts.get(c);
		if (iv != null) {
			try {
				c.setClickedNPC();
				iv.invokeFunction(START_ENTRY, mode, type, selection);
			} catch (final UndeclaredThrowableException | NoSuchMethodException ute) {
				FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", ute);
				dispose(c);
			} catch (final Exception e) {
				FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", e);
				dispose(c);
			}
		}
	}

	public void end(MapleClient c, short questid, int npc) {
		MapleQuest quest = MapleQuest.getInstance(questid);
		String path = String.format(SCRIPT_FORMAT, Short.toString(questid)); 

		if (!c.getPlayer().getQuest(quest).getStatus().equals(MapleQuestStatus.Status.STARTED) || !c.getPlayer().getMap().containsNPC(npc)) {
			dispose(c);
			return;
		}

		if (qms.containsKey(c)) {
			return;
		}

		if(c.canClickNPC()) {
			if(!scriptExists(path)) {
				FilePrinter.printError(FilePrinter.QUEST_UNCODED, "Quest " + questid + " is uncoded.\r\n");
				return;
			}

			QuestActionManager qm = new QuestActionManager(c, questid, npc, false);
			Invocable script = getInvocable(path, c);
			if (script == null) {
				dispose(c);
				return;
			}
			c.setClickedNPC();

			qms.put(c, qm);
			((ScriptEngine) script).put(QUEST_MANAGER_VAR, qm);
			scripts.put(c, script);

			try {
				script.invokeFunction(END_ENTRY, (byte) 1, (byte) 0, 0);
			} catch(final UndeclaredThrowableException | NoSuchMethodException ute) {
				FilePrinter.printError(FilePrinter.QUEST + questid + ".txt", ute);
				dispose(c);
			} catch(final Exception e) {
				FilePrinter.printError(FilePrinter.QUEST + questid + ".txt", e);
				dispose(c);
			}
		}
	}

	public void end(MapleClient c, byte mode, byte type, int selection) {
		Invocable iv = scripts.get(c);
		if (iv != null) {
			try {
				c.setClickedNPC();
				iv.invokeFunction(END_ENTRY, mode, type, selection);
			} catch (final UndeclaredThrowableException | NoSuchMethodException ute) {
				FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", ute);
				dispose(c);
			} catch (final Exception e) {
				FilePrinter.printError(FilePrinter.QUEST + getQM(c).getQuest() + ".txt", e);
				dispose(c);
			}
		}
	}

	public void dispose(QuestActionManager qm, MapleClient c) {
		qms.remove(c);
		scripts.remove(c);
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        resetContext(String.format(SCRIPT_FORMAT, Integer.toString(qm.getQuest())), c);
	}

	public void dispose(MapleClient c) {
		QuestActionManager qm = qms.get(c);
		if (qm != null) {
			dispose(qm, c);
		}
	}

	public QuestActionManager getQM(MapleClient c) {
		return qms.get(c);
	}
}
