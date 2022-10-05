package winsome_DB;

import winsome_comunication.Winsome_Exception;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Winsome_DB_Thread  extends Thread {
	private final Winsome_Database database;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	public Winsome_DB_Thread(Winsome_Database database) {
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
					} catch (Winsome_Exception e) {
						System.err.println(e.getMessage());
					}
				}
			} catch (Winsome_Exception e) {
				System.err.println(e.getMessage());
			} catch (InterruptedException e) {
				break;
			}
		} while (!isInterrupted());
	}
}
