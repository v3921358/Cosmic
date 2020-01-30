package api;

import io.javalin.Javalin;
import net.server.Server;

import api.controllers.*;

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
		// Get Requests
		this.app.get("/hello", ctx -> ctx.html("Hello, Javalin!"));
		this.app.get("/ranks", ServerCommunityController.getRanking);

		// Post Requests
		this.app.post("/duey", DueySystemController.givePackage);
		this.app.post("/dc", ConnectionToolController.disconnectPlayer);
	}
}