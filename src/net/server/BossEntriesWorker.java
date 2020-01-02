package net.server;

import net.server.world.World;
import client.MapleCharacter;
import constants.ServerConstants;

/**
* @author allenaerostar
*/

public class BossEntriesWorker implements Runnable {
    private World wserv;
    
    @Override
    public void run() {
        
        PlayerStorage ps = wserv.getPlayerStorage();
        for(MapleCharacter chr: ps.getAllCharacters()) {
            if(chr != null && chr.isLoggedin()) {
                chr.resetBossEntries();
            }
        }
        wserv.resetBossEntries();
    }
    
    public BossEntriesWorker(World world) {
        wserv = world;
    }
}