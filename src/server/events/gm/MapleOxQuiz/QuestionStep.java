package server.events.gm.MapleOxQuiz;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import okhttp3.*;

import org.json.JSONObject;
import org.json.JSONArray;

import org.apache.commons.lang3.StringEscapeUtils;

import constants.ExpTable;
import client.MapleCharacter;
import net.server.Server;
import server.maps.MapleMap;
import server.events.gm.core.EventStep;
import tools.MaplePacketCreator;
import tools.data.output.MaplePacketLittleEndianWriter;
import net.SendOpcode;

public class QuestionStep extends EventStep {
	public static final int MAP_EFFECT = 5120017;
	public static final int QUESTION_TIMER = 10;
	public static final String OPEN_TRIVIA_URL = "https://opentdb.com/api.php?amount=%d&type=boolean";

	public static final String ERROR_MSG = "Sorry everyone, looks like something went wrong with gathering trivia...";
	public static final String ROUND_MSG = "Ready?";
	public static final String CORRECT_MSG = "You got it!";
	public static final String INCORRECT_MSG = "Better luck next time!";

	private static final int COORD_LEFT_BOUND = -286;
    private static final int COORD_RIGHT_BOUND = -156;
    private static final int COORD_LOWER_BOUND = -26;
    private static final int EXP_PER_CORRECT_ANS = 200;

	MapleMap map;
	Map<String, Boolean> questions = new HashMap<String, Boolean>();
	Map<MapleCharacter, Integer> results = new HashMap<MapleCharacter, Integer>();

	public QuestionStep(MapleMap map, int numQuestions) {
		this.map = map;
		getProblems(numQuestions);
	}

	// Impl abstract method
	protected void executeStep()  throws InterruptedException {
		// Send error response to players
		if(questions.size() == 0) {
			map.startMapEffect(ERROR_MSG, MAP_EFFECT, 7 * 1000);
			Thread.sleep(8 * 1000);
			return;
		}

		// Send questions
		for(Map.Entry<String, Boolean> entry : questions.entrySet()) {
			map.startMapEffect(ROUND_MSG, MAP_EFFECT, 2 * 1000);
			Thread.sleep(3 * 1000);
			sendQuestion(entry.getKey());
			Thread.sleep(QUESTION_TIMER * 1000);
			removeClock();
			checkAnswer(entry.getValue());
			Thread.sleep(8 * 1000);
		}

		// Show Ranking
		showRanking();
	}

	private void getProblems(int numQuestions) {
		OkHttpClient httpClient = new OkHttpClient();
		Request request = new Request.Builder()
            .url(String.format(OPEN_TRIVIA_URL, numQuestions))
            .build();

        try {
        	Response response = httpClient.newCall(request).execute();
        	JSONObject jsonObj = new JSONObject(response.body().string());
        	JSONArray jsonArr = jsonObj.getJSONArray("results");
        	for(int i = 0; i < jsonArr.length(); i++) {
        		insertQuestion(jsonArr.getJSONObject(i));
        	}
        } catch(IOException e) {
        	e.printStackTrace();
        }
	}

	private void insertQuestion(JSONObject trivia) {
		String question = StringEscapeUtils.unescapeHtml4(trivia.getString("question"));
		Boolean ans = Boolean.parseBoolean(trivia.getString("correct_answer"));
		questions.put(question, ans);
	}

	private void sendQuestion(String question) {
		map.startMapEffect(question, MAP_EFFECT, (QUESTION_TIMER - 1) * 1000);
		map.broadcastMessage(MaplePacketCreator.serverNotice(6, question));
		map.broadcastMessage(MaplePacketCreator.getClock(QUESTION_TIMER));
	}

	private void removeClock() {
		map.broadcastMessage(MaplePacketCreator.removeClock());
	}

	private void checkAnswer(boolean answer) {
		List<MapleCharacter> chars = new ArrayList<>(map.getCharacters());
		for(MapleCharacter chr : chars) {
			if (chr != null) {
				if (isCorrectAnswer(chr, answer)) {
					GiveExpOnCorrectAnswer(chr);
					map.startMapEffect(CORRECT_MSG, MAP_EFFECT, 7 * 1000);
					map.broadcastMessage(MaplePacketCreator.showEffect("quest/party/clear"));
        			map.broadcastMessage(MaplePacketCreator.playSound("Party1/Clear"));
        			results.put(chr, results.getOrDefault(chr, 0) + 1);
				}
				else {
					map.startMapEffect(INCORRECT_MSG, MAP_EFFECT, 7 * 1000);
					chr.getClient().announce(MaplePacketCreator.showEffect("quest/party/wrong_kor"));
        			chr.getClient().announce(MaplePacketCreator.playSound("Party1/Failed"));
        			results.put(chr, results.getOrDefault(chr, 0));
				}
			}
		}
	}

	private void showRanking() {
		//creating our own specific packet
		final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendOpcode.GUILD_OPERATION.getValue());
        mplew.write(0x49);
        mplew.writeInt(9010000);
        mplew.writeInt(results.size());

        results.entrySet().stream()
        	.sorted(Map.Entry.<MapleCharacter, Integer>comparingByValue().reversed())
        	.forEach((player) -> {
        		mplew.writeMapleAsciiString(player.getKey().getName());
                mplew.writeInt(player.getValue());
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
                mplew.writeInt(0);
        	});

        map.broadcastMessage(mplew.getPacket());
	}

	private boolean isCorrectAnswer(MapleCharacter chr, boolean answer) {
        double x = chr.getPosition().getX();
        double y = chr.getPosition().getY();

        // Left is answering TRUE
        if(x < COORD_LEFT_BOUND && y > COORD_LOWER_BOUND && answer) {
            return true;
        } 
        // Right is answering FALSE
        else if (x > COORD_RIGHT_BOUND && y > COORD_LOWER_BOUND && !answer) {
            return true;
        }
        else {
            return false;
        }
    }

    private void GiveExpOnCorrectAnswer(MapleCharacter chr) {
    	int requiredExp = ExpTable.getExpNeededForLevel(chr.getLevel());
    	// Modified Sigmoid Exp % curve
    	long exp = Math.round(
    		requiredExp * (2.5 / (1 + Math.exp(
    			2.5 * ((double)chr.getLevel())/ExpTable.MAX_LEVEL
    		)))
    	) / (questions.size() == 0 ? 1 : questions.size());

    	exp = exp == 0 ? 1 : exp;
    	chr.gainExpNoModifiers(exp, true, true, true);
    }
}

