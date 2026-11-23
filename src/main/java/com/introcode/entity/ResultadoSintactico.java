package com.introcode.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class ResultadoSintactico {

	private final NodoSintactico raiz;
	private final List<String> errores;

	public ResultadoSintactico(NodoSintactico raiz, List<String> errores) {
		this.raiz = raiz;
		this.errores = new ArrayList<>(errores);
	}

	public List<String> getErrores() {
		return Collections.unmodifiableList(errores);
	}

	public boolean esValido() {
		return errores.isEmpty() && raiz != null;
	}
}
