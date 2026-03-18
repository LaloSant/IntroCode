package com.introcode.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.introcode.App;
import com.introcode.entity.PReservada;
import com.introcode.entity.PToken;
import com.introcode.entity.Palabra;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;

public class AnLexico {

	@Getter
	@Setter
	private TreeSet<Character> alfabeto = new TreeSet<>();

	@Getter
	@Setter
	private TreeSet<String> palabrasReserv = new TreeSet<>();

	@Getter
	@Setter
	private TreeMap<String, AtomicInteger> caracteresUsados = new TreeMap<>();

	@Getter
	@Setter
	ArrayList<String> texto = new ArrayList<>();

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
		String[] palabras = {
				"INICIO", "FIN", "LEER", "IMPRIMIR", "ENTER", "EN",
				"DECLARAR", "ENTERO", "REAL", "CADENA", "BOOLEANO", "FLOTANTE",
				"VERDADERO", "FALSO", "SOBREESCRIBIR",
				"SI", "ENTONCES", "FINSI", "SINO", "FINSINO",
				"MIENTRAS", "HACER", "FINMIENTRAS",
				"PARA", "HASTA", "INCREMENTA", "DECREMENTA", "FINPARA"
		};

		this.alfabeto = new TreeSet<>(Arrays.asList(alfabetoArr));
		this.palabrasReserv = new TreeSet<>(Arrays.asList(palabras));

		/* for (Character c : alfabeto) {
			mapa.put(c, new AtomicInteger(0));
		} */

	}

	public boolean analisisLexico(TextArea textAreaErrores, TextArea textAreaResultado) {
		String line;
		StringBuilder sbErrores = new StringBuilder();
		StringBuilder sbResultado = new StringBuilder();
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
					aniadirCaracter(c);
				}
				indiceLinea++;
			}
			caracteresUsados.forEach((display, valor) -> sbResultado.append(display).append("\t")
					.append(" <---> Coincidencias: ").append(valor)
					.append("\n"));
			String errString = sbErrores.toString();
			textAreaErrores.setText(errString.isBlank() ? "Sin errores lexicos" : errString);
			textAreaResultado.setText(sbResultado.toString());
			buffer.close();
			return huboError;
		} catch (Exception e) {
			IO.println(e.getMessage());
		}
		return huboError;
	}

	public void tokenizar(TextArea txtAreaTokensLexico, TextArea txtAreaErroresTokens) {
		ArrayList<Palabra> tokens = new ArrayList<>();
		StringBuilder sbResultado = new StringBuilder();
		//StringBuilder sbErrores = new StringBuilder();
		for (String linea : this.texto) {
			StringTokenizer st = new StringTokenizer(linea, " ");
			String lineaLimpia = linea.trim();
			if (lineaLimpia.startsWith("#") || lineaLimpia.isBlank()) {
				continue;
			}
			while (st.hasMoreTokens()) {
				String token = st.nextToken().trim();
				if (this.palabrasReserv.contains(token)) {
					sbResultado.append("Palabra reservada: ");
					PReservada p = new PReservada(token);
					tokens.add(p);
				} else {
					sbResultado.append("Token: ");
					PToken t = new PToken(token);
					tokens.add(t);
				}
				sbResultado.append(token).append("\n");
			}
			txtAreaTokensLexico.setText(sbResultado.toString());
		}
	}

	private String aniadirCaracter(char c) {
		String display = "";
		switch (c) {
			case '\t' -> {
				display = "(\\t)";
			}
			case '\n' -> {
				display = "(\\n)";
			}
			case ' ' -> {
				display = "( )";
			}
			default -> {
				display = String.valueOf(c);
			}
		}
		caracteresUsados.putIfAbsent(display, new AtomicInteger(0));
		caracteresUsados.get(display).incrementAndGet();
		return display;
	}

	public void alertaError() {
		new Alert(Alert.AlertType.WARNING,
				"Han habido errores en el analisis lexico!",
				ButtonType.CLOSE).show();
	}
}
