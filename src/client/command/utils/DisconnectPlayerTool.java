package client.command.utils;

import client.MapleCharacter;

import net.server.channel.Channel;
import net.server.world.World;

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

		return disconnectPlayer(chr, authority);
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

		return disconnectPlayer(chr, authority);
	}

	private static boolean disconnectPlayer(MapleCharacter chr, int authority) {
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
			chr.getClient().disconnect(true, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}