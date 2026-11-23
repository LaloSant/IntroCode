package com.introcode.controller;

import com.introcode.App;
import com.introcode.entity.AppStatus;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.ResultadoSintactico;
import com.introcode.entity.TokenLexico;
import com.introcode.helpers.Editor;
import com.introcode.helpers.FileUtilities;
import com.introcode.helpers.FileUtilities.FileType;

import java.io.File;
import java.net.URL;
import java.util.Map;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrincipalController implements Initializable {

	// Menu Bar
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

	// Layout Izq
	@FXML
	private TextArea txtAreaNumerosLinea;

	@FXML
	private TextArea txtAreaEditor;

	@FXML
	private Label lblPosCursor;

	@FXML
	private Label lblCaracteresEditor;

	// Layout Der
	@FXML
	private TabPane tabPane;

	@FXML
	private Tab tabAnalisisLexico;

	@FXML
	private Tab tabAnSintactico;

	@FXML
	private Tab tabAnSemantico;

	@FXML
	private Tab tabPrePosFija;

	@FXML
	private Tab tabOptimizar;

	// Tab Analisis Lexico

	@FXML
	private TextArea txtAreaErroresLexico;

	@FXML
	private Label lblAnLexicoTiempo;

	@FXML
	private Button btnAnLexico;

	@FXML
	private Button btnGuardarAnLexico;

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

	// Tab An Sintactico

	@FXML
	private Button btnAnSintactico;

	@FXML
	private Button btnErroresSintGuardar;

	@FXML
	private TextArea txtAreaSintactico;

	// Tab An Sintactico

	@FXML
	private Button btnAnSemantico;

	@FXML
	private Button btnErroresSemanGuardar;

	@FXML
	private TextArea txtAreaSemantico;

	// Tab Pre Pos Fija

	@FXML
	private Button btnPrePosFija;

	@FXML
	private Button btnPrePosFijaGuardar;

	@FXML
	private TextArea txtAreaPrePosFija;

	// Tab optmizar

	@FXML
	private Button btnOptimizar;

	@FXML
	private Button btnOptimizarGuardar;

	@FXML
	private TextArea txtAreaOptimizar;

	@FXML
	private Label lblOptimizarDetalles;

	// Propiedades

	private AppStatus LOCAL_APPSTATUS = AppStatus.PREPOSFIJA;

	private AnLexico analizadorLexico = new AnLexico();

	private ResultadoSintactico resultadoSintactico;

	private AnSemantico analizadorSemantico = new AnSemantico();

	private PrePosFija convertidorPrePosFija = new PrePosFija();

	private Optimizador optimizador = new Optimizador();

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
											false);
									btnAnLexicoOnAction();
								}
							}
							case "tabAnSintactico" -> {
								tabAnSintacticoOnChange();
								if (App.getWorkingFile() != null) {
									Editor.guardarArchivoEditor(
											txtAreaEditor,
											false,
											false);
									if (!btnAnSintactico.isDisabled()) {
										btnAnSintacticoOnAction();
									}
								}
							}
							case "tabAnSemantico" -> {
								tabAnSemanticoOnChange();
								if (App.getWorkingFile() != null) {
									Editor.guardarArchivoEditor(
											txtAreaEditor,
											false,
											false);
									if (!btnAnSemantico.isDisabled()) {
										btnAnSemanticoOnAction();
									}
								}
							}
							case "tabPrePosFija" -> {
								tabPrePosFijaOnChange();
								if (App.getWorkingFile() != null) {
									Editor.guardarArchivoEditor(
											txtAreaEditor,
											false,
											false);
									if (!btnPrePosFija.isDisabled()) {
										btnPrePosFijaOnAction();
									}
								}
							}
							case "tabOptimizar" -> {
								tabOptimizarOnChange();
								if (App.getWorkingFile() != null) {
									Editor.guardarArchivoEditor(
											txtAreaEditor,
											false,
											false);
									if (!btnOptimizar.isDisabled()) {
										btnOptimizarOnAction();
									}
								}
							}
						}
					}
				});
	}

	private void inicializarEditor() {
		txtAreaNumerosLinea.setText("1");
		txtAreaEditor.scrollTopProperty().addListener(
				(obs, oldValue, newValue) -> txtAreaNumerosLinea.setScrollTop(newValue.doubleValue()));

		txtAreaEditor.textProperty().addListener((obs, oldValue, newValue) -> {
			actualizarNumerosLinea(newValue);
			lblCaracteresEditor.setText(String.format("Caracteres: %s", newValue.length()));
			limpiarAnalizadores();
			App.changeTitle("IntroLua (*)");
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
					lblPosCursor.setText("Linea: " + line + ", Columna: " + column);
				});
	}

	private void actualizarNumerosLinea(String texto) {
		if (texto == null) {
			texto = "";
		}
		int totalLineas = 1;
		for (int i = 0; i < texto.length(); i++) {
			if (texto.charAt(i) == '\n') {
				totalLineas++;
			}
		}

		StringBuilder numeros = new StringBuilder();
		for (int linea = 1; linea <= totalLineas; linea++) {
			if (linea > 1) {
				numeros.append('\n');
			}
			numeros.append(linea);
		}
		txtAreaNumerosLinea.setText(numeros.toString());
	}

	private void inicializarTabla() {
		tblAnalisLexico.setRowFactory(tblAnalisLexico -> new TableRow<RegistroLexico>() {
			@Override
			protected void updateItem(RegistroLexico item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setStyle("");
				} else {
					String style = item.getToken() == TokenLexico.ERROR_LEXICO
							? "-fx-background-color: #FF6666;"
							: "";
					setStyle(style);
				}
			}
		});

		for (TableColumn<RegistroLexico, ?> column : tblAnalisLexico.getColumns()) {
			column.setReorderable(false);
		}
		tblColLexema
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLexema().toString()));

		tblColToken
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToken().toString()));
		tblColId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

		tblColIdToken
				.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

		tblAnalisLexico.setItems(null);
	}

	// UTILES

	private void limpiarAnalizadores() {
		LOCAL_APPSTATUS = AppStatus.NONE;
		lblPosCursor.setText("Linea: %s, Columna: %s");
		lblAnLexicoTiempo.setText("Tiempo de ejecucion: %s");
		lblOptimizarDetalles.setText("Lineas: %s Caracteres: %s");
		tblAnalisLexico.setItems(null);
		txtAreaErroresLexico.setText(null);
		txtAreaSintactico.setText(null);
		txtAreaSemantico.setText(null);
		txtAreaPrePosFija.setText(null);
		txtAreaOptimizar.setText(null);
		btnAnSintactico.setDisable(true);
		btnAnSemantico.setDisable(true);
		btnPrePosFija.setDisable(true);
		btnOptimizar.setDisable(true);
		btnPrePosFijaGuardar.setDisable(true);
		btnErroresLexicoGuardar.setDisable(true);
		btnErroresSintGuardar.setDisable(true);
		btnErroresSemanGuardar.setDisable(true);
		btnOptimizarGuardar.setDisable(true);
	}

	// TABS ON CHANGE

	private void tabAnLexicoOnChange() {
		// if (App.getWorkingFile() == null) {
		// btnAnLexico.setDisable(true);
		// }
		if (LOCAL_APPSTATUS.compareTo(AppStatus.FILE_READ) < 0) {
			btnAnLexico.setDisable(true);
		}
	}

	private void tabAnSintacticoOnChange() {
		if (LOCAL_APPSTATUS.compareTo(AppStatus.ANLEXICO) < 0) {
			btnAnSintactico.setDisable(true);
			return;
		}
		btnAnSintactico.setDisable(false);
	}

	private void tabAnSemanticoOnChange() {
		if (LOCAL_APPSTATUS.compareTo(AppStatus.ANSINTACTICO) < 0) {
			btnAnSemantico.setDisable(true);
			return;
		}
		btnAnSemantico.setDisable(false);
	}

	private void tabPrePosFijaOnChange() {
		if (LOCAL_APPSTATUS.compareTo(AppStatus.ANSEMANTICO) < 0) {
			btnPrePosFija.setDisable(true);
			return;
		}
		btnPrePosFija.setDisable(false);
	}

	private void tabOptimizarOnChange() {
		if (LOCAL_APPSTATUS.compareTo(AppStatus.PREPOSFIJA) < 0) {
			btnOptimizar.setDisable(true);
			return;
		}
		btnOptimizar.setDisable(false);
	}

	// Menu Bar

	@FXML
	private void mnuItAbrirArchOnAction() {
		File f = Editor.abrirArchivo(txtAreaEditor);
		if (f == null) {
			return;
		}
		LOCAL_APPSTATUS = AppStatus.FILE_READ;
		lblFilePath.setText(f.getPath());
		App.setWorkingFile(f);
		btnAnLexico.setDisable(false);
		tblAnalisLexico.setItems(null);
		txtAreaErroresLexico.setText(null);
		btnAnLexicoOnAction();
	}

	@FXML
	private void mnuItGuardarArchOnAction() {
		Editor.guardarArchivoEditor(
				txtAreaEditor,
				true,
				App.getWorkingFile() == null);
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
				ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			mnuItGuardarArchOnAction();
		} else if (alert.getResult() == ButtonType.CANCEL) {
			return;
		}
		LOCAL_APPSTATUS = AppStatus.NONE;
		lblFilePath.setText(null);
		App.setWorkingFile(null);
		txtAreaEditor.setText(null);
	}

	// Tab An Lexico

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
		long timpoInicio = System.nanoTime();

		String resultadoErrores = analizadorLexico.tokenizar(tblAnalisLexico);

		long tiempoFin = System.nanoTime();
		double durationMs = (tiempoFin - timpoInicio) / 1_000_000.0;
		lblAnLexicoTiempo.setText(String.format("Tiempo de ejecucion: %f", durationMs));

		btnErroresLexicoGuardar.setDisable(false);
		txtAreaErroresLexico.setText(resultadoErrores);
		if (!txtAreaErroresLexico.getText().isBlank()) {
			btnAnSintactico.setDisable(true);
			analizadorLexico.alerta();
			return;
		}
		LOCAL_APPSTATUS = AppStatus.ANLEXICO;
	}

	@FXML
	private void btnGuardarAnLexicoOnAction() {
		if (tblAnalisLexico.getItems() == null || tblAnalisLexico.getItems().size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder("Lexema,Col,Row,TokenDescripcion");
		for (RegistroLexico registro : tblAnalisLexico.getItems()) {
			sb.append(registro.toString()).append("\n");
		}
		FileUtilities.guardarArchivo(sb.toString(), "TablaAnLexico", FileType.CSV);
	}

	// Tab an Sintacito

	@FXML
	private void btnAnSintacticoOnAction() {
		if (analizadorLexico == null) {
			txtAreaSintactico.setText(
					"Ejecute primero el análisis léxico antes del análisis sintáctico.");
			return;
		}

		if (analizadorLexico.getRegistroLexico().isEmpty()) {
			txtAreaSintactico.setText(
					"No hay tokens disponibles. Ejecute el análisis léxico.");
			return;
		}

		AnSintactico an = new AnSintactico();
		ResultadoSintactico resultado = an.analizar(
				analizadorLexico.getRegistroLexico());
		StringBuilder salida = new StringBuilder();
		btnErroresSintGuardar.setDisable(false);

		if (resultado.esValido()) {
			salida.append("Analisis sintactico correcto.\n\n");
			// salida.append("Arbol sintactico:\n");
			salida.append(resultado.getRaiz().toString());
			btnAnSemantico.setDisable(false);
			LOCAL_APPSTATUS = AppStatus.ANSINTACTICO;
		} else {
			salida.append("Hubo errores sintacticos:\n\n");
			for (String error : resultado.getErrores()) {
				salida.append(error).append("\n");
			}
			if (resultado.getRaiz() != null) {
				salida.append("\nAnalisis sintactico con errores");
				// salida.append(resultado.getRaiz().toString());
			}
		}
		this.resultadoSintactico = resultado;
		txtAreaSintactico.setText(salida.toString());
	}

	@FXML
	private void btnAnSemanticoOnAction() {
		btnErroresSemanGuardar.setDisable(false);
		String resultado = analizadorSemantico.analizar(resultadoSintactico.getRaiz());
		boolean error = resultado.isBlank();
		resultado = (resultado.isBlank()) ? "Sin errores léxicos" : resultado;
		StringBuilder tablaSimbolos = new StringBuilder("=== Tabla de Simbolos ===\n");
		for (Map.Entry<String, Object> entry : analizadorSemantico.getTablaSimbolos().entrySet()) {
			tablaSimbolos.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
		}
		txtAreaSemantico.setText(resultado + "\n" + tablaSimbolos.toString());
		if (error) {
			LOCAL_APPSTATUS = AppStatus.ANSEMANTICO;
		}

	}

	// Tab Pre Pos Fija

	@FXML
	private void btnPrePosFijaOnAction() {
		if (LOCAL_APPSTATUS.compareTo(AppStatus.ANSEMANTICO) < 0) {
			txtAreaPrePosFija.setText(
					"Ejecute primero los analisis previos");
			return;
		}

		if (analizadorLexico.getRegistroLexico().isEmpty()) {
			txtAreaPrePosFija.setText(
					"No hay tokens disponibles. Ejecute el análisis léxico.");
			return;
		}
		txtAreaPrePosFija.setText(convertidorPrePosFija.convertirPrefija(resultadoSintactico.getRaiz(),
				analizadorSemantico.getTablaSimbolos()));
		LOCAL_APPSTATUS = AppStatus.PREPOSFIJA;
		btnPrePosFijaGuardar.setDisable(false);
	}

	// TAB OPTIMIZAR

	@FXML
	private void btnOptimizarOnAction() {
		if (LOCAL_APPSTATUS.compareTo(AppStatus.PREPOSFIJA) < 0) {
			txtAreaOptimizar.setText(
					"Ejecute primero los analisis previos");
			return;
		}

		if (analizadorLexico.getRegistroLexico().isEmpty()) {
			txtAreaPrePosFija.setText(
					"No hay tokens disponibles. Ejecute el análisis léxico.");
			return;
		}
		String resultado = optimizador.optimizar(resultadoSintactico.getRaiz());
		txtAreaOptimizar.setText(resultado);
		lblOptimizarDetalles
				.setText(String.format("Lineas: %s Caracteres: %s", resultado.lines().count(), resultado.length()));

		LOCAL_APPSTATUS = AppStatus.PREPOSFIJA;
		btnOptimizarGuardar.setDisable(false);
	}

	// GUARDAR ARCHIVOS

	@FXML
	private void btnErroresLexicoGuardarOnAction() {
		FileUtilities.guardarArchivo(
				txtAreaErroresLexico.getText(),
				"ErrLexico", FileType.TXT);
	}

	@FXML
	private void btnErroresSintGuardarOnAction() {
		FileUtilities.guardarArchivo(
				txtAreaSintactico.getText(),
				"ErrSintact", FileType.TXT);
	}

	@FXML
	private void btnErroresSemanticoGuardarOnAction() {
		FileUtilities.guardarArchivo(
				txtAreaSemantico.getText(),
				"ErrSemantico", FileType.TXT);
	}

	@FXML
	private void btnPrePosFijaGuardarOnAction() {
		FileUtilities.guardarArchivo(
				txtAreaPrePosFija.getText(),
				"PREPOSFIJA", FileType.TXT);

	}

	@FXML
	private void btnOptimizarGuardarOnAction() {
		FileUtilities.guardarArchivo(
				txtAreaOptimizar.getText(),
				"OPTIMIZADO", FileType.LUA);

	}

	// ACERCA DE

	@FXML
	private void mnuAcercaDeOnAction() {
		new Alert(
				Alert.AlertType.INFORMATION,
				"Creado por: " +
						"\nAlan Daniel Farfan Gomez" +
						"\nEduardo Jair Bautista Santiesteban" +
						"\nYael Sampayo Marin",
				ButtonType.CLOSE).show();
	}
}
