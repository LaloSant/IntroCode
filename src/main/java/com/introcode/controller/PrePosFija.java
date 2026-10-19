package com.introcode.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.introcode.entity.NodoSintactico;
import com.introcode.entity.NodoSintactico.TipoSintactico;

public class PrePosFija {

	private final StringBuilder salidaExpresiones = new StringBuilder();

	private final List<String[]> cuadruplas = new ArrayList<>();

	private Map<String, Object> tablaSimbolos;

	private int consecutivo;

	private int temporal;
	

	public String convertirPrefija(NodoSintactico raiz, Map<String, Object> tablaSimbolos) {
		if (raiz == null) {
			return "SIN EXPRESIONES ENCONTRADAS";
		}
		this.tablaSimbolos = tablaSimbolos;
		recorrer(raiz);
		return salidaExpresiones.toString();
	}


	public String recorrer(NodoSintactico padre) {
		if (padre == null) {
			return "";
		}
		salidaExpresiones.setLength(0);
		recolectarExpresiones(padre, new int[] { 0 });
		return salidaExpresiones.toString();
	}

	private void recolectarExpresiones(NodoSintactico padre, int[] numeroExpresion) {
		recolectarExpresiones(padre, numeroExpresion, null);
	}

	private void recolectarExpresiones(NodoSintactico padre, int[] numeroExpresion, String variableAsignada) {
		if (padre.getTipo() == TipoSintactico.DECLARACION) {
			List<NodoSintactico> hijos = padre.getHijos();
			if (hijos.size() > 2 && hijos.get(2).getHijos().size() > 1) {
				recolectarExpresiones(hijos.get(2).getHijos().get(1), numeroExpresion, hijos.get(1).getValor());
			}
			return;
		}
		if (padre.getTipo() == TipoSintactico.SOBREESCRIBIR) {
			List<NodoSintactico> hijos = padre.getHijos();
			if (hijos.size() > 2) {
				recolectarExpresiones(hijos.get(2), numeroExpresion, hijos.get(0).getValor());
			}
			return;
		}
		if (esOperador(padre)) {
			cuadruplas.clear();
			consecutivo = 0;
			temporal = 0;
			generarCuadrupla(padre);
			if (numeroExpresion[0] > 0) {
				salidaExpresiones.append(System.lineSeparator()).append(System.lineSeparator());
			}
			salidaExpresiones.append("EXPRESION ").append(++numeroExpresion[0]).append(':')
					.append(System.lineSeparator()).append("PREFIJA -> ")
					.append(imprimirPrefija(padre, new StringBuilder()))
					.append(System.lineSeparator()).append("POSFIJA -> ")
					.append(imprimirPosfija(padre, new StringBuilder()))
					.append(System.lineSeparator()).append(imprimirExpresiones())
					.append(System.lineSeparator()).append("VALOR: ")
					.append(obtenerValorFinal(variableAsignada));
			return;
		}
		for (NodoSintactico nodo : padre.getHijos()) {
			recolectarExpresiones(nodo, numeroExpresion, variableAsignada);
		}
	}

	private String obtenerValorFinal(String variableAsignada) {
		if (variableAsignada == null || tablaSimbolos == null || !tablaSimbolos.containsKey(variableAsignada)) {
			return "";
		}
		Object valor = tablaSimbolos.get(variableAsignada);
		return valor == null ? "nil" : valor.toString();
	}

	private String generarCuadrupla(NodoSintactico operador) {
		List<NodoSintactico> hijos = operador.getHijos();
		String argumento1 = hijos.isEmpty() ? "" : obtenerArgumento(hijos.get(0));
		String argumento2 = hijos.size() < 2 ? "" : obtenerArgumento(hijos.get(1));
		String resultado = evaluarConstante(operador, argumento1, argumento2);
		if (resultado == null) {
			resultado = "T" + (++temporal);
		}
		cuadruplas.add(new String[] {
				String.valueOf(++consecutivo), operador.getValor(), argumento1, argumento2, resultado
		});
		return resultado;
	}

	private String evaluarConstante(NodoSintactico operador, String argumento1, String argumento2) {
		if (operador.getTipo() != TipoSintactico.OP_ARITMETICO) {
			return null;
		}
		try {
			BigDecimal izquierda = new BigDecimal(argumento1);
			if (operador.getHijos().size() == 1) {
				return "-".equals(operador.getValor()) ? formatearNumero(izquierda.negate()) : null;
			}
			BigDecimal derecha = new BigDecimal(argumento2);
			BigDecimal resultado = switch (operador.getValor()) {
				case "+" -> izquierda.add(derecha);
				case "-" -> izquierda.subtract(derecha);
				case "*" -> izquierda.multiply(derecha);
				case "/" -> derecha.signum() == 0 ? null : izquierda.divide(derecha, MathContext.DECIMAL128);
				case "%" -> derecha.signum() == 0 ? null : izquierda.remainder(derecha);
				case "^" -> evaluarPotencia(izquierda, derecha);
				default -> null;
			};
			return resultado == null ? null : formatearNumero(resultado);
		} catch (NumberFormatException | ArithmeticException exception) {
			return null;
		}
	}

	private BigDecimal evaluarPotencia(BigDecimal base, BigDecimal exponente) {
		double resultado = Math.pow(base.doubleValue(), exponente.doubleValue());
		return Double.isFinite(resultado) ? BigDecimal.valueOf(resultado) : null;
	}

	private String formatearNumero(BigDecimal numero) {
		return numero.stripTrailingZeros().toPlainString();
	}

	private String obtenerArgumento(NodoSintactico nodo) {
		if (esOperador(nodo)) {
			return generarCuadrupla(nodo);
		}
		if (nodo.getHijos().isEmpty()) {
			return nodo.getValor() == null ? nodo.getTipo().toString() : nodo.getValor();
		}

		String resultado = "";
		for (NodoSintactico hijo : nodo.getHijos()) {
			String valorHijo = obtenerArgumento(hijo);
			if (!valorHijo.isEmpty()) {
				resultado = valorHijo;
			}
		}
		return resultado.isEmpty() && nodo.getValor() != null ? nodo.getValor() : resultado;
	}

	private boolean esOperador(NodoSintactico nodo) {
		TipoSintactico tipo = nodo.getTipo();
		return tipo == TipoSintactico.OP_ARITMETICO
				|| tipo == TipoSintactico.OP_RELACIONAL
				|| tipo == TipoSintactico.OP_LOGICO
				|| tipo == TipoSintactico.OP_NOT;
	}

	public String imprimirPrefija(NodoSintactico padre, StringBuilder sb) {
		if (padre == null) {
			return sb.toString();
		}
		if (padre.getValor() != null) {
			sb.append(padre.getValor()).append(' ');
		}
		for (NodoSintactico hijo : padre.getHijos()) {
			imprimirPrefija(hijo, sb);
		}
		return sb.toString();
	}

	public String imprimirPosfija(NodoSintactico padre, StringBuilder sb) {
		if (padre == null) {
			return sb.toString();
		}
		for (NodoSintactico hijo : padre.getHijos()) {
			imprimirPosfija(hijo, sb);
		}
		if (padre.getValor() != null) {
			sb.append(padre.getValor()).append(' ');
		}
		return sb.toString();
	}

	public String imprimirExpresiones() {
		String[] encabezados = { "# ", "OPERADOR", "ARGUMENTO 1", "ARGUMENTO 2", "RESULTADO" };
		int[] anchos = new int[encabezados.length];
		for (int columna = 0; columna < encabezados.length; columna++) {
			anchos[columna] = encabezados[columna].length();
		}
		for (String[] fila : cuadruplas) {
			for (int columna = 0; columna < fila.length; columna++) {
				anchos[columna] = Math.max(anchos[columna], fila[columna].length());
			}
		}

		StringBuilder resultado = new StringBuilder(formatearFila(encabezados, anchos));
		resultado.append(System.lineSeparator());
		for (int columna = 0; columna < anchos.length; columna++) {
			if (columna > 0) {
				resultado.append("-+-");
			}
			resultado.append("-".repeat(anchos[columna]));
		}
		for (String[] fila : cuadruplas) {
			resultado.append(System.lineSeparator()).append(formatearFila(fila, anchos));
		}
		return resultado.toString();
	}

	private String formatearFila(String[] celdas, int[] anchos) {
		StringBuilder fila = new StringBuilder();
		for (int columna = 0; columna < celdas.length; columna++) {
			if (columna > 0) {
				fila.append(" | ");
			}
			fila.append(String.format("%-" + anchos[columna] + "s", celdas[columna]));
		}
		return fila.toString();
	}
}
