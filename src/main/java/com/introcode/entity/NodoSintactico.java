package com.introcode.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class NodoSintactico {

	private final TipoSintactico tipo;
	private final String valor;
	private final int row;
	private final int column;
	private final List<NodoSintactico> hijos = new ArrayList<>();

	public NodoSintactico(TipoSintactico tipo) {
		this(tipo, null, -1, -1);
	}

	public NodoSintactico(TipoSintactico tipo, String valor) {
		this(tipo, valor, -1, -1);
	}

	public NodoSintactico(TipoSintactico tipo, String valor, int row, int column) {
		this.tipo = tipo;
		this.valor = valor;
		this.row = row;
		this.column = column;
	}

	public void agregarHijo(NodoSintactico hijo) {
		if (hijo != null) {
			hijos.add(hijo);
		}
	}

	public List<NodoSintactico> getHijos() {
		return Collections.unmodifiableList(hijos);
	}

	@Override
	public String toString() {
		return toString(0);
	}

	private String toString(int nivel) {
		String indent = "\t".repeat(Math.max(0, nivel));
		String textoBase = valor == null ? String.format("%s%s", indent, tipo)
				: String.format("%s%s: %s", indent, tipo, valor);
		StringBuilder sb = new StringBuilder(textoBase);
		for (NodoSintactico hijo : hijos) {
			sb.append(System.lineSeparator()).append(hijo.toString(nivel + 1));
		}
		return sb.toString();
	}

	public enum TipoSintactico {
		PROGRAMA("Programa"),
		SENTENCIA("Sentencia"),
		PUNTOCOMAOPC("Punto y coma opcional"),
		TERMINAL("Terminal"),
		TERMINAL_INVALIDO("Terminal invalido"),
		TERMINAL_ERRONEO("Terminal erroneo"),
		SENTENCIA_ERRONEA("Sentencia erronea"),
		DECLARACION("Declaracion"),
		VARIABLE("Variable"),
		VARIABLE_ERRONEA("Variable Erronea"),
		NUM_ENTERO("Numero entero"),
		NUM_REAL("Numero real"),
		CADENA("Cadena"),
		BOOLEANO("Boleano"),
		TIPO("Tipo"),
		TIPO_ERRONEO("Tipo Erroneo"),
		VALOR_OPCIONAL("Valor opcional"),
		SOBREESCRIBIR("Sobrescripcion"),
		LEER("Leer"),
		IMPRIMIR("Imprimir"),
		LISTA_IMPRIMIR("Lista de imprimir"),
		CONDICIONAL("Condicional"),
		RAMA_ELSE("Rama else"),
		WHILE("While"),
		PARA("Para"),
		PASO("Paso"),
		PASO_ERRONEO("Paso erroneo"),
		OP_RELACIONAL("Operador Relacional"),
		OP_ARITMETICO("Operador Aritmetico"),
		OP_LOGICO("Operador Logico"),
		OP_NOT("Operador NOT");

		private final String valorString;

		TipoSintactico(String valorString) {
			this.valorString = valorString;
		}

		@Override
		public String toString() {
			return this.valorString;
		}
	}

}
