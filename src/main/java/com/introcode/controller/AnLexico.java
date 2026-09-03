package com.introcode.controller;

import com.introcode.App;
import com.introcode.automatas.ACadenas;
import com.introcode.automatas.ANumeros;
import com.introcode.automatas.AVariables;
import com.introcode.entity.Alfabeto;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.TokenLexico;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnLexico {

	private final HashMap<String, Integer> IDTOKENS = new HashMap<>();

	private int consecutivoID = 1;

	private final ArrayList<String> texto = new ArrayList<>();

	private final ArrayList<RegistroLexico> registroLexico = new ArrayList<>();

	private final ANumeros automNumeros = new ANumeros();

	private final AVariables automVariables = new AVariables();

	private final ACadenas automCadenas;

	private Boolean multiLinea = false;

	private StringBuilder multiLineString = null;

	private int multiLineaFila = 0;

	private int multiLineaCol = 0;

	public AnLexico() {
		this.automCadenas = new ACadenas();

		for (String palReserv : Alfabeto.PALABRAS_RESERVADAS) {
			IDTOKENS.put(palReserv, consecutivoID++);
		}

		// for (String opLogico : Alfabeto.OPERADORES_LOGICOS) {
		// IDTOKENS.put(opLogico, consecutivoID++);
		// }

		for (String opAritmetico : Alfabeto.OPERADORES_ARITMETICOS) {
			IDTOKENS.put(opAritmetico, consecutivoID++);
		}

		for (String opRelacional : Alfabeto.OPERADORES_RELACIONALES) {
			IDTOKENS.put(opRelacional, consecutivoID++);
		}

		IDTOKENS.put(Alfabeto.OPERADOR_ASIGNACION, consecutivoID++);

		for (String separador : Alfabeto.SEPARADORES) {
			IDTOKENS.put(separador, consecutivoID++);
		}
	}

	public void leerArchivo() throws IOException {
		this.texto.clear();
		String line;
		FileReader fileReader = new FileReader(App.getWorkingFile());
		BufferedReader buffer = new BufferedReader(fileReader);
		while ((line = buffer.readLine()) != null) {
			this.texto.add(line);
		}
		buffer.close();
	}

	public String tokenizar(TableView<RegistroLexico> tabla) {
		registroLexico.clear();
		RegistroLexico.consecutivo = 0;
		List<RegistroLexico> listaRegistros = new ArrayList<>();
		StringBuilder sbErrores = new StringBuilder();

		for (int iRow = 0; iRow < this.texto.size(); iRow++) {
			if (!multiLinea && multiLineString != null) {
				RegistroLexico r = crearRegistro(
					multiLineString.toString(),
					multiLineaCol,
					multiLineaFila
				);
				listaRegistros.add(r);
				registroLexico.add(r);
				multiLineString = null;
				multiLinea = false;
			}
			String linea = this.texto.get(iRow);
			String lineaLimpia = linea.trim();
			if (lineaLimpia.isEmpty()) {
				continue;
			}

			for (RegistroLexico registro : extraerRegistrosLinea(
				linea,
				iRow + 1,
				sbErrores
			)) {
				listaRegistros.add(registro);
				registroLexico.add(registro);
			}
		}

		tabla.setItems(FXCollections.observableArrayList(listaRegistros));
		return sbErrores.toString();
	}

	private List<RegistroLexico> extraerRegistrosLinea(
		String linea,
		int fila,
		StringBuilder sbErrores
	) {
		List<RegistroLexico> registros = new ArrayList<>();
		int columna = 1;
		int indice = 0;

		while (indice < linea.length()) {
			char actual = linea.charAt(indice);
			int columnaInicio = columna;

			if (multiLinea) {
				if (linea.contains("]]")) {
					multiLinea = false;
				}
				if (multiLinea && multiLineString != null) {
					multiLineString.append("\n").append(linea);
					indice = linea.length();
					continue;
				}
				if (!multiLinea && multiLineString != null) {
					int indiceCorchetes = linea.indexOf("]]");
					multiLineString
						.append("\n")
						.append(linea.substring(indice, indiceCorchetes + 2));
					indice = indiceCorchetes + 2;
					break;
				}
				break;
			}

			if (Character.isWhitespace(actual)) {
				indice++;
				columna++;
				continue;
			}

			if (esComentario(linea, indice, actual)) {
				indice += 2;
				if (indice + 1 >= linea.length()) {
					break;
				}
				if (esMultilinea(linea, indice)) {
					multiLinea = true;
				}
				break;
			}

			if (esOperadorCompuesto(linea, indice)) {
				String operador = linea.substring(indice, indice + 2);
				if (!"[[".equals(operador)) {
					registros.add(crearRegistro(operador, columnaInicio, fila));
					indice += 2;
					columna += 2;
					continue;
				}
				multiLinea = true;
				multiLineString = new StringBuilder(
					linea.substring(indice, indice + 2)
				);
				int indiceCorchetesFin = linea.indexOf("]]");
				if (indiceCorchetesFin == -1) {
					multiLineaFila = fila;
					multiLineaCol = columnaInicio;
					multiLineString.append(linea.substring(indice + 2));
					indice = linea.length();
					continue;
				} else {
					multiLineaFila = fila;
					multiLineaCol = columna;
					multiLineString.append(
						linea.substring(indice + 2, indiceCorchetesFin + 2)
					);
					indice = indiceCorchetesFin + 2;
					columna = indice + 1;
					if (indice < linea.length()) {
						actual = linea.charAt(indice);
					}
					RegistroLexico registro = crearRegistro(
						multiLineString.toString(),
						columnaInicio,
						fila
					);
					registros.add(registro);
					multiLineString = null;
					multiLineaFila = 0;
					multiLineaCol = 0;
					multiLinea = false;
					continue;
				}
			}

			if (esSimboloSimple(actual)) {
				String lexema = String.valueOf(actual);
				registros.add(crearRegistro(lexema, columnaInicio, fila));
				indice++;
				columna++;
				continue;
			}

			if (!Alfabeto.ALFABETO.contains(actual)) {
				sbErrores.append(
					String.format(
						"Error lexico (0) en %d:%d -> %c%n",
						fila,
						columnaInicio,
						actual
					)
				);
				registros.add(
					crearRegistro(String.valueOf(actual), columnaInicio, fila)
				);
				indice++;
				columna++;
				continue;
			}

			StringBuilder lexemaActual = new StringBuilder();

			int indiceEsCadena = esCadena(linea, indice);
			if (indiceEsCadena == -2) {
				lexemaActual.append(linea.substring(indice));
				indice = linea.length();
			} else if (indiceEsCadena != -1) {
				String lexemaPropuesto = linea.substring(
					indice,
					indiceEsCadena + 1
				);
				int diff = indiceEsCadena - indice;
				indice += diff + 1;
				columna += diff + 1;
				lexemaActual.append(lexemaPropuesto);
			} else {
				while (
					indice < linea.length() &&
					!Character.isWhitespace(linea.charAt(indice)) &&
					!esSimboloSimple(linea.charAt(indice)) &&
					!esOperadorCompuesto(linea, indice) &&
					Alfabeto.ALFABETO.contains(linea.charAt(indice))
				) {
					lexemaActual.append(linea.charAt(indice));
					indice++;
					columna++;
				}
			}
			String lexema = lexemaActual.toString();
			RegistroLexico registro = crearRegistro(
				lexema,
				columnaInicio,
				fila
			);
			if (registro.getToken().equals(TokenLexico.ERROR_LEXICO)) {
				sbErrores.append(
					String.format(
						"Error lexico (1) en %d:%d -> %s%n",
						fila,
						columnaInicio,
						lexema
					)
				);
			}
			registros.add(registro);
		}

		return registros;
	}

	private boolean esMultilinea(String linea, int indice) {
		return linea.charAt(indice) == '[' && linea.charAt(indice + 1) == '[';
	}

	private boolean esComentario(String linea, int indice, char actual) {
		return (
			actual == '-' &&
			indice + 1 < linea.length() &&
			linea.charAt(indice + 1) == '-'
		);
	}

	private boolean esOperadorCompuesto(String linea, int indice) {
		if (indice + 1 >= linea.length()) {
			return false;
		}
		String posible = linea.substring(indice, indice + 2);
		return (
			"==".equals(posible) ||
			"~=".equals(posible) ||
			"<=".equals(posible) ||
			">=".equals(posible) ||
			"[[".equals(posible)
		);
	}

	private boolean esSimboloSimple(char valor) {
		return "()+-*/%^=<>~,;:[]{}".indexOf(valor) >= 0;
	}

	private int esCadena(String linea, int indice) {
		int tipo = "\"\'".indexOf(linea.charAt(indice));
		if (tipo == -1) {
			return -1;
		}
		int indiceAux = indice;
		char charTipo = "\"\'".charAt(tipo);
		do {
			indiceAux++;
		} while (
			indiceAux < linea.length() && linea.charAt(indiceAux) != charTipo
		);
		if (indiceAux == linea.length()) {
			return -2;
		}
		return linea.charAt(indiceAux) == charTipo ? indiceAux : -1;
	}

	@SuppressWarnings("rawtypes")
	private RegistroLexico crearRegistro(String lexema, int iCol, int iRow) {
		RegistroLexico rl = new RegistroLexico(lexema, iRow, iCol);
		Set[] categoria = {
			Alfabeto.PALABRAS_RESERVADAS,
			Alfabeto.OPERADORES_ARITMETICOS,
			Alfabeto.OPERADORES_RELACIONALES,
			Alfabeto.OPERADORES_LOGICOS,
			Alfabeto.SEPARADORES,
		};

		int i = 0;
		if (lexema.equals("=")) {
			rl.setToken(TokenLexico.OPERADOR_ASIGNACION);
			rl.setId(TokenLexico.OPERADOR_ASIGNACION.getTokenId());
			return rl;
		}
		for (Set set : categoria) {
			if (set.contains(lexema)) {
				rl.setId(IDTOKENS.get(lexema));
				switch (i) {
					case 0 -> {
						rl.setToken(TokenLexico.PALABRA_RESERVADA);
					}
					case 1 -> {
						rl.setToken(TokenLexico.OPERADOR_ARITMETICO);
					}
					case 2 -> {
						rl.setToken(TokenLexico.OPERADOR_RELACIONAL);
					}
					case 3 -> {
						rl.setToken(TokenLexico.OPERADOR_LOGICO);
					}
					case 4 -> {
						rl.setToken(TokenLexico.DELIMITADOR);
					}
				}
				return rl;
			}
			i++;
		}

		if (automNumeros.simulate(lexema)) {
			rl.setToken(TokenLexico.NUMERO);
			rl.setId(TokenLexico.NUMERO.getTokenId());
			return rl;
		}

		if (automCadenas.simulate(lexema)) {
			rl.setToken(TokenLexico.CADENA);
			rl.setId(TokenLexico.CADENA.getTokenId());
			return rl;
		}

		if (automVariables.simulate(lexema)) {
			rl.setToken(TokenLexico.VARIABLE);
			rl.setId(TokenLexico.VARIABLE.getTokenId());
			return rl;
		}

		rl.setToken(TokenLexico.ERROR_LEXICO);
		rl.setId(TokenLexico.ERROR_LEXICO.getTokenId());
		return rl;
	}

	public void alerta() {
		new Alert(
			Alert.AlertType.WARNING,
			"Han habido errores en el analisis lexico!",
			ButtonType.CLOSE
		).show();
	}
}
