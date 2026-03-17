package com.introcode;

import java.io.File;
import java.io.IOException;

//import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 * @author Alan Daniel Farfan Gomez
 * @author Eduardo Jair Bautista Santiesteban
 * @author Ximena Itzel Jimenez Hernandez
 * @author Yael Sampayo Marin
 */
public class App extends Application {

	private static Scene scene;
	private static Stage stage;
	private static File workingFile;
	private static final String sep = File.separator;

	//System.getProperty("user.dir")
	//public static final String defaultDir = "C:\\Users\\eduar\\Documents\\Programacion\\Java\\Proyectos\\IntroCode\\src\\main\\resources\\com\\introcode\\codigo";
	public static final String defaultDir = System.getProperty("user.dir") + " src main resources com introcode codigo".replaceAll(" ", sep);

	public static Stage getStage() {
		return stage;
	}

	public static File getWorkingFile() {
		return workingFile;
	}

	public static void setWorkingFile(File f) {
		workingFile = f;
	}

	public static void main(String[] args) {
		launch();
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	@Override
	public void start(Stage stage) throws IOException {
		//App.setUserAgentStylesheet(STYLESHEET_CASPIAN);
		//App.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		App.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
		scene = new Scene(loadFXML("vistas/principal"), 1000, 700);
		stage.setTitle("INTROCODE");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("img/Mio.png")));
		stage.show();
	}

}