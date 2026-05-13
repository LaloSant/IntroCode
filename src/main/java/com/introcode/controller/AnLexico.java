package com.introcode.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.introcode.App;
import com.introcode.automatas.ACadenas;
import com.introcode.automatas.ANumeros;
import com.introcode.automatas.AVariables;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.Token;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnLexico {

	private final TreeSet<Character> ALFABETO;

	private final TreeSet<String> PALABRASRESERVADAS;

	private final TreeSet<String> OPERADORESARITMETICOS;

	private final TreeSet<String> OPERADORESRELACIONALES;

	private final TreeSet<String> OPERADORESLOGICOS;

	private final TreeSet<String> SEPARADORES;

	private final HashMap<String, Integer> IDTOKENS = new HashMap<>();

	private int consecutivoID = 1;

	private final ArrayList<String> texto = new ArrayList<>();

	private final ArrayList<RegistroLexico> registroLexico = new ArrayList<>();

	private final ANumeros automNumeros = new ANumeros();

	private final AVariables automVariables = new AVariables();

	private final ACadenas automCadenas;

	public AnLexico() {
		Character[] alfabetoArr = {
				// dígitos
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				// letras mayúsculas
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
				// letras minúsculas
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
				'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
				// símbolos
				'+', '-', '*', '/', '=', '^', '>', '<', '!', '(', ')', '\"',
				',', ' ', ';', '#', '.', '\n', '\t'
		};

		String[] pR = {
				"INICIO", "FIN", "LEER", "EN", "IMPRIMIR", "ENTER",
				"DECLARAR", "SOBREESCRIBIR", "ENTERO", "REAL", "CADENA", "BOOLEANO",
				"VERDADERO", "FALSO",
				"SI", "ENTONCES", "SINO", "FINSI", "FINSINO",
				"MIENTRAS", "HACER", "FINMIENTRAS",
				"PARA", "HASTA", "INCREMENTA", "DECREMENTA", "FINPARA"
		};

		List<String> listaPR = Arrays.asList(pR);

		this.ALFABETO = new TreeSet<>(Arrays.asList(alfabetoArr));
		this.PALABRASRESERVADAS = new TreeSet<>(listaPR);
		this.automCadenas = new ACadenas(alfabetoArr);

		for (String palReserv : listaPR) {
			IDTOKENS.put(palReserv, consecutivoID++);
		}

		String[] opArit = { "+", "-", "*", "/", "^" };
		this.OPERADORESARITMETICOS = new TreeSet<>(Arrays.asList(opArit));
		for (String s : opArit) {
			IDTOKENS.put(s, consecutivoID++);
		}

		String[] opRela = { "==", "!=", "<", ">", "<=", ">=" };
		this.OPERADORESRELACIONALES = new TreeSet<>(Arrays.asList(opRela));

		for (String s : opRela) {
			IDTOKENS.put(s, consecutivoID++);
		}

		String[] opLog = { "AND", "OR", "NOT" };
		this.OPERADORESLOGICOS = new TreeSet<>(Arrays.asList(opLog));
		for (String s : opLog) {
			IDTOKENS.put(s, consecutivoID++);
		}

		IDTOKENS.put("=", 42);

		String[] sep = { ";", "(", ")", ",", "\"", "#" };
		this.SEPARADORES = new TreeSet<>(Arrays.asList(sep));
		for (String s : sep) {
			IDTOKENS.put(s, consecutivoID++);
		}

	}

	public boolean analisisLexico(TextArea txtAreaErrores) {
		String line;
		StringBuilder sbErrores = new StringBuilder();
		boolean huboError = false;

		try {
			FileReader fileReader = new FileReader(App.getWorkingFile());
			BufferedReader buffer = new BufferedReader(fileReader);
			int indiceLinea = 1;
			while ((line = buffer.readLine()) != null) {
				this.texto.add(line);
				int indiceColumna = 0;
				for (char c : line.toCharArray()) {
					indiceColumna++;
					if (!this.ALFABETO.contains(c)) {
						huboError = true;
						sbErrores.append("Error! Elemento no reconocido en alfabeto: ").append(c)
								.append(". En ").append(indiceLinea).append(" : ").append(indiceColumna).append('\n');
						continue;
					}
				}
				indiceLinea++;
			}
			String errString = sbErrores.toString();
			txtAreaErrores.setText(errString.isBlank() ? "Sin errores lexicos individuales" : errString);
			buffer.close();
			return huboError;
		} catch (Exception e) {
			IO.println(e.getMessage());
		}
		return huboError;
	}

	public boolean tokenizar(TableView<RegistroLexico> tabla, TextArea txtAreaErrores) {
		boolean huboError = false;
		ArrayList<RegistroLexico> listaRegistros = new ArrayList<>();
		int iCol = 0;
		int iRow = 0;
		StringBuilder sb = new StringBuilder();
		for (String linea : this.texto) {
			iRow++;
			StringTokenizer st = new StringTokenizer(linea, " ");
			String lineaLimpia = linea.trim();
			if (lineaLimpia.startsWith("#") || lineaLimpia.isBlank()) {
				continue;
			}
			while (st.hasMoreTokens()) {
				iCol++;
				String lexema = st.nextToken().trim();
				RegistroLexico rl = crearRegistro(lexema, iCol, iRow);
				if (rl.getToken().equals(Token.ERROR_LEXICO)) {
					huboError = true;
					sb.append(String.format("Error lexico en %d:%d por el lexema: %s", iRow, iCol, lexema));
				}
				listaRegistros.add(rl);
			}
		}
		txtAreaErrores.setText(sb.toString());
		tabla.setItems(FXCollections.observableArrayList(listaRegistros));
		return huboError;
	}

	@SuppressWarnings("rawtypes")
	private RegistroLexico crearRegistro(String lexema, int iCol, int iRow) {
		RegistroLexico rl = new RegistroLexico(lexema, iRow, iCol++);
		TreeSet[] categoria = { this.PALABRASRESERVADAS, this.OPERADORESARITMETICOS, this.OPERADORESRELACIONALES,
				this.OPERADORESLOGICOS, this.SEPARADORES };

		int i = 0;
		if (lexema.equals("=")) {
			rl.setId(IDTOKENS.get("="));
			rl.setToken(Token.OPERADOR_ASIGNACION);
			return rl;
		}
		for (TreeSet set : categoria) {
			if (set.contains(lexema)) {
				rl.setId(IDTOKENS.get(lexema));
				switch (i) {
					case 0 -> {
						rl.setToken(Token.PALABRA_RESERVADA);
					}
					case 1 -> {
						rl.setToken(Token.OPERADOR_ARITMETICO);
					}
					case 2 -> {
						rl.setToken(Token.OPERADOR_RELACIONAL);
					}
					case 3 -> {
						rl.setToken(Token.OPERADOR_LOGICO);
					}
					case 4 -> {
						rl.setToken(Token.SEPARADOR);
					}
				}
				return rl;
			}
			i++;
		}

		int resultAutomNum = automNumeros.simulate(lexema, true);
		if (resultAutomNum == 1 || resultAutomNum == 3) {
			rl.setToken(resultAutomNum == 1 ? Token.NUMERO_ENTERO : Token.NUMERO_REAL);
			rl.setId(consecutivoID++);
			return rl;
		}

		if (automCadenas.simulate(lexema)) {
			rl.setToken(Token.CADENA);
			rl.setId(consecutivoID++);
			return rl;
		}

		if (automVariables.simulate(lexema)) {
			rl.setToken(Token.VARIABLE);
			rl.setId(consecutivoID++);
			return rl;
		}

		rl.setToken(Token.ERROR_LEXICO);
		rl.setId(-1);
		return rl;

	}

	public void alertaError() {
		new Alert(Alert.AlertType.WARNING,
				"Han habido errores en el analisis lexico!",
				ButtonType.CLOSE).show();
	}
}
