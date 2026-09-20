package com.introcode.controller;

import java.util.HashMap;
import java.util.Map;

import com.introcode.entity.NodoSintactico;
import com.introcode.entity.NodoSintactico.TipoSintactico;

import lombok.Getter;

@Getter
public class AnSemantico {
	private final Map<String, Object> tablaSimbolos = new HashMap<>();
	private boolean error;

	public void clear() {
		tablaSimbolos.clear();
		error = false;
	}

	public String analizar(NodoSintactico raiz) {
		clear();
		if (raiz == null) {
			return "";
		}
		try {
			return recorrer(raiz);
		} catch (ErrorSemantico e) {
			error = true;
			return e.getMessage() + "\n";
		}
	}

	public String recorrer(NodoSintactico padre) {
		if (error || padre == null) {
			return "";
		}
		if (padre.getTipo() == TipoSintactico.DECLARACION) {
			guardarVariable(padre);
			return "";
		}
		if (padre.getTipo() == TipoSintactico.SOBREESCRIBIR) {
			asignarVariable(padre);
			return "";
		}
		StringBuilder resultado = new StringBuilder();
		for (NodoSintactico nodo : padre.getHijos()) {
			resultado.append(recorrer(nodo));
		}
		return resultado.toString();
	}

	public String guardarVariable(NodoSintactico declaracion) {
		NodoSintactico variable = declaracion.getHijos().get(1);
		NodoSintactico valorOpcional = declaracion.getHijos().get(2);
		String nombre = variable.getValor();
		if (tablaSimbolos.containsKey(nombre)) {
			throw error(variable, String.format(
					"Variable '%s' previamente declarada", nombre));
		}
		Object valor = valorOpcional.getHijos().isEmpty()
				? null
				: evaluar(valorOpcional.getHijos().get(1));
		tablaSimbolos.put(nombre, valor);
		return "";
	}

	private void asignarVariable(NodoSintactico asignacion) {
		NodoSintactico variable = asignacion.getHijos().get(0);
		String nombre = variable.getValor();
		if (!tablaSimbolos.containsKey(nombre)) {
			throw error(variable, "Variable '%s' no declarada", nombre);
		}
		tablaSimbolos.put(nombre, evaluar(asignacion.getHijos().get(2)));
	}

	public String parseExpresion(NodoSintactico nodo) {
		return formatear(evaluar(nodo));
	}

	private Object evaluar(NodoSintactico nodo) {
		if (nodo == null) {
			return null;
		}
		switch (nodo.getTipo()) {
			case NUMERO:
				return parseNumero(nodo.getValor());
			case CADENA:
				return nodo.getValor();
			case BOOLEANO:
				return Boolean.valueOf(nodo.getValor());
			case NIL:
				return null;
			case VARIABLE:
				if (!tablaSimbolos.containsKey(nodo.getValor())) {
					throw error(nodo, "Variable '%s' no declarada", nodo.getValor());
				}
				return tablaSimbolos.get(nodo.getValor());
			case OP_NOT:
				return !esVerdadero(evaluar(nodo.getHijos().get(0)));
			case OP_ARITMETICO:
				return evaluarAritmetica(nodo);
			case OP_RELACIONAL:
				return evaluarRelacional(nodo);
			case OP_LOGICO:
				return evaluarLogica(nodo);
			case TABLA:
				return evaluarTabla(nodo);
			default:
				throw error(nodo, "Expresión no evaluable de tipo '%s'", nodo.getTipo());
		}
	}

	private Object evaluarAritmetica(NodoSintactico nodo) {
		String operador = nodo.getValor();
		boolean unario = nodo.getHijos().size() == 1;
		Object izquierda = unario ? 0L : evaluar(nodo.getHijos().get(0));
		Object derecha = evaluar(nodo.getHijos().getLast());
		if (!(izquierda instanceof Number) || !(derecha instanceof Number)) {
			throw error(nodo, "El operador '%s' requiere números", operador);
		}
		double izquierdo = ((Number) izquierda).doubleValue();
		double derecho = ((Number) derecha).doubleValue();
		if (unario && "-".equals(operador)) {
			return -derecho;
		}
		if ("/".equals(operador) && derecho == 0) {
			throw error(nodo, "No se puede dividir entre cero");
		}
		return switch (operador) {
			case "+" -> normalizarNumero(izquierdo + derecho, izquierda, derecha);
			case "-" -> normalizarNumero(izquierdo - derecho, izquierda, derecha);
			case "*" -> normalizarNumero(izquierdo * derecho, izquierda, derecha);
			case "/" -> izquierdo / derecho;
			case "%" -> izquierdo % derecho;
			case "^" -> Math.pow(izquierdo, derecho);
			default -> throw error(nodo, "Operador aritmético desconocido '%s'", operador);
		};
	}

	private Object evaluarLogica(NodoSintactico nodo) {
		Object izquierda = evaluar(nodo.getHijos().get(0));
		return switch (nodo.getValor()) {
			case "and" -> esVerdadero(izquierda) ? evaluar(nodo.getHijos().get(1)) : izquierda;
			case "or" -> esVerdadero(izquierda) ? izquierda : evaluar(nodo.getHijos().get(1));
			default -> throw error(nodo, "Operador lógico desconocido '%s'", nodo.getValor());
		};
	}

	private Boolean evaluarRelacional(NodoSintactico nodo) {
		Object izquierda = evaluar(nodo.getHijos().get(0));
		Object derecha = evaluar(nodo.getHijos().get(1));
		int comparacion;
		if (izquierda instanceof Number && derecha instanceof Number) {
			comparacion = Double.compare(((Number) izquierda).doubleValue(), ((Number) derecha).doubleValue());
		} else if (izquierda instanceof String && derecha instanceof String) {
			comparacion = ((String) izquierda).compareTo((String) derecha);
		} else if ("==".equals(nodo.getValor()) || "~=".equals(nodo.getValor())) {
			return "==".equals(nodo.getValor())
					? java.util.Objects.equals(izquierda, derecha)
					: !java.util.Objects.equals(izquierda, derecha);
		} else {
			throw error(nodo, "Los operandos de '%s' no son comparables", nodo.getValor());
		}
		return switch (nodo.getValor()) {
			case "==" -> comparacion == 0;
			case "~=" -> comparacion != 0;
			case "<" -> comparacion < 0;
			case ">" -> comparacion > 0;
			case "<=" -> comparacion <= 0;
			case ">=" -> comparacion >= 0;
			default -> throw error(nodo, "Operador relacional desconocido '%s'", nodo.getValor());
		};
	}

	private Object evaluarTabla(NodoSintactico nodo) {
		java.util.List<Object> valores = new java.util.ArrayList<>();
		for (NodoSintactico hijo : nodo.getHijos()) {
			if (hijo.getTipo() == TipoSintactico.LISTA_CAMPOS) {
				recopilarCampos(hijo, valores);
			}
		}
		return valores;
	}

	private void recopilarCampos(NodoSintactico nodo, java.util.List<Object> valores) {
		if (nodo.getTipo() == TipoSintactico.CAMPO && !nodo.getHijos().isEmpty()) {
			valores.add(evaluar(nodo.getHijos().getLast()));
			return;
		}
		for (NodoSintactico hijo : nodo.getHijos()) {
			recopilarCampos(hijo, valores);
		}
	}

	private static Number parseNumero(String valor) {
		return valor.contains(".") ? Double.valueOf(valor) : Long.valueOf(valor);
	}

	private static Object normalizarNumero(double valor, Object izquierda, Object derecha) {
		return izquierda instanceof Long && derecha instanceof Long ? (long) valor : valor;
	}

	private static boolean esVerdadero(Object valor) {
		return valor != null && !Boolean.FALSE.equals(valor);
	}

	private static String formatear(Object valor) {
		return valor == null ? "nil" : String.valueOf(valor);
	}

	private static ErrorSemantico error(NodoSintactico nodo, String formato, Object... argumentos) {
		return new ErrorSemantico(String.format("Error (%d;%d): %s", nodo.getRow(), nodo.getColumn(),
				String.format(formato, argumentos)));
	}

	private static class ErrorSemantico extends RuntimeException {
		private static final long serialVersionUID = 1L;

		ErrorSemantico(String mensaje) {
			super(mensaje);
		}
	}
}
