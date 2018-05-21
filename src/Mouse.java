import java.awt.AWTEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
public class Mouse {

		
	    public static void main(String[] args) throws IOException {
	        JFrame frame = new JFrame();
	        Listener temp = new Listener();

	        Toolkit.getDefaultToolkit().addAWTEventListener(
			          temp, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK );
	        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	        frame.addWindowListener(new java.awt.event.WindowAdapter() {
	            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	            	System.out.println("closing");
	                Listener.writer.close();
	                System.exit(0);
	            }
	        });
	        frame.setVisible(true);
	       
	        
	    	while(true)
	    	{
	 	       if(Listener.i == -1)
		       {
	 	    	   frame.setVisible(false);
	 	    	   frame.dispose();
	 	    	   frame = new JFrame();
			       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	    	   frame.addWindowListener(new java.awt.event.WindowAdapter() {
	 		            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	 		                Listener.writer.close();
	 		                System.exit(0);
	 		            }
	 		        });
//	 	    	   temp = new Listener();
		    	   
//		    	   Toolkit.getDefaultToolkit().removeAWTEventListener(temp);
//		    	   temp = new Listener();
//			        Toolkit.getDefaultToolkit().addAWTEventListener(
//					          temp, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK );

		    	   frame.setVisible(true);
		    	   Listener.i = 0;
		       }
	    	}
	    	
	    }

	    private static class Listener implements AWTEventListener {
	    	volatile static PrintWriter writer;
	    	volatile static int i = 0;
	    	Listener(){
	    		try {
					writer = new PrintWriter("clicks.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	        public void eventDispatched(AWTEvent event) {
	            System.out.print(MouseInfo.getPointerInfo().getLocation() + " | ");
	            System.out.println(event);
	            if(writer == null)
	            {
	            	System.out.println("null");
	            }
	            System.out.println(writer);
//	        	writer.println(MouseInfo.getPointerInfo().getLocation());
	            i++;
	            if(i == 2)
	            {
	            	Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
	            	writer.println(mouseLoc.x + " "+ mouseLoc.y);
	            	i=-1;
	            	
	            }
	            System.out.println(i);
	            
//	            System.out.println(Mouse.flag);

	        }
	        
	        
	    }
	
}
