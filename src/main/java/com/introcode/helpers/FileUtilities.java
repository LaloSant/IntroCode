package com.introcode.helpers;

import com.introcode.App;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FileUtilities {

	public static final String defaultOutDir =
		System.getProperty("user.dir") +
		" src main resources com introcode codigo salida ".replaceAll(" ", App.getSep());

	public static void guardarArchivo(String txt, String titulo) {
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("_dd-MM-yyyy_HH-mm-ss")); // Current date & time
		File file = new File(defaultOutDir, titulo + timeStamp + ".lua");

		String texto = txt == null ? "" : txt;
		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write(texto);
			new Alert(AlertType.INFORMATION, "Guardado Exitosamente").show();;
			writer.close();
		} catch (IOException ex) {
			new Alert(AlertType.ERROR, ex.getMessage()).show();
		}
	}
}
