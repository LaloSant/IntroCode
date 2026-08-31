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
		programa.agregarHijo(
				esperarLexema(1, "Se esperaba la palabra reservada INICIO al inicio del programa."));

		programa.agregarHijo(parseSentencias());

		programa.agregarHijo(esperarLexema(2, "Se esperaba la palabra reservada FIN al final del programa."));

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
		int idLexema = idLexemaActual();
		return idLexema == 7 || idLexema == 8 || idLexema == 3
				|| idLexema == 5 || idLexema == 15 || idLexema == 20
				|| idLexema == 23;
	}

	private NodoSintactico parseSentencia() {
		int idLexema = idLexemaActual();
		return switch (idLexema) {
			case 7 -> parseDecl();
			case 8 -> parseSobrescribir();
			case 3, 5 -> parseIO();
			case 15 -> parseCondicional();
			case 20 -> parseWhile();
			case 23 -> parsePara();
			default -> {
				error(String.format("Sentencia inválida o no reconocida: '%s' en %d:%d.", idLexema,
						tokenActual().getRow(), tokenActual().getColumn()));
				avanzar();
				yield new NodoSintactico(TipoSintactico.SENTENCIA_ERRONEA);
			}
		};
	}

	private NodoSintactico parseDecl() {
		NodoSintactico decl = new NodoSintactico(TipoSintactico.DECLARACION);
		NodoSintactico inicio = esperarLexema(7, "Se esperaba DECLARAR en la declaración.");
		decl.agregarHijo(inicio);
		decl.agregarHijo(parseTipo());
		decl.agregarHijo(parseVariable("Se esperaba un identificador después del tipo."));
		decl.agregarHijo(parseValOpt());
		esperarLexema(43, "Se esperaba ';' al final de la declaración.");
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
		if (matchLexema(42)) {
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
		sobrescr.agregarHijo(esperarLexema(8, "Se esperaba SOBREESCRIBIR al inicio de la sentencia."));
		sobrescr.agregarHijo(parseVariable("Se esperaba un identificador después de SOBREESCRIBIR."));
		esperarLexema(42, "Se esperaba el operador de asignación '=' en la sobrescritura.");
		sobrescr.agregarHijo(parseExpresion());
		esperarLexema(43, "Se esperaba ';' al final de la sobrescritura.");
		return sobrescr;
	}

	private NodoSintactico parseIO() {
		if (matchLexema(3)) {
			NodoSintactico leer = new NodoSintactico(TipoSintactico.LEER);
			leer.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "LEER", tokenActual().getRow(),
					tokenActual().getColumn()));
			esperarLexema(4, "Se esperaba EN después de LEER.");
			leer.agregarHijo(parseVariable("Se esperaba un identificador después de EN."));
			esperarLexema(43, "Se esperaba ';' al final de la sentencia LEER.");
			return leer;
		}
		NodoSintactico imprimir = new NodoSintactico(TipoSintactico.IMPRIMIR);
		imprimir.agregarHijo(esperarLexema(5, "Se esperaba IMPRIMIR al inicio de la sentencia."));
		imprimir.agregarHijo(parseListaImpr());
		esperarLexema(43, "Se esperaba ';' al final de la sentencia IMPRIMIR.");
		return imprimir;
	}

	private NodoSintactico parseListaImpr() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.LISTA_IMPRIMIR);
		lista.agregarHijo(parseImprItem());
		while (matchLexema(46)) {
			lista.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, ",", tokenActual().getRow(),
					tokenActual().getColumn()));
			lista.agregarHijo(parseImprItem());
		}
		return lista;
	}

	private NodoSintactico parseImprItem() {
		if (matchLexema(6)) {
			return new NodoSintactico(TipoSintactico.TERMINAL, "ENTER", tokenActual().getRow(),
					tokenActual().getColumn());
		}
		return parseExpresion();
	}

	private NodoSintactico parseCondicional() {
		NodoSintactico condicional = new NodoSintactico(TipoSintactico.CONDICIONAL);
		condicional.agregarHijo(esperarLexema(15, "Se esperaba SI al inicio de la condicional."));
		condicional.agregarHijo(parseCondicion());
		esperarLexema(16, "Se esperaba ENTONCES después de la condición.");
		condicional.agregarHijo(parseSentencias());
		condicional.agregarHijo(parseRamaElse());
		esperarLexema(18, "Se esperaba FINSI al final de la condicional.");
		esperarLexema(43, "Se esperaba ';' después de FINSI.");
		return condicional;
	}

	private NodoSintactico parseRamaElse() {
		if (matchLexema(17)) {
			NodoSintactico rama = new NodoSintactico(TipoSintactico.RAMA_ELSE);
			rama.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "SINO", tokenActual().getRow(),
					tokenActual().getColumn()));
			esperarLexema(16, "Se esperaba ENTONCES después de SINO.");
			rama.agregarHijo(parseSentencias());
			esperarLexema(19, "Se esperaba FINSINO al final de la rama SINO.");
			esperarLexema(43, "Se esperaba ';' después de FINSINO.");
			return rama;
		}
		return new NodoSintactico(TipoSintactico.RAMA_ELSE);
	}

	private NodoSintactico parseWhile() {
		NodoSintactico ciclo = new NodoSintactico(TipoSintactico.WHILE);
		ciclo.agregarHijo(esperarLexema(20, "Se esperaba MIENTRAS al inicio del ciclo."));
		ciclo.agregarHijo(parseCondicion());
		esperarLexema(21, "Se esperaba HACER después de la condición del ciclo.");
		ciclo.agregarHijo(parseSentencias());
		esperarLexema(22, "Se esperaba FINMIENTRAS al final del ciclo.");
		esperarLexema(43, "Se esperaba ';' después de FINMIENTRAS.");
		return ciclo;
	}

	private NodoSintactico parsePara() {
		NodoSintactico para = new NodoSintactico(TipoSintactico.PARA);
		para.agregarHijo(esperarLexema(23, "Se esperaba PARA al inicio del ciclo para."));
		esperarLexema(44, "Se esperaba '(' después de PARA.");
		para.agregarHijo(parseDecl());
		esperarLexema(45, "Se esperaba ')' después de la declaración inicial del ciclo PARA.");
		esperarLexema(24, "Se esperaba HASTA después de la declaración del ciclo PARA.");
		para.agregarHijo(parseCondicion());
		para.agregarHijo(parsePaso());
		esperarLexema(21, "Se esperaba HACER antes del cuerpo del ciclo PARA.");
		para.agregarHijo(parseSentencias());
		esperarLexema(27, "Se esperaba FINPARA al final del ciclo.");
		esperarLexema(43, "Se esperaba ';' después de FINPARA.");
		return para;
	}

	private NodoSintactico parsePaso() {
		if (matchLexema(25) || matchLexema(26)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico paso = new NodoSintactico(TipoSintactico.PASO);
			paso.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn()));
			esperarLexema(44, "Se esperaba '(' después de " + operador.getLexema().getValor() + ".");
			RegistroLexico numero = esperarTipoToken(Token.NUMERO,
					"Se esperaba un número entero en el paso de incremento/decremento.");
			paso.agregarHijo(new NodoSintactico(TipoSintactico.NUM_ENTERO, numero.getLexema().getValor(),
					numero.getRow(), numero.getColumn()));
			esperarLexema(45, "Se esperaba ')' después del número en el paso de incremento/decremento.");
			return paso;
		}
		error("Se esperaba INCREMENTA o DECREMENTA en el paso del ciclo PARA.");
		avanzar();
		return new NodoSintactico(TipoSintactico.PASO_ERRONEO);
	}

	private NodoSintactico parseCondicion() {
		esperarLexema(44, "Se esperaba '(' al inicio de la condición.");
		NodoSintactico condicion = parseExpresion();
		esperarLexema(45, "Se esperaba ')' al final de la condición.");
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
		while (matchLexema(39) || matchLexema(40)) {
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
		while (matchLexema(33) || matchLexema(34) || matchLexema(35) || matchLexema(36) || matchLexema(37)
				|| matchLexema(38)) {
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
		while (matchLexema(28) || matchLexema(29)) {
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
		while (matchLexema(30) || matchLexema(31) || matchLexema(32)) {
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
		if (matchLexema(41)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico not = new NodoSintactico(TipoSintactico.OP_NOT, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			esperarLexema(44, "Se esperaba '(' después de NOT.");
			not.agregarHijo(parseExpresion());
			esperarLexema(45, "Se esperaba ')' después de la expresión de NOT.");
			return not;
		}

		if (matchLexema(44)) {
			NodoSintactico expresion = parseExpresion();
			esperarLexema(45, "Se esperaba ')' después de la expresión entre paréntesis.");
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

		if (token.getToken() == Token.NUMERO) {
			avanzar();
			int result = automNumeros.simulate(token.getLexema().getValor(), true);
			if (result == -1) {
				error(String.format("Número inválido '%s' en %d:%d.", token.getLexema().getValor(), token.getRow(),
						token.getColumn()));
			}
			return new NodoSintactico(
					token.getToken() == Token.NUMERO ? TipoSintactico.NUM_ENTERO : TipoSintactico.NUM_REAL,
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
			int idLexema = token.getId();
			if (idLexema == 13 || idLexema == 14) {
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

	private NodoSintactico esperarLexema(int idLexemaEsperado, String mensajeError) {
		if (!estaAlFinal() && idLexemaActual() == idLexemaEsperado) {
			RegistroLexico token = avanzar();
			return new NodoSintactico(TipoSintactico.TERMINAL, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}
		error(mensajeError);
		return new NodoSintactico(TipoSintactico.TERMINAL_ERRONEO, String.valueOf(idLexemaEsperado), currentRow(),
				currentColumn());
	}

	private boolean matchLexema(int idEsperado) {
		if (!estaAlFinal() && idLexemaActual() == idEsperado) {
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

	private int idLexemaActual() {
		return estaAlFinal() ? -1 : tokenActual().getId();
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
			if (idLexemaActual() == 43 || idLexemaActual() == 2 || idLexemaActual() == 18
					|| idLexemaActual() == 19 || idLexemaActual() == 22
					|| idLexemaActual() == 27 || idLexemaActual() == 15
					|| idLexemaActual() == 20
					|| idLexemaActual() == 23 || idLexemaActual() == 7
					|| idLexemaActual() == 8 || idLexemaActual() == 3
					|| idLexemaActual() == 5) {
				if (idLexemaActual() == 43) {
					avanzar();
				}
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
