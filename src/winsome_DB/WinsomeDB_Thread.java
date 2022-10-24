package winsome_DB;

import winsome_communication.WinsomeException;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WinsomeDB_Thread extends Thread {
	private final WinsomeDatabase database;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	boolean shall_stop = false;

	public WinsomeDB_Thread(WinsomeDatabase database) {
		super();
		this.database = database;
	}

	@Override
	public void run() {
		/*
		 * This method is used to run the thread.
		 * It will run until the thread is interrupted.
		 *
		 * 1. Save the database.
		 * 2. Sleep for 1 Minute.
		 */
		lock.lock();
		do{
			try {
				// 1. Save the database.
				database.save_DB();
				// 2. Sleep for 1 Minute.
				while (!condition.await(1, java.util.concurrent.TimeUnit.MINUTES))
				{
					try {
						database.save_DB();
					} catch (WinsomeException e) {
						System.err.println(e.getMessage());
					}
				}
			} catch (WinsomeException e) {
				System.err.println(e.getMessage());
			} catch (InterruptedException ignored) {
			}
		} while (!shall_stop);

		lock.unlock();
	}

	public void mustStop() {
		lock.lock();
		shall_stop = true;
		condition.signal();
		lock.unlock();
	}
}
