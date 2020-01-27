package api.controllers;

import java.util.List;

import io.javalin.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import client.command.utils.PlayerRanking;
import client.command.utils.PlayerRanking.Rank;

public class ServerCommunityController {
	public static Handler getRanking = ctx -> {
		ObjectMapper mapper = new ObjectMapper();
    	ArrayNode arrayNode = mapper.createArrayNode();
		List<Rank> rankings = PlayerRanking.getInstance().getRanking();

		for(Rank rank : rankings) {
			ObjectNode node = mapper.createObjectNode();
			node.put("rank", rank.getRank());
			node.put("id", rank.getId());
			node.put("name", rank.getName());
			node.put("level", rank.getLevel());
			arrayNode.add(node);
		}

		ctx.json(arrayNode);
	};
}
