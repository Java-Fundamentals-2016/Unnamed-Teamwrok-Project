package Game;

/**
 * This class is deprecated, remove from project.
 */
public class Loop implements Runnable{
	// TODO: place loop here, add to it all update and display calls
	// This file is likely redundant

	private boolean isRunning;
	private Thread thread;


	@Override
	public void run() {

	}

	public synchronized void start() {
		if (isRunning) {
			return;
		}
		this.isRunning = true;
		this.thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		if (!isRunning) {
			return;
		}

		this.isRunning = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
