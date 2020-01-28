/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.processor.npc;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutobanFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.ItemConstants;
import constants.ServerConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import net.server.channel.Channel;
import server.DueyPackages;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;

import tools.DatabaseConnection;
import tools.FilePrinter;
import tools.MaplePacketCreator;
/**
 *
 * @author Ronan, Nanson
 */
public class DueyProcessor {
    // Credits Ronan; part of a greater packages refactor
    public enum Actions {
        TOSERVER_RECV_ITEM(0x00),
        TOSERVER_SEND_ITEM(0x02),
        TOSERVER_CLAIM_PACKAGE(0x04),
        TOSERVER_REMOVE_PACKAGE(0x05),
        TOSERVER_CLOSE_DUEY(0x07),
        TOCLIENT_OPEN_DUEY(0x08),
        TOCLIENT_SEND_ENABLE_ACTIONS(0x09),
        TOCLIENT_SEND_NOT_ENOUGH_MESOS(0x0A),
        TOCLIENT_SEND_INCORRECT_REQUEST(0x0B),
        TOCLIENT_SEND_NAME_DOES_NOT_EXIST(0x0C),
        TOCLIENT_SEND_SAMEACC_ERROR(0x0D),
        TOCLIENT_SEND_RECEIVER_STORAGE_FULL(0x0E),
        TOCLIENT_SEND_RECEIVER_UNABLE_TO_RECV(0x0F),
        TOCLIENT_SEND_RECEIVER_STORAGE_WITH_UNIQUE(0x10),
        TOCLIENT_SEND_MESO_LIMIT(0x11),
        TOCLIENT_SEND_SUCCESSFULLY_SENT(0x12),
        TOCLIENT_RECV_UNKNOWN_ERROR(0x13),
        TOCLIENT_RECV_ENABLE_ACTIONS(0x14),
        TOCLIENT_RECV_NO_FREE_SLOTS(0x15),
        TOCLIENT_RECV_RECEIVER_WITH_UNIQUE(0x16),
        TOCLIENT_RECV_SUCCESSFUL_MSG(0x17),
        TOCLIENT_RECV_PACKAGE_MSG(0x1B);
        final byte code;

        private Actions(int code) {
            this.code = (byte) code;
        }

        public byte getCode() {
            return code;
        }
    }

    private static int getAccIdFromCNAME(String name, boolean accountid) {
        try {
            PreparedStatement ps;
            String text = "SELECT id,accountid FROM characters WHERE name = ?";
            if (accountid) {
                text = "SELECT id,accountid FROM characters WHERE name = ?";
            }
            Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement(text);
            ps.setString(1, name);
            int id_;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    return -1;
                }
                id_ = accountid ? rs.getInt("accountid") : rs.getInt("id");
            }
            ps.close();
            con.close();
            return id_;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public static void sendItem(MapleClient c, int fee, byte inventoryId, short itemPos, short quantity, int mesos, String recipient){
        
        if (mesos < 0 || (long) mesos > Integer.MAX_VALUE || ((long) mesos + fee + getFee(mesos)) > Integer.MAX_VALUE || (quantity < 1 && mesos == 0)) {
            	AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with duey.");
            	FilePrinter.printError(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to use duey with mesos " + mesos + " and amount " + quantity + "\r\n");           	
            	c.disconnect(true, false);
            	return;
        }
        
        int finalcost = mesos + fee + getFee(mesos);

        if (c.getPlayer().getMeso() < finalcost) {
            c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_SEND_NOT_ENOUGH_MESOS.getCode()));
            return;
        }
        int accid = getAccIdFromCNAME(recipient, true);
        if (accid == -1) {
            c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_SEND_NAME_DOES_NOT_EXIST.getCode()));
            return;
        }
        if (accid == c.getAccID()) {
            c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_SEND_SAMEACC_ERROR.getCode()));
            return;
        }
        
        c.getPlayer().gainMeso(-finalcost, false);
        
        boolean recipientOn = false;
        MapleClient rClient = null;
        int channel = c.getWorldServer().find(recipient);
        if (channel > -1) {
            recipientOn = true;
            Channel rcserv = c.getWorldServer().getChannel(channel);
            rClient = rcserv.getPlayerStorage().getCharacterByName(recipient).getClient();
        }

        if (ServerConstants.USE_DEBUG){
            System.out.print(c.getPlayer().getName() + " sends to " + recipient + mesos + " Mesos ");
            
        }
        
        if (inventoryId > 0) {
            MapleInventoryType inv = MapleInventoryType.getByType(inventoryId);
            Item item = c.getPlayer().getInventory(inv).getItem(itemPos);
            if (item != null && c.getPlayer().getItemQuantity(item.getItemId(), false) >= quantity) {
                if (ItemConstants.isRechargable(item.getItemId())) {
                    MapleInventoryManipulator.removeFromSlot(c, inv, itemPos, item.getQuantity(), true);
                } else {
                    MapleInventoryManipulator.removeFromSlot(c, inv, itemPos, quantity, true, false);
                }
                addItemToDB(item, quantity, mesos, c.getPlayer().getName(), getAccIdFromCNAME(recipient, false));
            } else {
                if (item != null) c.getPlayer().dropMessage(1, "You must assign up to " + c.getPlayer().getItemQuantity(item.getItemId(), false) + " of that item to send.");
                c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_SEND_INCORRECT_REQUEST.getCode()));
                return;
            }
        } else {
            addMesoToDB(mesos, c.getPlayer().getName(), getAccIdFromCNAME(recipient, false), 0);
        }
        if (recipientOn && rClient != null) {
            rClient.announce(MaplePacketCreator.sendDueyNotification(c.getPlayer().getName(), false));
        }
        
        c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_SEND_SUCCESSFULLY_SENT.getCode()));
    }
    
    public static void claimItem(MapleClient c, int packageId){
        List<DueyPackages> packages = new LinkedList<>();
        DueyPackages dp = null;
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            DueyPackages dueypack;
            try (PreparedStatement ps = con.prepareStatement("SELECT dueypackages.packageId, SenderName, Mesos, TimeStamp, Type, dueyitems.* FROM dueypackages LEFT JOIN dueyitems USING (PackageId) WHERE PackageId = ?")) {
                ps.setInt(1, packageId);
                try (ResultSet rs = ps.executeQuery()) {
                    dueypack = null;
                    if (rs.next()) {
                        dueypack = getItemByPID(rs);
                        dueypack.setSender(rs.getString("SenderName"));
                        dueypack.setMesos(rs.getInt("Mesos"));
                        dueypack.setSentTime(rs.getString("TimeStamp"));

                        packages.add(dueypack);
                    }
                }
            }
            dp = dueypack;
            if(dp == null) {
                System.out.println("Error: Null Duey package!");
                c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_RECV_UNKNOWN_ERROR.getCode()));
                c.announce(MaplePacketCreator.enableActions());
                return;
            }

            if (dp.getItem() != null) {
                if (!MapleInventoryManipulator.checkSpace(c, dp.getItem().getItemId(), dp.getItem().getQuantity(), dp.getItem().getOwner())) {
                    c.announce(MaplePacketCreator.sendDueyMSG(Actions.TOCLIENT_RECV_NO_FREE_SLOTS.getCode()));
                    return;
                } else {
                    MapleInventoryManipulator.addFromDrop(c, dp.getItem(), false);
                }
            }

            long gainmesos = 0;
            long totalmesos = (long) dp.getMesos() + (long) c.getPlayer().getMeso();

            if (totalmesos < 0 || dp.getMesos() < 0) gainmesos = 0;
            else {
                totalmesos = Math.min(totalmesos, Integer.MAX_VALUE);
                gainmesos = totalmesos - c.getPlayer().getMeso();
            }
            c.getPlayer().gainMeso((int)gainmesos, false);

            removeItemFromDB(packageId);
            c.announce(MaplePacketCreator.removeItemFromDuey(false, packageId));

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteItem(MapleClient c, int packageId){
        removeItemFromDB(packageId);
        c.announce(MaplePacketCreator.removeItemFromDuey(true, packageId));
    }
    
    // \/\/\/\/\/\/\/ METHODS FOR API \/\/\/\/\/\/\/\/
    
    // Sends a non-equipment item to an account
    public static void sendItem(int itemId, int quantity, int mesos, String sender, int recvAcctId){
        sendItem(itemId, quantity, mesos, -1, sender, 0, recvAcctId);
    }
    
    // Sends a time-limited item to an account
    public static void sendItem(int itemId, int quantity, int mesos, long timeLimit, String sender, int recvAcctId){
        sendItem(itemId, quantity, mesos, timeLimit, sender, 0, recvAcctId);
    }
    
    // Sends a time-limited item
    public static void sendItem(int itemId, int quantity, int mesos, long timeLimit, String sender, int recipientId, int recvAcctId){
        if (itemId < 2000000){
            sendEquip(itemId, mesos, timeLimit, sender, recipientId, recvAcctId);
            return;
        }
        Item item = new Item(itemId, (short) 0, (short) quantity);
        addItemToDB(item, quantity, mesos, sender, recipientId, recvAcctId, timeLimit);
    }
    
    // Sends an equipment with standard stats
    public static void sendEquip(int itemId, int mesos, String sender, int recvAcctId){
        sendEquip(itemId, mesos, -1, sender, 0, recvAcctId);
    }
    
    // Sends a time-limited equipment item with standard stats
    public static void sendEquip(int itemId, int mesos, long timeLimit, String sender, int recipientId, int recvAcctId){
        MapleItemInformationProvider infopro = MapleItemInformationProvider.getInstance();
        Equip eq = (Equip)infopro.getEquipById(itemId);
        addItemToDB(eq, 1, mesos, sender, recipientId, recvAcctId, timeLimit);
    }
    
    public static void sendEquip(int itemId, short upgradeSlots, byte level, 
            short str, short dex, short _int, short luk, short hp, short mp, 
            short watk, short matk, short wdef, short mdef, short acc, 
            short avoid, short hands, short speed, short jump, byte flag, String owner, 
            int mesos, long timeLimit, String senderName, int recipientId, int recvAcctId) {
        Equip eq = new Equip(itemId, (short) 0, upgradeSlots);
        eq.setLevel(level);
        eq.setStr(str);
        eq.setDex(dex);
        eq.setInt(_int);
        eq.setLuk(luk);
        eq.setHp(hp);
        eq.setMp(mp);
        eq.setWatk(watk);
        eq.setMatk(matk);
        eq.setWdef(wdef);
        eq.setMdef(mdef);
        eq.setAcc(acc);
        eq.setAvoid(avoid);
        eq.setHands(hands);
        eq.setSpeed(speed);
        eq.setJump(jump);
        eq.setFlag(flag);
        eq.setOwner(owner);
        addItemToDB(eq, 1, mesos, senderName, recipientId, recvAcctId, timeLimit);
    }

    private static void addMesoToDB(int mesos, String sName, int recipientID, int recvAcctId){
        addItemToDB(null, 1, mesos, sName, recipientID, recvAcctId, -1);
    }
    
    private static void addItemToDB(Item item, int quantity, int mesos, String sName, int recipientID){
        addItemToDB(item, quantity, mesos, sName, recipientID, 0, -1);
    }

    private static void addItemToDB(Item item, int quantity, int mesos, String sName, int recipientID, int recvAcctId, long timeLimit) {
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO dueypackages (ReceiverId, ReceiverAccountId, SenderName, Mesos, TimeStamp, Checked, Type) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, recipientID);
                ps.setInt(2, recvAcctId);
                ps.setString(3, sName);
                ps.setInt(4, mesos);
                ps.setString(5, getCurrentDate());
                ps.setInt(6, 1);
                if (item == null) {
                    ps.setInt(7, 3);
                    ps.executeUpdate();
                } else {
                    ps.setInt(7, item.getType());
                    
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        PreparedStatement ps2;
                        if (item.getType() == 1) { // equips
                            ps2 = con.prepareStatement("INSERT INTO dueyitems (PackageId, itemid, quantity, upgradeslots, level, itemLevel, itemExp, str, dex, `int`, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, flags, owner, TimeLimit, IsTimeLimitActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            Equip eq = (Equip) item;
                            ps2.setInt(2, eq.getItemId());
                            ps2.setInt(3, 1);
                            ps2.setInt(4, eq.getUpgradeSlots());
                            ps2.setInt(5, eq.getLevel());
                            ps2.setInt(6, eq.getItemLevel());
                            ps2.setInt(7, eq.getItemExp());
                            ps2.setInt(8, eq.getStr());
                            ps2.setInt(9, eq.getDex());
                            ps2.setInt(10, eq.getInt());
                            ps2.setInt(11, eq.getLuk());
                            ps2.setInt(12, eq.getHp());
                            ps2.setInt(13, eq.getMp());
                            ps2.setInt(14, eq.getWatk());
                            ps2.setInt(15, eq.getMatk());
                            ps2.setInt(16, eq.getWdef());
                            ps2.setInt(17, eq.getMdef());
                            ps2.setInt(18, eq.getAcc());
                            ps2.setInt(19, eq.getAvoid());
                            ps2.setInt(20, eq.getHands());
                            ps2.setInt(21, eq.getSpeed());
                            ps2.setInt(22, eq.getJump());
                            ps2.setByte(23, eq.getFlag());
                            ps2.setString(24, eq.getOwner());
                            if (timeLimit < 0) {
                                ps2.setLong(25, eq.getExpiration());
                                ps2.setBoolean(26, true);
                            }
                            else {
                                ps2.setLong(25, timeLimit);
                                ps2.setBoolean(26, false);
                            }
                        } else {
                            ps2 = con.prepareStatement("INSERT INTO dueyitems (PackageId, itemid, quantity, flags, owner, TimeLimit, IsTimeLimitActive) VALUES (?, ?, ?, ?, ?, ?, ?)");
                            ps2.setInt(2, item.getItemId());
                            ps2.setInt(3, quantity);
                            ps2.setInt(4, item.getFlag());
                            ps2.setString(5, item.getOwner());                            
                            if (timeLimit < 0) {
                                ps2.setLong(6, item.getExpiration());
                                ps2.setBoolean(7, true);
                            }
                            else {
                                ps2.setLong(6, timeLimit);
                                ps2.setBoolean(7, false);
                            }
                        }
                        ps2.setInt(1, rs.getInt(1));
                        ps2.executeUpdate();
                        ps2.close();
                    }
                }
            }
            
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<DueyPackages> loadItems(MapleCharacter chr) {
        List<DueyPackages> packages = new LinkedList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT dueypackages.packageId, SenderName, Mesos, TimeStamp, Type, itemid, quantity, upgradeslots, level, itemLevel, itemExp, str, dex, `int`, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, flags, owner, TimeLimit, IsTimeLimitActive FROM dueypackages LEFT JOIN dueyitems USING (PackageId) WHERE ReceiverId = ? OR ReceiverAccountId = ?")) {
                ps.setInt(1, chr.getId());
                ps.setInt(2, chr.getAccountID());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DueyPackages dueypack = getItemByPID(rs);
                        dueypack.setSender(rs.getString("SenderName"));
                        dueypack.setMesos(rs.getInt("Mesos"));
                        dueypack.setSentTime(rs.getString("TimeStamp"));
                        if (dueypack.sentTimeInMilliseconds() <= System.currentTimeMillis() - ((long) 30 * 24 * 60 * 60 * 1000)){
                            removeItemFromDB(dueypack.getPackageId());
                        }
                        else
                            packages.add(dueypack);
                    }
                }
            }
            
            con.close();
            return packages;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getCurrentDate() {
        String date = "";
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DATE) + (ServerConstants.USE_DUEY_INSTANT_DELIVERY ? -1 : 0); // instant duey ?
        int month = cal.get(Calendar.MONTH) + 1; // its an array of months.
        int year = cal.get(Calendar.YEAR);
        date += day <= 9 ? "0" + day + "-" : "" + day + "-";
        date += month <= 9 ? "0" + month + "-" : "" + month + "-";
        date += year;
        
        return date;
    }

    private static int getFee(int meso) {
        int fee = 0;
        if (meso >= 10000000) {
            fee = meso / 25;
        } else if (meso >= 5000000) {
            fee = meso * 3 / 100;
        } else if (meso >= 1000000) {
            fee = meso / 50;
        } else if (meso >= 100000) {
            fee = meso / 100;
        } else if (meso >= 50000) {
            fee = meso / 200;
        }
        return fee;
    }

    private static void removeItemFromDB(int packageid) {
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            
            PreparedStatement ps = con.prepareStatement("DELETE FROM dueypackages WHERE PackageId = ?");
            ps.setInt(1, packageid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("DELETE FROM dueyitems WHERE PackageId = ?");
            ps.setInt(1, packageid);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DueyPackages getItemByPID(ResultSet rs) {
        try {
            DueyPackages dueypack;
            if (rs.getInt("type") == 1) {
                Equip eq = new Equip(rs.getInt("itemid"), (byte) 0, -1);
                eq.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                eq.setLevel((byte) rs.getInt("level"));
                eq.setItemLevel((byte)rs.getInt("itemLevel"));
                eq.setItemExp(rs.getInt("itemExp"));
                eq.setStr((short) rs.getInt("str"));
                eq.setDex((short) rs.getInt("dex"));
                eq.setInt((short) rs.getInt("int"));
                eq.setLuk((short) rs.getInt("luk"));
                eq.setHp((short) rs.getInt("hp"));
                eq.setMp((short) rs.getInt("mp"));
                eq.setWatk((short) rs.getInt("watk"));
                eq.setMatk((short) rs.getInt("matk"));
                eq.setWdef((short) rs.getInt("wdef"));
                eq.setMdef((short) rs.getInt("mdef"));
                eq.setAcc((short) rs.getInt("acc"));
                eq.setAvoid((short) rs.getInt("avoid"));
                eq.setHands((short) rs.getInt("hands"));
                eq.setSpeed((short) rs.getInt("speed"));
                eq.setJump((short) rs.getInt("jump"));
                eq.setFlag(rs.getByte("Flags"));
                eq.setOwner(rs.getString("owner"));
                long timeLimit = rs.getLong("TimeLimit");
                if (timeLimit > 0){
                    if (rs.getBoolean("IsTimeLimitActive")){
                        eq.setExpiration(timeLimit);
                    }
                    else{
                        eq.setExpiration(System.currentTimeMillis() + timeLimit);
                    }
                }
                dueypack = new DueyPackages(rs.getInt("PackageId"), eq);
            } else if (rs.getInt("type") == 2) {
                Item newItem = new Item(rs.getInt("itemid"), (short) 0, (short) rs.getInt("quantity"));
                newItem.setOwner(rs.getString("owner"));
                long timeLimit = rs.getLong("TimeLimit");
                if (timeLimit > 0){
                    if (rs.getBoolean("IsTimeLimitActive")){
                        newItem.setExpiration(timeLimit);
                    }
                    else{
                        newItem.setExpiration(System.currentTimeMillis() + timeLimit);
                    }
                }
                dueypack = new DueyPackages(rs.getInt("PackageId"), newItem);
            } else {
                dueypack = new DueyPackages(rs.getInt("PackageId"));
            }
            return dueypack;
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
    }
}
