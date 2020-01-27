package server.events.gm.core;

import java.util.List;
import java.util.ArrayList;

import server.events.gm.core.EventStep;

import server.TimerManager;

public class EventStepRunner implements Runnable {
    private List<EventStep> stepsInOrder = new ArrayList<EventStep>();
    private boolean isComplete = false;

    public void register(EventStep step) {
        stepsInOrder.add(step);
    }

    public void reset() {
        isComplete = false;
        stepsInOrder.clear();
    }

    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void run() {
        for(EventStep step : stepsInOrder) {
            step.initialize();
            TimerManager.getInstance().schedule(step, 0);
            synchronized(step) {
                try {
                    while(!step.isComplete()) {
                        step.wait();
                    }
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronized(this) {
            isComplete = true;
            this.notifyAll();
        }
    }
}