package com.introcode.entity;

public enum Token {
	PALABRA_RESERVADA("Palabra Reservada", 1),
	OPERADOR_LOGICO("Operador Logico", 14),
	OPERADOR_ARITMETICO("Operador Aritmetico", 18),
	OPERADOR_RELACIONAL("Operador Relacional", 24),
	OPERADOR_ASIGNACION("Operador de Asignacion", 30),
	DELIMITADOR("Delimitador", 31),
	VARIABLE("Variable", 41),
	CADENA("Cadena", 42),
	NUMERO("Numero literal", 43),
	ERROR_LEXICO("---", -1);

	private final String descripcion;
	private final int tokenId;

	Token(String descripcion, int tokenId) {
		this.descripcion = descripcion;
		this.tokenId = tokenId;
	}

	@Override
	public String toString() {
		return this.descripcion;
	}

	public int getTokenId(){
		return this.tokenId;
	}
}
