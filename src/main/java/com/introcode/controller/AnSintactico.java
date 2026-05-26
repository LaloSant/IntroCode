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
	private final ACadenas automCadenas = new ACadenas();

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

		if (!estaAlFinal()) {
			RegistroLexico token = tokenActual();
			errores.add(String.format("Token inesperado al final: '%s' en %d:%d.", token.getLexema().getValor(),
					token.getRow(), token.getColumn()));
		}

		return new ResultadoSintactico(raiz, errores);
	}

	private NodoSintactico parsePrograma() {
		NodoSintactico programa = new NodoSintactico(TipoSintactico.PROGRAMA);
		programa.agregarHijo(esperarLexema("INICIO", "Se esperaba la palabra reservada INICIO al inicio del programa."));

		programa.agregarHijo(parseSentencias());

		programa.agregarHijo(esperarLexema("FIN", "Se esperaba la palabra reservada FIN al final del programa."));

		return programa;
	}

	private NodoSintactico parseSentencias() {
		NodoSintactico sentencias = new NodoSintactico(TipoSintactico.SENTENCIA);
		while (!estaAlFinal() && canStartSentencia()) {
			sentencias.agregarHijo(parseSentencia());
		}
		return sentencias;
	}

	private boolean canStartSentencia() {
		if (estaAlFinal()) {
			return false;
		}
		String lexema = lexemaActual();
		return lexema.equals("DECLARAR") || lexema.equals("SOBREESCRIBIR") || lexema.equals("LEER")
				|| lexema.equals("IMPRIMIR") || lexema.equals("SI") || lexema.equals("MIENTRAS")
				|| lexema.equals("PARA");
	}

	private NodoSintactico parseSentencia() {
		String lexema = lexemaActual();
		return switch (lexema) {
			case "DECLARAR" -> parseDecl();
			case "SOBREESCRIBIR" -> parseSobrescribir();
			case "LEER", "IMPRIMIR" -> parseIO();
			case "SI" -> parseCondicional();
			case "MIENTRAS" -> parseWhile();
			case "PARA" -> parsePara();
			default -> {
				error(String.format("Sentencia inválida o no reconocida: '%s' en %d:%d.", lexema,
						tokenActual().getRow(), tokenActual().getColumn()));
				avanzar();
				yield new NodoSintactico(TipoSintactico.SENTENCIA_ERRONEA);
			}
		};
	}

	private NodoSintactico parseDecl() {
		NodoSintactico decl = new NodoSintactico(TipoSintactico.DECLARACION);
		NodoSintactico inicio = esperarLexema("DECLARAR", "Se esperaba DECLARAR en la declaración.");
		decl.agregarHijo(inicio);
		decl.agregarHijo(parseTipo());
		decl.agregarHijo(parseVariable("Se esperaba un identificador después del tipo."));
		decl.agregarHijo(parseValOpt());
		esperarLexema(";", "Se esperaba ';' al final de la declaración.");
		return decl;
	}

	private NodoSintactico parseTipo() {
		return switch (lexemaActual()) {
			case "ENTERO", "REAL", "CADENA", "BOOLEANO" -> {
				RegistroLexico token = avanzar();
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
		if (matchLexema("=")) {
			NodoSintactico valOpt = new NodoSintactico(TipoSintactico.VALOR_OPCIONAL);
			valOpt.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "=", tokenActual().getRow(),
					tokenActual().getColumn()));
			valOpt.agregarHijo(parseExpresion());
			return valOpt;
		}
		return new NodoSintactico(TipoSintactico.VALOR_OPCIONAL);
	}

	private NodoSintactico parseSobrescribir() {
		NodoSintactico sobrescr = new NodoSintactico(TipoSintactico.SOBREESCRIBIR);
		sobrescr.agregarHijo(esperarLexema("SOBREESCRIBIR", "Se esperaba SOBREESCRIBIR al inicio de la sentencia."));
		sobrescr.agregarHijo(parseVariable("Se esperaba un identificador después de SOBREESCRIBIR."));
		esperarLexema("=", "Se esperaba el operador de asignación '=' en la sobrescritura.");
		sobrescr.agregarHijo(parseExpresion());
		esperarLexema(";", "Se esperaba ';' al final de la sobrescritura.");
		return sobrescr;
	}

	private NodoSintactico parseIO() {
		if (matchLexema("LEER")) {
			NodoSintactico leer = new NodoSintactico(TipoSintactico.LEER);
			leer.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "LEER", tokenActual().getRow(),
					tokenActual().getColumn()));
			esperarLexema("EN", "Se esperaba EN después de LEER.");
			leer.agregarHijo(parseVariable("Se esperaba un identificador después de EN."));
			esperarLexema(";", "Se esperaba ';' al final de la sentencia LEER.");
			return leer;
		}
		NodoSintactico imprimir = new NodoSintactico(TipoSintactico.IMPRIMIR);
		imprimir.agregarHijo(esperarLexema("IMPRIMIR", "Se esperaba IMPRIMIR al inicio de la sentencia."));
		imprimir.agregarHijo(parseListaImpr());
		esperarLexema(";", "Se esperaba ';' al final de la sentencia IMPRIMIR.");
		return imprimir;
	}

	private NodoSintactico parseListaImpr() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.LISTA_IMPRIMIR);
		lista.agregarHijo(parseImprItem());
		while (matchLexema(",")) {
			lista.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, ",", tokenActual().getRow(),
					tokenActual().getColumn()));
			lista.agregarHijo(parseImprItem());
		}
		return lista;
	}

	private NodoSintactico parseImprItem() {
		if (matchLexema("ENTER")) {
			return new NodoSintactico(TipoSintactico.TERMINAL, "ENTER", tokenActual().getRow(),
					tokenActual().getColumn());
		}
		return parseExpresion();
	}

	private NodoSintactico parseCondicional() {
		NodoSintactico condicional = new NodoSintactico(TipoSintactico.CONDICIONAL);
		condicional.agregarHijo(esperarLexema("SI", "Se esperaba SI al inicio de la condicional."));
		condicional.agregarHijo(parseCondicion());
		esperarLexema("ENTONCES", "Se esperaba ENTONCES después de la condición.");
		condicional.agregarHijo(parseSentencias());
		condicional.agregarHijo(parseRamaElse());
		esperarLexema("FINSI", "Se esperaba FINSI al final de la condicional.");
		esperarLexema(";", "Se esperaba ';' después de FINSI.");
		return condicional;
	}

	private NodoSintactico parseRamaElse() {
		if (matchLexema("SINO")) {
			NodoSintactico rama = new NodoSintactico(TipoSintactico.RAMA_ELSE);
			rama.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "SINO", tokenActual().getRow(),
					tokenActual().getColumn()));
			esperarLexema("ENTONCES", "Se esperaba ENTONCES después de SINO.");
			rama.agregarHijo(parseSentencias());
			esperarLexema("FINSINO", "Se esperaba FINSINO al final de la rama SINO.");
			esperarLexema(";", "Se esperaba ';' después de FINSINO.");
			return rama;
		}
		return new NodoSintactico(TipoSintactico.RAMA_ELSE);
	}

	private NodoSintactico parseWhile() {
		NodoSintactico ciclo = new NodoSintactico(TipoSintactico.WHILE);
		ciclo.agregarHijo(esperarLexema("MIENTRAS", "Se esperaba MIENTRAS al inicio del ciclo."));
		ciclo.agregarHijo(parseCondicion());
		esperarLexema("HACER", "Se esperaba HACER después de la condición del ciclo.");
		ciclo.agregarHijo(parseSentencias());
		esperarLexema("FINMIENTRAS", "Se esperaba FINMIENTRAS al final del ciclo.");
		esperarLexema(";", "Se esperaba ';' después de FINMIENTRAS.");
		return ciclo;
	}

	private NodoSintactico parsePara() {
		NodoSintactico para = new NodoSintactico(TipoSintactico.PARA);
		para.agregarHijo(esperarLexema("PARA", "Se esperaba PARA al inicio del ciclo para."));
		esperarLexema("(", "Se esperaba '(' después de PARA.");
		para.agregarHijo(parseDecl());
		esperarLexema(")", "Se esperaba ')' después de la declaración inicial del ciclo PARA.");
		esperarLexema("HASTA", "Se esperaba HASTA después de la declaración del ciclo PARA.");
		para.agregarHijo(parseCondicion());
		para.agregarHijo(parsePaso());
		esperarLexema("HACER", "Se esperaba HACER antes del cuerpo del ciclo PARA.");
		para.agregarHijo(parseSentencias());
		esperarLexema("FINPARA", "Se esperaba FINPARA al final del ciclo.");
		esperarLexema(";", "Se esperaba ';' después de FINPARA.");
		return para;
	}

	private NodoSintactico parsePaso() {
		if (matchLexema("INCREMENTA") || matchLexema("DECREMENTA")) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico paso = new NodoSintactico(TipoSintactico.PASO);
			paso.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn()));
			esperarLexema("(", "Se esperaba '(' después de " + operador.getLexema().getValor() + ".");
			RegistroLexico numero = esperarTipoToken(Token.NUMERO_ENTERO,
					"Se esperaba un número entero en el paso de incremento/decremento.");
			paso.agregarHijo(new NodoSintactico(TipoSintactico.NUM_ENTERO, numero.getLexema().getValor(),
					numero.getRow(), numero.getColumn()));
			esperarLexema(")", "Se esperaba ')' después del número en el paso de incremento/decremento.");
			return paso;
		}
		error("Se esperaba INCREMENTA o DECREMENTA en el paso del ciclo PARA.");
		avanzar();
		return new NodoSintactico(TipoSintactico.PASO_ERRONEO);
	}

	private NodoSintactico parseCondicion() {
		esperarLexema("(", "Se esperaba '(' al inicio de la condición.");
		NodoSintactico condicion = parseExpresion();
		esperarLexema(")", "Se esperaba ')' al final de la condición.");
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

	private NodoSintactico parseExpresion() {
		return parseLogica();
	}

	private NodoSintactico parseLogica() {
		NodoSintactico izquierda = parseRelacional();
		while (matchLexema("AND") || matchLexema("OR")) {
			RegistroLexico operador = tokenAnterior();
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
		while (matchLexema("==") || matchLexema("!=") || matchLexema("<") || matchLexema(">") || matchLexema("<=")
				|| matchLexema(">=")) {
			RegistroLexico operador = tokenAnterior();
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
		while (matchLexema("+") || matchLexema("-")) {
			RegistroLexico operador = tokenAnterior();
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
		while (matchLexema("*") || matchLexema("/") || matchLexema("^")) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico raiz = new NodoSintactico(TipoSintactico.OP_ARITMETICO, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			raiz.agregarHijo(izquierda);
			raiz.agregarHijo(parseFactor());
			izquierda = raiz;
		}
		return izquierda;
	}

	private NodoSintactico parseFactor() {
		if (matchLexema("NOT")) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico not = new NodoSintactico(TipoSintactico.OP_NOT, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			esperarLexema("(", "Se esperaba '(' después de NOT.");
			not.agregarHijo(parseExpresion());
			esperarLexema(")", "Se esperaba ')' después de la expresión de NOT.");
			return not;
		}

		if (matchLexema("(")) {
			NodoSintactico expresion = parseExpresion();
			esperarLexema(")", "Se esperaba ')' después de la expresión entre paréntesis.");
			return expresion;
		}

		return parseTerminal();
	}

	private NodoSintactico parseTerminal() {
		RegistroLexico token = tokenActual();
		if (token == null) {
			error("Expresión incompleta.");
			return new NodoSintactico(TipoSintactico.TERMINAL_INVALIDO);
		}

		if (token.getToken() == Token.VARIABLE) {
			avanzar();
			if (!automVariables.simulate(token.getLexema().getValor())) {
				error(String.format("Identificador inválido '%s' en %d:%d.", token.getLexema().getValor(),
						token.getRow(), token.getColumn()));
			}
			return new NodoSintactico(TipoSintactico.VARIABLE, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}

		if (token.getToken() == Token.NUMERO_ENTERO || token.getToken() == Token.NUMERO_REAL) {
			avanzar();
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
			avanzar();
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
				avanzar();
				return new NodoSintactico(TipoSintactico.BOOLEANO, lexema, token.getRow(), token.getColumn());
			}
		}

		error(String.format("Token inesperado en expresión: '%s' en %d:%d.", token.getLexema().getValor(),
				token.getRow(), token.getColumn()));
		avanzar();
		return new NodoSintactico(TipoSintactico.TERMINAL_INVALIDO);
	}

	private RegistroLexico esperarTipoToken(Token tipo, String mensaje) {
		if (!estaAlFinal() && tokenActual().getToken() == tipo) {
			return avanzar();
		}
		error(mensaje);
		return crearTokenError(mensaje);
	}

	private NodoSintactico parseVariable(String mensajeError) {
		if (!estaAlFinal() && tokenActual().getToken() == Token.VARIABLE) {
			RegistroLexico token = avanzar();
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

	private NodoSintactico esperarLexema(String lexemaEsperado, String mensajeError) {
		if (!estaAlFinal() && lexemaActual().equals(lexemaEsperado)) {
			RegistroLexico token = avanzar();
			return new NodoSintactico(TipoSintactico.TERMINAL, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}
		error(mensajeError);
		return new NodoSintactico(TipoSintactico.TERMINAL_ERRONEO, lexemaEsperado, currentRow(), currentColumn());
	}

	private boolean matchLexema(String lexemaEsperado) {
		if (!estaAlFinal() && lexemaActual().equals(lexemaEsperado)) {
			avanzar();
			return true;
		}
		return false;
	}

	private RegistroLexico avanzar() {
		if (!estaAlFinal()) {
			return tokens.get(posicion++);
		}
		return null;
	}

	private boolean estaAlFinal() {
		return posicion >= tokens.size();
	}

	private RegistroLexico tokenActual() {
		if (estaAlFinal()) {
			return null;
		}
		return tokens.get(posicion);
	}

	private RegistroLexico tokenAnterior() {
		if (posicion == 0) {
			return null;
		}
		return tokens.get(posicion - 1);
	}

	private String lexemaActual() {
		return estaAlFinal() ? "" : tokenActual().getLexema().getValor();
	}

	private int currentRow() {
		return estaAlFinal() ? -1 : tokenActual().getRow();
	}

	private int currentColumn() {
		return estaAlFinal() ? -1 : tokenActual().getColumn();
	}

	private void error(String mensaje) {
		if (!estaAlFinal()) {
			errores.add(String.format("Error sintáctico en %d:%d -> %s", currentRow(), currentColumn(), mensaje));
			sincronizar();
		} else {
			errores.add("Error sintáctico: " + mensaje);
		}
	}

	private void sincronizar() {
		while (!estaAlFinal()) {
			if (lexemaActual().equals(";") || lexemaActual().equals("FIN") || lexemaActual().equals("FINSI")
					|| lexemaActual().equals("FINSINO") || lexemaActual().equals("FINMIENTRAS")
					|| lexemaActual().equals("FINPARA") || lexemaActual().equals("SI")
					|| lexemaActual().equals("MIENTRAS")
					|| lexemaActual().equals("PARA") || lexemaActual().equals("DECLARAR")
					|| lexemaActual().equals("SOBREESCRIBIR") || lexemaActual().equals("LEER")
					|| lexemaActual().equals("IMPRIMIR")) {
				return;
			}
			avanzar();
		}
	}

	private RegistroLexico crearTokenError(String mensaje) {
		RegistroLexico fake = new RegistroLexico("<error>", currentRow(), currentColumn());
		fake.setToken(Token.ERROR_LEXICO);
		fake.setId(-1);
		errores.add(mensaje);
		return fake;
	}
}
