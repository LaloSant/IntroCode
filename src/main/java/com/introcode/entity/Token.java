package com.introcode.entity;

public enum Token {
	PALABRA_RESERVADA("Palabra Reservada"),
	OPERADOR("Operador"),
	VARIABLE("Variable"),
	NUMERO_ENTERO("Numero Entero"),
	NUMERO_REAL("Numero real");

	private final String descripcion;

	Token(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return this.descripcion;
	}
}
