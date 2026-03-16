package com.introcode.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.introcode.App;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Editor {
	public static File setTextArea(TextArea txtAr) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Abrir archivo IntroCode");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("IntroCode", "*.txt"));
		fc.setInitialDirectory(new File(App.defaultDir));
		File f = fc.showOpenDialog(App.getStage());
		if (f == null || !f.exists()) {
			alertaArchivo();
			return null;
		}
		String fileName = f.getAbsolutePath();

		String line;
		String content = "";
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader buffer = new BufferedReader(fileReader);
			while ((line = buffer.readLine()) != null) {
				content += line + System.lineSeparator();
			}
			buffer.close();
			txtAr.setText(content);
			return f;
		} catch (Exception e) {
			IO.println(e.getMessage());
		}
		return null;
	}

	public static void guardarArchivo(TextArea txtAr) {
		File archivoNuevo = null;
		if (App.getWorkingFile() == null) {
			FileChooser fc = new FileChooser();
			fc.setTitle("Guardar archivo IntroCode");
			fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("IntroCode", "*.txt"));
			fc.setInitialDirectory(new File(App.defaultDir));
			File f = fc.showSaveDialog(App.getStage());
			if (f == null) {
				alertaArchivo();
				return;
			}
			archivoNuevo = f;
		}
		String fileName = (archivoNuevo == null) ? App.getWorkingFile().getAbsolutePath()
				: archivoNuevo.getAbsolutePath();

		String texto = (txtAr.getText() == null) ? "" : txtAr.getText();
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(texto);
			new Alert(AlertType.INFORMATION, "Guardado Exitosamente").show();
		} catch (IOException ex) {
			new Alert(AlertType.ERROR, ex.getMessage()).show();
		}
	}

	private static void alertaArchivo() {
		new Alert(Alert.AlertType.WARNING,
				"No se ha podido cargar el archivo",
				ButtonType.CLOSE).show();
	}
}
