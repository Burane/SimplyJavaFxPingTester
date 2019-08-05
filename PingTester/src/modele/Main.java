package modele;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		Application.launch(args);

	}


	public void start(Stage stage) throws IOException {
		VBox vbox = (VBox) FXMLLoader.load(Main.class.getResource("testco.fxml"));
		stage.setScene(new Scene(vbox));
		stage.setTitle("PingTester");
		stage.show();
		stage.setOnCloseRequest(evt -> {
			System.exit(0);
		});
	}
	
	
	
	


	
	
}