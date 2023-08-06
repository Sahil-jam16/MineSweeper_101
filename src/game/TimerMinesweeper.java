package game;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

class TimerMinesweeper {
	
	private static float TIMER = 0f;
	private static boolean timer_running;
	private static Timer timer;

	private static TimerTask task;
	private static float game_time = 0f;

	
	TimerMinesweeper () {
		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				
				TIMER += 0.1;
				BigDecimal bd = new BigDecimal(TIMER); 
				BigDecimal res = bd.setScale(1, RoundingMode.HALF_UP);
				TIMER = res.floatValue();
				
				if (TIMER < 99999) FrameMinesweeper.setTimer(TIMER);
			}
		};
		
		timer_running = false;
	}
	
	public void startTimer() {
		timer.scheduleAtFixedRate(task, 100, 100);
		timer_running = true;
	}
	
	public void stopTimer() {
		timer.cancel();
		timer_running = false;
	}
	
	// getter & setter
	public float getTimer() {
		System.out.println(TIMER);
		return TIMER;

	}
	public void setTimer(int value) {
		TIMER = value;
	}
	public boolean isTimeRunning() {
		return timer_running;
	}

}
