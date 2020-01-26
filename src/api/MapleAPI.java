package api;

import io.javalin.Javalin;
import net.server.Server;

import api.controllers.ServerCommunityController;

public class MapleAPI {

	private Server server;
	private Javalin app;

	public MapleAPI(Server server, int port) {
		this.server = server;

		this.app = Javalin.create()
			.start(port);

		registerRoutes();
	}

	public void registerRoutes() {
		this.app.get("/hello", ctx -> ctx.html("Hello, Javalin!"));
		this.app.get("/ranks", ServerCommunityController.getRanking);
	}
}