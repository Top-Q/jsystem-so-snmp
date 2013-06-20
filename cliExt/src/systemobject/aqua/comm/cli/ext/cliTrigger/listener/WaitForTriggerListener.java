package systemobject.aqua.comm.cli.ext.cliTrigger.listener;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import systemobject.aqua.comm.cli.ext.cliTrigger.Trigger;

/**
 * @author Itzhak.Hovav
 */
public class WaitForTriggerListener implements TriggerListener {

	private boolean triggerFound = false;

	private ReadWriteLock rwLock = new ReentrantReadWriteLock();

	private Trigger trigger = null;

	private String name = null;

	public WaitForTriggerListener(Trigger trigger) {
		super();
		setName(getClass().getSimpleName() + "." + System.currentTimeMillis());
		setTrigger(trigger);
	}

	public void triggerNotify(Trigger trigger) {
		setTriggerFound(true);
	}

	public String listenerName() {
		return name;
	}

	public boolean waitForTrigger(long timeout, long pollingInterval) {
		trigger.addListener(this);
		boolean val = false;
		try {
			long start = System.currentTimeMillis();
			while ((System.currentTimeMillis() - start) < timeout
					&& !(val = isTriggerFound())) {
				try {
					Thread.sleep(pollingInterval);
				} catch (InterruptedException e) {
					continue;
				}
			}
		} finally {
			trigger.removeListener(this);
		}
		return !val;
	}

	public Trigger getTrigger() {
		return this.trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTriggerFound() {
		try {
			rwLock.readLock().lock();
			return this.triggerFound;
		} finally {
			try {
				rwLock.readLock().unlock();
			} catch (Exception e1) {
				try {
					rwLock.readLock().unlock();
				} catch (Exception e2) {
					e2.printStackTrace(System.out);
				}
			}
		}
	}

	public void setTriggerFound(boolean triggerFound) {
		try {
			rwLock.writeLock().lock();
			this.triggerFound = triggerFound;
		} finally {
			rwLock.writeLock().unlock();
		}
	}

}
