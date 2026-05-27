package com.introcode.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.introcode.App;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.ResultadoSintactico;

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

	@FXML
	private MenuItem mnuAbrirArch;

	@FXML
	private MenuItem mnuCerrarArch;

	@FXML
	private MenuItem mnuAcercaDe;

	@FXML
	private MenuItem mnuBorrarArch;

	@FXML
	private TextField lblFilePath;

	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabEditor;

	@FXML
	private Tab tabAnLexico;

	@FXML
	private Tab tabAnalisisLexico;

	@FXML
	private Tab tabAnSintactico;

	//Tab Editor
	@FXML
	private TextArea txtAreaEditor;

	@FXML
	private Label lblPosCursor;

	@FXML
	private Button btnEditorEscArch;

	@FXML
	private Button btnEditorGuardArch;

	@FXML
	private Button btnEditorCerrar;

	//Tab Analisis Lexico

	@FXML
	private TextArea txtAreaErroresLexico;

	@FXML
	private Button btnAnLexico;

	@FXML
	private TableView<RegistroLexico> tblAnalisLexico;

	@FXML
	private TableColumn<RegistroLexico, String> tblColLexema;

	@FXML
	private TableColumn<RegistroLexico, String> tblColToken;

	@FXML
	private TableColumn<RegistroLexico, Integer> tblColId;

	@FXML
	private TableColumn<RegistroLexico, Integer> tblColRow;

	@FXML
	private TableColumn<RegistroLexico, Integer> tblColColumn;

	@FXML
	private TableColumn<RegistroLexico, Integer> tblColConsecutivo;

	//Tab An Sintactico

	@FXML
	private Button btnAnSintactico;

	@FXML
	private TextArea txtAreaSintactico;

	private AnLexico analizadorLexico;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarTabla();
		inicializarEditor();
		inicializarTabPane();

	}

	private void inicializarTabPane() {
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
			if (newTab != null) {
				switch (newTab.getId()) {
					case "tabAnLexico" -> {
						tabAnLexicoOnChange();
						if (App.getWorkingFile() != null) {
							Editor.guardarArchivo(txtAreaEditor, false);
							btnAnLexicoOnAction();
						}
					}
					case "tabAnSintactico" -> {
						tabAnSintacticoOnChange();
						if (App.getWorkingFile() != null) {
							Editor.guardarArchivo(txtAreaEditor, false);
							if (!btnAnLexico.isDisabled()) {
								btnAnLexicoOnAction();
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
		txtAreaSintactico.setText(null);
		btnAnSintactico.setDisable(true);
	}

	private void inicializarEditor() {
		txtAreaEditor.textProperty().addListener((obs, oldValue, newValue) -> {
			limpiarAnalizadores();
			App.changeTitle("INTROCODE*");
		});

		txtAreaEditor.caretPositionProperty().addListener((obs, oldPos, newPos) -> {
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
		for (TableColumn<RegistroLexico, ?> column : tblAnalisLexico.getColumns()) {
			column.setReorderable(false);
		}
		tblColLexema
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLexema().toString()));
		tblColToken
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToken().toString()));
		tblColId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
		tblColRow.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRow()).asObject());
		tblColColumn
				.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getColumn()).asObject());
		tblColConsecutivo
				.setCellValueFactory(
						cellData -> new SimpleIntegerProperty(cellData.getValue().getConsecutivoID()).asObject());

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
		}
	}

	@FXML
	private void mnuAbirArchOnAction() {
		File f = Editor.setTextArea(txtAreaEditor);
		lblFilePath.setText(f.getAbsolutePath());
		App.setWorkingFile(f);
		btnAnLexico.setDisable(false);
		tblAnalisLexico.setItems(null);
		txtAreaErroresLexico.setText(null);
	}

	//Tab Editor
	@FXML
	private void btnEditorEscArchOnAction() {
		mnuAbirArchOnAction();
	}

	@FXML
	private void btnEditorGuardArchOnAction() {
		if (App.getWorkingFile() == null) {
			return;
		}
		Editor.guardarArchivo(txtAreaEditor, true);
	}

	@FXML
	private void btnEditorCerrarOnAction() {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Guardar cambios?", ButtonType.YES, ButtonType.NO,
				ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			btnEditorGuardArchOnAction();
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
		//txtAreaSintactico.setText(null);
		btnAnSintactico.setDisable(false);
		RegistroLexico.consecutivo = 0;
		txtAreaErroresLexico.setText(null);
		analizadorLexico = new AnLexico();
		boolean huboError = analizadorLexico.analisisLexico(txtAreaErroresLexico);
		if (huboError) {
			analizadorLexico.alertaError();
			tblAnalisLexico.setItems(null);
			btnAnSintactico.setDisable(true);
			return;
		}
		huboError = analizadorLexico.tokenizar(tblAnalisLexico, txtAreaErroresLexico);
		if (huboError) {
			btnAnSintactico.setDisable(true);
			analizadorLexico.alertaError();
		}
	}

	@FXML
	private void btnAnSintacticoOnAction() {
		if (analizadorLexico == null) {
			txtAreaSintactico.setText("Ejecute primero el análisis léxico antes del análisis sintáctico.");
			return;
		}

		if (analizadorLexico.getRegistroLexico().isEmpty()) {
			txtAreaSintactico.setText("No hay tokens disponibles. Ejecute el análisis léxico.");
			return;
		}

		AnSintactico an = new AnSintactico();
		ResultadoSintactico resultado = an.analizar(analizadorLexico.getRegistroLexico());
		StringBuilder salida = new StringBuilder();

		if (resultado.esValido()) {
			salida.append("Analisis sintactico correcto.\n\n");
			salida.append("Arbol sintactico:\n");
			salida.append(resultado.getRaiz().toString());
		} else {
			salida.append("Errores sintacticos:\n");
			for (String error : resultado.getErrores()) {
				salida.append(error).append("\n");
			}
			if (resultado.getRaiz() != null) {
				salida.append("\nArbol sintactico parcial:\n");
				salida.append(resultado.getRaiz().toString());
			}
		}

		txtAreaSintactico.setText(salida.toString());
	}

	@FXML
	private void mnuAcercaDeOnAction() {
		new Alert(Alert.AlertType.INFORMATION,
				"Creado por: "
						+ "\nAlan Daniel Farfan Gomez"
						+ "\nEduardo Jair Bautista Santiesteban"
						+ "\nXimena Itzel Jimenez Hernandez"
						+ "\nYael Sampayo Marin",
				ButtonType.CLOSE).show();
	}

}
