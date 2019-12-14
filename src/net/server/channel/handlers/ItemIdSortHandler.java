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

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import constants.ServerConstants;
import net.AbstractMaplePacketHandler;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.Equip;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import server.MapleItemInformationProvider;

/**
 *
 * @author BubblesDev
 * @author Ronan
 * @author Benjixd
 */

class PairedQuicksort {
    public static final short SORT_CRITERIA_ITEM_ID = 0;
    public static final short SORT_CRITERIA_QUANTITY = 1;
    public static final short SORT_CRITERIA_NAME = 2;
    public static final short SORT_CRITERIA_LEVEL = 3;

    private int i = 0;
    private int j = 0;
    private final ArrayList<Integer> intersect;
    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
    
    private void PartitionByItemId(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;
        
        x = A.get((i + j) / 2);
        do {
            while (x.getItemId() > A.get(i).getItemId()) i++;
            while (x.getItemId() < A.get(j).getItemId()) j--;
            
            if (i <= j) {
                w = A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }
    
    private void PartitionByName(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;
        
        x = A.get((i + j) / 2);
        do {
            while (ii.getName(x.getItemId()).compareTo(ii.getName(A.get(i).getItemId())) > 0) i++;
            while (ii.getName(x.getItemId()).compareTo(ii.getName(A.get(j).getItemId())) < 0) j--;
            
            if (i <= j) {
                w = A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }
    
    private void PartitionByQuantity(int Esq, int Dir, ArrayList<Item> A) {
        Item x, w;

        i = Esq;
        j = Dir;
        
        x = A.get((i + j) / 2);
        do {
            while (x.getQuantity() > A.get(i).getQuantity()) i++;
            while (x.getQuantity() < A.get(j).getQuantity()) j--;
            
            if (i <= j) {
                w = A.get(i);
                A.set(i, A.get(j));
                A.set(j, w);

                i++;
                j--;
            }
        } while (i <= j);
    }
    
    private void PartitionByLevel(int Esq, int Dir, ArrayList<Item> A) {
        Equip x, w, eqpI, eqpJ;

        i = Esq;
        j = Dir;
        
        x = (Equip)(A.get((i + j) / 2));
        
        do {
            eqpI = (Equip)A.get(i);
            eqpJ = (Equip)A.get(j);
            
            while (x.getLevel() > eqpI.getLevel()) i++;
            while (x.getLevel() < eqpJ.getLevel()) j--;
            
            if (i <= j) {
                w = (Equip)A.get(i);
                A.set(i, A.get(j));
                A.set(j, (Item)w);

                i++;
                j--;
            }
        } while (i <= j);
    }

    void MapleQuicksort(int Esq, int Dir, ArrayList<Item> A, int sort) {
        switch(sort) {
            case SORT_CRITERIA_LEVEL:
                PartitionByLevel(Esq, Dir, A);
                break;
            
            case SORT_CRITERIA_NAME:
                PartitionByName(Esq, Dir, A);
                break;
                
            case SORT_CRITERIA_QUANTITY:
                PartitionByQuantity(Esq, Dir, A);
                break;
                    
            default:
                PartitionByItemId(Esq, Dir, A);
        }
        
        
        if (Esq < j) MapleQuicksort(Esq, j, A, sort);
        if (i < Dir) MapleQuicksort(i, Dir, A, sort);
    }
    
    public PairedQuicksort(ArrayList<Item> A, int primarySort, int secondarySort) {
        intersect = new ArrayList<>();
        
        MapleQuicksort(0, A.size() - 1, A, primarySort);
        
        intersect.add(0);
        for(int ind = 1; ind < A.size(); ind++) {
            if(A.get(ind - 1).getItemId() != A.get(ind).getItemId()) {
                intersect.add(ind);
            }
        }
        intersect.add(A.size());
        
        for(int ind = 0; ind < intersect.size() - 1; ind++) {
            MapleQuicksort(intersect.get(ind), intersect.get(ind + 1) - 1, A, secondarySort);
        }
    }
}

public final class ItemIdSortHandler extends AbstractMaplePacketHandler {
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        chr.getAutobanManager().setTimestamp(3, slea.readInt(), 3);
        byte inventoryType = slea.readByte();
        
        if(!ServerConstants.USE_ITEM_SORT) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
		
        if (inventoryType < 1 || inventoryType > 5) {
            return;
        }
		
        MapleInventory inventory = chr.getInventory(MapleInventoryType.getByType(inventoryType));
        
        ArrayList<Item> itemarray = new ArrayList<>();
        List<ModifyInventory> mods = new ArrayList<>();
        

        LinkedHashMap<Short, Item> recovery = inventory.cloneInventory();
        for(Map.Entry<Short, Item> entry : recovery.entrySet()) {
            if(entry.getValue() != null) {
                itemarray.add(entry.getValue());
            }
        }

        try {
            int invTypeCriteria = (MapleInventoryType.getByType(inventoryType) == MapleInventoryType.EQUIP) ? PairedQuicksort.SORT_CRITERIA_LEVEL : PairedQuicksort.SORT_CRITERIA_QUANTITY;
            int sortCriteria = (ServerConstants.USE_ITEM_SORT_BY_NAME) ? PairedQuicksort.SORT_CRITERIA_NAME : PairedQuicksort.SORT_CRITERIA_ITEM_ID;
            PairedQuicksort pq = new PairedQuicksort(itemarray, sortCriteria, invTypeCriteria);
            inventory.clearInventory();
            
            // Remove all items from inventory client action
            for(Item item : itemarray) {
                mods.add(new ModifyInventory(ModifyInventory.REMOVE_ITEM_OP, item));
            }

            // Remove all items from inventory client action
            for(Item item : itemarray) {
                inventory.addItem(item);
                mods.add(new ModifyInventory(ModifyInventory.ADD_ITEM_OP, item.copy())); //to prevent crashes
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            inventory.setInventory(recovery);
            c.announce(MaplePacketCreator.finishedSort2(inventoryType));
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        
        c.announce(MaplePacketCreator.modifyInventory(true, mods));
        c.announce(MaplePacketCreator.finishedSort2(inventoryType));
        c.announce(MaplePacketCreator.enableActions());
    }
}
