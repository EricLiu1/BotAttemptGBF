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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {



    Button raid;
    Button mimic;
    Button customInput;
    Button exit;
    TextField iterations;
    Thread start;

    static TextArea output = new TextArea(); 
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Granblue Bot GUI");

        raid = new Button("Raids");
        mimic = new Button("Mimic");
        customInput = new Button("Custom Input");
        exit = new Button("Exit");
        raid.setPrefSize(100, 20);
        mimic.setPrefSize(100,20);
        customInput.setPrefSize(100, 20);
        exit.setPrefSize(100, 20);
        
        // Displays the boxes that take in user values
        BorderPane iterationHBox = new BorderPane();
        // can Move HeaderLbl to be above the delay stuff
        Label HeaderLbl = new Label("Insert values in seconds");
        Label iterationsLbl = new Label("Iterations: \t");
        iterations = new TextField();
        iterationHBox.setLeft(iterationsLbl);
        iterationHBox.setCenter(iterations);
        iterationHBox.setTop(HeaderLbl);
        
        
        
        // Output HBox
        BorderPane outputHBox = new BorderPane();
        Label outputLbl = new Label("Bot Output: ");
        outputHBox.setTop(outputLbl);
        outputHBox.setCenter(output);
        

        // This class will handle the button events
        raid.setOnAction(this);
        exit.setOnAction(this);
        mimic.setOnAction(this);
        customInput.setOnAction(  new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
            	FileChooser fileChooser = new FileChooser();
            	File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
						openFile(file);
					
                }
            }
        });
        	
        
        
        HBox raids = new HBox();
        raids.setPadding(new Insets(15, 12, 15, 12));
        raids.setSpacing(10);
        raids.setStyle("-fx-background-color: #336699;");
        raids.getChildren().addAll(raid,mimic,customInput, exit );
        
        BorderPane fields = new BorderPane();
        fields.setTop(iterationHBox);
        
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
        if(event.getSource() == mimic){
        	Mouse mouse = new Mouse();
        	try {
				mouse.mimic();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return;
        }
    
		try {
			writer = new PrintWriter("bot_input.txt", "UTF-8");

	        if (event.getSource() == raid) 
	        	writer.println("mode=3");

	        try{
	            int numberOfIterations = Integer.parseInt(iterations.getText());
	            writer.println("iterations="+numberOfIterations);
	           
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
    private void openFile(File file) {
        CustomInput CI = new CustomInput();  
    	try {
            int numberOfIterations = 0;
            try{
            	numberOfIterations =Integer.parseInt(iterations.getText());
            }catch(NumberFormatException e)
            {
            	numberOfIterations = 1;
            }
            		
            while(numberOfIterations >0)
            {
//            	write("Iteration: "+String.valueOf(numberOfIterations));
            	CI.execute(file.getName());
            	numberOfIterations--;
            	System.out.println("Iterations Left: " + numberOfIterations);
            }
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }

}