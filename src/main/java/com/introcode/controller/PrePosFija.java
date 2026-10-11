package com.introcode.controller;

import java.util.ArrayList;
import java.util.List;

import com.introcode.entity.NodoSintactico;
import com.introcode.entity.NodoSintactico.TipoSintactico;

public class PrePosFija {

    private final List<String[]> cuadruplas = new ArrayList<>();
    private int consecutivo;
    private int temporal;

    public String convertirPrefija(NodoSintactico raiz) {
        if (raiz == null) {
            return "SIN EXPRESIONES ENCONTRADAS";
        }
        recorrer(raiz);
        return salidaExpresiones.toString();
    }

    private final StringBuilder salidaExpresiones = new StringBuilder();

    public String recorrer(NodoSintactico padre) {
        if (padre == null) {
            return "";
        }
        salidaExpresiones.setLength(0);
        recolectarExpresiones(padre, new int[] { 0 });
        return salidaExpresiones.toString();
    }

    private void recolectarExpresiones(NodoSintactico padre, int[] numeroExpresion) {
        if (esOperador(padre)) {
            cuadruplas.clear();
            consecutivo = 0;
            temporal = 0;
            generarCuadrupla(padre);
            if (numeroExpresion[0] > 0) {
                salidaExpresiones.append(System.lineSeparator()).append(System.lineSeparator());
            }
            salidaExpresiones.append("EXPRESION ").append(++numeroExpresion[0])
                    .append(": (PREFIJA) -> ")
                    .append(imprimirPrefija(padre, new StringBuilder()))
                    .append(System.lineSeparator()).append(imprimirExpresiones());
            return;
        }
        for (NodoSintactico nodo : padre.getHijos()) {
            recolectarExpresiones(nodo, numeroExpresion);
        }
    }

    private String generarCuadrupla(NodoSintactico operador) {
        List<NodoSintactico> hijos = operador.getHijos();
        String argumento1 = hijos.isEmpty() ? "" : obtenerArgumento(hijos.get(0));
        String argumento2 = hijos.size() < 2 ? "" : obtenerArgumento(hijos.get(1));
        String resultado = "T" + (++temporal);
        cuadruplas.add(new String[] {
            String.valueOf(++consecutivo), operador.getValor(), argumento1, argumento2, resultado
        });
        return resultado;
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

    public String imprimirExpresiones(){
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
