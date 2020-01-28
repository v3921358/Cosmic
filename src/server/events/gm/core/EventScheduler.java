package server.events.gm.core;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import server.events.gm.MapleEvent;
import server.events.gm.MapleOxQuiz.MapleOxQuiz;
import server.maps.MapleMapFactory;
import server.TimerManager;

public class EventScheduler {
	public static final int EVENT_HOUR = 3;	//TODO: Eventually schedule this from database (10 PM EST -> 3 AM UTC)
	public static final int MAX_PLAYERS_IN_EVENT = 100;

	private List<MapleEvent> events = new ArrayList<>();
	private ReentrantLock lock = new ReentrantLock();
	private MapleEvent currentEvent;

	public EventScheduler(MapleMapFactory mmf) {
		registerEvents(mmf);
	}

	private void registerEvents(MapleMapFactory mmf) {
		//MapleOxQuiz
		events.add(new MapleOxQuiz(mmf.getMap(MapleOxQuiz.MAPLE_OX_MAP_ID), MAX_PLAYERS_IN_EVENT));

		// Register start event task
		TimerManager.getInstance().register(new Runnable() {
			@Override
			public void run() {
				startRandomEvent();
			}
		}, 60 * 1000 * 1440, TimerManager.calculateDifferenceFromTime(EVENT_HOUR, 0, 0));
	}

	public void startRandomEvent() {
		lock.lock();
		currentEvent = events.get(ThreadLocalRandom.current().nextInt(0, events.size()));
		currentEvent.reset();
		currentEvent.startEvent();
		try {
			currentEvent.waitForEvent();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		currentEvent = null;
		lock.unlock();
	}

	public MapleEvent getEvent() {
		return currentEvent;
	}
}