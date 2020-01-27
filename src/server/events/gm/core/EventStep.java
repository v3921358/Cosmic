package server.events.gm.core;

import java.util.concurrent.locks.ReentrantLock;

public abstract class EventStep implements Runnable {
    private ReentrantLock lock = new ReentrantLock();
    private boolean isComplete;


    protected abstract void executeStep() throws InterruptedException;

    @Override
    public void run() {
        synchronized(this) {
            lock.lock();
            try {
                executeStep();    
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                isComplete = true;
                lock.unlock();
            }

            this.notify();
        }
    }

    public void initialize() {
        isComplete = false;
    }

    public boolean isComplete() {
        return isComplete;
    }
}