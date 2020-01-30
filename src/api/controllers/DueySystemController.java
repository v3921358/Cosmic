package api.controllers;

import java.util.List;

import io.javalin.Handler;

import client.processor.npc.DueyProcessor;

public class DueySystemController {
	public static final String ACCOUNT_ID_KEY = "account_id";
	public static final String ITEM_ID_KEY = "item_id";
	public static final String QUANTITY_KEY = "quantity";
	public static final String MESOS_KEY = "mesos";
	public static final String SENDER_KEY = "sender";
	public static final String TIME_LIMIT_KEY = "time_limit";

	public static Handler givePackage = ctx -> {
		try {
			int accountId = Integer.parseInt(ctx.formParam(ACCOUNT_ID_KEY));
			int itemId = Integer.parseInt(ctx.formParam(ITEM_ID_KEY));
			int quantity = Integer.parseInt(ctx.formParam(QUANTITY_KEY));
			int mesos = Integer.parseInt(ctx.formParam(MESOS_KEY));
			String sender = ctx.formParam(SENDER_KEY);
			long timeLimit = Long.parseLong(ctx.formParam(TIME_LIMIT_KEY));

			DueyProcessor.sendItem(itemId, quantity, mesos, timeLimit, sender, accountId);

			ctx.status(200);
		} catch(Exception e) {
			e.printStackTrace();
			ctx.status(400);
		}
	};
}
