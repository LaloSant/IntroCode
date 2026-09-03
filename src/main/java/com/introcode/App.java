package com.introcode;

import java.io.File;
import java.io.IOException;

//import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * JavaFX App
 * @author Alan Daniel Farfan Gomez
 * @author Eduardo Jair Bautista Santiesteban
 * @author Ximena Itzel Jimenez Hernandez
 * @author Yael Sampayo Marin
 */
public class App extends Application {

	@Getter
	@Setter
	private static Scene scene;

	@Getter
	@Setter
	private static Stage stage;

	@Getter
	@Setter
	private static File workingFile;

	@Getter
	private static final String sep = File.separator;

	public static final String defaultDir =
		System.getProperty("user.dir") +
		" src main resources com introcode codigo".replace(" ", sep);

	public static void main(String[] args) {
		launch();
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(
			App.class.getResource(fxml + ".fxml")
		);
		return fxmlLoader.load();
	}

	@Override
	public void start(Stage stage) throws IOException {
		//App.setUserAgentStylesheet(STYLESHEET_CASPIAN);
		//App.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		App.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
		App.setStage(stage);
		scene = new Scene(loadFXML("vistas/principal"));
		stage.setTitle("INTROCODE");
		stage.setScene(scene);
		//stage.getIcons().add(new Image(getClass().getResourceAsStream("img/Mio.png")));
		stage.show();
	}

	public static void changeTitle(String title) {
		stage.setTitle(title);
	}
}
