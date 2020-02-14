package client.command.utils;

import client.MapleCharacter;

import net.server.channel.Channel;
import net.server.world.World;
import server.maps.MapleMap;
import tools.FilePrinter;

public class DisconnectPlayerTool {

	public static boolean disconnectPlayerByName(String chrName, World world, int authority) {
		// Standard character location
		MapleCharacter chr = world.getPlayerStorage().getCharacterByName(chrName);

		// Find character if stuck in channel
		if(chr == null) {
			for(Channel ch : world.getChannels()) {
				chr = ch.getPlayerStorage().getCharacterByName(chrName);
				if(chr != null) {
					FilePrinter.printError(FilePrinter.ACCOUNT_STUCK, String.format("Character {%s} found in channel {%d} but not in world.", chrName, ch.getId()));
					break;
				}
			}
		}

		return disconnectPlayer(chr, authority, false);
	}

	public static boolean disconnectPlayerById(int accId, World world, int authority) {
		// Standard character location
		MapleCharacter chr = world.getPlayerStorage().getCharacterById(accId);

		// Find character if stuck in channel
		if(chr == null) {
			for(Channel ch : world.getChannels()) {
				chr = ch.getPlayerStorage().getCharacterById(accId);
				if(chr != null) {
					FilePrinter.printError(FilePrinter.ACCOUNT_STUCK, String.format("Character id {%d} found in channel {%d} but not in world.", accId, ch.getId()));
					break;
				}
			}
		}

		return disconnectPlayer(chr, authority, false);
	}

	public static boolean forceDisconnectPlayerOnMapByName(MapleMap map, String chrName, int authority) {
		// Find character in map
		MapleCharacter chr = map.getCharacterByName(chrName);
		return disconnectPlayer(chr, authority, true);
	}

	public static boolean forceDisconnectPlayerOnMapById(MapleMap map, int accId, int authority) {
		// Find character in map
		MapleCharacter chr = map.getCharacterById(accId);
		return disconnectPlayer(chr, authority, true);
	}

	private static boolean disconnectPlayer(MapleCharacter chr, int authority, boolean shutdown) {
		if(chr == null) {
			return false;
		}

		if(chr.gmLevel() >= authority) {
			FilePrinter.printError(FilePrinter.UNAUTHORIZED, String.format("Not enough authorization to disconnect Character {%s}.", chr.getName()));
			return false;
		}

		try {
			if(chr.getMap() == null) {
				FilePrinter.printError(FilePrinter.ACCOUNT_STUCK, String.format("Character {%s} is not in a valid map.", chr.getName()));
			} 
			chr.getClient().disconnect(shutdown, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}