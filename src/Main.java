//import java.awt.Label;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {


    Button hardDailies;
    Button events;
    Button raid;
    Button repeat;
    Button repeatEvent;
    Button exit;
    TextField iterations;
    TextField delayTransitions;
    TextField delayClicks;
    Thread start;
    static TextArea output = new TextArea(); 
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Granblue Bot GUI");
        hardDailies = new Button("Hard Dailies");
        events = new Button("Event");
        raid = new Button("Raids");
        repeat = new Button("Repeat");
        repeatEvent = new Button("Repeat Event");
        exit = new Button("Exit");
        hardDailies.setPrefSize(100, 20);
        events.setPrefSize(100, 20);
        raid.setPrefSize(100, 20);
        repeat.setPrefSize(100, 20);
        repeatEvent.setPrefSize(100, 20);
        
        // Displays the boxes that take in user values
        BorderPane iterationHBox = new BorderPane();
        // can Move HeaderLbl to be above the delay stuff
        Label HeaderLbl = new Label("Insert values in seconds");
        Label iterationsLbl = new Label("Iterations: \t");
        iterations = new TextField();
        iterationHBox.setLeft(iterationsLbl);
        iterationHBox.setCenter(iterations);
        iterationHBox.setTop(HeaderLbl);
        
        BorderPane delayTransitionsHBox = new BorderPane();
        Label delayTransitionLbl = new Label("Delay between Transitions: \t");
        delayTransitions = new TextField();
        delayTransitionsHBox.setLeft(delayTransitionLbl);
        delayTransitionsHBox.setCenter(delayTransitions);
        
        BorderPane delayClicksHBox = new BorderPane();
        Label delayClicksLbl = new Label("Delay between Clicks: \t");
        delayClicks = new TextField();
        delayClicksHBox.setLeft(delayClicksLbl);
        delayClicksHBox.setCenter(delayClicks);
        
        // Output HBox
        BorderPane outputHBox = new BorderPane();
        Label outputLbl = new Label("Bot Output: ");
        outputHBox.setTop(outputLbl);
        outputHBox.setCenter(output);
        

        // This class will handle the button events
        hardDailies.setOnAction(this);
        events.setOnAction(this);
        raid.setOnAction(this);
        repeat.setOnAction(this);
        repeatEvent.setOnAction(this);
        exit.setOnAction(this);
        
        HBox raids = new HBox();
        raids.setPadding(new Insets(15, 12, 15, 12));
        raids.setSpacing(10);
        raids.setStyle("-fx-background-color: #336699;");
        raids.getChildren().addAll(hardDailies,events,raid,repeat,repeatEvent,exit);
        
        BorderPane fields = new BorderPane();
        fields.setTop(iterationHBox);
        fields.setCenter(delayTransitionsHBox);
        fields.setBottom(delayClicksHBox);
        
        // Main BorderPane
        BorderPane root = new BorderPane();
        root.setTop(outputHBox);
        root.setCenter(fields);
        root.setBottom(raids);
        
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //When button is clicked, handle() gets called
    //Button click is an ActionEvent (also MouseEvents, TouchEvents, etc...)

	@Override
    public void handle(ActionEvent event) {
        Properties properties = new Properties();
        PrintWriter writer = null;
        if(event.getSource() == exit)
        {
        	System.exit(0);
        	return;
        }
		try {
			writer = new PrintWriter("bot_input.txt", "UTF-8");
	        if (event.getSource() == hardDailies) 
	            writer.println("mode=1");
	        if (event.getSource() == events)
	        	writer.println("mode=2");
	        if (event.getSource() == raid) 
	        	writer.println("mode=3");
	        if (event.getSource() == repeat) 
	        	writer.println("mode=4");
	        if (event.getSource() == repeatEvent) 
	        	writer.println("mode=5");
	        try{
	            int numberOfIterations = Integer.parseInt(iterations.getText());
	            writer.println("iterations="+numberOfIterations);
	            double delay = Double.parseDouble(delayClicks.getText())*1000;
	            writer.println("clickDelay="+delay);
	            double transitionDelay = Double.parseDouble(delayTransitions.getText())*1000;
	            writer.println("delayNormal="+transitionDelay);
	           
	        }catch(NumberFormatException e){
	        	write("Using Default Values because input was either not a number or blank");
//	            System.out.println("Error: " + iterations + " is not a number");
	           
	        }
	        
	        
		} catch (FileNotFoundException e) {
			System.out.println("FILE NOT FOUND ERROR");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("UNSUPPORTED ENCODING EXCEPTION");

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        writer.close();
        InputStream input = null;
		try {
			input = new FileInputStream(new File("bot_input.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("FILE NOT FOUND EXCEPTION");

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			properties.load(input);
		} catch (IOException e) {
			System.out.println("IOEXCEPTION");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        start = new Thread(new Bot(properties));
        start.start();
    }
    
    public static void write(String msg)
    {
    	  output.appendText(msg + "\n");
    }
    

}