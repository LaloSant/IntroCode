package com.introcode.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.introcode.App;

import javafx.scene.control.TextArea;

public class AnLexico {
	private TreeSet<Character> alfabeto = new TreeSet<>();

	private TreeMap<String, AtomicInteger> mapa = new TreeMap<>();

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
		this.alfabeto = new TreeSet<>(Arrays.asList(alfabetoArr));
		/* for (Character c : alfabeto) {
			mapa.put(c, new AtomicInteger(0));
		} */
	}

	public void analisisLexico(TextArea textAreaErrores, TextArea textAreaResultado) {
		String line;
		StringBuilder sbErrores = new StringBuilder();
		StringBuilder sbResultado = new StringBuilder();

		try {
			FileReader fileReader = new FileReader(App.getWorkingFile());
			BufferedReader buffer = new BufferedReader(fileReader);
			int indiceLinea = 1;
			while ((line = buffer.readLine()) != null) {
				int indiceColumna = 1;
				for (char c : line.toCharArray()) {
					if (!this.alfabeto.contains(c)) {
						sbErrores.append("Error! Elemento no reconocido en alfabeto: ").append(c)
								.append(". En ").append(indiceLinea)
								.append(" : ").append(indiceColumna)
								.append('\n');
								indiceColumna++;
								continue;
					}
					indiceColumna++;
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
					mapa.putIfAbsent(display, new AtomicInteger(0));
					mapa.get(display).incrementAndGet();
				}
				indiceLinea++;
			}
			mapa.forEach((display, valor) -> sbResultado.append(display).append("\t").append(" <---> Coincidencias: ").append(valor)
					.append("\n"));
			String errString = sbErrores.toString();
			textAreaErrores.setText(errString.isBlank() ? "Sin errores lexicos" : errString);
			textAreaResultado.setText(sbResultado.toString());
			buffer.close();
		} catch (Exception e) {
			IO.println(e.getMessage());
		}
	}
}
