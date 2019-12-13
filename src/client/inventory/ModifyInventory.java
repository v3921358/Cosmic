package client.inventory;

import constants.ItemConstants;

/**
 *
 * @author kevin
 */

public class ModifyInventory {
    public static final short ADD_ITEM_OP = 0;
    public static final short UPDATE_QUANTITY_OP = 1;
    public static final short MOVE_ITEM_OP = 2;
    public static final short REMOVE_ITEM_OP = 3; 

    private int mode;
    private Item item;
    private short oldPos;

    public ModifyInventory(final int mode, final Item item) {
        this.mode = mode;
        this.item = item.copy();
    }

    public ModifyInventory(final int mode, final Item item, final short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
    }
    
    public final int getMode() {
        return mode;
    }

    public final int getInventoryType() {
        return ItemConstants.getInventoryType(item.getItemId()).getType();
    }

    public final short getPosition() {
        return item.getPosition();
    }

    public final short getOldPosition() {
        return oldPos;
    }
    
    public final short getQuantity() {
        return item.getQuantity();
    }

    public final Item getItem() {
        return item;
    }

    public final void clear() {
        this.item = null;
    }
}