package api.controllers;

import java.util.List;

import io.javalin.Handler;

import net.server.Server;
import net.server.world.World;
import client.command.utils.DisconnectPlayerTool;

public class ConnectionToolController {
	public static final String CHARACTER_ID_KEY = "char_id";
	public static final String WORLD_ID_KEY = "world_id";
	public static final int AUTHORITY = 1;

	public static Handler disconnectPlayer = ctx -> {
		try {
			int charId = Integer.parseInt(ctx.formParam(CHARACTER_ID_KEY));
			int worldId = Integer.parseInt(ctx.formParam(WORLD_ID_KEY));

			boolean res = DisconnectPlayerTool.disconnectPlayerById(charId, Server.getInstance().getWorld(worldId), AUTHORITY);

			if(res) {
				ctx.status(200);
			} else {
				ctx.status(403);
			}
		} catch(Exception e) {
			e.printStackTrace();
			ctx.status(400);
		}
	};
}
