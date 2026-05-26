package com.introcode.controller;

import java.util.ArrayList;
import java.util.List;

import com.introcode.automatas.ACadenas;
import com.introcode.automatas.ANumeros;
import com.introcode.automatas.AVariables;
import com.introcode.entity.NodoSintactico;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.ResultadoSintactico;
import com.introcode.entity.Token;
import com.introcode.entity.NodoSintactico.TipoSintactico;

public class AnSintactico {

	private final List<RegistroLexico> tokens = new ArrayList<>();
	private final List<String> errores = new ArrayList<>();
	private int posicion;
	private NodoSintactico raiz;

	private final ANumeros automNumeros = new ANumeros();
	private final AVariables automVariables = new AVariables();
	private final ACadenas automCadenas = new ACadenas(new Character[] {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'+', '-', '*', '/', '=', '^', '>', '<', '!', '(', ')', '"',
			',', ' ', ';', '#', '.', '\n', '\t'
	});

	public ResultadoSintactico analizar(List<RegistroLexico> listaTokens) {
		tokens.clear();
		errores.clear();
		posicion = 0;
		raiz = null;

		if (listaTokens == null || listaTokens.isEmpty()) {
			errores.add("No hay tokens para analizar.");
			return new ResultadoSintactico(null, errores);
		}

		tokens.addAll(listaTokens);
		raiz = parsePrograma();

		if (!isAtEnd()) {
			RegistroLexico token = currentToken();
			errores.add(String.format("Token inesperado al final: '%s' en %d:%d.", token.getLexema().getValor(),
					token.getRow(), token.getColumn()));
		}

		return new ResultadoSintactico(raiz, errores);
	}

	private NodoSintactico parsePrograma() {
		NodoSintactico programa = new NodoSintactico(TipoSintactico.PROGRAMA);
		programa.agregarHijo(expectLexeme("INICIO", "Se esperaba la palabra reservada INICIO al inicio del programa."));

		programa.agregarHijo(parseSentencias());

		programa.agregarHijo(expectLexeme("FIN", "Se esperaba la palabra reservada FIN al final del programa."));

		return programa;
	}

	private NodoSintactico parseSentencias() {
		NodoSintactico sentencias = new NodoSintactico(TipoSintactico.SENTENCIA);
		while (!isAtEnd() && canStartSentencia()) {
			sentencias.agregarHijo(parseSentencia());
		}
		return sentencias;
	}

	private boolean canStartSentencia() {
		if (isAtEnd()) {
			return false;
		}
		String lexema = currentLexeme();
		return lexema.equals("DECLARAR") || lexema.equals("SOBREESCRIBIR") || lexema.equals("LEER")
				|| lexema.equals("IMPRIMIR") || lexema.equals("SI") || lexema.equals("MIENTRAS")
				|| lexema.equals("PARA");
	}

	private NodoSintactico parseSentencia() {
		String lexema = currentLexeme();
		return switch (lexema) {
			case "DECLARAR" -> parseDecl();
			case "SOBREESCRIBIR" -> parseSobrescr();
			case "LEER", "IMPRIMIR" -> parseIO();
			case "SI" -> parseCondicional();
			case "MIENTRAS" -> parseWhile();
			case "PARA" -> parsePara();
			default -> {
				error(String.format("Sentencia inválida o no reconocida: '%s' en %d:%d.", lexema,
						currentToken().getRow(), currentToken().getColumn()));
				advance();
				yield new NodoSintactico(TipoSintactico.SENTENCIA_ERRONEA);
			}
		};
	}

	private NodoSintactico parseDecl() {
		NodoSintactico decl = new NodoSintactico(TipoSintactico.DECLARACION);
		NodoSintactico inicio = expectLexeme("DECLARAR", "Se esperaba DECLARAR en la declaración.");
		decl.agregarHijo(inicio);
		decl.agregarHijo(parseTipo());
		decl.agregarHijo(parseVariable("Se esperaba un identificador después del tipo."));
		decl.agregarHijo(parseValOpt());
		expectLexeme(";", "Se esperaba ';' al final de la declaración.");
		return decl;
	}

	private NodoSintactico parseTipo() {
		return switch (currentLexeme()) {
			case "ENTERO", "REAL", "CADENA", "BOOLEANO" -> {
				RegistroLexico token = advance();
				yield new NodoSintactico(TipoSintactico.TIPO, token.getLexema().getValor(), token.getRow(),
						token.getColumn());
			}
			default -> {
				error("Se esperaba un tipo de dato (ENTERO, REAL, CADENA, BOOLEANO).");
				yield new NodoSintactico(TipoSintactico.TIPO_ERRONEO);
			}
		};
	}

	private NodoSintactico parseValOpt() {
		if (matchLexeme("=")) {
			NodoSintactico valOpt = new NodoSintactico(TipoSintactico.VALOR_OPT);
			valOpt.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "=", currentToken().getRow(),
					currentToken().getColumn()));
			valOpt.agregarHijo(parseExpr());
			return valOpt;
		}
		return new NodoSintactico(TipoSintactico.VALOR_OPT);
	}

	private NodoSintactico parseSobrescr() {
		NodoSintactico sobrescr = new NodoSintactico(TipoSintactico.SOBREESCRIPCION);
		sobrescr.agregarHijo(expectLexeme("SOBREESCRIBIR", "Se esperaba SOBREESCRIBIR al inicio de la sentencia."));
		sobrescr.agregarHijo(parseVariable("Se esperaba un identificador después de SOBREESCRIBIR."));
		expectLexeme("=", "Se esperaba el operador de asignación '=' en la sobrescritura.");
		sobrescr.agregarHijo(parseExpr());
		expectLexeme(";", "Se esperaba ';' al final de la sobrescritura.");
		return sobrescr;
	}

	private NodoSintactico parseIO() {
		if (matchLexeme("LEER")) {
			NodoSintactico leer = new NodoSintactico(TipoSintactico.LEER);
			leer.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "LEER", currentToken().getRow(),
					currentToken().getColumn()));
			expectLexeme("EN", "Se esperaba EN después de LEER.");
			leer.agregarHijo(parseVariable("Se esperaba un identificador después de EN."));
			expectLexeme(";", "Se esperaba ';' al final de la sentencia LEER.");
			return leer;
		}
		NodoSintactico imprimir = new NodoSintactico(TipoSintactico.IMPRIMIR);
		imprimir.agregarHijo(expectLexeme("IMPRIMIR", "Se esperaba IMPRIMIR al inicio de la sentencia."));
		imprimir.agregarHijo(parseListaImpr());
		expectLexeme(";", "Se esperaba ';' al final de la sentencia IMPRIMIR.");
		return imprimir;
	}

	private NodoSintactico parseListaImpr() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.LISTA_IMPRIMIR);
		lista.agregarHijo(parseImprItem());
		while (matchLexeme(",")) {
			lista.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, ",", currentToken().getRow(),
					currentToken().getColumn()));
			lista.agregarHijo(parseImprItem());
		}
		return lista;
	}

	private NodoSintactico parseImprItem() {
		if (matchLexeme("ENTER")) {
			return new NodoSintactico(TipoSintactico.TERMINAL, "ENTER", currentToken().getRow(),
					currentToken().getColumn());
		}
		return parseExpr();
	}

	private NodoSintactico parseCondicional() {
		NodoSintactico condicional = new NodoSintactico(TipoSintactico.CONDICIONAL);
		condicional.agregarHijo(expectLexeme("SI", "Se esperaba SI al inicio de la condicional."));
		condicional.agregarHijo(parseCond());
		expectLexeme("ENTONCES", "Se esperaba ENTONCES después de la condición.");
		condicional.agregarHijo(parseSentencias());
		condicional.agregarHijo(parseRamaElse());
		expectLexeme("FINSI", "Se esperaba FINSI al final de la condicional.");
		expectLexeme(";", "Se esperaba ';' después de FINSI.");
		return condicional;
	}

	private NodoSintactico parseRamaElse() {
		if (matchLexeme("SINO")) {
			NodoSintactico rama = new NodoSintactico(TipoSintactico.RAMA_ELSE);
			rama.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "SINO", currentToken().getRow(),
					currentToken().getColumn()));
			expectLexeme("ENTONCES", "Se esperaba ENTONCES después de SINO.");
			rama.agregarHijo(parseSentencias());
			expectLexeme("FINSINO", "Se esperaba FINSINO al final de la rama SINO.");
			expectLexeme(";", "Se esperaba ';' después de FINSINO.");
			return rama;
		}
		return new NodoSintactico(TipoSintactico.RAMA_ELSE);
	}

	private NodoSintactico parseWhile() {
		NodoSintactico ciclo = new NodoSintactico(TipoSintactico.WHILE);
		ciclo.agregarHijo(expectLexeme("MIENTRAS", "Se esperaba MIENTRAS al inicio del ciclo."));
		ciclo.agregarHijo(parseCond());
		expectLexeme("HACER", "Se esperaba HACER después de la condición del ciclo.");
		ciclo.agregarHijo(parseSentencias());
		expectLexeme("FINMIENTRAS", "Se esperaba FINMIENTRAS al final del ciclo.");
		expectLexeme(";", "Se esperaba ';' después de FINMIENTRAS.");
		return ciclo;
	}

	private NodoSintactico parsePara() {
		NodoSintactico para = new NodoSintactico(TipoSintactico.PARA);
		para.agregarHijo(expectLexeme("PARA", "Se esperaba PARA al inicio del ciclo para."));
		expectLexeme("(", "Se esperaba '(' después de PARA.");
		para.agregarHijo(parseDecl());
		expectLexeme(")", "Se esperaba ')' después de la declaración inicial del ciclo PARA.");
		expectLexeme("HASTA", "Se esperaba HASTA después de la declaración del ciclo PARA.");
		para.agregarHijo(parseCond());
		para.agregarHijo(parsePaso());
		expectLexeme("HACER", "Se esperaba HACER antes del cuerpo del ciclo PARA.");
		para.agregarHijo(parseSentencias());
		expectLexeme("FINPARA", "Se esperaba FINPARA al final del ciclo.");
		expectLexeme(";", "Se esperaba ';' después de FINPARA.");
		return para;
	}

	private NodoSintactico parsePaso() {
		if (matchLexeme("INCREMENTA") || matchLexeme("DECREMENTA")) {
			RegistroLexico operador = previousToken();
			NodoSintactico paso = new NodoSintactico(TipoSintactico.PASO);
			paso.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn()));
			expectLexeme("(", "Se esperaba '(' después de " + operador.getLexema().getValor() + ".");
			RegistroLexico numero = expectTokenType(Token.NUMERO_ENTERO,
					"Se esperaba un número entero en el paso de incremento/decremento.");
			paso.agregarHijo(new NodoSintactico(TipoSintactico.NUM_ENTERO, numero.getLexema().getValor(),
					numero.getRow(), numero.getColumn()));
			expectLexeme(")", "Se esperaba ')' después del número en el paso de incremento/decremento.");
			return paso;
		}
		error("Se esperaba INCREMENTA o DECREMENTA en el paso del ciclo PARA.");
		advance();
		return new NodoSintactico(TipoSintactico.PASO_ERRONEO);
	}

	private NodoSintactico parseCond() {
		expectLexeme("(", "Se esperaba '(' al inicio de la condición.");
		NodoSintactico condicion = parseExpr();
		expectLexeme(")", "Se esperaba ')' al final de la condición.");
		if (!isCondicional(condicion)) {
			error("La condición debe ser una expresión relacional o lógica.");
		}
		NodoSintactico nodoCond = new NodoSintactico(TipoSintactico.CONDICIONAL);
		nodoCond.agregarHijo(condicion);
		return nodoCond;
	}

	private boolean isCondicional(NodoSintactico expr) {
		if (expr == null) {
			return false;
		}
		TipoSintactico tipo = expr.getTipo();
		return tipo.equals(TipoSintactico.OP_RELACIONAL) || tipo.equals(TipoSintactico.OP_LOGICO)
				|| tipo.equals(TipoSintactico.OP_NOT);
	}

	private NodoSintactico parseExpr() {
		return parseLogica();
	}

	private NodoSintactico parseLogica() {
		NodoSintactico izquierda = parseRelacional();
		while (matchLexeme("AND") || matchLexeme("OR")) {
			RegistroLexico operador = previousToken();
			NodoSintactico raizLog = new NodoSintactico(TipoSintactico.OP_LOGICO, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			raizLog.agregarHijo(izquierda);
			raizLog.agregarHijo(parseRelacional());
			izquierda = raizLog;
		}
		return izquierda;
	}

	private NodoSintactico parseRelacional() {
		NodoSintactico izquierda = parseAritmetica();
		while (matchLexeme("==") || matchLexeme("!=") || matchLexeme("<") || matchLexeme(">") || matchLexeme("<=")
				|| matchLexeme(">=")) {
			RegistroLexico operador = previousToken();
			NodoSintactico raizRel = new NodoSintactico(TipoSintactico.OP_RELACIONAL, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			raizRel.agregarHijo(izquierda);
			raizRel.agregarHijo(parseAritmetica());
			izquierda = raizRel;
		}
		return izquierda;
	}

	private NodoSintactico parseAritmetica() {
		NodoSintactico izquierda = parseTermino();
		while (matchLexeme("+") || matchLexeme("-")) {
			RegistroLexico operador = previousToken();
			NodoSintactico raizArit = new NodoSintactico(TipoSintactico.OP_ARITMETICO, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			raizArit.agregarHijo(izquierda);
			raizArit.agregarHijo(parseTermino());
			izquierda = raizArit;
		}
		return izquierda;
	}

	private NodoSintactico parseTermino() {
		NodoSintactico izquierda = parseFactor();
		while (matchLexeme("*") || matchLexeme("/") || matchLexeme("^")) {
			RegistroLexico operador = previousToken();
			NodoSintactico raiz = new NodoSintactico(TipoSintactico.OP_ARITMETICO, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			raiz.agregarHijo(izquierda);
			raiz.agregarHijo(parseFactor());
			izquierda = raiz;
		}
		return izquierda;
	}

	private NodoSintactico parseFactor() {
		if (matchLexeme("NOT")) {
			RegistroLexico operador = previousToken();
			NodoSintactico not = new NodoSintactico(TipoSintactico.OP_NOT, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			expectLexeme("(", "Se esperaba '(' después de NOT.");
			not.agregarHijo(parseExpr());
			expectLexeme(")", "Se esperaba ')' después de la expresión de NOT.");
			return not;
		}

		if (matchLexeme("(")) {
			NodoSintactico expresion = parseExpr();
			expectLexeme(")", "Se esperaba ')' después de la expresión entre paréntesis.");
			return expresion;
		}

		return parseTerminal();
	}

	private NodoSintactico parseTerminal() {
		RegistroLexico token = currentToken();
		if (token == null) {
			error("Expresión incompleta.");
			return new NodoSintactico(TipoSintactico.TERMINAL_INVALIDO);
		}

		if (token.getToken() == Token.VARIABLE) {
			advance();
			if (!automVariables.simulate(token.getLexema().getValor())) {
				error(String.format("Identificador inválido '%s' en %d:%d.", token.getLexema().getValor(),
						token.getRow(), token.getColumn()));
			}
			return new NodoSintactico(TipoSintactico.VARIABLE, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}

		if (token.getToken() == Token.NUMERO_ENTERO || token.getToken() == Token.NUMERO_REAL) {
			advance();
			int result = automNumeros.simulate(token.getLexema().getValor(), true);
			if (result == -1) {
				error(String.format("Número inválido '%s' en %d:%d.", token.getLexema().getValor(), token.getRow(),
						token.getColumn()));
			}
			return new NodoSintactico(
					token.getToken() == Token.NUMERO_ENTERO ? TipoSintactico.NUM_ENTERO : TipoSintactico.NUM_REAL,
					token.getLexema().getValor(), token.getRow(), token.getColumn());
		}

		if (token.getToken() == Token.CADENA) {
			advance();
			if (!automCadenas.simulate(token.getLexema().getValor())) {
				error(String.format("Cadena inválida '%s' en %d:%d.", token.getLexema().getValor(), token.getRow(),
						token.getColumn()));
			}
			return new NodoSintactico(TipoSintactico.CADENA, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}

		if (token.getToken() == Token.PALABRA_RESERVADA) {
			String lexema = token.getLexema().getValor();
			if (lexema.equals("VERDADERO") || lexema.equals("FALSO")) {
				advance();
				return new NodoSintactico(TipoSintactico.BOOLEANO, lexema, token.getRow(), token.getColumn());
			}
		}

		error(String.format("Token inesperado en expresión: '%s' en %d:%d.", token.getLexema().getValor(),
				token.getRow(), token.getColumn()));
		advance();
		return new NodoSintactico(TipoSintactico.TERMINAL_INVALIDO);
	}

	private RegistroLexico expectTokenType(Token tipo, String mensaje) {
		if (!isAtEnd() && currentToken().getToken() == tipo) {
			return advance();
		}
		error(mensaje);
		return createErrorToken(mensaje);
	}

	private NodoSintactico parseVariable(String mensajeError) {
		if (!isAtEnd() && currentToken().getToken() == Token.VARIABLE) {
			RegistroLexico token = advance();
			if (!automVariables.simulate(token.getLexema().getValor())) {
				error(String.format("Identificador inválido '%s' en %d:%d.", token.getLexema().getValor(),
						token.getRow(), token.getColumn()));
			}
			return new NodoSintactico(TipoSintactico.VARIABLE, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}
		error(mensajeError);
		return new NodoSintactico(TipoSintactico.VARIABLE_ERRONEA);
	}

	private NodoSintactico expectLexeme(String lexemaEsperado, String mensajeError) {
		if (!isAtEnd() && currentLexeme().equals(lexemaEsperado)) {
			RegistroLexico token = advance();
			return new NodoSintactico(TipoSintactico.TERMINAL, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}
		error(mensajeError);
		return new NodoSintactico(TipoSintactico.TERMINAL_ERRONEO, lexemaEsperado, currentRow(), currentColumn());
	}

	private boolean matchLexeme(String lexemaEsperado) {
		if (!isAtEnd() && currentLexeme().equals(lexemaEsperado)) {
			advance();
			return true;
		}
		return false;
	}

	private RegistroLexico advance() {
		if (!isAtEnd()) {
			return tokens.get(posicion++);
		}
		return null;
	}

	private boolean isAtEnd() {
		return posicion >= tokens.size();
	}

	private RegistroLexico currentToken() {
		if (isAtEnd()) {
			return null;
		}
		return tokens.get(posicion);
	}

	private RegistroLexico previousToken() {
		if (posicion == 0) {
			return null;
		}
		return tokens.get(posicion - 1);
	}

	private String currentLexeme() {
		return isAtEnd() ? "" : currentToken().getLexema().getValor();
	}

	private int currentRow() {
		return isAtEnd() ? -1 : currentToken().getRow();
	}

	private int currentColumn() {
		return isAtEnd() ? -1 : currentToken().getColumn();
	}

	private void error(String mensaje) {
		if (!isAtEnd()) {
			errores.add(String.format("Error sintáctico en %d:%d -> %s", currentRow(), currentColumn(), mensaje));
			synchronize();
		} else {
			errores.add("Error sintáctico: " + mensaje);
		}
	}

	private void synchronize() {
		while (!isAtEnd()) {
			if (currentLexeme().equals(";") || currentLexeme().equals("FIN") || currentLexeme().equals("FINSI")
					|| currentLexeme().equals("FINSINO") || currentLexeme().equals("FINMIENTRAS")
					|| currentLexeme().equals("FINPARA") || currentLexeme().equals("SI")
					|| currentLexeme().equals("MIENTRAS")
					|| currentLexeme().equals("PARA") || currentLexeme().equals("DECLARAR")
					|| currentLexeme().equals("SOBREESCRIBIR") || currentLexeme().equals("LEER")
					|| currentLexeme().equals("IMPRIMIR")) {
				return;
			}
			advance();
		}
	}

	private RegistroLexico createErrorToken(String mensaje) {
		RegistroLexico fake = new RegistroLexico("<error>", currentRow(), currentColumn());
		fake.setToken(Token.ERROR_LEXICO);
		fake.setId(-1);
		errores.add(mensaje);
		return fake;
	}
}
