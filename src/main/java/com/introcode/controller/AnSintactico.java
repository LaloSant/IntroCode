package com.introcode.controller;

import java.util.ArrayList;
import java.util.List;

import com.introcode.automatas.AVariables;
import com.introcode.entity.Alfabeto;
import com.introcode.entity.NodoSintactico;
import com.introcode.entity.RegistroLexico;
import com.introcode.entity.ResultadoSintactico;
import com.introcode.entity.TokenLexico;
import com.introcode.entity.NodoSintactico.TipoSintactico;

public class AnSintactico {

	private static final int LOCAL = 1;
	private static final int TRUE = 2;
	private static final int FALSE = 3;
	private static final int IF = 4;
	private static final int THEN = 5;
	private static final int ELSE = 6;
	private static final int ELSEIF = 7;
	private static final int END = 8;
	private static final int WHILE = 9;
	private static final int DO = 10;
	private static final int FOR = 11;
	private static final int REPEAT = 12;
	private static final int UNTIL = 13;
	private static final int AND = 14;
	private static final int OR = 15;
	private static final int NOT = 16;
	private static final int NIL = 17;
	private static final int MAS = 18;
	private static final int MENOS = 19;
	private static final int MULTIPLICACION = 20;
	private static final int DIVISION = 21;
	private static final int POTENCIA = 22;
	private static final int MODULO = 23;
	private static final int IGUAL_IGUAL = 24;
	private static final int DIFERENTE = 25;
	private static final int MENOR = 26;
	private static final int MAYOR = 27;
	private static final int MENOR_IGUAL = 28;
	private static final int MAYOR_IGUAL = 29;
	private static final int ASIGNACION = 30;
	private static final int PARENTESIS_ABRE = 31;
	private static final int PARENTESIS_CIERRA = 32;
	private static final int LLAVE_ABRE = 33;
	private static final int LLAVE_CIERRA = 34;
	private static final int COMA = 37;
	private static final int PUNTO_COMA = 38;
	private static final int PRINT = 41;

	private final List<RegistroLexico> tokens = new ArrayList<>();
	private final List<String> errores = new ArrayList<>();
	private int posicion;
	private NodoSintactico raiz;
	
	private final AVariables automVariables = new AVariables();

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

		while (!estaAlFinal()) {
			RegistroLexico token = tokenActual();
			errores.add(
					String.format(
							"Token inesperado al final: '%s' en %d:%d.",
							token.getLexema().getValor(),
							token.getRow(),
							token.getColumn()));
			avanzar();
		}

		if (!estaAlFinal()) {
			
		}

		return new ResultadoSintactico(raiz, errores);
	}

	private NodoSintactico parsePrograma() {
		NodoSintactico programa = new NodoSintactico(TipoSintactico.PROGRAMA);
		programa.agregarHijo(parseListaSentencias());
		return programa;
	}

	private NodoSintactico parseListaSentencias() {
		NodoSintactico secuencia = new NodoSintactico(TipoSintactico.SENTENCIA);
		while (!estaAlFinal() && puedeIniciarSentencia()) {
			secuencia.agregarHijo(parseSentencia());
			consumirPuntoComaOpcional();
		}
		return secuencia;
	}

	private boolean puedeIniciarSentencia() {
		if (estaAlFinal()) {
			return false;
		}
		int id = idLexemaActual();
		String lexema = lexemaActual();
		return id == LOCAL || id == IF || id == WHILE || id == FOR || id == REPEAT
				|| esVariable(lexema) || "print".equalsIgnoreCase(lexema);
	}

	private NodoSintactico parseSentencia() {
		if (estaAlFinal()) {
			error("Se esperaba una sentencia, pero no hay más tokens.");
			return new NodoSintactico(TipoSintactico.SENTENCIA_ERRONEA);
		}

		int id = idLexemaActual();
		String lexema = lexemaActual();

		if (id == LOCAL) {
			return parseDeclaracion();
		}
		if (id == IF) {
			return parseEstructuraIf();
		}
		if (id == WHILE) {
			return parseEstructuraWhile();
		}
		if (id == FOR) {
			return parseEstructuraFor();
		}
		if (id == REPEAT) {
			return parseEstructuraRepeat();
		}
		if ("print".equalsIgnoreCase(lexema)) {
			return parseLlamadaIO();
		}
		if (esVariable(lexema)) {
			return parseAsignacion();
		}

		error(String.format("Sentencia inválida o no reconocida: '%s' en %d:%d.", lexema, currentRow(),
				currentColumn()));
		avanzar();
		return new NodoSintactico(TipoSintactico.SENTENCIA_ERRONEA);
	}

	private NodoSintactico parseDeclaracion() {
		NodoSintactico decl = new NodoSintactico(TipoSintactico.DECLARACION);
		decl.agregarHijo(esperarLexema(LOCAL, "Se esperaba 'local'."));
		decl.agregarHijo(parseVariable("Se esperaba un identificador después de 'local'."));
		decl.agregarHijo(parseAsignacionOpcional());
		return decl;
	}

	private NodoSintactico parseAsignacionOpcional() {
		if (matchLexema(ASIGNACION)) {
			NodoSintactico valorOpcional = new NodoSintactico(TipoSintactico.VALOR_OPCIONAL);
			valorOpcional.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "=", currentRow(), currentColumn()));
			valorOpcional.agregarHijo(parseExpresion());
			return valorOpcional;
		}
		return new NodoSintactico(TipoSintactico.VALOR_OPCIONAL);
	}

	private NodoSintactico parseAsignacion() {
		NodoSintactico asignacion = new NodoSintactico(TipoSintactico.SOBREESCRIBIR);
		asignacion.agregarHijo(parseVariable("Se esperaba un identificador antes de la asignación."));
		asignacion.agregarHijo(esperarLexema(ASIGNACION, "Se esperaba '=' en la asignación."));
		asignacion.agregarHijo(parseExpresion());
		return asignacion;
	}

	private NodoSintactico parseEstructuraIf() {
		NodoSintactico condicional = new NodoSintactico(TipoSintactico.CONDICIONAL);
		condicional.agregarHijo(esperarLexema(IF, "Se esperaba 'if'."));
		condicional.agregarHijo(parseExpresion());
		condicional.agregarHijo(esperarLexema(THEN, "Se esperaba 'then' después de la condición."));
		condicional.agregarHijo(parseListaSentencias());
		condicional.agregarHijo(parseListaElseIf());
		condicional.agregarHijo(parseElseOpcional());
		condicional.agregarHijo(esperarLexema(END, "Se esperaba 'end' al final del bloque if."));
		return condicional;
	}

	private NodoSintactico parseListaElseIf() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.RAMA_ELSE);
		while (matchLexema(ELSEIF)) {
			NodoSintactico bloque = new NodoSintactico(TipoSintactico.CONDICIONAL);
			bloque.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "elseif", currentRow(), currentColumn()));
			bloque.agregarHijo(parseExpresion());
			bloque.agregarHijo(esperarLexema(THEN, "Se esperaba 'then' después de 'elseif'."));
			bloque.agregarHijo(parseListaSentencias());
			lista.agregarHijo(bloque);
		}
		return lista;
	}

	private NodoSintactico parseElseOpcional() {
		if (matchLexema(ELSE)) {
			NodoSintactico rama = new NodoSintactico(TipoSintactico.RAMA_ELSE);
			rama.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, "else", currentRow(), currentColumn()));
			rama.agregarHijo(parseListaSentencias());
			return rama;
		}
		return new NodoSintactico(TipoSintactico.RAMA_ELSE);
	}

	private NodoSintactico parseEstructuraWhile() {
		NodoSintactico ciclo = new NodoSintactico(TipoSintactico.WHILE);
		ciclo.agregarHijo(esperarLexema(WHILE, "Se esperaba 'while'."));
		ciclo.agregarHijo(parseExpresion());
		ciclo.agregarHijo(esperarLexema(DO, "Se esperaba 'do' después de la condición."));
		ciclo.agregarHijo(parseListaSentencias());
		ciclo.agregarHijo(esperarLexema(END, "Se esperaba 'end' al final del ciclo while."));
		return ciclo;
	}

	private NodoSintactico parseEstructuraRepeat() {
		NodoSintactico ciclo = new NodoSintactico(TipoSintactico.WHILE);
		ciclo.agregarHijo(esperarLexema(REPEAT, "Se esperaba 'repeat'."));
		ciclo.agregarHijo(parseListaSentencias());
		ciclo.agregarHijo(esperarLexema(UNTIL, "Se esperaba 'until' al final del bloque repeat."));
		ciclo.agregarHijo(parseExpresion());
		return ciclo;
	}

	private NodoSintactico parseEstructuraFor() {
		NodoSintactico para = new NodoSintactico(TipoSintactico.PARA);
		para.agregarHijo(esperarLexema(FOR, "Se esperaba 'for'."));
		para.agregarHijo(parseVariable("Se esperaba un identificador después de 'for'."));
		para.agregarHijo(esperarLexema(ASIGNACION, "Se esperaba '=' después del identificador del for."));
		para.agregarHijo(parseExpresion());
		para.agregarHijo(esperarLexema(COMA, "Se esperaba ',' después del inicio del for."));
		para.agregarHijo(parseExpresion());
		if (matchLexema(COMA)) {
			para.agregarHijo(parseExpresion());
		}
		para.agregarHijo(esperarLexema(DO, "Se esperaba 'do' después del rango del for."));
		para.agregarHijo(parseListaSentencias());
		para.agregarHijo(esperarLexema(END, "Se esperaba 'end' al final del for."));
		return para;
	}

	private NodoSintactico parseLlamadaIO() {
		NodoSintactico llamada = new NodoSintactico(TipoSintactico.IMPRIMIR);
		RegistroLexico token = tokenActual();
		avanzar();
		llamada.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, token.getLexema().getValor(),
				token.getRow(), token.getColumn()));
		llamada.agregarHijo(esperarLexema(PARENTESIS_ABRE, "Se esperaba '(' después de print."));
		if (!estaAlFinal() && idLexemaActual() != PARENTESIS_CIERRA) {
			llamada.agregarHijo(parseListaExpresiones());
		}
		llamada.agregarHijo(esperarLexema(PARENTESIS_CIERRA, "Se esperaba ')' al final de print."));
		return llamada;
	}

	private NodoSintactico parseListaExpresiones() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.LISTA_IMPRIMIR);
		lista.agregarHijo(parseExpresion());
		while (matchLexema(COMA)) {
			RegistroLexico coma = tokenAnterior();
			lista.agregarHijo(new NodoSintactico(TipoSintactico.TERMINAL, coma.getLexema().getValor(),
					coma.getRow(), coma.getColumn()));
			lista.agregarHijo(parseExpresion());
		}
		return lista;
	}

	private NodoSintactico parseExpresion() {
		return parseExpresionLogica();
	}

	private NodoSintactico parseExpresionLogica() {
		NodoSintactico izquierda = parseExpresionRelacional();
		while (matchLexema(AND) || matchLexema(OR)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico nodo = new NodoSintactico(
					TipoSintactico.OP_LOGICO,
					operador.getLexema().getValor(),
					operador.getRow(),
					operador.getColumn());
			nodo.agregarHijo(izquierda);
			nodo.agregarHijo(parseExpresionRelacional());
			izquierda = nodo;
		}
		return izquierda;
	}

	private NodoSintactico parseExpresionRelacional() {
		NodoSintactico izquierda = parseExpresionAritmetica();
		while (matchLexema(IGUAL_IGUAL) || matchLexema(DIFERENTE) || matchLexema(MENOR) || matchLexema(MAYOR) ||
				matchLexema(MENOR_IGUAL) || matchLexema(MAYOR_IGUAL)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico nodo = new NodoSintactico(
					TipoSintactico.OP_RELACIONAL,
					operador.getLexema().getValor(),
					operador.getRow(),
					operador.getColumn());
			nodo.agregarHijo(izquierda);
			nodo.agregarHijo(parseExpresionAritmetica());
			izquierda = nodo;
		}
		return izquierda;
	}

	private NodoSintactico parseExpresionAritmetica() {
		NodoSintactico izquierda = parseTermino();
		while (matchLexema(MAS) || matchLexema(MENOS)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico nodo = new NodoSintactico(
					TipoSintactico.OP_ARITMETICO,
					operador.getLexema().getValor(),
					operador.getRow(),
					operador.getColumn());
			nodo.agregarHijo(izquierda);
			nodo.agregarHijo(parseTermino());
			izquierda = nodo;
		}
		return izquierda;
	}

	private NodoSintactico parseTermino() {
		NodoSintactico izquierda = parseFactor();
		while (matchLexema(MULTIPLICACION) || matchLexema(DIVISION) || matchLexema(MODULO)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico nodo = new NodoSintactico(
					TipoSintactico.OP_ARITMETICO,
					operador.getLexema().getValor(),
					operador.getRow(),
					operador.getColumn());
			nodo.agregarHijo(izquierda);
			nodo.agregarHijo(parseFactor());
			izquierda = nodo;
		}
		return izquierda;
	}

	private NodoSintactico parseFactor() {
		NodoSintactico base = parseBase();
		if (matchLexema(POTENCIA)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico potencia = new NodoSintactico(
					TipoSintactico.OP_ARITMETICO,
					operador.getLexema().getValor(),
					operador.getRow(),
					operador.getColumn());
			potencia.agregarHijo(base);
			potencia.agregarHijo(parseFactor());
			return potencia;
		}
		return base;
	}

	private NodoSintactico parseBase() {
		if (matchLexema(NOT)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico nodo = new NodoSintactico(TipoSintactico.OP_NOT, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			nodo.agregarHijo(parseBase());
			return nodo;
		}
		if (matchLexema(MENOS)) {
			RegistroLexico operador = tokenAnterior();
			NodoSintactico nodo = new NodoSintactico(TipoSintactico.OP_ARITMETICO, operador.getLexema().getValor(),
					operador.getRow(), operador.getColumn());
			nodo.agregarHijo(parseBase());
			return nodo;
		}
		if (matchLexema(PARENTESIS_ABRE)) {
			NodoSintactico expresion = parseExpresion();
			esperarLexema(PARENTESIS_CIERRA, "Se esperaba ')' después de la expresión entre paréntesis.");
			return expresion;
		}
		if (matchLexema(LLAVE_ABRE)) {
			NodoSintactico tabla = new NodoSintactico(TipoSintactico.TABLA);
			tabla.agregarHijo(listaCamposOpc());
			tabla.agregarHijo(esperarLexema(LLAVE_CIERRA, "Se esperaba '}' después de la definición de tabla."));
			return tabla;
		}
		if (estaAlFinal()) {
			error("Expresión incompleta.");
			return new NodoSintactico(TipoSintactico.TERMINAL_INVALIDO);
		}

		int id = idLexemaActual();
		String lexema = lexemaActual();

		if (tokenActual().getToken() == TokenLexico.VARIABLE) {
			return parseVariable("Se esperaba una variable o literal.");
		}
		if (tokenActual().getToken() == TokenLexico.NUMERO) {
			RegistroLexico token = avanzar();
			return new NodoSintactico(TipoSintactico.NUM_ENTERO, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}
		if (tokenActual().getToken() == TokenLexico.CADENA) {
			RegistroLexico token = avanzar();
			return new NodoSintactico(TipoSintactico.CADENA, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}

		if (id == TRUE || id == FALSE || id == NIL) {
			RegistroLexico token = avanzar();
			return new NodoSintactico(TipoSintactico.BOOLEANO, token.getLexema().getValor(), token.getRow(),
					token.getColumn());
		}
		if (id == PARENTESIS_ABRE) {
			return parseFactor();
		}

		error(String.format("Token inesperado en expresión: '%s' en %d:%d.", lexema, currentRow(), currentColumn()));
		avanzar();
		return new NodoSintactico(TipoSintactico.TERMINAL_INVALIDO);
	}

	private NodoSintactico listaCamposOpc() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.LISTA_CAMPOS);
		if (!estaAlFinal() && idLexemaActual() != LLAVE_CIERRA) {
			lista.agregarHijo(parseListaCampos());
		}
		return lista;
	}

	private NodoSintactico parseListaCampos() {
		NodoSintactico lista = new NodoSintactico(TipoSintactico.LISTA_CAMPOS);
		lista.agregarHijo(parseCampo());

		while (matchLexema(COMA)) {
			RegistroLexico coma = tokenAnterior();
			lista.agregarHijo(new NodoSintactico(
				TipoSintactico.TERMINAL,
				coma.getLexema().getValor(),
				coma.getRow(),
				coma.getColumn()
			));
			lista.agregarHijo(parseCampo());
		}

		return lista;
	}

	private NodoSintactico parseCampo() {
		NodoSintactico campo = new NodoSintactico(TipoSintactico.CAMPO);

		if (tokenEsVariable() && siguienteId() == ASIGNACION) {
			campo.agregarHijo(parseVariable("Se esperaba el nombre del campo."));
			campo.agregarHijo(esperarLexema(ASIGNACION, "Se esperaba '=' después del nombre del campo."));
		}

		campo.agregarHijo(parseExpresion());
		return campo;
	}

	private boolean tokenEsVariable() {
		return !estaAlFinal() && tokenActual().getToken() == TokenLexico.VARIABLE;
	}

	private int siguienteId() {
		return posicion + 1 < tokens.size() ? tokens.get(posicion + 1).getId() : -1;
	}

	private NodoSintactico parseVariable(String mensajeError) {
		if (!estaAlFinal() && tokenActual().getToken() == TokenLexico.VARIABLE) {
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

	private void consumirPuntoComaOpcional() {
		if (matchLexema(PUNTO_COMA)) {
			return;
		}
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

	private boolean esVariable(String lexema) {
		return lexema != null && !lexema.isBlank() && !"print".equalsIgnoreCase(lexema)
				&& automVariables.simulate(lexema) && !Alfabeto.PALABRAS_RESERVADAS.contains(lexema);
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
			int id = idLexemaActual();
			if (id != PUNTO_COMA && id != END && id != ELSE && id != ELSEIF && id != UNTIL && id != DO && id != THEN
					&& id != LOCAL && id != IF && id != WHILE && id != FOR && id != REPEAT && id != PRINT) {
				avanzar();
			} else {
				break;
			}
		}
	}
}
