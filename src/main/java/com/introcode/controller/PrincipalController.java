package com.introcode.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.introcode.App;
import com.introcode.entity.RegistroLexico;

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
					}
				}

			}
		});
	}

	private void inicializarEditor() {
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
			lblPosCursor.setText("Linea: " + line + ", Columna: " + column);
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

		tblAnalisLexico.setItems(null);
	}

	private void tabAnLexicoOnChange() {
		if (App.getWorkingFile() == null) {
			btnAnLexico.setDisable(true);
		}
	}

	@FXML
	private void mnuAbirArchOnAction() {
		File f = Editor.setTextArea(txtAreaEditor);
		lblFilePath.setText(f.getAbsolutePath());
		App.setWorkingFile(f);
		btnAnLexico.setDisable(false);
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
		Editor.guardarArchivo(txtAreaEditor);
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
		txtAreaErroresLexico.setText(null);
		AnLexico lexico = new AnLexico();
		boolean huboError = lexico.analisisLexico(txtAreaErroresLexico);
		if (huboError) {
			lexico.alertaError();
			tblAnalisLexico.setItems(null);
			return;
		}
		huboError = lexico.tokenizar(tblAnalisLexico, txtAreaErroresLexico);
		if (huboError) {
			lexico.alertaError();
		}
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
