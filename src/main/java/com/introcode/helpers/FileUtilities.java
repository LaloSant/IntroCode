package com.introcode.helpers;

import com.introcode.App;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public class FileUtilities {

	public static void guardarArchivo(String txt) {
		// TODO: Guardar con nombre automatico Timestamp
		// LocalDateTime now = LocalDateTime.now(); // Current date & time
		// System.out.println("Before formatting: " + now);
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		// String formatted = now.format(formatter);
		FileChooser fc = new FileChooser();
		fc.setTitle("Guardar archivo de salida");
		fc.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Archivo de texto", "*.txt"));
		fc.setInitialDirectory(new File(App.defaultDir));
		File file = fc.showSaveDialog(App.getStage());
		if (file == null) {
			alertaArchivo();
			return;
		}

		String texto = txt == null ? "" : txt;
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(texto);
			new Alert(AlertType.INFORMATION, "Guardado Exitosamente");
		} catch (IOException ex) {
			new Alert(AlertType.ERROR, ex.getMessage()).show();
		}
	}

	private static void alertaArchivo() {
		new Alert(
				Alert.AlertType.WARNING,
				"No se ha podido cargar el archivo",
				ButtonType.CLOSE).show();
	}
}
