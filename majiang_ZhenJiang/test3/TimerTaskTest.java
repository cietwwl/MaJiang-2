import java.util.Timer;
import java.util.TimerTask;


public class TimerTaskTest {
	static int step = 0;
	public static void main(String[] args) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("dddd");
				//Thread.currentThread().interrupt();
				step ++;
				if(step==10){
					timer.cancel();
				}
			}
		}, 0,1000);
	}
}
