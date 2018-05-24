

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
//import java.util.Scanner;

//import javax.imageio.ImageIO;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.Clip;

public class Bot implements Runnable {

  //settings
  private int iterations;
  private int safeRound;
  private Stage stage; 
  private int HLBound;
  private int clickDelay;
  private int delayNormal;
  private double delayLag;
  private int delayHL;
  private boolean medium;
  private Mode mode;
  private Loc[][] skillButtons;


  public Bot(Properties properties) {

    Main.write("############READING SETTINGS");
    //runtime
    iterations = checkProperty(properties, "iterations", 4);
//    if (runtime < 0) {
//      runtime = 60;
//    }
//    iterations = 30;
    Main.write("iterations=" + iterations);
    
    int m = checkProperty(properties,"mode",1);
//    m = 3;
//    System.out.println("MODE "+m + "\n\n\n");

    if(m == 1)
    {
        mode = Mode.HARD;
    }
    if(m == 2)
    {
    	mode = Mode.EVENT;
    }
    if(m == 3)
    {
    	mode = Mode.RAIDS;
    }
    if(m == 4)
    {
    	mode = Mode.REPEAT;
    }
    if(m == 5)
    {
    	// want to set the flag of aid to true if it is 
    	// for a raid and false for a quest
    	mode = Mode.REPEATEVENT;
    }
    if(m == 6)
    {
    	mode = Mode.ANGELHALO;
    }
    
    int med = checkProperty(properties, "medium", 0);
    medium = !(med == 0);
    delayLag=1;
    //delayNormal
    delayNormal = checkProperty(properties, "delayNormal", 3000);
    if (delayNormal < 0) {
      delayNormal = 3000;
    }
    if (delayNormal < 3000) {
      System.out.println("WARNING: delayNormal below 3000ms may cause the bot to desync");
    }
    delayNormal *= delayLag;
    System.out.println("delayNormal=" + delayNormal);

    //delayHL
    delayHL = checkProperty(properties, "delayHL", 2500);
    if (delayHL < 0) {
      delayHL = 2500;
    }
    if (delayHL < 2500) {
      System.out.println("WARNING: delayHL below 2500ms may cause the bot to desync");
    }
    delayHL *=delayLag;
    System.out.println("delayHL=" + delayHL);

    //clickDelay
    clickDelay = checkProperty(properties, "clickDelay", 150);
    if (clickDelay < 0) {
      clickDelay = 150;
    }
    if (clickDelay < 150) {
      System.out.println("WARNING: clickDelay below 150ms causes the bot to act too quickly, possibly flagging you for making too many actions. Not recommended");
    }
    Main.write("clickDelay=" + clickDelay);

    safeRound = checkProperty(properties, "safeRound", 7);
    Main.write("safeRound=" + safeRound);

    HLBound = checkProperty(properties, "HLBound", 1);
    Main.write("HLBound=" + HLBound);

    // may want to reimplement later
//    String[] soundString = new String[]{"Lyria singing", "Ifrit screaming", "Sagitarius warning"};
//    sound = checkProperty(properties, "sound", 1);
//    if (sound < 1 || sound > 3) {
//      sound = 1;
//    }
//    System.out.println("Warning sound =" + (soundString[sound - 1]));
  }

  // no idea what this does
  public int checkProperty(Properties properties, String name, int def) {
    int result;
    try {
      result = Integer.parseInt(properties.getProperty(name));
    } catch (NumberFormatException e) {
      System.out.println("Could not parse " + name + ", using default value");
      result = def;
    }
    return result;
  }

  //screenshot being used in current iteration
  private BufferedImage currentSS;
  private BufferedImage nightmareHalo; 
  private Loc start;



  private Random rand = new Random();

  //STAGE
  private int actionDelay;



  public void run() {
    try {
      init();
      if (start != null) {
    	  // makes the skill buttons
    	skillButtonCreation();
        //random delay in ms between actions
        actionDelay = delayNormal;

        System.out.println("############STARTING BOT");

        //main loop
//        int i = 0;
//        boolean confirm = true; 
        while (iterations > 0) {
        	// testing purposes
//        	if(i==0)
//        	{
//        		stage = Stage.OTHER;
//        		clickExact(480,700);
//        		delay();
////        		i++;
//        	}
          //take screenshot
          currentSS = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

//          System.out.println("FOUND STAGE:" + newStage);
//          handle(newStage);
          switch(stage)
          {
          case HOME:      actionDelay = delayNormal;
          			      questChoosing();
          			      delay();
          			      break;
          case QUEST:     actionDelay = delayHL;
          			  	  questAcceptance();	  
          			  	  delay();
          			  	  break;
          case CONFIRM:	  questConfirm();		
          			   	  delay();
          			   	  break;
          case SELECTION: actionDelay = delayNormal;
          				  summonSelection(true);
          				  delay();
          				  break;
	      case AID:       actionDelay = delayNormal;
				          Thread.sleep(actionDelay + 7500);
				     	  requestAid(false);
				     	  delay();
				     	  break;
          case BATTLE:    
//        	  			  delay();
        	  			  auto(false);
          			      delay();
          			      break;
          case DONE:      actionDelay = delayNormal;
			     	 	  done(); 
			     	 	  iterations --; 
			     	 	  delay();
			     	 	  break;
          case RAIDCODE:  raidFinder();
          				  break;
          case ENTERCODE: enterCodeViraMate();
          				  delay();
          				  break;
          case REPEAT:    repeatQuest();
          			   	  delay();
          			   	  break;
          default:
        	  break;
          }

          //actionDelay+random delay in ms

//          current = System.currentTimeMillis();
        }

      } else {
    	  Main.write("Something went wrong");
      }
    } catch (Exception e) {
      Main.write("Exception");
      
    }
   

  }


private void delay() throws InterruptedException{
    Main.write("Current iteration: "+iterations  +  " Current Stage: " + stage);

    int delay = rand.nextInt(300);
    Thread.sleep(actionDelay + delay);
}

private void delay(int x) throws InterruptedException{
    Thread.sleep(x);
}


  private void init() throws AWTException, IOException, InterruptedException {
//    BufferedImage nightmareHalo = ImageIO.read(getClass().getClassLoader().getResource("img/D-Halo.jpg"));;
//    Main.write("The bot is set to run for " + iterations +" iterations");
// might want to check this later
    while (start == null) {
      // can reduce delay later between running and button click 
      Thread.sleep(1000);
      currentSS = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      //find base pic in screenshot
      start = new Loc(200,625);
      if (start == null) {
        System.out.println("############GAMEBOARD NOT FOUND");
      } else {
        //adjust to old values
        if (!medium) {
          start.setY(start.getY() - 196);
        }

      }
    }
    startingStage();
  }
  boolean flag = true;
  private void startingStage(){
	    if(mode == Mode.HARD )
	    {   if(flag)
		    {
	    		stage = Stage.HOME; 
	    		flag = false;
		    }
	    	else
	    	{
	    		stage = Stage.QUEST;
	    	}
	    	
	    }
	    if(mode == Mode.RAIDS)
	    {
	    	stage = Stage.RAIDCODE;
	    }
	    if(mode == Mode.REPEAT || mode == Mode.REPEATEVENT || mode == Mode.ANGELHALO)
	    {
	    	stage = Stage.REPEAT;
	    }
  }
  
  //calls countMisses for each possible position inside bigImage
  //return null if not found. returns upperleft pixel location if found
//  private Loc findMatches(BufferedImage bigImage, BufferedImage target, int maxMiss, int maxDiff) {
//    for (int y = 0; y < bigImage.getHeight() - target.getHeight(); y++) {
//      for (int x = 0; x < bigImage.getWidth() - target.getWidth(); x++) {
//        int miss = countMisses(bigImage, x, y, target, maxMiss, maxDiff);
//        if (miss < maxMiss) {
//        	System.out.println(miss);
//          return new Loc(x, y);
//        }
//      }
//    }
//    return null;
//  }
  
  /* Exclusize for hard level question*/
  // hits the quest button and moves to the next stage
  private void questChoosing() throws AWTException{
	  if(mode == Mode.HARD)
	  {
	     Loc questButton = new Loc(425, 550);
		 click(questButton);
	  }
	  else if (mode == Mode.EVENT)
	  {
		 Loc eventBannerButton = new Loc(400,840);
		 click(eventBannerButton);
	  }

      stage = Stage.QUEST;
  }
  
  // clicks the hard showdown quest 
  private void questAcceptance() throws AWTException{
	  Loc showdownButton = new Loc(400,840);
	  // 2nd is for non magna fest
//	  Loc showdownButton = new Loc(400,750);
	  click(showdownButton);
	  stage = Stage.CONFIRM;

	  
  }
  private void questConfirm() throws AWTException{
	  Loc showdownButton = new Loc(450,525);
	  click(showdownButton);
	  stage = Stage.SELECTION;
  }

  // will actually do summon checking later
  // need to check if friend or not
  private void summonSelection(boolean aid) throws AWTException, InterruptedException{
	  Loc summonSelectButton = new Loc(500,370);
	  click(summonSelectButton);
      delay();
	  Loc clickingSummon = new Loc(500,470);
	  click(clickingSummon);
      delay();
	  Loc clickingConfirm = new Loc(450,730);
	  click(clickingConfirm);
	  if(aid == false)
	  {
		  stage = Stage.BATTLE;
	  }
	  else
	  {
		  stage = Stage.AID;
	  }
	  
	  
  }
  private void okButton() throws AWTException {
	  Loc okButton = new Loc(300,570);
	  click(okButton);
  }
  private void requestAid(boolean aid) throws AWTException, InterruptedException{
	  Loc showdownButton = null;
	  
	  if(mode == Mode.REPEAT )
	  {
		 Loc okButton = new Loc(300,520);
		 click(okButton);
		 delay();
		 okButton();
	  }
	  else if(aid)
	  {
		  showdownButton = new Loc(450,650);
		  click(showdownButton);
	      // haven't tested the done feature
		  Loc confirmButton = new Loc(400,650);
		  click(confirmButton);
	  }
	  else
	  {
		  showdownButton = new Loc(100,650);
		  click(showdownButton);

	  }
	  stage = Stage.BATTLE;
	  
  }
  private void skillButtonCreation(){
	  skillButtons = new Loc[4][4];
	  int cordX = 65;
	  int cordY = 720;
	  for(int x = 0;x<4;x++)
	  {

		  for(int y = 0; y<4;y++)
		  {
			  int cordPrimeY = y> 1? cordY +30 : cordY ;
			  int cordPrimeX = y%2==1? cordX + 50:cordX;

			  skillButtons[x][y] = new Loc(cordPrimeX,cordPrimeY);
		  }
		  cordX += 84;
	  }

  }
  private void dmg(Loc clicks[])throws AWTException, InterruptedException{
	  if(skillButtons[3][3]==null)
	  {
		  skillButtonCreation();
	  }
	for(int i = 0; i<clicks.length;i++)
	{
		int x = clicks[i].getX();
		int y = clicks[i].getY();
//		System.out.println(x+" "+y);
		clickExact(skillButtons[x][y]);
		
		delay(1000);
	}
	
  }
  private Loc[] smORC()
  {	  
	  Loc clicks[] = new Loc[8];
	  clicks[0]= new Loc(3,2);
	  clicks[1]= new Loc(2,1);
	  clicks[2]= new Loc(1,1);
	  clicks[3]= new Loc(1,0);
	  clicks[4]= new Loc(0,0);
	  clicks[5]= new Loc(0,1);
	  clicks[6]= new Loc(0,2);
	  clicks[7]= new Loc(0,3);
	  return clicks;
	  
  }
//  // will get from user or something
//  private Loc[] sequencing(){
//	  Loc clicks[] = new Loc[13];
//	  clicks[0]= new Loc(0,0);
//	  clicks[1]= new Loc(0,1);
//	  clicks[2]= new Loc(0,2);
//	  clicks[3]= new Loc(0,3);
//	  clicks[4]= new Loc(1,2);
//	  clicks[5]= new Loc(3,0);
//	  clicks[6]= new Loc(3,1);
//	  clicks[7]= new Loc(2,0);
//	  clicks[8]= new Loc(2,2);
//	  clicks[9]= new Loc(3,2);
//	  clicks[10]= new Loc(2,1);
//	  clicks[11]= new Loc(1,1);
//	  clicks[12]= new Loc(1,0);
//
//
//
//
//	  return clicks;
//  }
//  
  // will get from user or something
//  private Loc[] guildWarsLight(){
//	  Loc clicks[] = new Loc[13];
//	  clicks[0]= new Loc(0,0);
//	  clicks[1]= new Loc(0,1);
//	  clicks[2]= new Loc(3,0);
//	  clicks[3]= new Loc(3,2);
//	  clicks[4]= new Loc(0,2);
//	  clicks[5]= new Loc(0,3);
//	  clicks[6]= new Loc(1,2);
//	  clicks[7]= new Loc(2,0);
//	  clicks[8]= new Loc(2,2);
//	  clicks[9]= new Loc(2,1);
//	  clicks[10]= new Loc(1,1);
//	  clicks[11]= new Loc(1,0);
//	  clicks[12]= new Loc(3,0);
//
//
//
//
//	  return clicks;
//  }
  private void auto(boolean leech) throws AWTException, InterruptedException{
	  if(leech == false)
	  {
		
		  dmg(smORC());

	  }
	  delay();
	  Loc attackButton = new Loc(475,475);
	  click(attackButton);
      delay();
	  Loc autoButton = new Loc(100,500);
	  click(autoButton);
	  if(mode == Mode.HARD)
	  {
		  Thread.sleep(actionDelay + 100000 );
	  }
	  else if ( mode == Mode.RAIDS)
	  {
//		  Thread.sleep(actionDelay + 50000);
		  Thread.sleep(actionDelay + 125000 ); 
	  }
	  else{
		  Thread.sleep(actionDelay + 100000 );
//		  Thread.sleep(actionDelay + 250000);
	  }
	  stage = Stage.DONE;
  }
  //quest cancel 2x as friend request
//  private void friendRequest() throws AWTException{
//	  Loc cancelButton = new Loc(150,600);
//	  click(cancelButton);
//  }
  // add friend request cancel feature later
  
  // does a comparison between the two images starting at XY and ending at XY 
  // returns true if comparison works false otherwise
  private boolean imageCompare(BufferedImage input, BufferedImage key, 
		  					   int startingX, int startingY, int endingX, int endingY){

  for (int y = startingY; y < endingY; y++) 
  {
	  for (int x = startingX; x < endingX; x++) 
	  {
		  int val1 = (input.getRGB(x, y)) & 0xff;
		  int val2 = (key.getRGB(x, y)) & 0xff;
		  int abs_val = Math.abs(val1-val2);
		  if(abs_val > 50)
		  {
			  return false;
		  }
	   }
  }
  return true;
  }
  // checks if nightmare and if it is then stops the bot
  private boolean nightmareHalo() throws HeadlessException, AWTException{
      currentSS = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      if(imageCompare(currentSS,nightmareHalo, 100,350,480,700 ))
      {
    	  // if it is a nightmare proc then we will stop the bot after playing a sound.
    	  stage = Stage.OTHER;
    	  
      }
      return false;
	  
  }
  
  
  
  
  private void done() throws AWTException, InterruptedException{

	  okButton();
      delay();
	  Loc returnButton = new Loc(300,530);
	  click(returnButton);
      delay();
      // Checks if it's an angel halo proc if not repeat. 
      if(mode == Mode.ANGELHALO)
      {
    	  nightmareHalo();
      }
      else{
    	  startingStage();
      }
//	  friendRequest();
      
	  
  }
  
  
  
  
  /* Raiding */
  private void raidFinder() throws AWTException, InterruptedException{
	  Loc raidCodeButton = new Loc(1000,150);
	  clickExact(raidCodeButton);
	  stage = Stage.ENTERCODE;
  }
  
  private void enterCodeViraMate() throws AWTException, InterruptedException{
	  Loc viraMateButton = new Loc(675,55);
	  clickExact(viraMateButton);
	  delay();
	  Loc joinButton = new Loc(550,100);
	  clickExact(joinButton);
	  stage = Stage.SELECTION;
  }
  
  private void repeatQuest() throws AWTException, InterruptedException
  {
	  Loc repeatQuestViramate = new Loc(5,195);
	  clickExact(repeatQuestViramate);
	  clickExact(repeatQuestViramate);

	  stage = Stage.SELECTION;
  }
  //search for target image inside bigImg by comparing pixel rbg value
  //maxDiff is max difference in rgb value between 2 pixels to consider it a miss
  //bound is max number of misses
  //bx and by are the start coordinates inside bigImg
//  private int countMisses(BufferedImage bigImg, int bx, int by, BufferedImage target, int bound, int maxDiff) {
//    int miss = 0;
//    for (int y = 0; y < target.getHeight(); y++) {
//      for (int x = 0; x < target.getWidth(); x++) {
//        if (miss > bound) {
//          return miss;
//        }
//        int val1 = (target.getRGB(x, y)) & 0xff;
//        int val2 = (bigImg.getRGB(bx + x, by + y)) & 0xff;
//        int diff = Math.abs(val1 - val2);
//        if (diff > maxDiff) {
//          miss++;
//        }
//      }
//    }
//    return miss;
//  }


//  //randomize the spot that is clicked when clicking cards
//  private void click(Card card) throws AWTException {
//    click(new Loc(card.getLocation(), new Loc(rand.nextInt(20) + 30, rand.nextInt(20) + 30)));
//  }


  public void click(Loc loc) throws AWTException {
    click(loc.getX(), loc.getY());
  }
  public void clickExact(Loc loc) throws AWTException {
    clickExact(loc.getX(), loc.getY());
  }
  //move mouse instantly and click
  public void clickFast(Loc loc) throws AWTException {
    Robot bot = new Robot();
    bot.mouseMove(loc.getX(), loc.getY());
  }
 // exactly like the click function except the randomness is taken out
  public void clickExact(int x, int y) throws AWTException
  {
	    PointerInfo a = MouseInfo.getPointerInfo();
	    Point b = a.getLocation();
	    int mx = (int) b.getX();
	    int my = (int) b.getY();

	    double t = clickDelay + rand.nextInt(250);
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
  //move mouse to location x,y and click
  //location and speed slightly randomized
  public void click(int x, int y) throws AWTException {
    x += rand.nextInt(20) - 10;
    y += rand.nextInt(10) - 5;

    PointerInfo a = MouseInfo.getPointerInfo();
    Point b = a.getLocation();
    int mx = (int) b.getX();
    int my = (int) b.getY();

    double t = clickDelay + rand.nextInt(250);
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
}