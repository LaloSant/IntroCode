package com.introcode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

//import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
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

	private static final List<String> exampleFiles = List.of(
			"basico1.lua",
			"bloques.lua",
			"ejemploTokens.lua",
			"introlua.lua",
			"introluav2.lua",
			"optimizacion.lua",
			"optimizacion2.lua",
			"prefijas.lua");

	public static final Path defaultDir = Path.of(System.getProperty("user.home"), "IntroLua", "codigo");

	public static void main(String[] args) {
		launch();
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	private static void prepareCodeDirectory() throws IOException {
		Files.createDirectories(defaultDir);
		for (String fileName : exampleFiles) {
			Path destination = defaultDir.resolve(fileName);
			if (Files.exists(destination)) {
				continue;
			}
			try (InputStream example = App.class.getResourceAsStream("/com/introcode/codigo/" + fileName)) {
				if (example == null) {
					throw new IOException("No se encontró el ejemplo incluido: " + fileName);
				}
				try {
					Files.copy(example, destination);
				} catch (FileAlreadyExistsException ignored) {
					// Another app instance may have copied the same example.
				}
			}
		}
	}

	@Override
	public void start(Stage stage) throws IOException {
		prepareCodeDirectory();
		stage.getIcons().add(new Image(Objects.requireNonNull(
				getClass().getResourceAsStream("/com/introcode/introlua.png"))));
		//App.setUserAgentStylesheet(STYLESHEET_CASPIAN);
		//App.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
		Font.loadFont(App.class.getResourceAsStream("fonts/adwaita-mono.ttf"), 14);

		App.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
		App.setStage(stage);
		scene = new Scene(loadFXML("vistas/principal"));
		stage.setTitle("IntroLua");
		stage.setScene(scene);
		stage.show();
	}

	public static void changeTitle(String title) {
		stage.setTitle(title);
	}
}
