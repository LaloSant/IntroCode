package com.introcode.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodoSintactico {

    private final String tipo;
    private final String valor;
    private final int row;
    private final int column;
    private final List<NodoSintactico> hijos = new ArrayList<>();

    public NodoSintactico(String tipo) {
        this(tipo, null, -1, -1);
    }

    public NodoSintactico(String tipo, String valor) {
        this(tipo, valor, -1, -1);
    }

    public NodoSintactico(String tipo, String valor, int row, int column) {
        this.tipo = tipo;
        this.valor = valor;
        this.row = row;
        this.column = column;
    }

    public String getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void agregarHijo(NodoSintactico hijo) {
        if (hijo != null) {
            hijos.add(hijo);
        }
    }

    public List<NodoSintactico> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String toString(int nivel) {
        String indent = "  ".repeat(Math.max(0, nivel));
        String textoBase = valor == null ? String.format("%s%s", indent, tipo)
                : String.format("%s%s : %s", indent, tipo, valor);
        StringBuilder sb = new StringBuilder(textoBase);
        for (NodoSintactico hijo : hijos) {
            sb.append(System.lineSeparator()).append(hijo.toString(nivel + 1));
        }
        return sb.toString();
    }
}
