package com.introcode.helpers;

import com.introcode.App;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FileUtilities {

	public static final Path defaultOutDir = App.defaultDir.resolve("salida");

	public static void guardarArchivo(String txt, String titulo, FileType ft) {
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("_dd-MM-yyyy_HH-mm-ss")); // Current date & time
		File file = defaultOutDir.resolve(titulo + timeStamp + ft.toString()).toFile();

		String texto = txt == null ? "" : txt;
		try {
			Files.createDirectories(defaultOutDir);
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(texto);
			}
			new Alert(AlertType.INFORMATION, "Guardado Exitosamente").show();
		} catch (IOException ex) {
			new Alert(AlertType.ERROR, ex.getMessage()).show();
		}
	}

	public enum FileType {
		TXT(".txt"),
		LUA(".lua"),
		CSV(".csv");

		private final String extension;
	
		FileType(String extension){
			this.extension = extension;
		}

		@Override
		public String toString(){
			return this.extension;
		}
	}


}
