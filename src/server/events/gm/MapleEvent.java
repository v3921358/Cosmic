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

package server.events.gm;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import client.MapleCharacter;
import server.events.gm.core.EventStepRunner;

import server.maps.MapleMap;

/**
 *
 * @author kevintjuh93
 * @author Benjixd
 */
public abstract class MapleEvent {
    private ReentrantLock lock = new ReentrantLock();
    protected EventStepRunner runner = new EventStepRunner();
    protected MapleMap map;
    protected int limit;
    protected boolean isOpen;

    public MapleEvent(MapleMap map, int limit) {
        this.map = map;
        this.limit = limit;
        this.isOpen = false;
    }

    public int getMapId() {
        return map.getId();
    }

    public MapleMap getMap() {
        return map;
    }

    public int getLimit() {
        return limit;
    }

    public void minusLimit() {
        if(limit > 0) {
            this.limit--;
        }
    }

    public void addLimit() {
        this.limit++;
    }

    public void reset() {
        lock.lock();
        runner.reset();
        isOpen = false;
        lock.unlock();
    }

    public void openEntry() {
        lock.lock();
        isOpen = true;
        lock.unlock();
    }

    public void closeEntry() {
        lock.lock();
        isOpen = false;
        lock.unlock();
    }

    public boolean tryEnterEvent(MapleCharacter chr) {;
        lock.lock();
        try {
            if(isOpen && map.getCharacters().size() < limit && chr.getClient().getChannel() == map.getChannel()) {
                chr.changeMap(map);
                return true;
            }
            else {
                return false;
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void waitForEvent() throws InterruptedException {
        synchronized(runner) {
            while(!runner.isComplete()) {
                runner.wait();
            }
        }
    }

    // Abstract Methods
    public abstract void startEvent();
}  