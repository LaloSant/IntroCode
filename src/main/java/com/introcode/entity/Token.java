package com.introcode.entity;

public enum Token {
	PALABRA_RESERVADA("Palabra Reservada"),
	OPERADOR_ARITMETICO("Operador Aritmetico"),
	OPERADOR_RELACIONAL("Operador Relacional"),
	OPERADOR_LOGICO("Operador Logico"),
	OPERADOR_ASIGNACION("Operador de Asignacion"),
	SEPARADOR("Separador"),
	VARIABLE("Variable"),
	CADENA("Cadena"),
	NUMERO_ENTERO("Numero Entero"),
	NUMERO_REAL("Numero real"),
	ERROR_LEXICO("---");

	private final String descripcion;

	Token(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return this.descripcion;
	}
}
