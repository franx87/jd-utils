/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.utils.event.queue
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package org.appwork.utils.event.queue;

import java.util.ArrayList;
import java.util.HashMap;

import org.appwork.utils.logging.Log;

/**
 * @author daniel
 * @param <D>
 * @param <T>
 * 
 */
public abstract class Queue extends Thread {

    public enum QueuePriority {
        HIGH,
        LOW,
        NORM
    }

    protected boolean debugFlag = false;
    protected QueuePriority[] prios;
    protected HashMap<QueuePriority, ArrayList<QueueAction<?, ? extends Throwable>>> queue = new HashMap<QueuePriority, ArrayList<QueueAction<?, ? extends Throwable>>>();
    protected final Object queueLock = new Object();

    protected ArrayList<QueueAction<?, ? extends Throwable>> queueThreadHistory = new ArrayList<QueueAction<?, ? extends Throwable>>(20);
    protected Thread thread = null;
    protected boolean waitFlag = true;

    public Queue(final String id) {
        super(id);
        /* init queue */
        this.prios = QueuePriority.values();
        for (final QueuePriority prio : this.prios) {
            this.queue.put(prio, new ArrayList<QueueAction<?, ? extends Throwable>>());
        }
        /* jvm should not wait for waiting queues */
        this.setDaemon(true);
        this.start();
    }

    /**
     * This method adds an action to the queue. if the caller is a queueaction
     * itself, the action will be executed directly. In this case, this method
     * can throw Exceptions. If the caller is not the QUeuethread, this method
     * is not able to throw exceptions, but the exceptions are passed to the
     * exeptionhandler method of the queueaction
     * 
     * @param <T>
     * @param <E>
     * @param item
     * @throws T
     */
    public <E, T extends Throwable> void add(final QueueAction<?, T> action) throws T {
        /* set calling Thread to current item */
        action.reset();
        action.setCallerThread(this, Thread.currentThread());
        if (this.isQueueThread(action)) {
            /*
             * call comes from current running item, so lets start item
             */
            this.startItem(action, false);
        } else {
            /* call does not come from current running item, so lets queue it */
            this.internalAdd(action);
        }
    }

    /**
     * Only use this method if you can asure that the caller is NEVER the queue
     * itself. if you are not sure use #add
     * 
     * @param <E>
     * @param <T>
     * @param action
     * @throws T
     */
    public <E, T extends Throwable> void addAsynch(final QueueAction<?, T> action) {
        /* set calling Thread to current item */
        if (this.isQueueThread(action)) {
            throw new RuntimeException("called addAsynch from the queue itself");
        } else {
            action.reset();
            action.setCallerThread(this, Thread.currentThread());
            this.internalAdd(action);
        }

    }

    @SuppressWarnings("unchecked")
    public <E, T extends Throwable> E addWait(final QueueAction<E, T> item) throws T, InterruptedException {
        /* set calling Thread to current item */
        item.reset();
        item.setCallerThread(this, Thread.currentThread());
        if (this.isQueueThread(item)) {
            /*
             * call comes from current running item, so lets start item
             * excaption handling is passed to top item. startItem throws an
             * exception in error case
             */
            this.startItem(item, false);
        } else {
            /* call does not come from current running item, so lets queue it */
            this.internalAdd(item);
            /* wait till item is finished */
            while (!item.isFinished()) {

                synchronized (item) {
                    item.wait(1000);
                }

            }
            if (item.getExeption() != null) {
                // throw exception if item canot handle the exception itself
                if (!item.callExceptionHandler()) {
                    if (item.getExeption() instanceof RuntimeException) {
                        throw (RuntimeException) item.getExeption();
                    } else {
                        throw (T) item.getExeption();
                    }
                }

            }
            if (item.gotKilled() && !item.gotStarted()) { throw new InterruptedException("Queue got killed!"); }

        }

        return item.getResult();
    }

    public void enqueue(final QueueAction<?, ?> action) {
        /* set calling Thread to current item */
        action.reset();
        action.setCallerThread(this, Thread.currentThread());
        this.internalAdd(action);
    }

    protected QueueAction<?, ? extends Throwable> getLastHistoryItem() {
        synchronized (this.queueThreadHistory) {
            if (this.queueThreadHistory.size() == 0) { return null; }
            return this.queueThreadHistory.get(this.queueThreadHistory.size() - 1);
        }
    }

    /**
     * Overwrite this to hook before a action execution
     */
    protected void handlePreRun() {
        // TODO Auto-generated method stub

    }

    public void internalAdd(final QueueAction<?, ?> action) {
        synchronized (this.queueLock) {
            this.queue.get(action.getQueuePrio()).add(action);
        }
        synchronized (this) {
            if (this.waitFlag) {
                this.waitFlag = false;
                this.notify();
            }
        }
    }

    /**
     * returns true if this queue shows debug info
     * 
     * @return
     */
    public boolean isDebug() {
        return this.debugFlag;
    }

    public boolean isEmpty() {
        synchronized (this.queueLock) {
            for (final QueuePriority prio : this.prios) {
                if (!this.queue.get(prio).isEmpty()) { return false; }
            }
            return true;
        }
    }

    /**
     * this functions returns true if the current running Thread is our
     * QueueThread OR the SourceQueueItem chain is rooted in current running
     * QueueItem
     */
    public boolean isQueueThread(final QueueAction<?, ? extends Throwable> item) {
        if (Thread.currentThread() == this.thread) { return true; }
        QueueAction<?, ? extends Throwable> last = item;
        Thread t = null;
        /*
         * we walk through actionHistory to check if we are still in our
         * QueueThread
         */
        while (last != null && (t = last.getCallerThread()) != null) {
            if (t != null && t instanceof Queue) {
                if (t == this.thread) {
                    if (this.debugFlag) {
                        org.appwork.utils.logging.Log.L.warning("Multiple queues detected-> external synchronization may be required! " + item);
                    }
                    return true;
                }
                last = ((Queue) t).getLastHistoryItem();
            } else {
                break;
            }
        }
        return false;
    }

    public boolean isWaiting() {
        return this.waitFlag;
    }

    public void killQueue() {
        synchronized (this.queueLock) {
            for (final QueuePriority prio : this.prios) {
                for (final QueueAction<?, ? extends Throwable> item : this.queue.get(prio)) {
                    /* kill item */
                    item.kill();
                    synchronized (item) {
                        item.notify();
                    }
                }
                /* clear queue */
                this.queue.get(prio).clear();
            }

        }
    }

    @Override
    public void run() {
        if (this.thread != null) { return; }
        this.thread = this;
        QueueAction<?, ? extends Throwable> item = null;
        while (true) {
            try {
                this.handlePreRun();
                synchronized (this) {
                    while (this.waitFlag) {
                        try {
                            this.wait();
                        } catch (final Exception e) {
                            org.appwork.utils.logging.Log.exception(e);
                        }
                    }
                }
                synchronized (this.queueLock) {
                    item = null;
                    for (final QueuePriority prio : this.prios) {
                        if (this.queue.get(prio).size() > 0) {
                            item = this.queue.get(prio).remove(0);
                            break;
                        }
                    }
                    if (item == null) {
                        this.waitFlag = true;
                    }
                }
                if (item == null || this.waitFlag) {
                    continue;
                }
                try {
                    this.startItem(item, true);
                } catch (final Throwable e) {

                }
            } catch (final Throwable e) {
                Log.L.info("Queue rescued!");
                Log.exception(e);
            }
        }
    }

    /**
     * changes this queue's debugFlag
     * 
     * @param b
     */
    public void setDebug(final boolean b) {
        this.debugFlag = b;
    }

    /* if you override this, DON'T forget to notify item when its done! */
    @SuppressWarnings("unchecked")
    protected <T extends Throwable> void startItem(final QueueAction<?, T> item, final boolean callExceptionhandler) throws T {
        try {
            if (this.thread != item.getCallerThread()) {
                synchronized (this.queueThreadHistory) {
                    this.queueThreadHistory.add(item);
                }
            }
            item.start(this);
        } catch (final Throwable e) {
            if (!callExceptionhandler || !item.callExceptionHandler()) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw (T) e;
                }
            }
        } finally {
            item.setFinished(true);
            if (this.thread != item.getCallerThread()) {
                synchronized (this.queueThreadHistory) {
                    if (this.queueThreadHistory.size() != 0) {
                        this.queueThreadHistory.remove(this.queueThreadHistory.size() - 1);
                    }
                }
            }
            synchronized (item) {
                item.notify();
            }

        }
    }
}
