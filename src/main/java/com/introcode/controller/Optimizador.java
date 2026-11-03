package com.introcode.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.introcode.entity.NodoSintactico;
import com.introcode.entity.NodoSintactico.TipoSintactico;

public class Optimizador {

	public String optimizar(NodoSintactico raiz) {
		if (raiz == null) {
			return "";
		}

		List<Linea> lineas = new ArrayList<>();
		Map<String, Boolean> valoresBooleanos = new HashMap<>();
		renderizarSecuencia(raiz, valoresBooleanos, lineas);
		eliminarCodigoMuerto(lineas);
		StringBuilder sb = new StringBuilder();
		for (Linea linea : lineas) {
			sb.append(linea.texto).append(System.lineSeparator());
		}
		return sb.toString();
		// return lineas.stream()
		// .filter(linea -> linea.activa)
		// .map(linea -> linea.texto)
		// .reduce((anterior, siguiente) -> anterior + System.lineSeparator() +
		// siguiente)
		// .orElse("");
	}

	private void renderizarSecuencia(NodoSintactico secuencia, Map<String, Boolean> valoresBooleanos,
			List<Linea> salida) {
		if (secuencia == null) {
			return;
		}
		if (secuencia.getTipo() == TipoSintactico.SENTENCIA || secuencia.getTipo() == TipoSintactico.PROGRAMA) {
			for (NodoSintactico sentencia : secuencia.getHijos()) {
				renderizarSecuencia(sentencia, valoresBooleanos, salida);
			}
			return;
		}
		renderizarSentencia(secuencia, valoresBooleanos, salida);
	}

	private void renderizarSentencia(NodoSintactico sentencia, Map<String, Boolean> valoresBooleanos,
			List<Linea> salida) {
		if (sentencia == null) {
			return;
		}

		List<NodoSintactico> hijos = sentencia.getHijos();
		switch (sentencia.getTipo()) {
			case DECLARACION -> {
				if (hijos.size() < 2) {
					return;
				}
				String nombre = hijos.get(1).getValor();
				NodoSintactico inicializador = hijos.size() > 2 && !hijos.get(2).getHijos().isEmpty()
						? hijos.get(2).getHijos().get(1)
						: null;
				String texto = "local " + nombre;
				if (inicializador != null) {
					texto += "=" + formatearExpresion(inicializador);
				}
				salida.add(linea(texto + "", nombre, true, inicializador));
				actualizarValorBooleano(nombre, inicializador, valoresBooleanos);
			}
			case SOBREESCRIBIR -> {
				if (hijos.size() < 3) {
					return;
				}
				String nombre = hijos.get(0).getValor();
				NodoSintactico expresion = hijos.get(2);
				salida.add(linea(nombre + "=" + formatearExpresion(expresion) + "", nombre, false, expresion));
				actualizarValorBooleano(nombre, expresion, valoresBooleanos);
			}
			case IMPRIMIR -> renderizarImprimir(hijos, salida);
			case CONDICIONAL -> renderizarCondicional(sentencia, valoresBooleanos, salida);
			case WHILE -> renderizarWhile(sentencia, valoresBooleanos, salida);
			case PARA -> renderizarPara(sentencia, valoresBooleanos, salida);
			default -> {
			}
		}
	}

	private void renderizarImprimir(List<NodoSintactico> hijos, List<Linea> salida) {
		if (hijos.size() < 3) {
			return;
		}
		StringBuilder texto = new StringBuilder("print(");
		NodoSintactico lista = hijos.get(2);
		boolean primero = true;
		for (NodoSintactico hijo : lista.getHijos()) {
			if (hijo.getTipo() == TipoSintactico.TERMINAL) {
				continue;
			}
			if (!primero) {
				texto.append(',');
			}
			texto.append(formatearExpresion(hijo));
			primero = false;
		}
		texto.append(");");
		Linea linea = new Linea(texto.toString(), null, false);
		for (NodoSintactico hijo : lista.getHijos()) {
			if (hijo.getTipo() != TipoSintactico.TERMINAL) {
				recopilarLecturas(hijo, linea.lecturas);
			}
		}
		salida.add(linea);
	}

	private void renderizarCondicional(NodoSintactico condicional, Map<String, Boolean> valoresBooleanos,
			List<Linea> salida) {
		List<NodoSintactico> hijos = condicional.getHijos();
		if (hijos.size() < 7) {
			return;
		}

		List<RamaCondicional> ramas = new ArrayList<>();
		ramas.add(new RamaCondicional(hijos.get(1), hijos.get(3), false));
		for (NodoSintactico rama : hijos.get(4).getHijos()) {
			List<NodoSintactico> ramaHijos = rama.getHijos();
			if (ramaHijos.size() >= 4) {
				ramas.add(new RamaCondicional(ramaHijos.get(1), ramaHijos.get(3), true));
			}
		}
		NodoSintactico ramaElse = hijos.get(5);
		NodoSintactico secuenciaElse = ramaElse.getHijos().size() > 1 ? ramaElse.getHijos().get(1) : null;

		int primeraDesconocida = -1;
		int primeraVerdadera = -1;
		for (int indice = 0; indice < ramas.size(); indice++) {
			Boolean resultado = evaluarBooleano(ramas.get(indice).condicion, valoresBooleanos);
			if (resultado == null) {
				primeraDesconocida = indice;
				break;
			}
			if (resultado) {
				primeraVerdadera = indice;
				break;
			}
		}

		if (primeraDesconocida < 0) {
			if (primeraVerdadera >= 0) {
				renderizarSecuencia(ramas.get(primeraVerdadera).secuencia, valoresBooleanos, salida);
			} else if (secuenciaElse != null) {
				renderizarSecuencia(secuenciaElse, valoresBooleanos, salida);
			}
			return;
		}

		Map<String, Boolean> valoresIniciales = new HashMap<>(valoresBooleanos);
		Set<String> escrituras = new HashSet<>();
		for (int indice = primeraDesconocida; indice < ramas.size(); indice++) {
			RamaCondicional rama = ramas.get(indice);
			Boolean resultado = evaluarBooleano(rama.condicion, valoresIniciales);
			if (Boolean.FALSE.equals(resultado)) {
				continue;
			}
			String prefijo = rama.esElseIf ? "elseif(" : "if(";
			salida.add(linea(prefijo + formatearExpresion(rama.condicion) + ") then", null, false,
					rama.condicion));
			escrituras.addAll(recopilarEscrituras(rama.secuencia));
			renderizarSecuencia(rama.secuencia, new HashMap<>(valoresIniciales), salida);
			if (Boolean.TRUE.equals(resultado)) {
				primeraVerdadera = indice;
				break;
			}
		}
		if (primeraVerdadera < 0 && secuenciaElse != null) {
			salida.add(new Linea("else", null, false));
			escrituras.addAll(recopilarEscrituras(secuenciaElse));
			renderizarSecuencia(secuenciaElse, new HashMap<>(valoresIniciales), salida);
		}
		salida.add(new Linea("end", null, false));
		escrituras.forEach(valoresBooleanos::remove);
	}

	private void renderizarWhile(NodoSintactico ciclo, Map<String, Boolean> valoresBooleanos, List<Linea> salida) {
		List<NodoSintactico> hijos = ciclo.getHijos();
		if (hijos.isEmpty()) {
			return;
		}
		if ("repeat".equalsIgnoreCase(hijos.get(0).getValor())) {
			if (hijos.size() < 4) {
				return;
			}
			salida.add(new Linea("repeat", null, false));
			renderizarSecuencia(hijos.get(1), new HashMap<>(valoresBooleanos), salida);
			salida.add(linea("until " + formatearExpresion(hijos.get(3)), null, false, hijos.get(3)));
			recopilarEscrituras(hijos.get(1)).forEach(valoresBooleanos::remove);
			return;
		}
		if (hijos.size() < 5) {
			return;
		}
		salida.add(linea("while " + formatearExpresion(hijos.get(1)) + " do", null, false, hijos.get(1)));
		renderizarSecuencia(hijos.get(3), new HashMap<>(valoresBooleanos), salida);
		salida.add(new Linea("end", null, false));
		recopilarEscrituras(hijos.get(3)).forEach(valoresBooleanos::remove);
	}

	private void renderizarPara(NodoSintactico ciclo, Map<String, Boolean> valoresBooleanos, List<Linea> salida) {
		List<NodoSintactico> hijos = ciclo.getHijos();
		int indiceDo = -1;
		for (int indice = 0; indice < hijos.size(); indice++) {
			if ("do".equalsIgnoreCase(hijos.get(indice).getValor())) {
				indiceDo = indice;
				break;
			}
		}
		if (indiceDo < 0 || hijos.size() <= indiceDo + 2) {
			return;
		}
		StringBuilder texto = new StringBuilder("for ")
				.append(hijos.get(1).getValor()).append('=')
				.append(formatearExpresion(hijos.get(3))).append(',')
				.append(formatearExpresion(hijos.get(5)));
		if (indiceDo == 7) {
			texto.append(',').append(formatearExpresion(hijos.get(6)));
		}
		texto.append(" do");
		Linea linea = linea(texto.toString(), null, false, hijos.get(3), hijos.get(5));
		if (indiceDo == 7) {
			recopilarLecturas(hijos.get(6), linea.lecturas);
		}
		salida.add(linea);
		NodoSintactico cuerpo = hijos.get(indiceDo + 1);
		renderizarSecuencia(cuerpo, new HashMap<>(valoresBooleanos), salida);
		salida.add(new Linea("end", null, false));
		recopilarEscrituras(cuerpo).forEach(valoresBooleanos::remove);
		valoresBooleanos.remove(hijos.get(1).getValor());
	}

	private void actualizarValorBooleano(String nombre, NodoSintactico expresion,
			Map<String, Boolean> valoresBooleanos) {
		Boolean valor = evaluarBooleano(expresion, valoresBooleanos);
		if (valor == null) {
			valoresBooleanos.remove(nombre);
		} else {
			valoresBooleanos.put(nombre, valor);
		}
	}

	private Boolean evaluarBooleano(NodoSintactico nodo, Map<String, Boolean> valoresBooleanos) {
		if (nodo == null) {
			return null;
		}
		switch (nodo.getTipo()) {
			case BOOLEANO:
				if ("true".equalsIgnoreCase(nodo.getValor())) {
					return true;
				}
				return "false".equalsIgnoreCase(nodo.getValor()) ? false : null;
			case VARIABLE:
				return valoresBooleanos.get(nodo.getValor());
			case OP_NOT:
				Boolean operando = evaluarBooleano(nodo.getHijos().getFirst(), valoresBooleanos);
				return operando == null ? null : !operando;
			case OP_LOGICO:
				Boolean izquierda = evaluarBooleano(nodo.getHijos().get(0), valoresBooleanos);
				Boolean derecha = evaluarBooleano(nodo.getHijos().get(1), valoresBooleanos);
				if ("and".equalsIgnoreCase(nodo.getValor())) {
					return Boolean.FALSE.equals(izquierda) || Boolean.FALSE.equals(derecha)
							? false
							: Boolean.TRUE.equals(izquierda) && Boolean.TRUE.equals(derecha) ? true : null;
				}
				if ("or".equalsIgnoreCase(nodo.getValor())) {
					return Boolean.TRUE.equals(izquierda) || Boolean.TRUE.equals(derecha)
							? true
							: Boolean.FALSE.equals(izquierda) && Boolean.FALSE.equals(derecha) ? false : null;
				}
				return null;
			case OP_RELACIONAL:
				if (!"==".equals(nodo.getValor()) && !"~=".equals(nodo.getValor())) {
					return null;
				}
				Boolean izquierdaRelacional = evaluarBooleano(nodo.getHijos().get(0), valoresBooleanos);
				Boolean derechaRelacional = evaluarBooleano(nodo.getHijos().get(1), valoresBooleanos);
				if (izquierdaRelacional == null || derechaRelacional == null) {
					return null;
				}
				boolean iguales = izquierdaRelacional.equals(derechaRelacional);
				return "==".equals(nodo.getValor()) ? iguales : !iguales;
			default:
				return null;
		}
	}

	private String formatearExpresion(NodoSintactico nodo) {
		return formatearExpresion(nodo, 0, false, null);
	}

	private String formatearExpresion(NodoSintactico nodo, int precedenciaPadre, boolean esHijoDerecho,
			String operadorPadre) {
		if (nodo == null) {
			return "";
		}
		TipoSintactico tipo = nodo.getTipo();
		if (tipo == TipoSintactico.OP_ARITMETICO) {
			BigDecimal constante = evaluarConstante(nodo);
			if (constante != null) {
				return formatearNumero(constante);
			}
		}
		if (tipo == TipoSintactico.OP_ARITMETICO || tipo == TipoSintactico.OP_RELACIONAL
				|| tipo == TipoSintactico.OP_LOGICO || tipo == TipoSintactico.OP_NOT) {
			String operador = nodo.getValor() == null ? "" : nodo.getValor();
			List<NodoSintactico> hijos = nodo.getHijos();
			int precedencia = precedencia(tipo, operador);
			String expresion;
			if (tipo == TipoSintactico.OP_NOT || hijos.size() == 1) {
				String prefijo = tipo == TipoSintactico.OP_NOT ? operador + " " : operador;
				String operando = formatearExpresion(hijos.getFirst(), precedencia, true, operador);
				if ("-".equals(operador) && hijos.getFirst().getTipo() == TipoSintactico.OP_ARITMETICO
						&& "-".equals(hijos.getFirst().getValor())) {
					operando = "(" + operando + ")";
				}
				expresion = prefijo + operando;
			} else if (hijos.size() >= 2) {
				String izquierda = formatearExpresion(hijos.get(0), precedencia, false, operador);
				String derecha = formatearExpresion(hijos.get(1), precedencia, true, operador);
				String separador = tipo == TipoSintactico.OP_LOGICO ? " " + operador + " " : operador;
				expresion = izquierda + separador + derecha;
			} else {
				expresion = operador;
			}
			boolean parentesis = precedencia < precedenciaPadre
					|| precedencia == precedenciaPadre && esHijoDerecho
							&& !("^".equals(operadorPadre) && "^".equals(operador))
					|| precedencia == precedenciaPadre && !esHijoDerecho
							&& "^".equals(operadorPadre) && "^".equals(operador);
			return parentesis ? "(" + expresion + ")" : expresion;
		}
		if (tipo == TipoSintactico.TABLA) {
			return formatearTabla(nodo);
		}
		return nodo.getValor() == null ? "" : nodo.getValor();
	}

	private BigDecimal evaluarConstante(NodoSintactico nodo) {
		if (nodo.getTipo() == TipoSintactico.NUMERO) {
			try {
				return new BigDecimal(nodo.getValor());
			} catch (NumberFormatException exception) {
				return null;
			}
		}
		if (nodo.getTipo() != TipoSintactico.OP_ARITMETICO || nodo.getHijos().isEmpty()) {
			return null;
		}

		List<NodoSintactico> hijos = nodo.getHijos();
		BigDecimal izquierda = evaluarConstante(hijos.get(0));
		if (izquierda == null) {
			return null;
		}
		if (hijos.size() == 1) {
			return "-".equals(nodo.getValor()) ? izquierda.negate() : null;
		}
		if (hijos.size() != 2) {
			return null;
		}

		BigDecimal derecha = evaluarConstante(hijos.get(1));
		if (derecha == null) {
			return null;
		}
		try {
			return switch (nodo.getValor()) {
				case "+" -> izquierda.add(derecha);
				case "-" -> izquierda.subtract(derecha);
				case "*" -> izquierda.multiply(derecha);
				case "/" -> derecha.signum() == 0 ? null : izquierda.divide(derecha, MathContext.DECIMAL128);
				case "%" -> derecha.signum() == 0 ? null : izquierda.remainder(derecha);
				case "^" -> evaluarPotencia(izquierda, derecha);
				default -> null;
			};
		} catch (ArithmeticException exception) {
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

	private String formatearTabla(NodoSintactico tabla) {
		StringBuilder resultado = new StringBuilder("{");
		boolean primero = true;
		for (NodoSintactico hijo : tabla.getHijos()) {
			if (hijo.getTipo() != TipoSintactico.LISTA_CAMPOS) {
				continue;
			}
			for (NodoSintactico campo : hijo.getHijos()) {
				if (campo.getTipo() != TipoSintactico.CAMPO) {
					continue;
				}
				if (!primero) {
					resultado.append(',');
				}
				List<NodoSintactico> partes = campo.getHijos();
				if (partes.size() == 3) {
					resultado.append(partes.get(0).getValor()).append('=')
							.append(formatearExpresion(partes.get(2)));
				} else if (!partes.isEmpty()) {
					resultado.append(formatearExpresion(partes.getLast()));
				}
				primero = false;
			}
		}
		return resultado.append('}').toString();
	}

	private int precedencia(TipoSintactico tipo, String operador) {
		if (tipo == TipoSintactico.OP_LOGICO) {
			return "and".equalsIgnoreCase(operador) ? 2 : 1;
		}
		if (tipo == TipoSintactico.OP_RELACIONAL) {
			return 3;
		}
		if (tipo == TipoSintactico.OP_ARITMETICO) {
			if ("+".equals(operador) || "-".equals(operador)) {
				return 4;
			}
			if ("*".equals(operador) || "/".equals(operador) || "%".equals(operador)) {
				return 5;
			}
			if ("^".equals(operador)) {
				return 7;
			}
			return 6;
		}
		return tipo == TipoSintactico.OP_NOT ? 6 : 8;
	}

	private Linea linea(String texto, String destino, boolean declaracion, NodoSintactico... expresiones) {
		Linea linea = new Linea(texto, destino, declaracion);
		for (NodoSintactico expresion : expresiones) {
			recopilarLecturas(expresion, linea.lecturas);
		}
		return linea;
	}

	private void recopilarLecturas(NodoSintactico nodo, Set<String> lecturas) {
		if (nodo == null) {
			return;
		}
		if (nodo.getTipo() == TipoSintactico.VARIABLE) {
			lecturas.add(nodo.getValor());
			return;
		}
		if (nodo.getTipo() == TipoSintactico.CAMPO && nodo.getHijos().size() == 3) {
			recopilarLecturas(nodo.getHijos().get(2), lecturas);
			return;
		}
		for (NodoSintactico hijo : nodo.getHijos()) {
			recopilarLecturas(hijo, lecturas);
		}
	}

	private Set<String> recopilarEscrituras(NodoSintactico nodo) {
		Set<String> escrituras = new HashSet<>();
		recolectarEscrituras(nodo, escrituras);
		return escrituras;
	}

	private void recolectarEscrituras(NodoSintactico nodo, Set<String> escrituras) {
		if (nodo == null) {
			return;
		}
		if (nodo.getTipo() == TipoSintactico.DECLARACION && nodo.getHijos().size() > 1) {
			escrituras.add(nodo.getHijos().get(1).getValor());
		} else if (nodo.getTipo() == TipoSintactico.SOBREESCRIBIR && !nodo.getHijos().isEmpty()) {
			escrituras.add(nodo.getHijos().get(0).getValor());
		} else if (nodo.getTipo() == TipoSintactico.PARA && nodo.getHijos().size() > 1) {
			escrituras.add(nodo.getHijos().get(1).getValor());
		}
		for (NodoSintactico hijo : nodo.getHijos()) {
			recolectarEscrituras(hijo, escrituras);
		}
	}

	private void eliminarCodigoMuerto(List<Linea> lineas) {
		Set<String> declaradas = new HashSet<>();
		for (Linea linea : lineas) {
			if (linea.declaracion && linea.destino != null) {
				declaradas.add(linea.destino);
			}
		}

		boolean cambio;
		do {
			Set<String> utilizadas = new HashSet<>();
			for (Linea linea : lineas) {
				if (linea.activa) {
					utilizadas.addAll(linea.lecturas);
				}
			}
			cambio = false;
			for (Linea linea : lineas) {
				if (linea.activa && linea.destino != null && declaradas.contains(linea.destino)
						&& !utilizadas.contains(linea.destino)) {
					linea.activa = false;
					cambio = true;
				}
			}
		} while (cambio);
	}

	private static class Linea {
		private final String texto;
		private final String destino;
		private final boolean declaracion;
		private final Set<String> lecturas = new HashSet<>();
		private boolean activa = true;

		private Linea(String texto, String destino, boolean declaracion) {
			this.texto = texto;
			this.destino = destino;
			this.declaracion = declaracion;
		}
	}

	private record RamaCondicional(NodoSintactico condicion, NodoSintactico secuencia, boolean esElseIf) {
	}
}
