package com.introcode.controller;

import com.introcode.App;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.ResultadoSintactico;
import com.introcode.helpers.Editor;
import com.introcode.helpers.FileUtilities;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrincipalController implements Initializable {

	//Menu Bar
	@FXML
	private MenuItem mnuItAbrirArch;

	@FXML
	private MenuItem mnuItCerrarArch;

	@FXML
	private MenuItem mnuItGuardarArch;

	@FXML
	private MenuItem mnuItGuardarComoArch;

	@FXML
	private MenuItem mnuItAcercaDe;

	// File Path
	@FXML
	private TextField lblFilePath;

	//Layout Izq
	@FXML
	private TextArea txtAreaEditor;

	@FXML
	private Label lblPosCursor;

	// Layout
	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabAnalisisLexico;

	@FXML
	private Tab tabAnSintactico;

	//Tab Analisis Lexico

	@FXML
	private TextArea txtAreaErroresLexico;

	@FXML
	private Button btnAnLexico;

	@FXML
	private Button btnErroresLexicoGuardar;

	@FXML
	private TableView<RegistroLexico> tblAnalisLexico;

	@FXML
	private TableColumn<RegistroLexico, String> tblColLexema;

	@FXML
	private TableColumn<RegistroLexico, String> tblColToken;

	@FXML
	private TableColumn<RegistroLexico, Integer> tblColId;

	@FXML
	private TableColumn<RegistroLexico, Integer> tblColIdToken;

	//Tab An Sintactico

	@FXML
	private Button btnAnSintactico;

	@FXML
	private Button btnErroresSintGuardar;

	@FXML
	private TextArea txtAreaSintactico;

	@FXML
	private TextArea txtAreaErroresSint;

	//Propiedades
	private AnLexico analizadorLexico = new AnLexico();

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarTabla();
		inicializarEditor();
		inicializarTabPane();
	}

	private void inicializarTabPane() {
		tabPane
			.getSelectionModel()
			.selectedItemProperty()
			.addListener((obs, oldTab, newTab) -> {
				if (newTab != null) {
					switch (newTab.getId()) {
						case "tabAnLexico" -> {
							tabAnLexicoOnChange();
							if (App.getWorkingFile() != null) {
								Editor.guardarArchivoEditor(
									txtAreaEditor,
									false,
									false
								);
								btnAnLexicoOnAction();
							}
						}
						case "tabAnSintactico" -> {
							tabAnSintacticoOnChange();
							if (App.getWorkingFile() != null) {
								Editor.guardarArchivoEditor(
									txtAreaEditor,
									false,
									false
								);
								if (!btnAnSintactico.isDisabled()) {
									btnAnSintacticoOnAction();
								}
							}
						}
					}
				}
			});
	}

	private void limpiarAnalizadores() {
		tblAnalisLexico.setItems(null);
		txtAreaErroresLexico.setText(null);
		txtAreaErroresSint.setText(null);
		txtAreaSintactico.setText(null);
		btnAnSintactico.setDisable(true);
		btnErroresLexicoGuardar.setDisable(true);
		btnErroresSintGuardar.setDisable(true);
	}

	private void inicializarEditor() {
		txtAreaEditor.textProperty().addListener((obs, oldValue, newValue) -> {
			limpiarAnalizadores();
			App.changeTitle("INTROCODE (*)");
			Editor.isEdited = true;
			btnAnLexico.setDisable(false);
		});

		txtAreaEditor
			.caretPositionProperty()
			.addListener((obs, oldPos, newPos) -> {
				int caretPos = newPos.intValue();
				String text = txtAreaEditor.getText();

				int line = 1;
				int column = 1;
				for (int i = 0; i < caretPos; i++) {
					if (text.charAt(i) == '\n') {
						line++;
						column = 1;
					} else {
						column++;
					}
				}
				lblPosCursor.setText("Row: " + line + ", Column: " + column);
			});
	}

	private void inicializarTabla() {
		for (TableColumn<
			RegistroLexico,
			?
		> column : tblAnalisLexico.getColumns()) {
			column.setReorderable(false);
		}
		tblColLexema.setCellValueFactory(cellData ->
			new SimpleStringProperty(cellData.getValue().getLexema().toString())
		);
		tblColToken.setCellValueFactory(cellData ->
			new SimpleStringProperty(cellData.getValue().getToken().toString())
		);
		tblColId.setCellValueFactory(cellData ->
			new SimpleIntegerProperty(cellData.getValue().getId()).asObject()
		);

		tblColIdToken.setCellValueFactory(cellData ->
			new SimpleIntegerProperty(cellData.getValue().getId()).asObject()
		);

		tblAnalisLexico.setItems(null);
	}

	private void tabAnLexicoOnChange() {
		if (App.getWorkingFile() == null) {
			btnAnLexico.setDisable(true);
		}
	}

	private void tabAnSintacticoOnChange() {
		if (App.getWorkingFile() == null) {
			btnAnSintactico.setDisable(true);
			return;
		}
		btnAnSintactico.setDisable(false);
	}

	// Menu Bar

	@FXML
	private void mnuItAbrirArchOnAction() {
		File f = Editor.abrirArchivo(txtAreaEditor);
		if (f == null) {
			return;
		}
		lblFilePath.setText(f.getPath());
		App.setWorkingFile(f);
		btnAnLexico.setDisable(false);
		tblAnalisLexico.setItems(null);
		txtAreaErroresLexico.setText(null);
	}

	@FXML
	private void mnuItGuardarArchOnAction() {
		Editor.guardarArchivoEditor(
			txtAreaEditor,
			true,
			App.getWorkingFile() == null
		);
		if (App.getWorkingFile() == null) {
			return;
		}
		lblFilePath.setText(App.getWorkingFile().getPath());
	}

	@FXML
	private void mnuItGuardarComoArchOnAction() {
		Editor.guardarArchivoEditor(txtAreaEditor, true, true);
		if (App.getWorkingFile() == null) {
			return;
		}
		lblFilePath.setText(App.getWorkingFile().getPath());
	}

	@FXML
	private void mnuItCerrarArchOnAction() {
		Alert alert = new Alert(
			AlertType.CONFIRMATION,
			"Guardar cambios?",
			ButtonType.YES,
			ButtonType.NO,
			ButtonType.CANCEL
		);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			mnuItGuardarArchOnAction();
		} else if (alert.getResult() == ButtonType.CANCEL) {
			return;
		}
		lblFilePath.setText(null);
		App.setWorkingFile(null);
		txtAreaEditor.setText(null);
	}

	//Tab An Lexico

	@FXML
	private void btnAnLexicoOnAction() {
		if (Editor.isEdited) {
			mnuItGuardarArchOnAction();
		}
		if (App.getWorkingFile() == null) {
			return;
		}
		txtAreaErroresLexico.setText(null);
		try {
			analizadorLexico.leerArchivo();
		} catch (Exception e) {
			analizadorLexico.alerta();
			tblAnalisLexico.setItems(null);
			btnAnSintactico.setDisable(true);
			return;
		}
		btnErroresLexicoGuardar.setDisable(false);
		txtAreaErroresLexico.setText(analizadorLexico.tokenizar(tblAnalisLexico));
		if (!txtAreaErroresLexico.getText().isBlank()) {
			btnAnSintactico.setDisable(true);
			analizadorLexico.alerta();
		}
	}

	@FXML
	private void btnErroresLexicoGuardarOnAction() {
		FileUtilities.guardarArchivo(txtAreaErroresLexico.getText(), "ErrLexico");
	}

	//Tab an Sintacito

	@FXML
	private void btnAnSintacticoOnAction() {
		if (analizadorLexico == null) {
			txtAreaSintactico.setText(
				"Ejecute primero el análisis léxico antes del análisis sintáctico."
			);
			return;
		}

		if (analizadorLexico.getRegistroLexico().isEmpty()) {
			txtAreaSintactico.setText(
				"No hay tokens disponibles. Ejecute el análisis léxico."
			);
			return;
		}

		AnSintactico an = new AnSintactico();
		ResultadoSintactico resultado = an.analizar(
			analizadorLexico.getRegistroLexico()
		);
		StringBuilder salida = new StringBuilder();
		StringBuilder erroresSalida = new StringBuilder();
		btnErroresSintGuardar.setDisable(false);

		if (resultado.esValido()) {
			salida.append("Analisis sintactico correcto.\n\n");
			// salida.append("Arbol sintactico:\n");
			// salida.append(resultado.getRaiz().toString());
		} else {
			salida.append("Hubo errores sintacticos:");
			for (String error : resultado.getErrores()) {
				erroresSalida.append(error).append("\n");
			}
			if (resultado.getRaiz() != null) {
				salida.append("\nAnalisis sintactico con errores");
				//salida.append(resultado.getRaiz().toString());
			}
		}

		txtAreaSintactico.setText(salida.toString());
		txtAreaErroresSint.setText(erroresSalida.toString());
	}

	@FXML
	private void btnErroresSintGuardarOnAction() {
		FileUtilities.guardarArchivo(txtAreaErroresSint.getText(), "ErrSintact");
	}

	@FXML
	private void mnuAcercaDeOnAction() {
		new Alert(
			Alert.AlertType.INFORMATION,
			"Creado por: " +
				"\nAlan Daniel Farfan Gomez" +
				"\nEduardo Jair Bautista Santiesteban" +
				"\nXimena Itzel Jimenez Hernandez" +
				"\nYael Sampayo Marin",
			ButtonType.CLOSE
		).show();
	}
}
