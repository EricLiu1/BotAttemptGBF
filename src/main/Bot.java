package main;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Bot implements Runnable {

  //settings
  private int iterations;
  private int safeRound;
  private Stage stage; 
  private int HLBound;
  private int clickDelay;
  private int delayNormal;
  private int delayLag;
  private int delayHL;
  private int sound;
  private boolean medium;
  private Mode mode;

  public Bot(Properties properties) {

    System.out.println("############READING SETTINGS");
    //runtime
    iterations = checkProperty(properties, "iterations", 4);
//    if (runtime < 0) {
//      runtime = 60;
//    }
    iterations = 18;
    System.out.println("iterations=" + iterations);
    
    int m = checkProperty(properties,"mode",1);
    m = 5;
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
    if (m==5)
    {
    	mode = Mode.REPEATEVENT;
    }
    
    int med = checkProperty(properties, "medium", 0);
    medium = !(med == 0);
    delayLag=2;
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
    System.out.println("clickDelay=" + clickDelay);

    safeRound = checkProperty(properties, "safeRound", 7);
    System.out.println("safeRound=" + safeRound);

    HLBound = checkProperty(properties, "HLBound", 1);
    System.out.println("HLBound=" + HLBound);

    String[] soundString = new String[]{"Lyria singing", "Ifrit screaming", "Sagitarius warning"};
    sound = checkProperty(properties, "sound", 1);
    if (sound < 1 || sound > 3) {
      sound = 1;
    }
    System.out.println("Warning sound =" + (soundString[sound - 1]));
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

  private Loc start;



  private Random rand = new Random();

  //STAGE
  private int actionDelay;




  public void testSuits() throws AWTException, IOException, InterruptedException {
    medium = true;

    init();
    System.out.println(start);
    // clickFast(start);
    //identifyStage1();
    //  System.out.println(newStage);
    //initCards();
    while (true) {
      System.out.println("PRES ENTER");
      Scanner scanner = new Scanner(System.in);
      scanner.nextLine();
      currentSS = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      //identifyStage1();
      //System.out.println(newStage);
    }

  }

  public void run() {
    try {
      init();
      if (start != null) {
        //found game board
        System.out.println("############GAMEBOARD FOUND");
        //init card images

        //false during higher-lower, true otherwise
//
//        long current = System.currentTimeMillis();
//        long end = current + runtime * 60 * 1000;

        //random delay in ms between actions
        int delay;
        actionDelay = delayNormal;

        System.out.println("############STARTING BOT");

        //main loop
        int i = 0;
//        boolean confirm = true; 
        while (iterations > 0) {
        	// testing purposes
//        	if(i==0)
//        	{
//        		stage = Stage.BATTLE;
//        		i++;
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
          				  summonSelection();
          				  delay();
          				  break;
	      case AID:       actionDelay = delayNormal;
				          Thread.sleep(actionDelay + 7500);
				     	  requestAid(false);
				     	  delay();
				     	  break;
          case BATTLE:    delay();
        	  			  auto();
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
          }

          //actionDelay+random delay in ms

//          current = System.currentTimeMillis();
        }

      } else {
        System.out.println("ERROR: Could not find gameboard, make sure size is set to full and the whole gameboard is visible");
      }
    } catch (Exception e) {
      System.err.println(e);
    }

  }


private void delay() throws InterruptedException{
    System.out.println("iterations: "+iterations  +  "\nStage: " + stage);

    int delay = rand.nextInt(300);
    Thread.sleep(actionDelay + delay);
}


  private void init() throws AWTException, IOException, InterruptedException {
	// alter this
    System.out.println("############LOADING ASSETS");
    //gameboard not used currently 
    BufferedImage gameboard = null;
    String sizeString = (medium) ? "medium" : "large";
    //load base picture and icons
    try {
      //load card icons
     

     // need to make a home icon, a repeat quest one, a potion one
    	// gonna actually try verfication later
//      BufferedImage questIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/quest/showdown.png"));
      //stage icons
//      BufferedImage dealIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/dealIcon.png"));
//      BufferedImage selectIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/selectIcon.png"));
//      BufferedImage winIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/winIcon.png"));
//      BufferedImage loseIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/loseIcon.png"));
//      BufferedImage selectHLIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/selectHLIcon.png"));
//      BufferedImage winHLIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/winHLIcon.png"));
//      BufferedImage loseHLIcon = ImageIO.read(getClass().getClassLoader().getResource("img/" + sizeString + "/stage/loseHLIcon.png"));

//      stageIcons1 = new BufferedImage[]{selectIcon, winIcon, loseIcon, dealIcon};
//      stageIcons2 = new BufferedImage[]{selectHLIcon, winHLIcon, loseHLIcon};
      System.out.println("############FINISHED LOADING");

      //gameboard hook
      gameboard = ImageIO.read(getClass().getClassLoader().getResource("img/" + "large" + "/questing.png"));


    } catch (IOException e) {
      System.out.println("############ERROR WHILE LOADING ASSETS");
    }

    System.out.println();
    System.out.println("The bot is set to run for " + iterations +" iterations");
    System.out.println("Currently using "+sizeString+" settings");
    System.out.println("This can be changed in the settings.txt file");
    Scanner scanner = new Scanner(System.in);

    while (start == null) {
      Thread.sleep(1000);
      System.out.println();
      System.out.println("-Make sure the Quest is fully visible and the game resolution is set to \"Lite\"");
      System.out.println("-Position your browser as close as you can to the top left corner of your screen");
      System.out.println("-If you are using viramate, disable the improved fonts");
      System.out.println();
      System.out.println("Press ENTER to start searching for the gameboard (may take 1 or 2 mins)");
      scanner.nextLine();
      System.out.println("############SEARCHING GAMEBOARD");
      //take ss
      currentSS = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      //find base pic in screenshot
//      start = findMatches(currentSS, gameboard, 500, 2500);
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

    //init buttons
//    if (medium) {
//    	// worry about medium later
//      centerButton = new Loc(start, new Loc(226, 111));
//      leftButton = new Loc(start, new Loc(153, 109));
//      rightButton = new Loc(start, new Loc(300, 109));
//    } else {
//      centerButton = new Loc(start, new Loc(324, 726));
//      leftButton = new Loc(start, new Loc(240, 726));
//      rightButton = new Loc(start, new Loc(400, 726));
//    }
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
	    if(mode == Mode.REPEAT || mode == Mode.REPEATEVENT)
	    {
	    	stage = Stage.REPEAT;
	    }
  }
  
  //calls countMisses for each possible position inside bigImage
  //return null if not found. returns upperleft pixel location if found
  private Loc findMatches(BufferedImage bigImage, BufferedImage target, int maxMiss, int maxDiff) {
    for (int y = 0; y < bigImage.getHeight() - target.getHeight(); y++) {
      for (int x = 0; x < bigImage.getWidth() - target.getWidth(); x++) {
        int miss = countMisses(bigImage, x, y, target, maxMiss, maxDiff);
        if (miss < maxMiss) {
        	System.out.println(miss);
          return new Loc(x, y);
        }
      }
    }
    return null;
  }
  
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
  
  private void questAcceptance() throws AWTException{
	  Loc showdownButton = new Loc(400,840);
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
  private void summonSelection() throws AWTException, InterruptedException{
	  Loc summonSelectButton = new Loc(500,370);
	  click(summonSelectButton);
      delay();
	  Loc clickingSummon = new Loc(500,470);
	  click(clickingSummon);
      delay();
	  Loc clickingConfirm = new Loc(450,730);
	  click(clickingConfirm);
	  if(mode == Mode.REPEATEVENT)
	  {
		  stage = Stage.BATTLE;
	  }
	  else
	  {
		  stage = Stage.AID;
	  }
	  
	  
  }
  private void okButton() throws AWTException {
	  Loc okButton = new Loc(300,550);
	  click(okButton);
  }
  private void requestAid(boolean aid) throws AWTException, InterruptedException{
	  Loc showdownButton = null;
	  
	  if(mode == Mode.REPEAT )
	  {
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
  
  private void auto() throws AWTException, InterruptedException{
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
		  Thread.sleep(actionDelay + 200000 ); 
	  }
	  else{
//		  Thread.sleep(actionDelay + 50000 );
		  Thread.sleep(actionDelay + 250000);
	  }
	  stage = Stage.DONE;
  }
  //quest cancel 2x as friend request
//  private void friendRequest() throws AWTException{
//	  Loc cancelButton = new Loc(150,600);
//	  click(cancelButton);
//  }
  // add friend request cancel feature later
  private void done() throws AWTException, InterruptedException{

	  okButton();
      delay();
	  Loc returnButton = new Loc(300,530);
	  click(returnButton);
      delay();
//	  friendRequest();
      startingStage();
	  
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
  private int countMisses(BufferedImage bigImg, int bx, int by, BufferedImage target, int bound, int maxDiff) {
    int miss = 0;
    for (int y = 0; y < target.getHeight(); y++) {
      for (int x = 0; x < target.getWidth(); x++) {
        if (miss > bound) {
          return miss;
        }
        int val1 = (target.getRGB(x, y)) & 0xff;
        int val2 = (bigImg.getRGB(bx + x, by + y)) & 0xff;
        int diff = Math.abs(val1 - val2);
        if (diff > maxDiff) {
          miss++;
        }
      }
    }
    return miss;
  }


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