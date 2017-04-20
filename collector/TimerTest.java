import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class TimerTest {
	
	private static Date date = new Date();
	private Timer timer;
	
	public TimerTest() {
		timer = new Timer();
		System.out.println("A task will be start after 1 secs");
		timer.scheduleAtFixedRate(new PeriodTask(), 1*1000, 10*1000);
		
		
	}
	
	class PeriodTask extends TimerTask {

		@Override
		public void run() {
			System.out.println(date.toString());
			
		}
		
	}
	

	public static void main(String[] args) throws Exception {
		TimerTest test = new TimerTest();

	}

}
