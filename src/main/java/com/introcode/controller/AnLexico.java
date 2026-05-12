package com.introcode.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.introcode.App;
import com.introcode.automatas.AutomNumeros;
import com.introcode.entity.PReservada;
import com.introcode.entity.PToken;
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

	private TreeSet<Character> alfabeto = new TreeSet<>();

	private TreeSet<String> palabrasReserv = new TreeSet<>();

	private ArrayList<String> texto = new ArrayList<>();

	private ArrayList<RegistroLexico> registroLexico = new ArrayList<>();

	private final AutomNumeros automNumeros = new AutomNumeros();

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
				'+', '-', '*', '/', '=', '>', '<', '!', '(', ')', '\"',
				',', ' ', ';', '#', '.', '\n', '\t'
		};
		String[] pR = {
				"INICIO", "FIN", "LEER", "IMPRIMIR", "ENTER", "EN",
				"DECLARAR", "ENTERO", "REAL", "CADENA", "BOOLEANO", "FLOTANTE",
				"VERDADERO", "FALSO", "SOBREESCRIBIR",
				"SI", "ENTONCES", "FINSI", "SINO", "FINSINO",
				"MIENTRAS", "HACER", "FINMIENTRAS",
				"PARA", "HASTA", "INCREMENTA", "DECREMENTA", "FINPARA"
		};

		this.alfabeto = new TreeSet<>(Arrays.asList(alfabetoArr));
		this.palabrasReserv = new TreeSet<>(Arrays.asList(pR));
	}

	public boolean analisisLexico(TextArea textAreaErrores) {
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
					if (!this.alfabeto.contains(c)) {
						huboError = true;
						sbErrores.append("Error! Elemento no reconocido en alfabeto: ").append(c)
								.append(". En ").append(indiceLinea).append(" : ").append(indiceColumna).append('\n');
						continue;
					}
				}
				indiceLinea++;
			}
			String errString = sbErrores.toString();
			textAreaErrores.setText(errString.isBlank() ? "Sin errores lexicos" : errString);
			buffer.close();
			return huboError;
		} catch (Exception e) {
			IO.println(e.getMessage());
		}
		return huboError;
	}

	public void tokenizar(TableView<RegistroLexico> tabla) {
		ArrayList<RegistroLexico> listaRegistros = new ArrayList<>();
		int iCol = 0;
		int iRow = 0;
		for (String linea : this.texto) {
			iRow++;
			StringTokenizer st = new StringTokenizer(linea, " ");
			String lineaLimpia = linea.trim();
			if (lineaLimpia.startsWith("#") || lineaLimpia.isBlank()) {
				continue;
			}
			while (st.hasMoreTokens()) {
				iCol++;
				String token = st.nextToken().trim();
				RegistroLexico rl = new RegistroLexico(iRow, iCol++);
				if (this.palabrasReserv.contains(token)) {
					rl.setLexema(new PReservada(token));
					rl.setToken(Token.PALABRA_RESERVADA);
					listaRegistros.add(rl);
					continue;
				} // Analisis con automatas para: Variable, Numero entero, numero Real
				int resultAutomNum = automNumeros.simulate(token, true);
				if (resultAutomNum == 1) {
					rl.setToken(Token.NUMERO_ENTERO);
				} else if (resultAutomNum == 3) {
					rl.setToken(Token.NUMERO_REAL);
				} else {
					rl.setToken(Token.VARIABLE);
				}
				rl.setLexema(new PToken(token));
				listaRegistros.add(rl);
			}
		}
		tabla.setItems(FXCollections.observableArrayList(listaRegistros));

	}

	public void alertaError() {
		new Alert(Alert.AlertType.WARNING,
				"Han habido errores en el analisis lexico!",
				ButtonType.CLOSE).show();
	}
}
