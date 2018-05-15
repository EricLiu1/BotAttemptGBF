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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {

    Button hardDailies;
    Button events;
    Button raid;
    Button repeat;
    Button repeatEvent;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Granblue Bot GUI");
        hardDailies = new Button();
        hardDailies.setText("Hard Dailies");
        events = new Button("Event");
        raid = new Button();
        raid.setText("Raids");
        repeat = new Button("Repeat");
        repeatEvent = new Button("Repeat Event");
        
        

        //This class will handle the button events
        hardDailies.setOnAction(this);
        events.setOnAction(this);
        raid.setOnAction(this);
        repeat.setOnAction(this);
        repeatEvent.setOnAction(this);
        
        HBox hbox = new HBox(hardDailies,events,raid,repeat,repeatEvent);
//        StackPane layout = new StackPane();
//        StackPane layout2 = new StackPane();
//        layout.getChildren().add(button);
//        layout2.getChildren().add(raid);
        
        Scene scene = new Scene(hbox, 350, 300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //When button is clicked, handle() gets called
    //Button click is an ActionEvent (also MouseEvents, TouchEvents, etc...)
    @Override
    public void handle(ActionEvent event) {
        Properties properties = new Properties();
//        properties.load("iterations");
        PrintWriter writer = null;
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
        new Thread(new Bot(properties)).start();
//        System.out.println("???");
    }

}