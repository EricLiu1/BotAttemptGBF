import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;


public class CustomInput {
	private Random rand = new Random();
	
	
	public void execute(String fileName) throws FileNotFoundException{
		FileReader file = new FileReader(fileName);
		Scanner scanFile = new Scanner(file);
		while(scanFile.hasNext() == true)
		{
			String s = scanFile.nextLine();
			String [] coord = s.split(" ");
			try{
				int x = Integer.parseInt(coord[0]);
				int y = Integer.parseInt(coord[1]);
				clickExact(x,y);
				System.out.println("Clicked "+ x+" "+y);
//				Main.write("Clicked "+ x+" "+y);
			}
			catch(Exception e){
				try {
					delay(Integer.parseInt(coord[1]) );
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			
			
			
		}
		scanFile.close();
		
		
	}
	public void clickExact(int x, int y) throws AWTException
	{
		    PointerInfo a = MouseInfo.getPointerInfo();
		    Point b = a.getLocation();
		    int mx = (int) b.getX();
		    int my = (int) b.getY();

		    double t = 200 + rand.nextInt(250);
		    double n = 50;
		    double dx = (x - mx) / n;
		    double dy = (y - my) / n;
		    double dt = t / n;

		    try {
		      Robot bot = new Robot();
		      for (int i = 0; i < n; i++) {
		        Thread.sleep((int) dt);
		        bot.mouseMove((int) (mx + dx * i), (int) (my + dy * i));
		      }
		      bot.mousePress(InputEvent.BUTTON1_MASK);
		      bot.mouseRelease(InputEvent.BUTTON1_MASK);
		    } catch (InterruptedException e) {
		      System.out.println(e);
		    }

	 }
	private void delay(int x) throws InterruptedException{
	    Thread.sleep(x);
	}

}
